package TP1.AEDS.III.models;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import TP1.AEDS.III.repository.Registro;

public class Usuario implements Registro {
    private int id;
    private String login;
    private String senhaCifrada; // Agora armazena o texto criptografado via RSA (Base64)
    
    // Construtor vazio (necessário para o DAO)
    public Usuario() {}

    // Construtor completo
    public Usuario(int id, String login, String senhaCifrada) {
        this.id = id;
        this.login = login;
        this.senhaCifrada = senhaCifrada;
    }

    // --- NOTA: A lógica de criptografia (XOR) foi removida daqui ---
    // Agora a criptografia RSA é feita pela classe utilitária Criptografia.java
    // e chamada no Controller antes de salvar o usuário.

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getSenhaCifrada() { return senhaCifrada; }
    public void setSenhaCifrada(String senhaCifrada) { this.senhaCifrada = senhaCifrada; }

    // MÉTODOS DE SERIALIZAÇÃO (Para salvar no arquivo .db)

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        
        dos.writeInt(id);
        dos.writeUTF(login);
        dos.writeUTF(senhaCifrada); // Grava a string gigante do RSA
        
        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] b) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        DataInputStream dis = new DataInputStream(bais);
        
        this.id = dis.readInt();
        this.login = dis.readUTF();
        this.senhaCifrada = dis.readUTF();
    }
}