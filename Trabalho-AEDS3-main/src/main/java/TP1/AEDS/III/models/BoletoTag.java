package TP1.AEDS.III.models;

import TP1.AEDS.III.repository.Registro;
import java.io.*;

public class BoletoTag implements Registro {
    private int id;
    private int idBoleto; // FK de boleto
    private int idTag;    // FK de TAG

    public BoletoTag() {}

    // Construtor para criar novo (sem ID, o DAO define)
    public BoletoTag(int idBoleto, int idTag) {
        this.idBoleto = idBoleto;
        this.idTag = idTag;
    }

    // Construtor completo (com ID)
    public BoletoTag(int id, int idBoleto, int idTag) {
        this.id = id;
        this.idBoleto = idBoleto;
        this.idTag = idTag;
    }

    @Override
    public void setId(int id) { this.id = id; }
    
    @Override
    public int getId() { return this.id; }
    
    public int getIdBoleto() { return idBoleto; }
    public void setIdBoleto(int idBoleto) { this.idBoleto = idBoleto; }
    
    public int getIdTag() { return idTag; }
    public void setIdTag(int idTag) { this.idTag = idTag; }
    
    // Serialização
    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream arq = new ByteArrayOutputStream();
        DataOutputStream dados = new DataOutputStream(arq);
        
        dados.writeInt(this.id);
        dados.writeInt(this.idBoleto);
        dados.writeInt(this.idTag);
        
        return arq.toByteArray();
    }
    
    // Desserialização
    @Override
    public void fromByteArray(byte[] bytes) throws IOException {
        ByteArrayInputStream arq = new ByteArrayInputStream(bytes);
        DataInputStream dados = new DataInputStream(arq);
        
        this.id = dados.readInt();
        this.idBoleto = dados.readInt();
        this.idTag = dados.readInt();
    }
}