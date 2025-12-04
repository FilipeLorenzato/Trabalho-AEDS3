package TP1.AEDS.III;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Tp1AedsIiiApplication {

    public static void main(String[] args) {
        // Inicia o servidor Tomcat (Web) na porta 8080
        SpringApplication.run(Tp1AedsIiiApplication.class, args);
        System.out.println("🚀 Sistema rodando em: http://localhost:8080");
    }
}