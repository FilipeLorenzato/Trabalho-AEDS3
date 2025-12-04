package TP1.AEDS.III.models;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RegistroHashClienteCPF implements RegistroHashExtensivel<RegistroHashClienteCPF> {
    private String cpf;
    private long endereco;
    
    public RegistroHashClienteCPF() {
        this.cpf = "";
        this.endereco = -1;
    }
    
    public RegistroHashClienteCPF(String cpf, long endereco) {
        this.cpf = cpf != null ? cpf : "";
        this.endereco = endereco;
    }
    
    public String getCpf() {
        return cpf;
    }
    
    public void setCpf(String cpf) {
        this.cpf = cpf != null ? cpf : "";
    }
    
    public long getEndereco() {
        return endereco;
    }
    
    public void setEndereco(long endereco) {
        this.endereco = endereco;
    }
    
    @Override
    public int hashCode() {
        // Usa o CPF como base para o hash
        return cpf.hashCode();
    }
    
    @Override
    public short size() {
        // CPF: 11 chars max = 13 bytes UTF (2 + 11)
        // Endereço: 8 bytes (long)
        // Total: 21 bytes fixo
        return 21;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        RegistroHashClienteCPF that = (RegistroHashClienteCPF) obj;
        return cpf.equals(that.cpf);
    }
    
    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        
        // CPF: String de tamanho fixo (11 chars = 11 bytes + 2 para length = ~15 bytes)
        dos.writeUTF(cpf);
        
        // Endereço: 8 bytes (long)
        dos.writeLong(endereco);
        
        return baos.toByteArray();
    }
    
    @Override
    public void fromByteArray(byte[] dados) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(dados);
        DataInputStream dis = new DataInputStream(bais);
        
        this.cpf = dis.readUTF();
        this.endereco = dis.readLong();
    }
    
    @Override
    public String toString() {
        return "CPF: " + cpf + " | Endereço: " + endereco;
    }
}