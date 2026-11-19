# Sistema de Consulta de Alocação Docente (CSA)

Projeto acadêmico desenvolvido para a disciplina de **Projeto Integrador III-A** do curso de Análise e Desenvolvimento de Sistemas (ADS) da **PUC Goiás**, sob orientação do professor **Thalles Santos**.

---

## Sobre o Projeto

Este projeto foi desenvolvido com base em uma necessidade real identificada no **Colégio Santo Agostinho** (Goiânia - GO), tendo sua homologação e validação técnica realizadas junto ao departamento de Tecnologia da Informação da instituição.

A instituição conta com mais de 45 professores e cerca de 23 turmas entre Ensino Fundamental (Anos Finais) e Ensino Médio. No dia a dia, a coordenação pedagógica frequentemente precisa localizar um professor de forma rápida — seja para cobrir uma falta inesperada, organizar horários para simulados, atender pais de alunos ou verificar janelas livres na grade semanal.

O problema identificado é que o ERP acadêmico utilizado pelo colégio (TOTVS RM) tem acesso restrito aos setores administrativo, financeiro e de TI. Desta forma, todo o processo de criação e desenvolvimento da grade curricular e das turmas é realizado pelo setor administrativo, tornando necessário que a coordenação solicite relatórios à secretaria, o que dificulta o acesso imediato.

Para resolver esse gargalo, foi desenvolvido este sistema em **Java (Cliente-Servidor via Sockets TCP)**. A grade de horários, elaborada no software **Urânia**, é exportada em formato de planilha/CSV e carregada pelo servidor. Assim, a coordenação consegue realizar consultas instantâneas e seguras direto pelo terminal, sem depender de solicitações manuais ao administrativo.

---

## Como o Sistema Funciona

A aplicação é dividida em duas pontas comunicando via rede local:

1. **Servidor TCP (`Servidor.java`):**
   - Roda na porta padrão `5000` usando `ServerSocket`.
   - Trabalha com múltiplas threads (`Thread` por cliente), permitindo que mais de um coordenador use o sistema ao mesmo tempo.
   - Carrega as alocações da planilha em memória (`RepositorioHorarios.java`) para que as buscas sejam praticamente instantâneas.
   - Não expõe dados sigilosos ou financeiros do ERP, apenas as informações necessárias para a rotina pedagógica (professor, disciplina, dia, horário e turma).

2. **Segurança e Controle de Acesso:**
   - **Autenticação com SHA-256:** As senhas dos usuários ficam gravadas em hash SHA-256 no arquivo `dados/usuarios.csv`. Nenhuma senha em texto puro é salva ou aceita.
   - **Bloqueio contra força bruta:** Se houver 3 tentativas consecutivas de login com senha incorreta para o mesmo IP/usuário, o acesso é travado pelo servidor.
   - **Protocolo de rede simples:** Mensagens com formato padronizado e terminador `<<<FIM_MSG>>>`, evitando problemas de buffer ou travamento de sockets.

3. **Cliente CLI (`Cliente.java`):**
   - Interface de linha de comando direta.
   - Solicita usuário e senha na inicialização.
   - Após o login, disponibiliza os comandos de consulta.

---

## Comandos do Terminal

Com a sessão autenticada, a coordenação pode utilizar:

| Comando | O que faz | Exemplo |
| :--- | :--- | :--- |
| `LISTAR` | Lista todos os horários cadastrados na grade | `LISTAR` |
| `BUSCAR <nome>` | Localiza as aulas de um professor específico | `BUSCAR CARLOS` |
| `TURMA <turma>` | Mostra todos os horários e matérias de uma turma | `TURMA 6º ANO A` |
| `DIA <dia>` | Filtra os professores que dão aula em um dia | `DIA SEGUNDA` |
| `DETALHAR <id>` | Mostra todos os dados de uma alocação pelo número do ID | `DETALHAR 3` |
| `AJUDA` | Mostra a lista de comandos no terminal | `AJUDA` |
| `SAIR` | Encerra a conexão | `SAIR` |

---

## Estrutura do Projeto

```text
├── dados/
│   ├── professores.csv         # Base dos horários exportada do Urânia
│   └── usuarios.csv            # Logins e hashes SHA-256
├── src/
│   ├── cliente/
│   │   └── Cliente.java        # Cliente de terminal
│   ├── modelo/
│   │   └── AulaHorario.java    # Modelo de dados (POO)
│   └── servidor/
│       ├── Servidor.java             # Servidor TCP multi-thread
│       ├── ProcessadorComandos.java  # Parser dos comandos
│       ├── RepositorioHorarios.java  # Consultas em memória
│       ├── RepositorioUsuarios.java  # Validação de credenciais e bloqueio
│       └── SegurancaUtil.java        # Hashing SHA-256
├── cadastrar_usuario.bat       # Script utilitário para cadastrar novos usuários
├── .gitignore
└── README.md
```

---

## Como Rodar

### Pré-requisitos
- **Java JDK 17 ou superior** instalado.

### 1. Compilação
No terminal, na raiz do projeto:

```bash
javac -encoding UTF-8 -d out src/modelo/*.java src/servidor/*.java src/cliente/*.java
```

### 2. Iniciar o Servidor
```bash
java -cp out servidor.Servidor
```

### 3. Iniciar o Cliente
Em outro terminal:

```bash
java -cp out cliente.Cliente
```

**Credenciais padrão:**
- `coordenacao` / senha: `123456`
- `direcao` / senha: `123456`

Para cadastrar novos coordenadores com senha criptografada sem precisar calcular hash manualmente, execute o arquivo `cadastrar_usuario.bat`.

---

## Autor

**Alysson Feliciano de Souza**  
Graduando em Análise e Desenvolvimento de Sistemas (ADS) — Pontifícia Universidade Católica de Goiás (PUC Goiás)  
Projeto Integrador III-A — 2026
