package TP1.AEDS.III.models;

import TP1.AEDS.III.repository.ArquivoBD;
import TP1.AEDS.III.repository.HashExtensivel;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TagDAO {
    private ArquivoBD<Tag> arquivoTag;
    private HashExtensivel<RegistroHashTag> indiceTags;
    
    public TagDAO() throws Exception {
        // Criar diretório para índices se não existir
        File dirIndices = new File("./dados/indices");
        if (!dirIndices.exists()) dirIndices.mkdirs();
        
        this.arquivoTag = new ArquivoBD<>("tags", Tag.class.getConstructor());
        
        // Inicializar índice hash para tags
        indiceTags = new HashExtensivel<>(
            RegistroHashTag.class.getConstructor(),
            4, // 4 registros por cesto
            "./dados/indices/tags_diretorio.hash_d",
            "./dados/indices/tags_cestos.hash_c"
        );
    }
    
    public boolean criarTag(Tag tag) throws Exception {
        // Salva a tag no arquivo de dados e obtém o endereço
        long endereco = arquivoTag.create(tag);
        
        if (endereco > 0) {
            // Adiciona o registro no índice hash
            RegistroHashTag regHash = new RegistroHashTag(tag.getId(), endereco);
            indiceTags.create(regHash);
            return true;
        }
        return false;
    }
    
    public Tag buscarTag(int id) throws Exception {
        // Busca primeiro no índice hash
        RegistroHashTag regHash = indiceTags.read(id);
        if (regHash == null) {
            return null; // Tag não encontrada no índice
        }
        
        // Usa o endereço do índice para buscar diretamente no arquivo
        return arquivoTag.readAtAddress(regHash.getEndereco());
    }
    
    public boolean alterarTag(Tag tag) throws Exception {
        // Primeiro verifica se a tag existe no índice
        RegistroHashTag regHash = indiceTags.read(tag.getId());
        if (regHash == null) {
            return false; // Tag não encontrada
        }
        
        // Busca o endereço antigo antes do update
        long enderecoAntigo = regHash.getEndereco();
        
        // Atualiza o registro no arquivo de dados
        boolean atualizado = arquivoTag.update(tag);
        
        if (atualizado) {
            // Busca o novo endereço após o update
            // (pode ter mudado se o novo tamanho for maior)
            Tag tagAtualizada = arquivoTag.read(tag.getId());
            if (tagAtualizada != null) {
                long novoEndereco = encontrarEndereco(tag.getId());
                
                // Se o endereço mudou, atualiza o índice hash
                if (novoEndereco != enderecoAntigo && novoEndereco != -1) {
                    indiceTags.delete(tag.getId());
                    RegistroHashTag novoRegHash = new RegistroHashTag(tag.getId(), novoEndereco);
                    indiceTags.create(novoRegHash);
                }
            }
        }
        
        return atualizado;
    }
    
    // Método auxiliar para encontrar o endereço atual de uma tag
    private long encontrarEndereco(int id) throws Exception {
        return arquivoTag.findAddress(id);
    }
    
    public boolean excluirTag(int id) throws Exception {
        // Remove do arquivo de dados
        boolean removido = arquivoTag.delete(id);
        
        if (removido) {
            // Remove do índice hash
            indiceTags.delete(id);
        }
        
        return removido;
    }
    
    public List<Tag> listarTodasTags() throws Exception {
        List<Tag> tags = new ArrayList<>();
        
        int tentativasVazias = 0;
        final int MAX_TENTATIVAS = 50;
        
        for (int i = 1; i <= 1000 && tentativasVazias < MAX_TENTATIVAS; i++) {
            try {
                Tag tag = buscarTag(i); // Usa o índice hash
                if (tag == null) {
                    tentativasVazias++;
                } else {
                    tentativasVazias = 0;
                    tags.add(tag);
                }
            } catch (Exception e) {
                tentativasVazias++;
            }
        }
        
        return tags;
    }
}