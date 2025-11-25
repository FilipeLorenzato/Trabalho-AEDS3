package TP1.AEDS.III.models;

import TP1.AEDS.III.repository.Registro;
import java.io.*;

public class Tag implements Registro {
    private int id;
    private String nome;
    
    public Tag() {}
    
    public Tag(String nome) {
        this.nome = nome;
    }
    
    // Getters e Setters
    @Override
    public void setId(int id) { this.id = id; }
    
    @Override
    public int getId() { return this.id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    
    // Serialização
    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        
        dos.writeInt(this.id);
        dos.writeUTF(this.nome != null ? this.nome : "");
        
        return baos.toByteArray();
    }
    
    @Override
    public void fromByteArray(byte[] b) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        DataInputStream dis = new DataInputStream(bais);
        
        this.id = dis.readInt();
        this.nome = dis.readUTF();
    }
    
    @Override
    public String toString() {
        return "Tag: " + nome + " ";
    }
}