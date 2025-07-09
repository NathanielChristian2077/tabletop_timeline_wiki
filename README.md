# Story Telling Manager (nome provisório) — Projeto Acadêmico

Este projeto está sendo desenvolvido como parte da disciplina **Desenvolvimento de Sistemas Orientados a Objetos (DOO)**, no curso de **Ciência da Computação (3º Semestre) da Instituição Federal de Santa Catarina (IFSC)**.

Seu principal objetivo é demonstrar, de forma prática, a aplicação dos **conceitos fundamentais de Programação Orientada a Objetos (POO)** por meio do desenvolvimento de uma aplicação desktop em **Java**, voltada à organização de campanhas de RPG de mesa, podendo também ser aplicada a qualquer história que deseja ser sintetizada de modo mais prático e fluido.

---

## Funcionalidades do Projeto

- **Gerenciamento de Campanhas**: Crie, edite, exclua e pesquise campanhas de RPG, com suporte a imagens de capa.
- **Cadastro e Autenticação de Usuários**: Sistema de login e cadastro com diferentes tipos de usuário (mestre e jogador).
- **Gestão de Elementos Narrativos**: Registre personagens, locais, eventos, objetos e notas, todos associados a campanhas.
- **Associação entre Elementos**: Relacione personagens, eventos, locais e objetos de forma flexível.
- **Sistema de Notas**: Crie notas com diferentes níveis de privacidade (WIP).
- **Interface Gráfica Moderna**: Desenvolvida com **JavaFX** e **FXML**, com efeitos visuais, animações e responsividade.
- **Barra de Busca**: Filtro dinâmico de campanhas por nome.
- **Edição de Perfil**: Permite ao usuário editar nome e senha, além de deletar a conta.
- **Persistência em Banco de Dados**: Utiliza JDBC para armazenamento dos dados.
- **Arquitetura Modular**: Separação clara entre controle, modelo, persistência e interface gráfica.
- **Tratamento de Exceções**: Mensagens amigáveis e robustez contra erros de banco e entrada de dados.

---

## Requisitos e Configurações

### Requisitos de Software

- **Java 17** (compatível até Java 24)
- **Maven 3.6+**
- **JavaFX 20** (gerenciado via Maven)
- **PostgreSQL** (para o banco de dados)
- **Sistema Operacional**: Windows, Linux ou MacOS

---

## Passo a Passo para Configuração do Banco de Dados

1. **Instale o PostgreSQL** (caso ainda não tenha).
2. **Crie um banco de dados** para o projeto (exemplo: `tabletop_timeline`):

   ```sql
   CREATE DATABASE tabletop_timeline;
   ```

3. **Acesse o banco de dados** pelo terminal:

   ```bash
   psql -U seu_usuario -d tabletop_timeline
   ```

4. **Execute o script de criação das tabelas**:

   - Localize o arquivo `campanha_db.sql` na raiz do projeto.
   - No terminal do `psql`, execute:

     ```sql
     \i /caminho/para/campanha_db.sql
     ```

   - Ou copie e cole o conteúdo do arquivo diretamente no terminal do `psql`.

5. **Pronto!** O banco estará preparado para uso com a aplicação.

---

## Configuração do Projeto

1. **Clone o projeto**:

   ```bash
   git clone https://github.com/NathanielChristian2077/tabletop_timeline_manager.git
   cd tabletop_timeline_manager
   ```

2. **Compile o projeto com Maven**:

   ```bash
   mvn clean install
   ```

3. **Execute a aplicação**:

   ```bash
   mvn javafx:run
   ```

   > **Obs:** Certifique-se de que o JavaFX está corretamente configurado no seu ambiente. O plugin Maven já inclui as dependências necessárias.

### Trecho essencial do `pom.xml`:

```xml
<plugin>
  <groupId>org.openjfx</groupId>
  <artifactId>javafx-maven-plugin</artifactId>
  <version>0.0.8</version>
  <configuration>
    <mainClass>me.gui.App</mainClass>
  </configuration>
</plugin>
```

---

## Estrutura de Pacotes

```
me/
├── controle/         # Lógica de negócio e gerenciadores
├── gui/              # Interface gráfica (FXML, controladores, componentes)
│   ├── componentes/
│   └── controladores/
├── modelo/           # Entidades, enums, interfaces e exceções
│   ├── abstracts/
│   ├── entidades/
│   ├── enums/
│   ├── interfaces/
│   └── exceptions/
├── persistencia/     # DAOs e conexão com banco de dados
└── rede/             # (Futuro) Comunicação em rede
```

---

## Observações Finais

Este projeto tem finalidade exclusivamente **acadêmica**, para fins de avaliação na disciplina de DOO. Futuras melhorias, como integração em rede, editor visual de grafos, suporte a múltiplos temas e exportação de dados, estão previstas para versões posteriores.

---

> *Projeto desenvolvido por Nathaniel Christian Silva Alves – Ciência da Computação (3º Semestre)*

