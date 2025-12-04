package TP1.AEDS.III.repository;

import java.io.*;
import java.util.*;

public class Huffman {

    // Nó da Árvore de Huffman
    private static class No implements Comparable<No> {
        Byte dado;
        int frequencia;
        No esquerda, direita;

        public No(Byte dado, int frequencia) {
            this.dado = dado;
            this.frequencia = frequencia;
        }

        public No(No esquerda, No direita) {
            this.esquerda = esquerda;
            this.direita = direita;
            this.frequencia = esquerda.frequencia + direita.frequencia;
        }

        @Override
        public int compareTo(No o) {
            return this.frequencia - o.frequencia;
        }
    }

    // COMPRESSÃO 
    public static void comprimirArquivo(String origem, String destino) throws IOException {
        byte[] entrada = new FileInputStream(origem).readAllBytes();
        if (entrada.length == 0) return;

        // 1. Contar frequências
        Map<Byte, Integer> frequencias = new HashMap<>();
        for (byte b : entrada) {
            frequencias.put(b, frequencias.getOrDefault(b, 0) + 1);
        }

        // 2. Criar Fila de Prioridade e Árvore
        PriorityQueue<No> fila = new PriorityQueue<>();
        for (Map.Entry<Byte, Integer> entry : frequencias.entrySet()) {
            fila.add(new No(entry.getKey(), entry.getValue()));
        }

        while (fila.size() > 1) {
            No esq = fila.poll();
            No dir = fila.poll();
            fila.add(new No(esq, dir));
        }
        No raiz = fila.poll();

        // 3. Gerar Tabela de Códigos
        Map<Byte, String> codigos = new HashMap<>();
        gerarCodigos(raiz, "", codigos);

        // 4. Codificar os dados
        StringBuilder bitsString = new StringBuilder();
        for (byte b : entrada) {
            bitsString.append(codigos.get(b));
        }

        // 5. Gravar Arquivo 
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(destino))) {
            // A. Gravar a Tabela de Frequências (pra reconstruir a árvore na volta)
            dos.writeInt(frequencias.size());
            for (Map.Entry<Byte, Integer> entry : frequencias.entrySet()) {
                dos.writeByte(entry.getKey());
                dos.writeInt(entry.getValue());
            }

            // B. Gravar o tamanho original 
            dos.writeInt(bitsString.length());

            // C. Gravar os bits compactados em bytes
            BitSet bitSet = new BitSet(bitsString.length());
            for (int i = 0; i < bitsString.length(); i++) {
                if (bitsString.charAt(i) == '1') {
                    bitSet.set(i);
                }
            }
            byte[] bytesComprimidos = bitSet.toByteArray();
            dos.write(bytesComprimidos);
        }
    }

    private static void gerarCodigos(No no, String codigo, Map<Byte, String> codigos) {
        if (no != null) {
            if (no.esquerda == null && no.direita == null) {
                codigos.put(no.dado, codigo);
            }
            gerarCodigos(no.esquerda, codigo + "0", codigos);
            gerarCodigos(no.direita, codigo + "1", codigos);
        }
    }

    // DESCOMPRESSÃO 
    public static void descomprimirArquivo(String origem, String destino) throws IOException {
        try (DataInputStream dis = new DataInputStream(new FileInputStream(origem))) {
            // 1. Ler Tabela de Frequências
            int tamTabela = dis.readInt();
            Map<Byte, Integer> frequencias = new HashMap<>();
            for (int i = 0; i < tamTabela; i++) {
                byte b = dis.readByte();
                int f = dis.readInt();
                frequencias.put(b, f);
            }

            // 2. Reconstruir Árvore
            PriorityQueue<No> fila = new PriorityQueue<>();
            for (Map.Entry<Byte, Integer> entry : frequencias.entrySet()) {
                fila.add(new No(entry.getKey(), entry.getValue()));
            }
            while (fila.size() > 1) {
                No esq = fila.poll();
                No dir = fila.poll();
                fila.add(new No(esq, dir));
            }
            No raiz = fila.poll();

            // 3. Ler Dados
            int numBits = dis.readInt();
            byte[] bytesComprimidos = dis.readAllBytes();
            BitSet bitSet = BitSet.valueOf(bytesComprimidos);

            // 4. Decodificar navegando na árvore
            List<Byte> saida = new ArrayList<>();
            No atual = raiz;
            for (int i = 0; i < numBits; i++) {
                boolean bit = bitSet.get(i);
                if (!bit) atual = atual.esquerda; // 0
                else atual = atual.direita;       // 1

                if (atual.esquerda == null && atual.direita == null) {
                    saida.add(atual.dado);
                    atual = raiz;
                }
            }

            // 5. Salvar Arquivo Original
            byte[] dadosFinais = new byte[saida.size()];
            for (int i = 0; i < saida.size(); i++) {
                dadosFinais[i] = saida.get(i);
            }
            try (FileOutputStream fos = new FileOutputStream(destino)) {
                fos.write(dadosFinais);
            }
        }
    }
}