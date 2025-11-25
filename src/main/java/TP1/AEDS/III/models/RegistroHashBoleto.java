package TP1.AEDS.III.models;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RegistroHashBoleto implements RegistroHashExtensivel<RegistroHashBoleto> {
    private int id;           // Chave primária (ID do boleto)
    private long endereco;    // Endereço no arquivo de dados
    private short TAMANHO = 12; // int (4) + long (8) = 12 bytes

    public RegistroHashBoleto() {
        this.id = -1;
        this.endereco = -1;
    }

    public RegistroHashBoleto(int id, long endereco) {
        this.id = id;
        this.endereco = endereco;
    }

    @Override
    public int hashCode() {
        return this.id;
    }

    @Override
    public short size() {
        return TAMANHO;
    }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(this.id);
        dos.writeLong(this.endereco);
        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        this.id = dis.readInt();
        this.endereco = dis.readLong();
    }

    @Override
    public String toString() {
        return "ID: " + this.id + " | Endereço: " + this.endereco;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public long getEndereco() { return endereco; }
    public void setEndereco(long endereco) { this.endereco = endereco; }
}