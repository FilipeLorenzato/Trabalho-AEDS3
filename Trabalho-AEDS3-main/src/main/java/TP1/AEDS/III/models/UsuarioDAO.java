package TP1.AEDS.III.models;

import java.io.File;
import TP1.AEDS.III.repository.ArquivoBD;
import TP1.AEDS.III.repository.Criptografia;

public class UsuarioDAO extends ArquivoBD<Usuario> {

    public UsuarioDAO() throws Exception {
        super("usuarios", Usuario.class.getConstructor());
        new File("dados/usuarios").mkdirs();
        
        // Garante que as chaves RSA existem (ou cria se não existirem)
        Criptografia.gerarChaves();
        
        // Cria usuário ADMIN padrão se o banco estiver vazio
        try {
            if (super.read(1) == null) {
                System.out.println("Criando usuário padrão (admin/123)...");
                
                // --- ALTERAÇÃO AQUI: Usando RSA ---
                String senhaRSA = Criptografia.criptografar("123");
                
                // Salva o usuário ID 1 com a senha criptografada em RSA
                super.create(new Usuario(1, "admin", senhaRSA));
            }
        } catch (Exception e) {
            // Se der erro de leitura na primeira vez, ignora
        }
    }

    // Busca sequencial simples por login
    public Usuario buscarPorLogin(String login) throws Exception {
        // Varre os primeiros 100 IDs (suficiente para o teste)
        for (int i = 1; i <= 100; i++) {
            Usuario u = super.read(i);
            if (u != null && u.getLogin().equals(login)) {
                return u;
            }
        }
        return null;
    }
}