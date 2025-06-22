// TODO: Melhorar css, adicionar animações etc

package me.gui.controladores;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import me.controle.*;
import me.modelo.abstracts.ElementoNarrativo;
import me.modelo.entidades.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;

public class ControladorTimeline {
    @FXML private TreeView<Object> treeView;
    @FXML private TextField searchField;
    @FXML private HBox navButtonsBox;
    @FXML private AnchorPane graphPane;
    private TreeItem<Object> rootCampanha;
    private final Deque<Object> historicoRoots = new ArrayDeque<>();

    private GerenciadorEvento gerenciadorEvento;
    private GerenciadorPersonagem gerenciadorPersonagem;
    private GerenciadorLocal gerenciadorLocal;
    private GerenciadorObjeto gerenciadorObjeto;

    private List<Evento> eventos;
    private List<Personagem> personagens;
    private List<Local> locais;
    private List<Objeto> objetos;

    private Campanha c;

    public ControladorTimeline() {
        try {
            gerenciadorEvento = new GerenciadorEvento();
            gerenciadorPersonagem = new GerenciadorPersonagem();
            gerenciadorLocal = new GerenciadorLocal();
            gerenciadorObjeto = new GerenciadorObjeto();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setCampanha(Campanha c) {
        this.c = c;
        recarregarDados();
        setTreeView(treeView, c, gerenciadorEvento, gerenciadorPersonagem, gerenciadorLocal, gerenciadorObjeto);
    }

    private void recarregarDados() {
        try {
            eventos = gerenciadorEvento.listarPorCampanha(c.getId());
            personagens = gerenciadorPersonagem.listarPorCampanha(c.getId());
            locais = gerenciadorLocal.listarPorCampanha(c.getId());
            objetos = gerenciadorObjeto.listarPorCampanha(c.getId());
        } catch (SQLException e) {
            eventos = new ArrayList<>();
            personagens = new ArrayList<>();
            locais = new ArrayList<>();
            objetos = new ArrayList<>();
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {}

    private void setTreeView(TreeView<Object> treeView, Campanha c, GerenciadorEvento gerenciadorEvento,
            GerenciadorPersonagem gerenciadorPersonagem, GerenciadorLocal gerenciadorLocal,
            GerenciadorObjeto gerenciadorObjeto) {
        TreeItem<Object> root = new TreeItem<>(c.getNome());
        root.setExpanded(true);

        root.getChildren().add(criarGrupo("Events", eventos));
        root.getChildren().add(criarGrupo("Characters", personagens));
        root.getChildren().add(criarGrupo("Locations", locais));
        root.getChildren().add(criarGrupo("Objects", objetos));

        this.rootCampanha = root;
        treeView.setRoot(root);

        configExib(treeView);
        configSelec(treeView);
    }

    private <T> TreeItem<Object> criarGrupo(String nome, List<T> itens) {
        TreeItem<Object> grupo = new TreeItem<>(nome);
        grupo.setExpanded(false);
        for (T item : itens) {
            grupo.getChildren().add(new TreeItem<>(item));
        }
        return grupo;
    }

    private void configExib(TreeView<Object> treeView) {
        treeView.setCellFactory(tv -> new TreeCell<>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else if (item instanceof Button button) {
                    setText(null);
                    setGraphic(button);
                } else if (item instanceof Evento e) {
                    setText(e.getNome());
                    setGraphic(null);
                } else if (item instanceof Personagem p) {
                    setText(p.getNome());
                    setGraphic(null);
                } else if (item instanceof Local l) {
                    setText(l.getNome());
                    setGraphic(null);
                } else if (item instanceof Objeto o) {
                    setText(o.getNome());
                    setGraphic(null);
                } else {
                    setText(item.toString());
                    setGraphic(null);
                }
            }
        });
    }

    private void configSelec(TreeView<Object> treeView) {
        treeView.setOnMouseClicked(event -> {
            TreeItem<Object> selecionado = treeView.getSelectionModel().getSelectedItem();
            if (selecionado == null) return;
            Object valor = selecionado.getValue();

            if (event.getButton() == MouseButton.PRIMARY) {
                if (event.getClickCount() == 2) {
                    mostrarRelacoes(treeView, valor);
                }
            } else if (event.getButton() == MouseButton.SECONDARY) {
                ContextMenu menu = criarMenuContextual(valor, treeView);
                if (menu != null) {
                    menu.show(treeView, event.getScreenX(), event.getScreenY());
                }
            }
        });
    }

    private void mostrarRelacoes(TreeView<Object> treeView, Object elemento) {
        // Não empilha a root se já está na root da campanha
        if (treeView.getRoot() != null && !Objects.equals(treeView.getRoot().getValue(), rootCampanha.getValue())) {
            historicoRoots.push(treeView.getRoot().getValue());
        }

        TreeItem<Object> newRoot = new TreeItem<>(elemento);
        newRoot.setExpanded(true);

        navButtonsBox.getChildren().clear();

        if (!historicoRoots.isEmpty()) {
            Button backBtn = new Button("← Back");
            backBtn.setOnAction(e -> {
                if (!historicoRoots.isEmpty()) {
                    Object previous = historicoRoots.pop();
                    mostrarRelacoes(treeView, previous);
                }
            });
            navButtonsBox.getChildren().add(backBtn);
        }

        if (rootCampanha != null && !elemento.equals(rootCampanha.getValue())) {
            Button rootBtn = new Button("⤒ Campaign Root");
            rootBtn.setOnAction(e -> {
                treeView.setRoot(rootCampanha);
                historicoRoots.clear();
                navButtonsBox.getChildren().clear();
            });
            navButtonsBox.getChildren().add(rootBtn);
        }

        if (elemento instanceof Evento e) {
            if (e.getAnterior() != null) {
                Button prevBtn = new Button("« Previous: " + e.getAnterior().getNome());
                prevBtn.setOnAction(ev -> mostrarRelacoes(treeView, e.getAnterior()));
                navButtonsBox.getChildren().add(prevBtn);
            }
            if (e.getPosterior() != null) {
                Button nextBtn = new Button("Next: " + e.getPosterior().getNome() + " »");
                nextBtn.setOnAction(ev -> mostrarRelacoes(treeView, e.getPosterior()));
                navButtonsBox.getChildren().add(nextBtn);
            }

            newRoot.getChildren().add(criarGrupo("Characters", e.getPersonagensRelacionados()));
            newRoot.getChildren().add(criarGrupo("Locations", e.getLocaisRelacionados()));
            newRoot.getChildren().add(criarGrupo("Objects", e.getObjetosRelacionados()));

        } else if (elemento instanceof ElementoNarrativo en) {
            newRoot.getChildren().add(criarGrupo("Characters", en.getPersonagensRelacionados()));
            newRoot.getChildren().add(criarGrupo("Locations", en.getLocaisRelacionados()));
            newRoot.getChildren().add(criarGrupo("Objects", en.getObjetosRelacionados()));
            newRoot.getChildren().add(criarGrupo("Events", en.getEventosRelacionados()));
        }

        treeView.setRoot(newRoot);
    }

    private ContextMenu criarMenuContextual(Object elemento, TreeView<Object> treeView) {
        if (elemento == null)
            return null;

        // Proíbe excluir a campanha root
        boolean isRootCampanha = rootCampanha != null && elemento.equals(rootCampanha.getValue());

        MenuItem descricao = new MenuItem("View description");
        descricao.setOnAction(e -> mostrarDescricao(elemento));

        Menu adicionarRelacao = new Menu("Add link");

        MenuItem relEvento = new MenuItem("Event");
        MenuItem relPersonagem = new MenuItem("Character");
        MenuItem relLugar = new MenuItem("Location");
        MenuItem relObject = new MenuItem("Object");

        relEvento.setOnAction(e -> adicionarRelacao(elemento, "Event"));
        relPersonagem.setOnAction(e -> adicionarRelacao(elemento, "Character"));
        relLugar.setOnAction(e -> adicionarRelacao(elemento, "Location"));
        relObject.setOnAction(e -> adicionarRelacao(elemento, "Object"));

        adicionarRelacao.getItems().addAll(relEvento, relPersonagem, relLugar, relObject);

        MenuItem editar = new MenuItem("Edit");
        editar.setOnAction(e -> editarElemento(elemento));

        MenuItem remover = new MenuItem("Delete");
        remover.setOnAction(e -> {
            try {
                removerElemento(elemento, treeView);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        });
        if (isRootCampanha) {
            remover.setDisable(true);
            remover.setText("Cannot delete campaign root");
        }

        return new ContextMenu(descricao, adicionarRelacao, editar, remover);
    }

    private void mostrarDescricao(Object elemento) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Element Description");
        alert.setHeaderText("Details:");

        if (elemento instanceof Evento e) {
            alert.setContentText(e.exportar());
        } else if (elemento instanceof ElementoNarrativo en) {
            alert.setContentText(en.getNome() + "\n\n" + en.getDescricao());
        } else {
            alert.setContentText(elemento.toString());
        }

        alert.showAndWait();
    }

    private void adicionarRelacao(Object elemento, String tipo) {
        List<?> candidatos = switch (tipo) {
            case "Event" -> eventos;
            case "Character" -> personagens;
            case "Location" -> locais;
            case "Object" -> objetos;
            default -> List.of();
        };

        ChoiceDialog<Object> dialog = new ChoiceDialog<>(null, candidatos);
        dialog.setTitle("Add Relation");
        dialog.setHeaderText("Select a " + tipo + " to link.");
        dialog.setContentText("Choose:");

        dialog.showAndWait().ifPresent(selecionado -> {
            if (elemento instanceof Evento e) {
                switch (tipo) {
                    case "Event" -> e.adicionarEventoAnterior((Evento) selecionado);
                    case "Character" -> e.adicionarPersonagem((Personagem) selecionado);
                    case "Location" -> e.adicionarLocal((Local) selecionado);
                    case "Object" -> e.adicionarObjeto((Objeto) selecionado);
                }
                try {
                    gerenciadorEvento.atualizar(e);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            } else if (elemento instanceof ElementoNarrativo en) {
                switch (tipo) {
                    case "Event" -> en.adicionarEvento((Evento) selecionado);
                    case "Character" -> en.adicionarPersonagem((Personagem) selecionado);
                    case "Location" -> en.adicionarLocal((Local) selecionado);
                    case "Object" -> en.adicionarObjeto((Objeto) selecionado);
                }
            }
            recarregarDados();
            setTreeView(treeView, c, gerenciadorEvento, gerenciadorPersonagem, gerenciadorLocal, gerenciadorObjeto);
        });
    }

    private void editarElemento(Object elemento) {
        if (elemento instanceof Evento e) {
            editarEvento(e);
        } else if (elemento instanceof ElementoNarrativo en) {
            editarElementoNarrativo(en);
        }
        treeView.setRoot(rootCampanha);
    }

    private void editarEvento(Evento evento) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Edit Event");

        TextField nameField = new TextField(evento.getNome());
        TextArea descriptionArea = new TextArea(evento.getDescricao());

        VBox relationsBox = new VBox(10);
        relationsBox.getChildren().addAll(
                criarSeletorDeRelacoes("Previous Events", new ArrayList<>(evento.getEventosRelacionados()), eventos,
                        e -> evento.adicionarEventoAnterior((Evento) e),
                        e -> evento.removerEvento((Evento) e)),

                criarSeletorDeRelacoes("Characters", evento.getPersonagensRelacionados(), personagens,
                        evento::adicionarPersonagem,
                        evento::removerPersonagem),

                criarSeletorDeRelacoes("Locations", evento.getLocaisRelacionados(), locais,
                        evento::adicionarLocal,
                        evento::removerLocal),

                criarSeletorDeRelacoes("Objects", evento.getObjetosRelacionados(), objetos,
                        evento::adicionarObjeto,
                        evento::removerObjeto));

        VBox content = new VBox(10,
                new Label("Name:"), nameField,
                new Label("Description:"), descriptionArea,
                new Label("Relations:"), relationsBox);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().setPrefWidth(500);
        dialog.getDialogPane().getButtonTypes().addAll(
                new ButtonType("Save", ButtonBar.ButtonData.OK_DONE),
                ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                evento.setTitulo(nameField.getText());
                evento.setDescricao(descriptionArea.getText());
                try {
                    gerenciadorEvento.atualizar(evento);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return null;
        });

        dialog.showAndWait();
        recarregarDados();
        setTreeView(treeView, c, gerenciadorEvento, gerenciadorPersonagem, gerenciadorLocal, gerenciadorObjeto);
    }

    private <T> VBox criarSeletorDeRelacoes(String titulo, List<T> relacionados, List<T> todosDisponiveis,
            Consumer<T> adicionador, Consumer<T> removedor) {
        VBox box = new VBox(5);
        Label label = new Label(titulo);
        FlowPane flow = new FlowPane(5, 5);

        for (T rel : new ArrayList<>(relacionados)) {
            Button b = new Button(rel.toString() + " ❌");
            b.setOnAction(e -> {
                removedor.accept(rel);
                flow.getChildren().remove(b);
            });
            flow.getChildren().add(b);
        }

        Button btnAdd = new Button("➕ Adicionar");
        btnAdd.setOnAction(e -> {
            List<T> candidatos = todosDisponiveis.stream()
                    .filter(item -> !relacionados.contains(item))
                    .toList();

            ChoiceDialog<T> dialog = new ChoiceDialog<>(null, candidatos);
            dialog.setTitle("Adicionar relação");
            dialog.setHeaderText(null);
            dialog.setContentText("Escolha um:");

            dialog.showAndWait().ifPresent(sel -> {
                adicionador.accept(sel);
                Button b = new Button(sel.toString() + " ❌");
                b.setOnAction(ev -> {
                    removedor.accept(sel);
                    flow.getChildren().remove(b);
                });
                flow.getChildren().add(b);
            });
        });

        box.getChildren().addAll(label, flow, btnAdd);
        return box;
    }

    private void editarElementoNarrativo(ElementoNarrativo en) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Edit " + en.getClass().getSimpleName());

        TextField nameField = new TextField(en.getNome());
        TextArea descriptionArea = new TextArea(en.getDescricao());

        VBox relationsBox = new VBox(10);
        relationsBox.getChildren().addAll(
                criarSeletorDeRelacoes("Events", en.getEventosRelacionados(), eventos,
                        e -> en.adicionarEvento((Evento) e),
                        e -> en.removerEvento((Evento) e)),

                criarSeletorDeRelacoes("Characters", en.getPersonagensRelacionados(), personagens,
                        en::adicionarPersonagem,
                        en::removerPersonagem),

                criarSeletorDeRelacoes("Locations", en.getLocaisRelacionados(), locais,
                        en::adicionarLocal,
                        en::removerLocal),

                criarSeletorDeRelacoes("Objects", en.getObjetosRelacionados(), objetos,
                        en::adicionarObjeto,
                        en::removerObjeto));

        VBox content = new VBox(10,
                new Label("Name:"), nameField,
                new Label("Description:"), descriptionArea,
                new Label("Relations:"), relationsBox);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().setPrefWidth(500);
        dialog.getDialogPane().getButtonTypes().addAll(
                new ButtonType("Save", ButtonBar.ButtonData.OK_DONE),
                ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                en.setNome(nameField.getText());
                en.setDescricao(descriptionArea.getText());
                try {
                    if (en instanceof Personagem personagem) {
                        gerenciadorPersonagem.atualizar(personagem);
                    } else if (en instanceof Local local) {
                        gerenciadorLocal.atualizar(local);
                    } else if (en instanceof Objeto objeto) {
                        gerenciadorObjeto.atualizar(objeto);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return null;
        });

        dialog.showAndWait();
        recarregarDados();
        setTreeView(treeView, c, gerenciadorEvento, gerenciadorPersonagem, gerenciadorLocal, gerenciadorObjeto);
    }

    private void removerElemento(Object elemento, TreeView<Object> treeView) throws SQLException {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText("Are you sure you want to delete this element?");
        confirm.setContentText(elemento.toString());

        confirm.showAndWait().ifPresent(resposta -> {
            if (resposta != ButtonType.OK)
                return;

            try {
                if (elemento instanceof Evento e) {
                    eventos.remove(e);
                    gerenciadorEvento.remover(e.getId());
                } else if (elemento instanceof Personagem p) {
                    personagens.remove(p);
                    gerenciadorPersonagem.remover(p);
                } else if (elemento instanceof Local l) {
                    locais.remove(l);
                    gerenciadorLocal.remover(l);
                } else if (elemento instanceof Objeto o) {
                    objetos.remove(o);
                    gerenciadorObjeto.remover(o);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            // Remover referências reversas
            for (Evento evt : eventos) {
                evt.getEventosRelacionados().remove(elemento);
                evt.getPersonagensRelacionados().remove(elemento);
                evt.getLocaisRelacionados().remove(elemento);
                evt.getObjetosRelacionados().remove(elemento);
            }
            for (Personagem p : personagens)
                p.getEventosRelacionados().remove(elemento);
            for (Local l : locais)
                l.getEventosRelacionados().remove(elemento);
            for (Objeto o : objetos)
                o.getEventosRelacionados().remove(elemento);

            recarregarDados();
            setTreeView(treeView, c, gerenciadorEvento, gerenciadorPersonagem, gerenciadorLocal, gerenciadorObjeto);
        });
    }

    @FXML
    private void onSearch() {
        String termo = searchField.getText().trim().toLowerCase();
        if (termo.isEmpty()) return;

        List<Object> resultados = new ArrayList<>();

        for (Evento e : eventos)
            if (e.getNome().toLowerCase().contains(termo)) resultados.add(e);
        for (Personagem p : personagens)
            if (p.getNome().toLowerCase().contains(termo)) resultados.add(p);
        for (Local l : locais)
            if (l.getNome().toLowerCase().contains(termo)) resultados.add(l);
        for (Objeto o : objetos)
            if (o.getNome().toLowerCase().contains(termo)) resultados.add(o);

        if (resultados.isEmpty()) {
            Alert alerta = new Alert(Alert.AlertType.INFORMATION, "No results found.");
            alerta.setHeaderText(null);
            alerta.show();
            return;
        }

        ChoiceDialog<Object> dialog = new ChoiceDialog<>(resultados.get(0), resultados);
        dialog.setTitle("Search Results");
        dialog.setHeaderText("Select an element to explore:");
        dialog.setContentText("Matches:");

        dialog.showAndWait().ifPresent(item -> mostrarRelacoes(treeView, item));
    }

    private void criarElemento(String tipo, TreeView<Object> treeView) throws SQLException {
        switch (tipo) {
            case "Event" -> {
                Evento novoEvento = new Evento("New Event", "Event description");
                novoEvento.setCampanhaid(c.getId());
                editarEvento(novoEvento);
                gerenciadorEvento.adicionar(novoEvento);
            }
            case "Character" -> {
                Personagem novoPersonagem = new Personagem("New Character");
                novoPersonagem.setCampanhaId(c.getId());
                editarElementoNarrativo(novoPersonagem);
                gerenciadorPersonagem.adicionar(novoPersonagem);
            }
            case "Location" -> {
                Local novoLocal = new Local("New Location");
                novoLocal.setCampanhaId(c.getId());
                editarElementoNarrativo(novoLocal);
                gerenciadorLocal.adicionar(novoLocal);
            }
            case "Object" -> {
                Objeto novoObjeto = new Objeto("New Object");
                novoObjeto.setCampanhaId(c.getId());
                editarElementoNarrativo(novoObjeto);
                gerenciadorObjeto.adicionar(novoObjeto);
            }
            default -> {
                System.err.println("Unknown type: " + tipo);
                return;
            }
        }
        recarregarDados();
        setTreeView(treeView, c, gerenciadorEvento, gerenciadorPersonagem, gerenciadorLocal, gerenciadorObjeto);
    }

    @FXML
    private void onCriarEvento() throws SQLException {
        criarElemento("Event", treeView);
    }

    @FXML
    private void onCriarPersonagem() throws SQLException {
        criarElemento("Character", treeView);
    }

    @FXML
    private void onCriarLocal() throws SQLException {
        criarElemento("Location", treeView);
    }

    @FXML
    private void onCriarObjeto() throws SQLException {
        criarElemento("Object", treeView);
    }

    @FXML
    private void onReturnToCampaigns() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/me/gui/TelaPrincipal.fxml"));
            Parent root = loader.load();
            Scene novaCena = new Scene(root, (int)treeView.getScene().getWidth(), (int)treeView.getScene().getHeight());
            Stage stage = (Stage) treeView.getScene().getWindow();
            stage.setScene(novaCena);
            stage.setTitle("Codex Core");
            stage.setResizable(true);
            stage.setMinWidth(640);
            stage.setMinHeight(480);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
