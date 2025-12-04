# Sistema de Gestão Web - AEDS III

Este projeto é a evolução final de um sistema de gerenciamento de dados desenvolvido para a disciplina de **Algoritmos e Estruturas de Dados III**. O que começou como um simples CRUD de console, transformou-se em uma aplicação **Web completa**, com segurança, compressão de dados e algoritmos avançados de busca.

---

## 🚀 O Que o Sistema Faz? (Visão Geral)

O sistema gerencia **Clientes**, **Boletos** e **Tags** (categorias), permitindo criar, editar e excluir registros. O diferencial está no que acontece "por baixo do capô":
* Não usamos Banco de Dados SQL (como MySQL ou Postgres).
* **Nós construímos o nosso próprio banco de dados** usando arquivos binários (`.db`).
* Implementamos índices Hash Extensível, Árvores, Criptografia e Compressão manualmente.

### Principais Funcionalidades (Fase 1 a 5)

* **🌐 Interface Web Moderna:** Desenvolvida com Spring Boot + Thymeleaf (adeus tela preta!).
* **🔒 Segurança:** Login obrigatório com senha criptografada via **RSA** (Chaves Pública/Privada).
* **🔍 Busca Inteligente:** Pesquisa textual em boletos usando algoritmos **KMP** (Knuth-Morris-Pratt) e **Boyer-Moore**.
* **💾 Compressão de Dados:** Backup dos arquivos usando **LZW** e **Huffman**.
* **🏷️ Relacionamento N:N:** Um boleto pode ter várias tags e uma tag pode estar em vários boletos (tabela intermediária `boleto_tag.db`).
* **♻️ Recuperação de Perda de Dados:** Funcionalidade de "Limpar Dados" (simular perda de dados) e "Restauração" via backup.

---

## 🛠️ Tecnologias Utilizadas

* **Java 17** (Backend robusto)
* **Spring Boot 3** (Motor Web)
* **Thymeleaf + Bootstrap 5** (Frontend bonito e responsivo)
* **Maven** (Gerenciador de dependências)
* **Git** (Versionamento)

---

## ⚙️ Como Rodar o Projeto (Passo a Passo)

### Pré-requisitos
* Ter o **Java 17** (ou superior) instalado.
* Navegador de internet (Chrome, Edge, etc.).

### Executando

1. **Clone o repositório:**
   ```bash
   git clone [https://github.com/FilipeLorenzato/Trabalho-AEDS3.git](https://github.com/FilipeLorenzato/Trabalho-AEDS3.git)
   cd Trabalho-AEDS3

2. **Inicie o Servidor:** No terminal (dentro da pasta do projeto), rode:

    Windows: .\mvnw.cmd spring-boot:run

    Linux/Mac: ./mvnw spring-boot:run

3. **Acesse:** Abra http://localhost:8080 no seu navegador.

4. **Login Padrão:**

    *Usuário: admin

    *Senha: 123

**🧪 Roteiro de Testes (Para Avaliação)**
O sistema possui funcionalidades específicas para facilitar a correção e demonstração dos requisitos de AEDS III:

1. **Geração de Massa de Dados**

    No painel principal, clique no botão roxo "Gerar Dados".

    Isso cria automaticamente 500 boletos e clientes, além de vincular tags aleatórias (N:N).

    Essencial para testar a eficiência da compressão e da busca.

2. **Busca Textual (Fase 5)**

    Vá no menu Boletos:

    Digite um termo (ex: "Fibra" ou "Luz").

    Escolha o algoritmo no menu: KMP ou Boyer-Moore.

    O sistema filtrará a lista usando a lógica matemática implementada manualmente.

3. **Compressão e Backup (Fase 4)**

    No painel principal:

    Clique em Backup LZW (ou Huffman).

    Verifique na pasta dados/ que o arquivo .lzw gerado é significativamente menor que o original .db (Compressão Positiva).

4. **Teste de Perda de Dados (Fase 4 Extra)**

    Clique no botão "Apagar Dados" para apagar o banco de dados propositalmente.

    Tente listar os boletos (estará vazio).

    Volte e clique em **"Restaurar"**.

    Veja os dados reaparecerem.

**📁 Estrutura de Arquivos Gerados**

    O sistema gerencia seus próprios arquivos na pasta dados/, garantindo persistência sem SGBD externo.
        dados/
        ├── boletos/
        │   └── boletos.db            # Banco de dados binário principal
        ├── clientes/
        │   └── clientes.db           # Banco de dados de clientes
        ├── boleto_tag/
        │   └── boleto_tag.db         # Tabela intermediária (Relacionamento N:N)
        ├── chaves/                   # Chaves de segurança RSA (Pública/Privada)
        ├── backup_boletos.lzw        # Arquivo de Backup Comprimido (LZW)
        └── backup_boletos.huffman    # Arquivo de Backup Comprimido (Huffman)

**🎓 Sobre o Projeto**

Este trabalho foi desenvolvido em etapas progressivas, cobrindo desde a manipulação básica de arquivos até algoritmos complexos de grafos e textos.

**Fase 1/2:** CRUD, Persistência em Arquivos e Hash Extensível.

**Fase 3:** Relacionamento Muitos-para-Muitos (N:N) e Tabela Intermediária.

**Fase 4:** Interface Web, Criptografia RSA e Compressão LZW/Huffman.

**Fase 5:** Busca por Padrão em texto (KMP e Boyer-Moore).

**Autores:** Filipe Lorenzato, Felipe Birchal, Bruno Maximo, Pedro Leite, Rafael Rehfeld.

**Disciplina:** Algoritmos e Estruturas de Dados III - PUC Minas

Projeto acadêmico desenvolvido em 2025.