# 🎯 GUIA RÁPIDO DE APRESENTAÇÃO - TP1 AEDS III

## ⚡ PREPARAÇÃO (Antes do Professor Chegar)

### 1. Limpar Dados Antigos
```bash
# Deletar manualmente a pasta:
dados/
```

### 2. Compilar o Projeto
```bash
mvn compile
```

---

## 🎬 ROTEIRO DE APRESENTAÇÃO

### PASSO 1: Executar o Script de Demonstração
```bash
# Windows (PowerShell)
java -cp "target/classes" TP1.AEDS.III.DemonstracaoApresentacao

# Linux/Mac
java -cp target/classes TP1.AEDS.III.DemonstracaoApresentacao
```

---

## 📋 O QUE O SCRIPT FAZ (AUTOMÁTICO)

### ✅ PARTE 1: CRUD COMPLETO (3-4 min)
**O que acontece:**
- ✅ CREATE: Cria cliente "João Silva"
- ✅ READ: Busca por CPF usando Hash O(1)
- ✅ UPDATE: Altera salário de R$ 3.500 → R$ 4.200
- ✅ Confirma UPDATE com nova busca

**Arquivos gerados:**
```
dados/clientes/clientes.db
dados/indices/clientes_cpf_diretorio.hash_d
dados/indices/clientes_cpf_cestos.hash_c
```

**🔍 Mostrar ao professor:**
- Abrir `clientes.db` em editor hexadecimal (HxD ou similar)
- Apontar: Cabeçalho (12 bytes) → Lápide (' ') → Dados do cliente

---

### ✅ PARTE 2: RELACIONAMENTO 1:N (4-5 min)
**O que acontece:**
- ✅ Cria segundo cliente "Maria Santos"
- ✅ Cria 3 boletos para João Silva (CPF: 12345678901)
  - Boleto #1: Luz - R$ 185,50
  - Boleto #2: Água - R$ 95,80
  - Boleto #3: Internet - R$ 129,90
- ✅ Cria 1 boleto para Maria Santos
  - Boleto #4: Aluguel - R$ 1.800,00
- ✅ Lista todos os boletos de João Silva (demonstra 1:N)

**Arquivos gerados:**
```
dados/boletos/boletos.db
dados/indices/boletos_diretorio.hash_d
dados/indices/boletos_cestos.hash_c
```

**🔍 Mostrar ao professor:**
- Abrir `boletos.db` em editor hexadecimal
- Apontar campo `cpfCliente` nos registros
- Mostrar que boletos 1, 2, 3 têm CPF: 12345678901
- Explicar: **1 Cliente → N Boletos**

---

### ✅ PARTE 3: RELACIONAMENTO N:N (5-6 min)
**O que acontece:**
- ✅ Cria 3 tags:
  - Tag #1: Urgente
  - Tag #2: Recorrente
  - Tag #3: Residencial
- ✅ Cria relacionamentos:
  - Boleto #1 (Luz) → Tags: Urgente, Recorrente, Residencial
  - Boleto #2 (Água) → Tags: Recorrente, Residencial
  - Boleto #3 (Internet) → Tag: Recorrente
- ✅ Demonstra navegação **Boleto → Tags**
- ✅ Demonstra navegação **Tag → Boletos**

**Arquivos gerados:**
```
dados/tags/tags.db
dados/boleto_tag/boleto_tag.db  ← TABELA INTERMEDIÁRIA N:N
dados/indices/tags_diretorio.hash_d
dados/indices/boleto_tag_diretorio.hash_d
```

**🔍 Mostrar ao professor:**
- Abrir `boleto_tag.db` em editor hexadecimal
- Apontar pares (idBoleto, idTag):
  - Registro 1: idBoleto=1, idTag=1
  - Registro 2: idBoleto=1, idTag=2
  - Registro 3: idBoleto=1, idTag=3
  - etc.
- Explicar: **N Boletos ↔ N Tags** (tabela intermediária)

---

## 🎯 PONTOS-CHAVE PARA FALAR

### Durante PARTE 1 (CRUD):
- ✅ "Aqui criamos o cliente e vemos o arquivo .db sendo gerado"
- ✅ "O Hash Extensível permite busca O(1) por CPF"
- ✅ "A lápide (' ') indica registro ativo, ('*') seria excluído"
- ✅ "O cabeçalho guarda o último ID e lista de excluídos"

