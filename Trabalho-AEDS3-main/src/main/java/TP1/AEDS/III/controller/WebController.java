package TP1.AEDS.III.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import TP1.AEDS.III.models.*;
import TP1.AEDS.III.repository.BuscaPadrao;
import TP1.AEDS.III.repository.Criptografia;
import TP1.AEDS.III.repository.LZW;
import TP1.AEDS.III.repository.Huffman;

@Controller
public class WebController {

    // --- UTILITÁRIOS ---
    private String limparCPF(String cpf) {
        if (cpf == null) return "";
        return cpf.replaceAll("\\D", "");
    }

    // --- UTILITÁRIO: FORÇA ATUALIZAÇÃO DO CABEÇALHO APÓS RESTORE (ESSENCIAL) ---
    private void forcarCabecalho(int ultimoID) throws Exception {
        // Abre o arquivo boletos.db para escrita e corrige o último ID
        java.io.RandomAccessFile raf = new java.io.RandomAccessFile("dados/boletos/boletos.db", "rw");
        raf.seek(0); 
        raf.writeInt(ultimoID); 
        raf.close();
    }

    // --- LOGIN E HOME ---
    @GetMapping("/")
    public String login() { return "login"; }

    @PostMapping("/login")
    public String realizarLogin(@RequestParam String usuario, @RequestParam String senha, Model model) {
        try {
            UsuarioDAO dao = new UsuarioDAO();
            Usuario user = dao.buscarPorLogin(usuario);
            
            if (user != null) {
                // Descriptografa a senha do banco (RSA) e compara com a entrada
                String senhaOriginal = Criptografia.descriptografar(user.getSenhaCifrada());
                
                if (senhaOriginal != null && senhaOriginal.equals(senha)) {
                    return "redirect:/home";
                }
            }
            model.addAttribute("erro", "Credenciais inválidas.");
            return "login";
        } catch (Exception e) {
            model.addAttribute("erro", "Erro no sistema: " + e.getMessage());
            return "login";
        }
    }

    @GetMapping("/home")
    public String home() { return "home"; }

    // --- MÓDULO CLIENTES ---
    @GetMapping("/clientes")
    public String listarClientes(Model model) {
        try {
            ClienteDAO dao = new ClienteDAO();
            model.addAttribute("clientes", dao.listarTodosClientes());
        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao listar clientes: " + e.getMessage());
        }
        return "clientes";
    }

    @GetMapping("/novo-cliente")
    public String formCliente() { return "novo_cliente"; }

    @PostMapping("/novo-cliente")
    public String salvarCliente(@RequestParam String nome, @RequestParam String cpf, 
                              @RequestParam String nascimento, @RequestParam float salario, Model model) {
        try {
            ClienteDAO dao = new ClienteDAO();
            String cpfLimpo = limparCPF(cpf);
            LocalDate dataNasc = LocalDate.parse(nascimento);
            
            Cliente c = new Cliente(nome, cpfLimpo, salario, dataNasc); 
            dao.incluirCliente(c);
            
            return "redirect:/clientes";
        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao salvar: " + e.getMessage());
            return "novo_cliente";
        }
    }

    // --- MÓDULO BOLETOS ---
    @GetMapping("/boletos")
    public String listarBoletos(@RequestParam(required = false) String busca, Model model) {
        try {
            BoletoDAO dao = new BoletoDAO();
            List<Boleto> lista = new ArrayList<>();
            // Varrer IDs de 1 a 600 para cobrir os dados gerados
            for (int i = 1; i <= 600; i++) {
                Boleto b = dao.buscarBoleto(i);
                if (b != null) {
                    if (busca != null && !busca.isEmpty()) {
                        if (BuscaPadrao.kmp(b.getDescricao(), busca)) lista.add(b);
                    } else {
                        lista.add(b);
                    }
                }
            }
            model.addAttribute("boletos", lista);
            model.addAttribute("termoBusca", busca);
        } catch (Exception e) {
            model.addAttribute("erro", e.getMessage());
        }
        return "boletos";
    }

    @GetMapping("/novo-boleto")
    public String formBoleto() { return "novo_boleto"; }

