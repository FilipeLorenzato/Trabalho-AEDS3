package TP1.AEDS.III.repository;
import java.io.File;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;

public class ArquivoBD<TipoGenerico extends Registro> {
    
    private static final int TAM_CABECALHO = 12; // 4 bytes (int) + 8 bytes (long)
    private RandomAccessFile arquivo;
    private String nomeArquivo;
    private Constructor<TipoGenerico> construtor;

    public ArquivoBD(String nomeArquivo, Constructor<TipoGenerico> construtor) throws Exception {
        File diretorio = new File("./dados");
        if (!diretorio.exists()) diretorio.mkdir();

        diretorio = new File("./dados/" + nomeArquivo);
        if (!diretorio.exists()) diretorio.mkdir();

        this.nomeArquivo = "./dados/" + nomeArquivo + "/" + nomeArquivo + ".db";
        this.construtor = construtor;
        this.arquivo = new RandomAccessFile(this.nomeArquivo, "rw");

        /* O bloco abaixo garante que ao criar um novo arq de dados
         * ele já começa com as informações básicas necessárias para
         * o controle os registros.
        */
        if (arquivo.length() < TAM_CABECALHO) {
            arquivo.writeInt(0);    // Último ID usado (4 bytes)
            arquivo.writeLong(-1);  // Lista de registros excluídos (8 bytes)
        }
    }

    public long create(TipoGenerico obj) throws Exception {
        arquivo.seek(0);
        int novoID = arquivo.readInt() + 1;
        arquivo.seek(0);
        arquivo.writeInt(novoID);
        obj.setId(novoID);
        byte[] dados = obj.toByteArray();

        long endereco = getDeleted(dados.length);
        if (endereco == -1) {
            arquivo.seek(arquivo.length());
            endereco = arquivo.getFilePointer();
            arquivo.writeByte(' ');  // Lápide
            arquivo.writeShort(dados.length);
            arquivo.write(dados);
        } else {
            arquivo.seek(endereco);
            arquivo.writeByte(' ');  // Remove a lápide
            arquivo.skipBytes(2);
            arquivo.write(dados);
        }
        return endereco; // Retorna o endereço onde foi salvo
    }

    public TipoGenerico read(int id) throws Exception {
        arquivo.seek(TAM_CABECALHO);
        while (arquivo.getFilePointer() < arquivo.length()) {
            byte lapide = arquivo.readByte();
            short tamanho = arquivo.readShort();
            byte[] dados = new byte[tamanho];
            arquivo.read(dados);

            if (lapide == ' ') {
                TipoGenerico obj = construtor.newInstance();
                obj.fromByteArray(dados);
                if (obj.getId() == id) {
                    return obj;
                }
            }
        }
        return null;
    }

    // Método para ler registro em endereço específico (usado pelo índice hash)
    public TipoGenerico readAtAddress(long endereco) throws Exception {
        arquivo.seek(endereco);
        byte lapide = arquivo.readByte();
        
        if (lapide != ' ') {
            return null; // Registro foi excluído
        }
        
        short tamanho = arquivo.readShort();
        byte[] dados = new byte[tamanho];
        arquivo.read(dados);
        
        TipoGenerico obj = construtor.newInstance();
        obj.fromByteArray(dados);
        return obj;
    }

    // Método para encontrar o endereço de um registro por ID
    public long findAddress(int id) throws Exception {
        arquivo.seek(TAM_CABECALHO);
        while (arquivo.getFilePointer() < arquivo.length()) {
            long posicao = arquivo.getFilePointer();
            byte lapide = arquivo.readByte();
            short tamanho = arquivo.readShort();
            byte[] dados = new byte[tamanho];
            arquivo.read(dados);

            if (lapide == ' ') {
                TipoGenerico obj = construtor.newInstance();
                obj.fromByteArray(dados);
                if (obj.getId() == id) {
                    return posicao; // Retorna o endereço onde está o registro
                }
            }
        }
        return -1; // Não encontrado
    }

    public boolean delete(int id) throws Exception {
        arquivo.seek(TAM_CABECALHO);
        while (arquivo.getFilePointer() < arquivo.length()) {
            long posicao = arquivo.getFilePointer();
            byte lapide = arquivo.readByte();
            short tamanho = arquivo.readShort();
            byte[] dados = new byte[tamanho];
            arquivo.read(dados);

            if (lapide == ' ') {
                TipoGenerico obj = construtor.newInstance();
                obj.fromByteArray(dados);
                if (obj.getId() == id) {
                    arquivo.seek(posicao);
                    arquivo.writeByte('*');
                    addDeleted(tamanho, posicao);
                    
                    // Recalcula o último ID após exclusão
                    recalcularUltimoID();
                    
                    return true;
                }
            }
        }
        return false;
    }
    
