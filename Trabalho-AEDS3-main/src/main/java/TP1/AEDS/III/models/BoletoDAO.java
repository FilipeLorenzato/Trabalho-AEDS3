package TP1.AEDS.III.models;
import TP1.AEDS.III.repository.ArquivoBD;
import TP1.AEDS.III.repository.HashExtensivel;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BoletoDAO {
    private ArquivoBD<Boleto> arqBoletos;
    private HashExtensivel<RegistroHashBoleto> indiceBoletos;

    public BoletoDAO() throws Exception {
        // Criar diretório para índices se não existir
        File dirIndices = new File("./dados/indices");
        if (!dirIndices.exists()) dirIndices.mkdirs();
        
        arqBoletos = new ArquivoBD<>("boletos", Boleto.class.getConstructor());
        
        // Inicializar índice hash para boletos
        indiceBoletos = new HashExtensivel<>(
            RegistroHashBoleto.class.getConstructor(),
            4, // 4 registros por cesto
            "./dados/indices/boletos_diretorio.hash_d",
            "./dados/indices/boletos_cestos.hash_c"
        );
    }

    public Boleto buscarBoleto(int id) throws Exception {
        // Busca primeiro no índice hash
        RegistroHashBoleto regHash = indiceBoletos.read(id);
        if (regHash == null) {
            return null; // Boleto não encontrado no índice
        }
        
        // Usa o endereço do índice para buscar diretamente no arquivo
        return arqBoletos.readAtAddress(regHash.getEndereco());
    }

    public boolean incluirBoleto(Boleto boleto) throws Exception {
        // Verifica se o CPF do cliente foi informado
        if (boleto.getCPFCliente() == null || boleto.getCPFCliente().trim().isEmpty()) {
            throw new Exception("CPF do cliente e obrigatorio para incluir boleto");
        }
        
        // Verifica se o cliente existe no sistema
        ClienteDAO clienteDAO = new ClienteDAO();
        Cliente cliente = clienteDAO.buscarClientePorCPF(boleto.getCPFCliente());
        
        if (cliente == null) {
            throw new Exception("Cliente nao encontrado. E necessario ter clientes cadastrados primeiro.");
        }
        
        // Salva o boleto no arquivo de dados e obtém o endereço
        long endereco = arqBoletos.create(boleto);
        
        if (endereco > 0) {
            // Adiciona o registro no índice hash
            RegistroHashBoleto regHash = new RegistroHashBoleto(boleto.getId(), endereco);
            indiceBoletos.create(regHash);
            return true;
        }
        return false;
    }

    public boolean alterarBoleto(Boleto boleto) throws Exception {
        // Primeiro verifica se o boleto existe no índice
        RegistroHashBoleto regHash = indiceBoletos.read(boleto.getId());
        if (regHash == null) {
            return false; // Boleto não encontrado
        }
        
        // Busca o endereço antigo antes do update
        long enderecoAntigo = regHash.getEndereco();
        
        // Atualiza o registro no arquivo de dados
        boolean atualizado = arqBoletos.update(boleto);
        
        if (atualizado) {
            // Busca o novo endereço após o update
            long novoEndereco = arqBoletos.findAddress(boleto.getId());
            
            // Se o endereço mudou, atualiza o índice hash
            if (novoEndereco != enderecoAntigo && novoEndereco != -1) {
                indiceBoletos.delete(boleto.getId());
                RegistroHashBoleto novoRegHash = new RegistroHashBoleto(boleto.getId(), novoEndereco);
                indiceBoletos.create(novoRegHash);
            }
        }
        
        return atualizado;
    }

    public boolean excluirBoleto(int id) throws Exception {
        // Remove do arquivo de dados
        boolean removido = arqBoletos.delete(id);
        
        if (removido) {
            // Remove do índice hash
            indiceBoletos.delete(id);
        }
        
        return removido;
    }

    public List<Boleto> listarBoletosPorCPF(String cpfCliente) throws Exception {
        List<Boleto> boletosDoCLiente = new ArrayList<>();
        
        if (cpfCliente == null || cpfCliente.trim().isEmpty()) {
            return boletosDoCLiente;
        }
        
        // Primeiro, vamos verificar o último ID usado no arquivo
        //System.out.println("DEBUG: Buscando boletos para cliente CPF: " + cpfCliente);
        
        int id = 1;
        int tentativasVazias = 0;
        final int MAX_TENTATIVAS_VAZIAS = 50; // Parar após 50 IDs consecutivos não encontrados
        
        while (tentativasVazias < MAX_TENTATIVAS_VAZIAS && id <= 10000) {
            try {
                Boleto boleto = buscarBoleto(id); // Usa o índice hash
                if (boleto == null) {
                    tentativasVazias++;
                } else {
                    tentativasVazias = 0; // Reset contador
                    //System.out.println("DEBUG: Boleto encontrado - ID: " + boleto.getId() + ", CPF Cliente: " + boleto.getCPFCliente());
                    
                    if (cpfCliente.equals(boleto.getCPFCliente())) {
                        boletosDoCLiente.add(boleto);
                        //System.out.println("Boleto adicionado à lista!");
                    }
                }
                id++;
            } catch (Exception e) {
                System.out.println("Erro ao ler boleto ID " + id + ": " + e.getMessage());
                tentativasVazias++;
                id++;
            }
        }
        
        System.out.println("Total de boletos encontrados para o cliente: " + boletosDoCLiente.size());
        return boletosDoCLiente;
    }

    // Método para compatibilidade (deprecated)
    @Deprecated
    public List<Boleto> listarBoletosPorCliente(int idCliente) throws Exception {
        // Buscar o CPF do cliente pelo ID (método menos eficiente)
        // Este método deveria ser evitado, mas mantido para compatibilidade
        System.out.println("AVISO: Usando método deprecated listarBoletosPorCliente(int). Use listarBoletosPorCPF(String)");
        
        List<Boleto> boletosDoCLiente = new ArrayList<>();
        
        int id = 1;
        int tentativasVazias = 0;
        final int MAX_TENTATIVAS_VAZIAS = 50;
        
        while (tentativasVazias < MAX_TENTATIVAS_VAZIAS && id <= 10000) {
            try {
                Boleto boleto = buscarBoleto(id);
                if (boleto == null) {
                    tentativasVazias++;
                } else {
                    tentativasVazias = 0;
                    // Como agora o boleto armazena CPF, não podemos comparar com int idCliente
                    // Este método não funciona mais adequadamente
                    System.out.println("DEBUG: Boleto encontrado - ID: " + boleto.getId() + ", CPF Cliente: " + boleto.getCPFCliente());
                }
                id++;
            } catch (Exception e) {
                tentativasVazias++;
                id++;
            }
        }
        
        System.out.println("AVISO: Método deprecated retorna lista vazia. Use listarBoletosPorCPF(String)");
        return boletosDoCLiente; // Retorna lista vazia
    }

    public List<Boleto> listarTodosBoletos() throws Exception {
        List<Boleto> todosBoletos = new ArrayList<>();
        
        int id = 1;
        int tentativasVazias = 0;
        final int MAX_TENTATIVAS_VAZIAS = 50;
        
        while (tentativasVazias < MAX_TENTATIVAS_VAZIAS && id <= 10000) {
            try {
                Boleto boleto = buscarBoleto(id); // Agora usa o índice hash
                if (boleto == null) {
                    tentativasVazias++;
                } else {
                    tentativasVazias = 0;
                    todosBoletos.add(boleto);
                }
                id++;
            } catch (Exception e) {
                tentativasVazias++;
                id++;
            }
        }
        
        return todosBoletos;
    }

    public void close() throws Exception {
        arqBoletos.close();
    }
}