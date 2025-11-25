 # TP-1-AEDS-III - Sistema de Gerenciamento de Clientes e Boletos

Este é um projeto desenvolvido para a disciplina de Algoritmos e Estruturas de Dados III, implementando um sistema completo de gerenciamento de clientes e boletos com persistência em arquivos e indexação hash extensível.

## 🚀 Características

### Funcionalidades Principais
- ✅ **CRUD Completo** para Clientes, Boletos e Tags
- ✅ **Persistência em Arquivos** usando RandomAccessFile
- ✅ **Indexação Hash Extensível** para performance O(1)
- ✅ **Interface Console** com menus interativos
- ✅ **Relacionamento Cliente-Boleto** (1:N)
- ✅ **Relacionamento Boleto-Tag** (N:N) - **NOVO!**
- ✅ **Categorização de Boletos** com Tags personalizáveis
- ✅ **Navegação Bidirecional** (Boleto→Tags e Tag→Boletos)

### Tecnologias Utilizadas
- **Java 17** - Linguagem principal
- **Spring Boot** - Framework base
- **Maven** - Gerenciamento de dependências
- **RandomAccessFile** - Persistência customizada
- **Hash Extensível** - Estrutura de dados para indexação

## 📁 Estrutura do Projeto

```
src/main/java/TP1/AEDS/III/
├── Tp1AedsIiiApplication.java          # Aplicação principal
├── MenuClientes.java                   # Menu de clientes
├── MenuBoletos.java                    # Menu de boletos
├── MenuTags.java                       # Menu de tags (NOVO)
├── TesteAutomatizado.java              # Testes automáticos (NOVO)
├── models/
│   ├── Cliente.java                    # Entidade Cliente
│   ├── Boleto.java                     # Entidade Boleto
│   ├── Tag.java                        # Entidade Tag (NOVO)
│   ├── BoletoTag.java                  # Tabela N:N (NOVO)
│   ├── BoletoStatus.java               # Enum de status
│   ├── ClienteDAO.java                 # DAO para clientes
│   ├── BoletoDAO.java                  # DAO para boletos
│   ├── TagDAO.java                     # DAO para tags (NOVO)
│   ├── BoletoTagDAO.java               # DAO para N:N (NOVO)
│   ├── RegistroHashClienteCPF.java     # Índice hash CPF
│   ├── RegistroHashBoleto.java         # Índice hash boletos
│   ├── RegistroHashTag.java            # Índice hash tags (NOVO)
│   └── RegistroHashBoletoTag.java      # Índice hash N:N (NOVO)
└── repository/
    ├── Registro.java                   # Interface para serialização
    ├── ArquivoBD.java                  # Engine de banco de dados
    ├── HashExtensivel.java             # Implementação hash extensível
    └── RegistroHashExtensivel.java     # Interface para registros hash
```

## 🎯 Como Executar

### Pré-requisitos
- **Java 17** ou superior
- **Maven 3.6+** ou superior

### Passo a Passo

#### 1. Clonar o Repositório
```bash
git clone https://github.com/BrunoMaximo03/TP1-TrabalhoPratico-AEDS-III.git
cd TP1-TrabalhoPratico-AEDS-III
```

#### 2. Compilar o Projeto
```bash
mvn compile
```

#### 3. Executar a Aplicação Principal
```bash
# Windows (PowerShell)
java -cp "target/classes" TP1.AEDS.III.Tp1AedsIiiApplication

# Linux/Mac
java -cp target/classes TP1.AEDS.III.Tp1AedsIiiApplication
```

#### 4. Executar Testes Automatizados (Opcional)
```bash
# Windows (PowerShell)
java -cp "target/classes" TP1.AEDS.III.TesteAutomatizado

# Linux/Mac
java -cp target/classes TP1.AEDS.III.TesteAutomatizado
```

### Menu Principal
```
=== SISTEMA DE GESTÃO DE BOLETOS ===
1 - Clientes
2 - Boletos
3 - Tags (N:N)
0 - Sair
```