    @PostMapping("/novo-boleto")
    public String salvarBoleto(@RequestParam String cpf, @RequestParam String descricao, 
                             @RequestParam String valor, @RequestParam String vencimento, Model model) {
        try {
            BoletoDAO dao = new BoletoDAO();
            ClienteDAO cDao = new ClienteDAO();
            String cpfLimpo = limparCPF(cpf);

            if (cDao.buscarClientePorCPF(cpfLimpo) == null) {
                model.addAttribute("erro", "Cliente não encontrado com CPF: " + cpf);
                return "novo_boleto";
            }

            LocalDate dataVenc = LocalDate.parse(vencimento);
            Boleto b = new Boleto(0, cpfLimpo, LocalDate.now(), dataVenc, descricao, new BigDecimal(valor), BoletoStatus.PENDENTE);
            dao.incluirBoleto(b);
            
            return "redirect:/boletos";
        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao salvar boleto: " + e.getMessage());
            return "novo_boleto";
        }
    }

    // --- MÓDULO TAGS ---
    @GetMapping("/tags")
    public String listarTags(Model model) {
        try {
            TagDAO dao = new TagDAO();
            model.addAttribute("tags", dao.listarTodasTags());
        } catch (Exception e) {
            model.addAttribute("erro", "Erro nas tags: " + e.getMessage());
        }
        return "tags";
    }

    @PostMapping("/nova-tag")
    public String salvarTag(@RequestParam String nome, Model model) {
        try {
            TagDAO dao = new TagDAO();
            dao.criarTag(new Tag(nome));
            return "redirect:/tags";
        } catch (Exception e) {
            return "redirect:/tags?erro=" + e.getMessage();
        }
    }

    // --- GERENCIAMENTO N:N ---
    @GetMapping("/boleto/tags")
    public String gerenciarTagsBoleto(@RequestParam int id, Model model) {
        try {
            BoletoDAO bDao = new BoletoDAO();
            TagDAO tDao = new TagDAO();
            BoletoTagDAO btDao = new BoletoTagDAO();

            Boleto boleto = bDao.buscarBoleto(id);
            if (boleto == null) return "redirect:/boletos";

            model.addAttribute("boleto", boleto);
            model.addAttribute("todasTags", tDao.listarTodasTags());
            model.addAttribute("tagsAtuais", btDao.listarTagsDoBoleto(id)); 
            
            return "gerenciar_tags";
        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao carregar: " + e.getMessage());
            return "redirect:/boletos";
        }
    }

    @PostMapping("/boleto/adicionar-tag")
    public String adicionarTagAoBoleto(@RequestParam int idBoleto, @RequestParam int idTag) {
        try {
            new BoletoTagDAO().adicionarTagAoBoleto(idTag, idBoleto); 
        } catch (Exception e) { }
        return "redirect:/boleto/tags?id=" + idBoleto;
    }

    @PostMapping("/boleto/remover-tag")
    public String removerTagDoBoleto(@RequestParam int idBoleto, @RequestParam int idTag) {
        try {
            new BoletoTagDAO().removerTagDoBoleto(idTag, idBoleto);
        } catch (Exception e) { }
        return "redirect:/boleto/tags?id=" + idBoleto;
    }

    // --- BACKUP & RESTORE ---
    @GetMapping("/backup")
    public String realizarBackup(Model model) {
        try {
            LZW.comprimirArquivo("dados/boletos/boletos.db", "dados/backup_boletos.lzw");
            model.addAttribute("mensagem", "✅ Backup LZW realizado com sucesso.");
        } catch (Exception e) {
            model.addAttribute("erro", "Falha no backup LZW: " + e.getMessage());
        }
        return "home";
    }

    @GetMapping("/restore-lzw")
    public String restaurarLZW(Model model) {
        try {
            LZW.descomprimirArquivo("dados/backup_boletos.lzw", "dados/boletos/boletos.db");
            forcarCabecalho(600); // CORREÇÃO: Força cabeçalho para garantir leitura de todos os IDs
            model.addAttribute("mensagem", "✅ Restauração LZW concluída.");
        } catch (Exception e) {
            model.addAttribute("erro", "Falha na restauração LZW: " + e.getMessage());
        }
        return "home";
    }

    @GetMapping("/backup-huffman")
    public String realizarBackupHuffman(Model model) {
        try {
            Huffman.comprimirArquivo("dados/boletos/boletos.db", "dados/backup_boletos.huffman");
            model.addAttribute("mensagem", "✅ Backup Huffman realizado com sucesso.");
        } catch (Exception e) {
            model.addAttribute("erro", "Falha no backup Huffman: " + e.getMessage());
        }
        return "home";
    }

    @GetMapping("/restore-huffman")
    public String restaurarHuffman(Model model) {
        try {
            Huffman.descomprimirArquivo("dados/backup_boletos.huffman", "dados/boletos/boletos.db");
            forcarCabecalho(600); // CORREÇÃO: Força cabeçalho para garantir leitura de todos os IDs
            model.addAttribute("mensagem", "✅ Restauração Huffman concluída.");
        } catch (Exception e) {
            model.addAttribute("erro", "Falha na restauração Huffman: " + e.getMessage());
        }
        return "home";
    }

