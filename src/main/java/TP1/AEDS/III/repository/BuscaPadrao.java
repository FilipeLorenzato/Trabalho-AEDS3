package TP1.AEDS.III.repository;

public class BuscaPadrao {

    // Algoritmo KMP pra buscar um termo dentro de um texto
    public static boolean kmp(String texto, String padrao) {
        if (padrao == null || padrao.length() == 0) return false;
        if (texto == null || texto.length() == 0) return false;
        
        // Ignorar maiúsculas/minúsculas para facilitar a busca
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
                return true; // Padrão encontrado
            } else if (i < texto.length() && padrao.charAt(j) != texto.charAt(i)) {
                if (j != 0)
                    j = lps[j - 1];
                else
                    i = i + 1;
            }
        }
        return false;
    }

    // Pré-processamento do KMP
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
}