    // Método para recalcular o último ID baseado nos registros ativos
    private void recalcularUltimoID() throws Exception {
        int maiorID = 0;
        
        arquivo.seek(TAM_CABECALHO);
        while (arquivo.getFilePointer() < arquivo.length()) {
            byte lapide = arquivo.readByte();
            short tamanho = arquivo.readShort();
            byte[] dados = new byte[tamanho];
            arquivo.read(dados);

            if (lapide == ' ') {
                TipoGenerico obj = construtor.newInstance();
                obj.fromByteArray(dados);
                if (obj.getId() > maiorID) {
                    maiorID = obj.getId();
                }
            }
        }
        
        // Atualiza o cabeçalho com o maior ID encontrado
        arquivo.seek(0);
        arquivo.writeInt(maiorID);
    }

    public boolean update(TipoGenerico novoObj) throws Exception {
        arquivo.seek(TAM_CABECALHO);
        while (arquivo.getFilePointer() < arquivo.length()) {
            long posicao = arquivo.getFilePointer();
            byte lapide = arquivo.readByte();
            short tamanho = arquivo.readShort();
            byte[] dados = new byte[tamanho];
            arquivo.read(dados);

            if (lapide == ' ') {
                TipoGenerico obj = construtor.newInstance();
                obj.fromByteArray(dados);
                if (obj.getId() == novoObj.getId()) {
                    byte[] novosDados = novoObj.toByteArray();
                    short novoTam = (short) novosDados.length;

                    if (novoTam <= tamanho) {
                        arquivo.seek(posicao + 3);
                        arquivo.write(novosDados);
                    } else {
                        arquivo.seek(posicao);
                        arquivo.writeByte('*');
                        addDeleted(tamanho, posicao);

                        long novoEndereco = getDeleted(novosDados.length);
                        if (novoEndereco == -1) {
                            arquivo.seek(arquivo.length());
                            novoEndereco = arquivo.getFilePointer();
                            arquivo.writeByte(' ');
                            arquivo.writeShort(novoTam);
                            arquivo.write(novosDados);
                        } else {
                            arquivo.seek(novoEndereco);
                            arquivo.writeByte(' ');
                            arquivo.skipBytes(2);
                            arquivo.write(novosDados);
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private void addDeleted(int tamanhoEspaco, long enderecoEspaco) throws Exception {
        long posicao = 4; // Posição da lista de excluídos (após o int do ID)
        arquivo.seek(posicao);
        long endereco = arquivo.readLong();
        long proximo;

        if (endereco == -1) {
            arquivo.seek(4);
            arquivo.writeLong(enderecoEspaco);
            arquivo.seek(enderecoEspaco + 3);
            arquivo.writeLong(-1);
        } else {
            do {
                arquivo.seek(endereco + 1);
                int tamanho = arquivo.readShort();
                proximo = arquivo.readLong();

                if (tamanho > tamanhoEspaco) {
                    if (posicao == 4)
                        arquivo.seek(posicao);
                    else
                        arquivo.seek(posicao + 3);
                    arquivo.writeLong(enderecoEspaco);
                    arquivo.seek(enderecoEspaco + 3);
                    arquivo.writeLong(endereco);
                    break;
                }

                if (proximo == -1) {
                    arquivo.seek(endereco + 3);
                    arquivo.writeLong(enderecoEspaco);
                    arquivo.seek(enderecoEspaco + 3);
                    arquivo.writeLong(-1);
                    break;
                }

                posicao = endereco;
                endereco = proximo;
            } while (endereco != -1);
        }
    }

    private long getDeleted(int tamanhoNecessario) throws Exception {
        long posicao = 4; // Posição da lista de excluídos (após o int do ID)
        arquivo.seek(posicao);
        long endereco = arquivo.readLong();
        long proximo;
        int tamanho;

        while (endereco != -1) {
            arquivo.seek(endereco + 1);
            tamanho = arquivo.readShort();
            proximo = arquivo.readLong();

            if (tamanho > tamanhoNecessario) {
                if (posicao == 4)
                    arquivo.seek(posicao);
                else
                    arquivo.seek(posicao + 3);
                arquivo.writeLong(proximo);
                return endereco;
            }
            posicao = endereco;
            endereco = proximo;
        }
        return -1;
    }

    public void close() throws Exception {
        arquivo.close();
    }
}
