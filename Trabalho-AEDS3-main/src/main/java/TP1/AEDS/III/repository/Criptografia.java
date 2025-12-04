package TP1.AEDS.III.repository;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;

public class Criptografia {

    private static final String CAMINHO_CHAVE_PUBLICA = "dados/chaves/publica.key";
    private static final String CAMINHO_CHAVE_PRIVADA = "dados/chaves/privada.key";

    // Gera as chaves se não existirem
    public static void gerarChaves() {
        try {
            File pasta = new File("dados/chaves");
            if (!pasta.exists()) pasta.mkdirs();

            File publicaFile = new File(CAMINHO_CHAVE_PUBLICA);
            File privadaFile = new File(CAMINHO_CHAVE_PRIVADA);

            if (!publicaFile.exists() || !privadaFile.exists()) {
                System.out.println("🔑 Gerando novo par de chaves RSA...");
                KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
                keyGen.initialize(2048);
                KeyPair par = keyGen.generateKeyPair();

                try (FileOutputStream fos = new FileOutputStream(publicaFile)) {
                    fos.write(par.getPublic().getEncoded());
                }
                try (FileOutputStream fos = new FileOutputStream(privadaFile)) {
                    fos.write(par.getPrivate().getEncoded());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Criptografa
    public static String criptografar(String texto) {
        try {
            byte[] keyBytes = Files.readAllBytes(new File(CAMINHO_CHAVE_PUBLICA).toPath());
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            PublicKey publica = KeyFactory.getInstance("RSA").generatePublic(spec);

            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publica);
            byte[] cifrado = cipher.doFinal(texto.getBytes());
            
            return Base64.getEncoder().encodeToString(cifrado);
        } catch (Exception e) {
            return null;
        }
    }

    // Descriptografa (Usa Chave PRIVADA)
    public static String descriptografar(String textoCifradoBase64) {
        try {
            byte[] keyBytes = Files.readAllBytes(new File(CAMINHO_CHAVE_PRIVADA).toPath());
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            PrivateKey privada = KeyFactory.getInstance("RSA").generatePrivate(spec);

            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privada);
            byte[] original = cipher.doFinal(Base64.getDecoder().decode(textoCifradoBase64));
            
            return new String(original);
        } catch (Exception e) {
            return null;
        }
    }
}