# Story Telling Manager (nome provisório) — Projeto Acadêmico

Este projeto foi desenvolvido como parte da disciplina **Desenvolvimento de Sistemas Orientados a Objetos (DOO)**, no curso de **Ciência da Computação (3º Semestre) da Instituição Federal de Santa Catarina (IFSC)**.

Seu principal objetivo é demonstrar, de forma prática, a aplicação dos **conceitos fundamentais de Programação Orientada a Objetos (POO)** por meio do desenvolvimento de uma aplicação desktop em **Java**, voltada à organização de campanhas de RPG de mesa, podendo também ser aplicada a qualquer história que deseja a ser sintetisada de modo mais prático e flúido.

---

## Requisitos Atendidos

### Conceitos de POO

- **Herança**: Superclasses como `ElementoNarrativo` e `EntradaDiario`, com subclasses como `Personagem`, `Objeto`, `Nota`, `Evento`.
- **Polimorfismo**: Sobrescrita de métodos em subclasses especializadas.
- **Classes Abstratas e Interfaces**: 
  - Abstratas: `ElementoNarrativo`, `EntradaDiario`
  - Interfaces: `Associavel`, `Exportavel`
- **Collections**: Uso de `List`, `Set` e `Map` para gerenciamento de dados.
- **Tratamento de Exceções**: Criação e uso de exceções customizadas.

---

### Funcionalidades Implementadas

- Criação e gerenciamento de campanhas.
- Registro de personagens, locais, eventos e objetos.
- Associação entre elementos narrativos.
- Sistema de notas com níveis de privacidade.
- Controle de usuários (mestre e jogadores).
- Interface gráfica implementada com **JavaFX** e **FXML** (não implementado na atual versão).
- Arquitetura modular utilizando `module-info.java`.

---

## Tecnologias Utilizadas

- **Java 17 (a ser atualizado para Java 21)**
- **JavaFX 20**
- **Maven**
- **FXML**
- **Arquitetura Modular**

---

## Estrutura de Pacotes

```
me/
├── controle/
├── gui/
│   ├── componentes/
│   └── controladores/
├── modelo/
│   ├── abstracts/
│   ├── entidades/
│   ├── enums/
│   ├── interfaces/
│   └── exceptions/
├── persistencia/
└── rede/
```

---

## Execução

1. **Clone o projeto** (caso necessário):

```bash
git clone https://github.com/seuusuario/rpg-campaign-manager.git
```

2. **Compile o projeto com Maven**:

```bash
mvn clean install
```

3. **Execute com JavaFX**:

```bash
mvn javafx:run
```

Trecho essencial do `pom.xml`:

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

## Observações Finais

Este projeto tem finalidade exclusivamente **acadêmica**, para fins de avaliação na disciplina de DOO. Futuras melhorias, como integração em rede, editor visual de grafos e suporte a múltiplos temas, estão previstas para versões posteriores.

---

> *Projeto desenvolvido por Nathaniel Christian Silva Alves – Ciência da Computação (3º Semestre)*

