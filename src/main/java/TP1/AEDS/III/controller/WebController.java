package TP1.AEDS.III.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import TP1.AEDS.III.models.Boleto;
import TP1.AEDS.III.models.BoletoDAO;
import TP1.AEDS.III.models.BoletoStatus;
import TP1.AEDS.III.models.BoletoTagDAO;
import TP1.AEDS.III.models.Cliente;
import TP1.AEDS.III.models.ClienteDAO;
import TP1.AEDS.III.models.Tag;
import TP1.AEDS.III.models.TagDAO;
import TP1.AEDS.III.models.Usuario;
import TP1.AEDS.III.models.UsuarioDAO;
import TP1.AEDS.III.repository.BuscaPadrao;
import TP1.AEDS.III.repository.Criptografia;
import TP1.AEDS.III.repository.Huffman;
import TP1.AEDS.III.repository.LZW;

@Controller
public class WebController {

    // --- UTILITÁRIOS ---
    private String limparCPF(String cpf) {
        if (cpf == null) return "";
        return cpf.replaceAll("\\D", "");
    }

    // --- UTILITÁRIO: FORÇA ATUALIZAÇÃO DO CABEÇALHO APÓS RESTORE (NOVO) ---
    private void forcarCabecalho(int ultimoID) throws Exception {
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

    // --- CLIENTES ---
    @GetMapping("/clientes")
    public String listarClientes(Model model) {
        try {
            ClienteDAO dao = new ClienteDAO();
            model.addAttribute("clientes", dao.listarTodosClientes());
        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao listar: " + e.getMessage());
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
            Cliente c = new Cliente(nome, limparCPF(cpf), salario, LocalDate.parse(nascimento)); 
            dao.incluirCliente(c);
            return "redirect:/clientes";
        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao salvar: " + e.getMessage());
            return "novo_cliente";
        }
    }

    // --- BOLETOS ---
    @GetMapping("/boletos")
    public String listarBoletos(@RequestParam(required = false) String busca, Model model) {
        try {
            BoletoDAO dao = new BoletoDAO();
            List<Boleto> lista = new ArrayList<>();
            // Limite de visualização para performance
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
                model.addAttribute("erro", "Cliente não encontrado: " + cpf);
                return "novo_boleto";
            }

            Boleto b = new Boleto(0, cpfLimpo, LocalDate.now(), LocalDate.parse(vencimento), 
                                descricao, new BigDecimal(valor), BoletoStatus.PENDENTE);
            dao.incluirBoleto(b);
            return "redirect:/boletos";
        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao salvar: " + e.getMessage());
            return "novo_boleto";
        }
    }

    // --- TAGS ---
    @GetMapping("/tags")
    public String listarTags(Model model) {
        try {
            TagDAO dao = new TagDAO();
            model.addAttribute("tags", dao.listarTodasTags());
        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao listar tags: " + e.getMessage());
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
            forcarCabecalho(200); // CHAMA O FIX
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
            forcarCabecalho(200); // CHAMA O FIX
            model.addAttribute("mensagem", "✅ Restauração Huffman concluída.");
        } catch (Exception e) {
            model.addAttribute("erro", "Falha na restauração Huffman: " + e.getMessage());
        }
        return "home";
    }

    // --- UTILITÁRIOS DE TESTE ---
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

            System.out.println("Gerando massa de dados (200 registros)...");
            for (int i = 1; i <= 200; i++) {
                String descricao = (i % 3 == 0) ? "Pagamento referente ao Aluguel de Maio" :
                                   (i % 3 == 1) ? "Conta de Luz Residencial" : 
                                                  "Assinatura de Internet Fibra Optica";

                Boleto b = new Boleto(0, cpfCliente, LocalDate.now(), LocalDate.now(), 
                                    descricao, new BigDecimal("100.00"), BoletoStatus.PENDENTE);
                bDao.incluirBoleto(b);
                
                try {
                    if (i % 2 == 0) btDao.adicionarTagAoBoleto(1, i);
                    if (i % 3 == 0) btDao.adicionarTagAoBoleto(2, i);
                } catch (Exception e) {}
            }

            model.addAttribute("mensagem", "✅ 200 registros gerados para teste.");
        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao gerar dados: " + e.getMessage());
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

            for (int i = 1; i <= 20; i++) {
                Boleto b = bDao.buscarBoleto(i);
                if (b != null) {
                    relatorio.append("<p><b>Boleto " + b.getId() + ":</b> " + b.getDescricao() + "<br>");
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