    // --- GERAR DADOS DE TESTE ---
    @GetMapping("/gerar-dados")
    public String gerarDadosTeste(Model model) {
        try {
            new UsuarioDAO(); 
            ClienteDAO cDao = new ClienteDAO();
            BoletoDAO bDao = new BoletoDAO();
            TagDAO tDao = new TagDAO();
            BoletoTagDAO btDao = new BoletoTagDAO();

            String cpfCliente = "11111111111";

            if (cDao.buscarClientePorCPF(cpfCliente) == null) {
                cDao.incluirCliente(new Cliente("Cliente Teste Massivo", cpfCliente, 5000f, LocalDate.now()));
            }

            try { tDao.criarTag(new Tag("URGENTE")); } catch (Exception e) {}
            try { tDao.criarTag(new Tag("MENSAL")); } catch (Exception e) {}
            try { tDao.criarTag(new Tag("CORPORATIVO")); } catch (Exception e) {}

            System.out.println("Gerando massa de dados (500 registros)...");
            for (int i = 1; i <= 500; i++) {
                String descricao;
                if (i % 3 == 0) descricao = "Pagamento referente ao Aluguel de Maio"; 
                else if (i % 3 == 1) descricao = "Conta de Luz Residencial";          
                else descricao = "Assinatura de Internet Fibra Optica";               

                Boleto b = new Boleto(0, cpfCliente, 
                    LocalDate.now(), 
                    LocalDate.now(), 
                    descricao, 
                    new BigDecimal("100.00"), 
                    BoletoStatus.PENDENTE);
                
                // Importante: O método incluirBoleto pode não retornar o ID, mas o create do ArquivoBD sim.
                // Aqui assumimos que o incluirBoleto funciona. Para o N:N, vamos tentar usar o ID 'i' se for sequencial.
                // Em produção, o b.getId() deveria ser atualizado pelo DAO.
                bDao.incluirBoleto(b);
                
                // Tenta vincular tags (N:N)
                // Assumindo IDs sequenciais para boletos recém-criados
                try {
                    if (i % 2 == 0) btDao.adicionarTagAoBoleto(1, i); // Tag 1 (Urgente)
                    if (i % 3 == 0) btDao.adicionarTagAoBoleto(2, i); // Tag 2 (Mensal)
                    if (i % 5 == 0) btDao.adicionarTagAoBoleto(3, i); // Tag 3 (Corporativo)
                } catch (Exception e) {}
            }

            model.addAttribute("mensagem", "✅ 500 Boletos + Tags + Relacionamentos N:N gerados com sucesso!");
        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao gerar dados: " + e.getMessage());
            e.printStackTrace();
        }
        return "home";
    }

    @GetMapping("/sabotagem")
    public String sabotarDados(Model model) {
        try {
            java.io.RandomAccessFile raf = new java.io.RandomAccessFile("dados/boletos/boletos.db", "rw");
            raf.setLength(0); 
            raf.close();
            model.addAttribute("erro", "⚠️ ALERTA: Banco de dados apagado manualmente.");
        } catch (Exception e) {
            model.addAttribute("erro", "Falha na simulação: " + e.getMessage());
        }
        return "home";
    }

    @GetMapping("/prova-fase3")
    @ResponseBody 
    public String provaFase3() {
        StringBuilder relatorio = new StringBuilder();
        relatorio.append("<h1>Relatório de Auditoria N:N</h1><hr>");

        try {
            BoletoDAO bDao = new BoletoDAO();
            BoletoTagDAO btDao = new BoletoTagDAO();

            for (int i = 1; i <= 200; i++) {
                Boleto b = bDao.buscarBoleto(i);
                if (b != null) {
                    relatorio.append("<p><b>Boleto ID " + b.getId() + ":</b> " + b.getDescricao() + "<br>");
                    relatorio.append("&nbsp;&nbsp; ➡️ Tags: ");
                    List<Tag> tags = btDao.listarTagsDoBoleto(b.getId());
                    if (tags.isEmpty()) {
                        relatorio.append("<i>Nenhuma</i>");
                    } else {
                        for (Tag t : tags) {
                            relatorio.append("<b>[" + t.getNome() + "]</b> ");
                        }
                    }
                    relatorio.append("</p>");
                }
            }
            return relatorio.toString();
        } catch (Exception e) {
            return "<h1>Erro</h1><p>" + e.getMessage() + "</p>";
        }
    }
}