## 🏗️ Arquitetura

### Persistência
- **ArquivoBD.java**: Engine genérica para CRUD em arquivos
- **Cabeçalho**: 12 bytes (4 int + 8 long) para metadados
- **Registros**: Serialização customizada via interface `Registro`

### Indexação Hash Extensível
- **Performance O(1)** para buscas por chave primária
- **Diretório dinâmico** que cresce conforme necessário
- **Cestos** com capacidade configurável
- **Rehashing automático** quando cestos ficam cheios

### Modelo de Dados

#### Cliente
- ID (int) - Chave primária
- Nome (String)
- CPF (String) - Chave natural indexada
- Salário (float)
- Data Nascimento (LocalDate)

#### Boleto
- ID (int) - Chave primária
- CPF Cliente (String) - Foreign Key
- Data Emissão (LocalDate)
- Data Vencimento (LocalDate)
- Descrição (String)
- Valor (BigDecimal)
- Status (BoletoStatus: PAGO/PENDENTE/CANCELADO)

#### Tag ⭐ **NOVO**
- ID (int) - Chave primária
- Nome (String)

#### BoletoTag (Tabela Intermediária N:N) ⭐ **NOVO**
- ID (int) - Chave primária
- ID Boleto (int) - Foreign Key
- ID Tag (int) - Foreign Key

### Relacionamentos
```
Cliente (1) ─────< (N) Boleto (N) >─────< (N) Tag
                                  └─ BoletoTag ─┘
```

## 🔧 Funcionalidades

### Menu Clientes
1. **Buscar por CPF** - Busca O(1) via hash extensível por CPF
2. **Buscar por ID** - Busca sequencial por ID
3. **Incluir** - Cadastro com validação de CPF único
4. **Alterar** - Atualização (CPF é imutável)
5. **Excluir** - Remoção lógica com limpeza de índices
6. **Listar Todos** - Listagem completa

### Menu Boletos
1. **Incluir** - Cadastro vinculado a cliente (por CPF)
2. **Buscar** - Busca O(1) via hash extensível
3. **Alterar** - Atualização com validações
4. **Excluir** - Remoção lógica
5. **Listar por Cliente** - Filtro por CPF do cliente
6. **Listar Todos** - Listagem completa
7. **Alterar Status** - Mudança de status do boleto

### Menu Tags ⭐ **NOVO**
1. **Criar Tag** - Cadastro de nova tag
2. **Listar Todas** - Visualizar todas as tags
3. **Buscar Tag** - Busca por ID
4. **Alterar Tag** - Editar nome da tag
5. **Excluir Tag** - Remover tag
6. **Adicionar Tag a Boleto** - Criar relacionamento N:N
7. **Remover Tag de Boleto** - Desfazer relacionamento
8. **Listar Tags de Boleto** - Ver tags de um boleto específico
9. **Listar Boletos por Tag** - Ver boletos com determinada tag
10. **Relatório Completo** - Visão geral de todos os relacionamentos

## 🚀 Performance

- **Busca por ID**: O(1) através de hash extensível
- **Inserção**: O(1) amortizado
- **Atualização**: O(1) para localização + escrita
- **Exclusão**: O(1) para localização + marcação

## 📊 Estrutura de Arquivos

```
./dados/
├── clientes/
│   └── clientes.db                    # Dados dos clientes
├── boletos/
│   └── boletos.db                     # Dados dos boletos
├── tags/                              # NOVO
│   └── tags.db                        # Dados das tags
├── boleto_tag/                        # NOVO
│   └── boleto_tag.db                  # Tabela intermediária N:N
└── indices/
    ├── clientes_cpf_diretorio.hash_d  # Diretório hash CPF
    ├── clientes_cpf_cestos.hash_c     # Cestos hash CPF
    ├── boletos_diretorio.hash_d       # Diretório hash boletos
    ├── boletos_cestos.hash_c          # Cestos hash boletos
    ├── tags_diretorio.hash_d          # Diretório hash tags (NOVO)
    ├── tags_cestos.hash_c             # Cestos hash tags (NOVO)
    ├── boleto_tag_diretorio.hash_d    # Diretório hash N:N (NOVO)
    └── boleto_tag_cestos.hash_c       # Cestos hash N:N (NOVO)
```

