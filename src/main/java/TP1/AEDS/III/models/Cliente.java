package TP1.AEDS.III.models;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import TP1.AEDS.III.repository.Registro;

public class Cliente implements Registro {
    private String CPF;
    private int id;
    private String nome;
    private String telefone;
    private String endereco;
    private List<String> emails;
    private float salario;
    private LocalDate nascimento;

    public Cliente() {

    }

    public Cliente(String nome, String CPF, String telefone, String endereco, List<String> emails) {
        this.nome = nome;
        this.CPF = CPF;
        this.telefone = telefone;
        this.endereco = endereco;
        this.emails = emails;
    }

    // Construtor para o MenuClientes
    public Cliente(String nome, String CPF, float salario, LocalDate nascimento) {
        this.nome = nome;
        this.CPF = CPF;
        this.salario = salario;
        this.nascimento = nascimento;
    }

    // setters

    @Override
    public void setId(int i) {
        this.id = i;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCPF(String CPF) {
        this.CPF = CPF;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }

    public void setCpf(String CPF) {
        this.CPF = CPF;
    }

    public void setSalario(float salario) {
        this.salario = salario;
    }

    public void setNascimento(LocalDate nascimento) {
        this.nascimento = nascimento;
    }

    // getters

    @Override
    public int getId() {
        return this.id;
    }

    public String getNome() {
        return nome;
    }

    public String getCPF() {
        return CPF;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getEndereco() {
        return endereco;
    }

    public List<String> getEmails() {
        return emails;
    }

    public float getSalario() {
        return salario;
    }

    public LocalDate getNascimento() {
        return nascimento;
    }

    // Métodos da interface Registro
    // Implementação do método toByteArray()
    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream arquivo = new ByteArrayOutputStream();
        DataOutputStream dados = new DataOutputStream(arquivo);
        
        dados.writeInt(this.id);
        dados.writeUTF(this.nome != null ? this.nome : "");
        dados.writeUTF(this.CPF != null ? this.CPF : "");
        dados.writeUTF(this.telefone != null ? this.telefone : "");
        dados.writeUTF(this.endereco != null ? this.endereco : "");
        
        // Serializar lista de emails
        if (this.emails != null) {
            dados.writeInt(this.emails.size());
            for (String email : this.emails) {
                dados.writeUTF(email != null ? email : "");
            }
        } else {
            dados.writeInt(0);
        }
        
        dados.writeFloat(this.salario);
        
        // Serializar data de nascimento
        if (this.nascimento != null) {
            dados.writeLong(this.nascimento.toEpochDay());
        } else {
            dados.writeLong(0);
        }
        
        return arquivo.toByteArray();
    }

    // Implementação do método fromByteArray()
    @Override
    public void fromByteArray(byte[] arrayBites) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(arrayBites);
        DataInputStream dis = new DataInputStream(bais);
        
        this.id = dis.readInt();
        this.nome = dis.readUTF();
        this.CPF = dis.readUTF();
        this.telefone = dis.readUTF();
        this.endereco = dis.readUTF();
        
        // Deserializar lista de emails
        int emailCount = dis.readInt();
        if (emailCount > 0) {
            this.emails = new java.util.ArrayList<>();
            for (int i = 0; i < emailCount; i++) {
                this.emails.add(dis.readUTF());
            }
        } else {
            this.emails = null;
        }
        
        this.salario = dis.readFloat();
        
        // Deserializar data de nascimento
        long nascimentoEpoch = dis.readLong();
        if (nascimentoEpoch > 0) {
            this.nascimento = LocalDate.ofEpochDay(nascimentoEpoch);
        } else {
            this.nascimento = null;
        }
    }

    @Override
    public String toString() {
        return "\nID........: " + this.id +
                "\nNome......: " + this.nome +
                "\nCPF.......: " + this.CPF +
                "\nSalário...: " + this.salario +
                "\nNascimento: " + this.nascimento;
    }
}