### Durante PARTE 2 (1:N):
- ✅ "Cada boleto tem o campo cpfCliente ligando ao cliente"
- ✅ "Um cliente pode ter vários boletos (1:N)"
- ✅ "Aqui João Silva tem 3 boletos, Maria Santos tem 1"

### Durante PARTE 3 (N:N):
- ✅ "A tabela BoletoTag é a intermediária do N:N"
- ✅ "Um boleto pode ter várias tags (ex: Luz tem 3 tags)"
- ✅ "Uma tag pode estar em vários boletos (ex: Recorrente em 3 boletos)"
- ✅ "Isso demonstra relacionamento muitos-para-muitos verdadeiro"

---

## 📁 ARQUIVOS IMPORTANTES PARA ABRIR

### Recomendado ter aberto antes:
1. **Editor Hexadecimal** (HxD, VSCode com extensão Hex Editor)
2. **Terminal** pronto para executar

### Arquivos para demonstrar:
```
1. dados/clientes/clientes.db          → Mostrar estrutura
2. dados/boletos/boletos.db            → Mostrar campo cpfCliente
3. dados/boleto_tag/boleto_tag.db      → Mostrar pares (id, id)
4. dados/indices/*_diretorio.hash_d    → Explicar Hash Extensível
```

---

## ⏱️ TEMPO ESTIMADO

- Execução do script: **12-15 minutos**
- Explicações durante pausas: **+5 minutos**
- Mostrar arquivos .db: **+3 minutos**
- **TOTAL: ~20-23 minutos**

---

## 🆘 PLANO B (Se algo der errado)

### Se o script falhar:
```bash
# Executar teste completo alternativo
java -cp "target/classes" TP1.AEDS.III.TesteAutomatizado
```

### Se precisar refazer:
1. Deletar pasta `dados/`
2. Executar novamente `DemonstracaoApresentacao`

---

## 💡 DICAS PARA A APRESENTAÇÃO

### ✅ FAÇA:
- Deixe o script rodar até a pausa
- Explique enquanto os dados aparecem
- Mostre os arquivos .db quando o script pausar
- Responda perguntas do professor com segurança

### ❌ NÃO FAÇA:
- Não tente editar código durante apresentação
- Não pule as pausas (use para explicar)
- Não feche os arquivos .db muito rápido

---

## 📊 RESUMO DOS DADOS CRIADOS

Ao final da execução, você terá:

| Entidade | Quantidade | Arquivo |
|----------|-----------|---------|
| Clientes | 2 | `clientes.db` |
| Boletos | 4 | `boletos.db` |
| Tags | 3 | `tags.db` |
| Relações N:N | 6 | `boleto_tag.db` |

**Relacionamentos demonstrados:**
- **1:N** → João Silva tem 3 boletos
- **N:N** → Boleto Luz tem 3 tags + Tag Recorrente em 3 boletos

---

## 🎤 FRASES PRONTAS

**Ao iniciar:**
> "Vou executar um script que demonstra todas as funcionalidades requisitadas: CRUD, relacionamento 1:N e N:N. Os arquivos .db serão criados em tempo real."

**Ao mostrar clientes.db:**
> "Aqui está o arquivo de clientes com cabeçalho de 12 bytes, lápide para exclusão lógica e os dados serializados do cliente João Silva."

**Ao mostrar boletos.db:**
> "Cada boleto armazena o CPF do cliente, demonstrando o relacionamento 1 para N. O cliente João tem 3 boletos vinculados a ele."

**Ao mostrar boleto_tag.db:**
> "Esta é a tabela intermediária do relacionamento N para N. Cada registro tem o ID do boleto e o ID da tag, permitindo navegação bidirecional."

**Ao finalizar:**
> "Como podem ver, todos os arquivos .db foram criados, demonstrando CRUD completo, relacionamento 1:N através do CPF no boleto, e N:N através da tabela intermediária BoletoTag."

---

## ✅ CHECKLIST PRÉ-APRESENTAÇÃO

- [ ] Pasta `dados/` deletada
- [ ] Projeto compilado (`mvn compile`)
- [ ] Terminal aberto na pasta do projeto
- [ ] Editor hexadecimal instalado (HxD, etc.)
- [ ] Comando copiado e pronto para colar
- [ ] Este guia aberto para consulta rápida

---

**🍀 BOA SORTE NA APRESENTAÇÃO!**
