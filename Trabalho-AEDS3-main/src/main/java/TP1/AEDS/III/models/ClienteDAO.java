package TP1.AEDS.III.models;
import TP1.AEDS.III.repository.ArquivoBD;
import TP1.AEDS.III.repository.HashExtensivel;
import java.io.File;

public class ClienteDAO {
    private ArquivoBD<Cliente> arqClientes;
    private HashExtensivel<RegistroHashClienteCPF> indiceClientesCPF; // Mudança: usar CPF como chave

    public ClienteDAO() throws Exception {
        // Criar diretório para índices se não existir
        File dirIndices = new File("./dados/indices");
        if (!dirIndices.exists()) dirIndices.mkdirs();
        
        arqClientes = new ArquivoBD<>("clientes", Cliente.class.getConstructor());
        
        // Inicializar índice hash para clientes por CPF
        indiceClientesCPF = new HashExtensivel<>(
            RegistroHashClienteCPF.class.getConstructor(),
            4, // 4 registros por cesto
            "./dados/indices/clientes_cpf_diretorio.hash_d",
            "./dados/indices/clientes_cpf_cestos.hash_c"
        );
    }

    // Buscar cliente por CPF (novo método principal)
    public Cliente buscarClientePorCPF(String cpf) throws Exception {
        if (cpf == null || cpf.trim().isEmpty()) {
            return null;
        }
        
        // Busca primeiro no índice hash por CPF
        RegistroHashClienteCPF regHash = new RegistroHashClienteCPF(cpf, -1);
        RegistroHashClienteCPF encontrado = indiceClientesCPF.read(regHash.hashCode());
        
        if (encontrado == null) {
            return null; // Cliente não encontrado no índice
        }
        
        // Usa o endereço do índice para buscar diretamente no arquivo
        return lerClienteNoEndereco(encontrado.getEndereco());
    }
    
    // Manter busca por ID para compatibilidade (busca sequencial)
    public Cliente buscarCliente(int id) throws Exception {
        return arqClientes.read(id);
    }

    public boolean incluirCliente(Cliente cliente) throws Exception {
        if (cliente.getCPF() == null || cliente.getCPF().trim().isEmpty()) {
            throw new Exception("CPF é obrigatório para incluir cliente");
        }
        
        // Verificar se já existe cliente com este CPF
        Cliente existente = buscarClientePorCPF(cliente.getCPF());
        if (existente != null) {
            throw new Exception("Já existe um cliente com este CPF: " + cliente.getCPF());
        }
        
        // Salva o cliente no arquivo de dados e obtém o endereço
        long endereco = arqClientes.create(cliente);
        
        if (endereco > 0) {
            // Adiciona o registro no índice hash por CPF
            RegistroHashClienteCPF regHash = new RegistroHashClienteCPF(cliente.getCPF(), endereco);
            indiceClientesCPF.create(regHash);
            return true;
        }
        return false;
    }

    public boolean alterarCliente(Cliente cliente) throws Exception {
        if (cliente.getCPF() == null || cliente.getCPF().trim().isEmpty()) {
            throw new Exception("CPF é obrigatório para alterar cliente");
        }
        
        // Primeiro verifica se o cliente existe no índice por CPF
        RegistroHashClienteCPF regHash = new RegistroHashClienteCPF(cliente.getCPF(), -1);
        RegistroHashClienteCPF encontrado = indiceClientesCPF.read(regHash.hashCode());
        
        if (encontrado == null) {
            return false; // Cliente não encontrado
        }
        
        // Busca o endereço antigo antes do update
        long enderecoAntigo = encontrado.getEndereco();
        
        // Atualiza o registro no arquivo de dados
        boolean atualizado = arqClientes.update(cliente);
        
        if (atualizado) {
            // Busca o novo endereço após o update
            long novoEndereco = arqClientes.findAddress(cliente.getId());
            
            // Se o endereço mudou, atualiza o índice hash
            if (novoEndereco != enderecoAntigo && novoEndereco != -1) {
                indiceClientesCPF.delete(regHash.hashCode());
                RegistroHashClienteCPF novoRegHash = new RegistroHashClienteCPF(cliente.getCPF(), novoEndereco);
                indiceClientesCPF.create(novoRegHash);
            }
        }
        
        return atualizado;
    }

    public boolean excluirClientePorCPF(String cpf) throws Exception {
        if (cpf == null || cpf.trim().isEmpty()) {
            return false;
        }
        
        // Buscar o cliente primeiro para obter o ID
        Cliente cliente = buscarClientePorCPF(cpf);
        if (cliente == null) {
            return false;
        }
        
        // Remove do arquivo de dados
        boolean removido = arqClientes.delete(cliente.getId());
        
        if (removido) {
            // Remove do índice hash por CPF
            RegistroHashClienteCPF regHash = new RegistroHashClienteCPF(cpf, -1);
            indiceClientesCPF.delete(regHash.hashCode());
        }
        
        return removido;
    }
    
    // Manter método de exclusão por ID para compatibilidade
    public boolean excluirCliente(int id) throws Exception {
        // Buscar cliente para obter o CPF
        Cliente cliente = arqClientes.read(id);
        if (cliente == null) {
            return false;
        }
        
        // Remove do arquivo de dados
        boolean removido = arqClientes.delete(id);
        
        if (removido && cliente.getCPF() != null) {
            // Remove do índice hash por CPF
            RegistroHashClienteCPF regHash = new RegistroHashClienteCPF(cliente.getCPF(), -1);
            indiceClientesCPF.delete(regHash.hashCode());
        }
        
        return removido;
    }
    
    // Método para listar todos os clientes (busca sequencial)
    public java.util.List<Cliente> listarTodosClientes() throws Exception {
        java.util.List<Cliente> clientes = new java.util.ArrayList<>();
        
        // Busca sequencial no arquivo
        // Como não temos readAll(), vamos fazer uma busca por IDs
        // Isso não é eficiente, mas funciona para demonstração
        for (int id = 1; id <= 100; id++) { // Assume máximo 100 clientes
            try {
                Cliente cliente = arqClientes.read(id);
                if (cliente != null) {
                    clientes.add(cliente);
                }
            } catch (Exception e) {
                // Cliente não encontrado, continua
            }
        }
        
        return clientes;
    }
    
    // Método auxiliar para ler cliente em um endereço específico
    private Cliente lerClienteNoEndereco(long endereco) throws Exception {
        return arqClientes.readAtAddress(endereco);
    }
}
