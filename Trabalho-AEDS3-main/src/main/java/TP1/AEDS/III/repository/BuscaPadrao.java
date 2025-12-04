package TP1.AEDS.III.repository;

import java.util.Arrays;

public class BuscaPadrao {

    // --- ALGORITMO 1: KMP ---
    public static boolean kmp(String texto, String padrao) {
        if (padrao == null || padrao.length() == 0) return false;
        if (texto == null || texto.length() == 0) return false;
        
        texto = texto.toLowerCase();
        padrao = padrao.toLowerCase();

        int[] lps = calcularLPS(padrao);
        int i = 0; // índice do texto
        int j = 0; // índice do padrão

        while (i < texto.length()) {
            if (padrao.charAt(j) == texto.charAt(i)) {
                j++;
                i++;
            }
            if (j == padrao.length()) {
                return true; // Encontrou!
            } else if (i < texto.length() && padrao.charAt(j) != texto.charAt(i)) {
                if (j != 0)
                    j = lps[j - 1];
                else
                    i = i + 1;
            }
        }
        return false;
    }

    private static int[] calcularLPS(String padrao) {
        int len = 0;
        int i = 1;
        int[] lps = new int[padrao.length()];
        lps[0] = 0;

        while (i < padrao.length()) {
            if (padrao.charAt(i) == padrao.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len != 0) {
                    len = lps[len - 1];
                } else {
                    lps[i] = len;
                    i++;
                }
            }
        }
        return lps;
    }

    // --- ALGORITMO 2: BOYER-MOORE (Heurística Bad Character) Aula do dia 04/12/2025
    // NOVO PARA A ETAPA 5
    public static boolean boyerMoore(String texto, String padrao) {
        if (padrao == null || padrao.length() == 0) return false;
        if (texto == null || texto.length() == 0) return false;

        texto = texto.toLowerCase();
        padrao = padrao.toLowerCase();

        int m = padrao.length();
        int n = texto.length();

        int[] badChar = new int[256]; // Tabela para caracteres ASCII
        preencherBadChar(padrao, m, badChar);

        int s = 0; // s é o deslocamento do padrão em relação ao texto
        while (s <= (n - m)) {
            int j = m - 1;

            // Reduz j enquanto caracteres batem da direita para a esquerda
            while (j >= 0 && padrao.charAt(j) == texto.charAt(s + j)) {
                j--;
            }

            if (j < 0) {
                return true; // Encontrou!
                // s += (s + m < n) ? m - badChar[texto.charAt(s + m)] : 1;
            } else {
                // Desloca o padrão baseado no caractere ruim do texto
                s += Math.max(1, j - badChar[texto.charAt(s + j)]);
            }
        }
        return false;
    }

    // Preenche a tabela de ocorrências do caractere ruim
    private static void preencherBadChar(String str, int size, int[] badChar) {
        Arrays.fill(badChar, -1); // Inicializa tudo com -1
        for (int i = 0; i < size; i++) {
            // Guarda a última posição de cada caractere
            if (str.charAt(i) < 256) {
                badChar[str.charAt(i)] = i;
            }
        }
    }
}