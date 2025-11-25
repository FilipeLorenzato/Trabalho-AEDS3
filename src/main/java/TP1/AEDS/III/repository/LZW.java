package TP1.AEDS.III.repository;

import java.io.*;
import java.util.*;

public class LZW {

    // Comprime um arquivo de origem para um arquivo de destino
    public static void comprimirArquivo(String arquivoOrigem, String arquivoDestino) throws IOException {
        byte[] entrada = new FileInputStream(arquivoOrigem).readAllBytes();
        List<Integer> comprimido = comprimir(entrada);
        
        // Salva os códigos comprimidos (inteiros) no arquivo
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(arquivoDestino))) {
            for (int codigo : comprimido) {
                dos.writeInt(codigo);
            }
        }
    }

    // Descomprime um arquivo LZW de volta para o original
    public static void descomprimirArquivo(String arquivoOrigem, String arquivoDestino) throws IOException {
        List<Integer> entrada = new ArrayList<>();
        
        try (DataInputStream dis = new DataInputStream(new FileInputStream(arquivoOrigem))) {
            while (dis.available() > 0) {
                entrada.add(dis.readInt());
            }
        }
        
        byte[] descomprimido = descomprimir(entrada);
        try (FileOutputStream fos = new FileOutputStream(arquivoDestino)) {
            fos.write(descomprimido);
        }
    }

    // LÓGICA DO ALGORITMO LZW

    private static List<Integer> comprimir(byte[] dados) {
        int dictSize = 256;
        Map<String, Integer> dicionario = new HashMap<>();
        
        // Inicializa dicionário com todos os bytes possíveis (0-255)
        for (int i = 0; i < 256; i++) {
            dicionario.put("" + (char) i, i);
        }

        String w = "";
        List<Integer> resultado = new ArrayList<>();
        
        // Converte bytes para String
        String dadosStr = new String(dados, java.nio.charset.StandardCharsets.ISO_8859_1);

        for (char c : dadosStr.toCharArray()) {
            String wc = w + c;
            if (dicionario.containsKey(wc)) {
                w = wc;
            } else {
                resultado.add(dicionario.get(w));
                // Adiciona nova sequência ao dicionário
                dicionario.put(wc, dictSize++);
                w = "" + c;
            }
        }

        if (!w.equals("")) {
            resultado.add(dicionario.get(w));
        }
        return resultado;
    }

    private static byte[] descomprimir(List<Integer> comprimido) {
        int dictSize = 256;
        Map<Integer, String> dicionario = new HashMap<>();
        
        for (int i = 0; i < 256; i++) {
            dicionario.put(i, "" + (char) i);
        }

        String w = "" + (char) (int) comprimido.remove(0);
        StringBuffer resultado = new StringBuffer(w);
        
        for (int k : comprimido) {
            String entrada;
            if (dicionario.containsKey(k)) {
                entrada = dicionario.get(k);
            } else if (k == dictSize) {
                entrada = w + w.charAt(0);
            } else {
                throw new IllegalArgumentException("Compressão corrompida: k=" + k);
            }

            resultado.append(entrada);

            // Adiciona ao dicionário
            dicionario.put(dictSize++, w + entrada.charAt(0));
            w = entrada;
        }
        
        return resultado.toString().getBytes(java.nio.charset.StandardCharsets.ISO_8859_1);
    }
}