### Formato dos Arquivos

#### Cabeçalho (12 bytes)
- Último ID usado: 4 bytes (int)
- Lista de excluídos: 8 bytes (long)

#### Cada Registro
- Lápide: 1 byte (' ' = ativo, '*' = excluído)
- Tamanho: 2 bytes (short)
- Dados: N bytes (serialização customizada)

## 🧪 Testes

### Teste Automatizado Completo
Execute o teste automatizado para validar todas as funcionalidades:

```bash
java -cp "target/classes" TP1.AEDS.III.TesteAutomatizado
```

**O teste executa 9 fases:**
1. ✅ Criação de 2 clientes
2. ✅ Criação de 4 boletos
3. ✅ Criação de 4 tags
4. ✅ Criação de 6 relacionamentos N:N
5. ✅ Navegação Boleto → Tags
6. ✅ Navegação Tag → Boletos
7. ✅ Estatísticas de relacionamentos
8. ✅ Remoção de relacionamento
9. ✅ Performance Hash O(1) (114-394 microssegundos)

### Teste Manual
1. Execute a aplicação principal
2. Crie clientes pelo menu (opção 1)
3. Crie boletos para os clientes (opção 2)
4. Crie tags e associe aos boletos (opção 3)
5. Navegue entre os relacionamentos

## 📈 Atualizações Recentes

### v2.0 - Relacionamento N:N com Tags ⭐
**Data:** Novembro 2025

**Novas Funcionalidades:**
- ✅ Sistema completo de Tags para categorização
- ✅ Relacionamento N:N entre Boleto e Tag
- ✅ Navegação bidirecional (Boleto↔Tag)
- ✅ Hash Extensível para todas as tabelas
- ✅ Integridade referencial com validações
- ✅ Menu dedicado para gerenciamento de Tags
- ✅ Suite de testes automatizados
- ✅ Relatórios de relacionamentos

**Arquivos Adicionados:**
- `MenuTags.java` - Interface de gerenciamento
- `Tag.java` / `TagDAO.java` - Entidade e persistência
- `BoletoTag.java` / `BoletoTagDAO.java` - Tabela N:N
- `RegistroHashTag.java` / `RegistroHashBoletoTag.java` - Índices
- `TesteAutomatizado.java` - Testes completos

**Melhorias Técnicas:**
- Performance O(1) mantida em todas as operações
- 4 estruturas Hash Extensível simultâneas
- Persistência padronizada em todas as tabelas
- Validação de integridade referencial

### v1.0 - Sistema Base
- CRUD de Clientes e Boletos
- Hash Extensível para indexação
- Persistência em arquivos binários
- Relacionamento 1:N Cliente-Boleto

## 👨‍💻 Autor

**Bruno Máximo**
- GitHub: [@BrunoMaximo03](https://github.com/BrunoMaximo03)
- Repositório: [TP1-TrabalhoPratico-AEDS-III](https://github.com/BrunoMaximo03/TP1-TrabalhoPratico-AEDS-III)

Desenvolvido para a disciplina de **Algoritmos e Estruturas de Dados III**.

## 🎓 Conceitos Implementados

- ✅ Persistência customizada com RandomAccessFile
- ✅ Hash Extensível (crescimento dinâmico)
- ✅ Serialização de objetos
- ✅ Relacionamentos 1:N e N:N
- ✅ Indexação com performance O(1)
- ✅ Exclusão lógica (lápide)
- ✅ Integridade referencial
- ✅ Padrão DAO (Data Access Object)
- ✅ Interface Genérica para persistência

## 📝 Licença

Projeto acadêmico - AEDS III

---

**⭐ Se este projeto foi útil, considere dar uma estrela no GitHub!**