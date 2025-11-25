package TP1.AEDS.III.models;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import TP1.AEDS.III.repository.ArquivoBD;

public class BoletoTagDAO extends ArquivoBD<BoletoTag> {

    public BoletoTagDAO() throws Exception {
        super("boleto_tag", BoletoTag.class.getConstructor());
        new File("dados/boleto_tag").mkdirs();
    }

    // 1. ADICIONAR RELAÇÃO (Cria o par Boleto-Tag)
    public boolean adicionarTagAoBoleto(int idTag, int idBoleto) throws Exception {
        List<BoletoTag> existentes = listarTudo();
        for (BoletoTag bt : existentes) {
            if (bt.getIdTag() == idTag && bt.getIdBoleto() == idBoleto) {
                return false; 
            }
        }
        
        BoletoTag novo = new BoletoTag();
        novo.setIdTag(idTag);
        novo.setIdBoleto(idBoleto);
        super.create(novo);
        return true;
    }

    // 2. REMOVER RELAÇÃO (Apaga o par)
    public boolean removerTagDoBoleto(int idTag, int idBoleto) throws Exception {
        List<BoletoTag> existentes = listarTudo();
        for (BoletoTag bt : existentes) {
            if (bt.getIdTag() == idTag && bt.getIdBoleto() == idBoleto) {
                return super.delete(bt.getId()); 
            }
        }
        return false; 
    }

    // 3. LISTAR TAGS DE UM BOLETO
    public List<Tag> listarTagsDoBoleto(int idBoleto) throws Exception {
        List<Tag> tagsDoBoleto = new ArrayList<>();
        TagDAO tagDAO = new TagDAO(); 

        List<BoletoTag> relacoes = listarTudo();

        for (BoletoTag bt : relacoes) {
            if (bt.getIdBoleto() == idBoleto) {
                try {
                    // --- ALTERAÇÃO AQUI: de .read() para .buscarTag() ---
                    Tag t = tagDAO.buscarTag(bt.getIdTag()); 
                    if (t != null) {
                        tagsDoBoleto.add(t);
                    }
                } catch (Exception e) {
                    // Ignora erro de leitura individual
                }
            }
        }
        return tagsDoBoleto;
    }

    // --- MÉTODOS AUXILIARES ---
    private List<BoletoTag> listarTudo() {
        List<BoletoTag> lista = new ArrayList<>();
        try {
            int i = 1;
            int errosSeguidos = 0;
            while (errosSeguidos < 10) { 
                try {
                    BoletoTag bt = super.read(i);
                    if (bt != null) {
                        lista.add(bt);
                        errosSeguidos = 0; 
                    } else {
                        errosSeguidos++;
                    }
                } catch (Exception e) {
                    errosSeguidos++;
                }
                i++;
            }
        } catch (Exception e) {}
        return lista;
    }
}