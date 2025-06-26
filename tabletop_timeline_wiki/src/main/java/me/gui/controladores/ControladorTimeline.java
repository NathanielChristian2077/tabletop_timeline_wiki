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
import java.util.function.BiConsumer;

public class ControladorTimeline {
    @FXML
    private TreeView<Object> treeView;
    @FXML
    private TextField searchField;
    @FXML
    private HBox navButtonsBox;
    @FXML
    private AnchorPane graphPane;
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
        gerenciadorEvento = new GerenciadorEvento();
        gerenciadorPersonagem = new GerenciadorPersonagem();
        gerenciadorLocal = new GerenciadorLocal();
        gerenciadorObjeto = new GerenciadorObjeto();
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
            mostrarErro("Erro ao recarregar dados: " + e.getMessage());
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

        for (TreeItem<Object> child : root.getChildren()) {
            child.setExpanded(true);
        }

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

    private <T> TreeItem<Object> criarGrupoRelacionados(String nome, List<T> relacionados) {
        TreeItem<Object> grupo = new TreeItem<>(nome);
        grupo.setExpanded(false);
        for (T item : relacionados) {
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
                } else {
                    setText(item.toString());
                }
            }
        });
    }

    private void configSelec(TreeView<Object> treeView) {
        treeView.setOnMouseClicked(event -> {
            TreeItem<Object> selecionado = treeView.getSelectionModel().getSelectedItem();
            if (selecionado == null)
                return;
            Object valor = selecionado.getValue();

            if (valor != null && rootCampanha != null && valor.equals(rootCampanha.getValue())) {
                return;
            }

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
                    Object prev = historicoRoots.pop();
                    mostrarRelacoes(treeView, prev);
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
                for (TreeItem<Object> child : rootCampanha.getChildren()) {
                    child.setExpanded(true);
                }
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

            newRoot.getChildren().add(criarGrupoRelacionados("Characters", e.getPersonagensRelacionados()));
            newRoot.getChildren().add(criarGrupoRelacionados("Locations", e.getLocaisRelacionados()));
            newRoot.getChildren().add(criarGrupoRelacionados("Objects", e.getObjetosRelacionados()));

        } else if (elemento instanceof ElementoNarrativo en) {
            newRoot.getChildren().add(criarGrupoRelacionados("Characters", en.getPersonagensRelacionados()));
            newRoot.getChildren().add(criarGrupoRelacionados("Locations", en.getLocaisRelacionados()));
            newRoot.getChildren().add(criarGrupoRelacionados("Objects", en.getObjetosRelacionados()));
            newRoot.getChildren().add(criarGrupoRelacionados("Events", en.getEventosRelacionados()));
        } else if (elemento instanceof @SuppressWarnings("unused") String grupo) {
            // Grupo textual, não faz nada especial
        }

        for (TreeItem<Object> child : newRoot.getChildren()) {
            child.setExpanded(true);
        }

        treeView.setRoot(newRoot);
    }

    private ContextMenu criarMenuContextual(Object elemento, TreeView<Object> treeView) {
        if (elemento == null)
            return null;

        boolean isRootCampanha = rootCampanha != null && elemento.equals(rootCampanha.getValue());
        if (isRootCampanha)
            return null;

        Menu adicionarRelacao = new Menu("Adicionar relação");

        if (elemento instanceof Evento) {
            MenuItem relEvento = new MenuItem("Evento");
            relEvento.setOnAction(e -> adicionarRelacao(elemento, "Evento"));
            MenuItem relPersonagem = new MenuItem("Personagem");
            relPersonagem.setOnAction(e -> adicionarRelacao(elemento, "Personagem"));
            MenuItem relLocal = new MenuItem("Local");
            relLocal.setOnAction(e -> adicionarRelacao(elemento, "Local"));
            MenuItem relObjeto = new MenuItem("Objeto");
            relObjeto.setOnAction(e -> adicionarRelacao(elemento, "Objeto"));
            adicionarRelacao.getItems().addAll(relEvento, relPersonagem, relLocal, relObjeto);
        } else if (elemento instanceof Personagem) {
            MenuItem relEvento = new MenuItem("Evento");
            relEvento.setOnAction(e -> adicionarRelacao(elemento, "Evento"));
            MenuItem relLocal = new MenuItem("Local");
            relLocal.setOnAction(e -> adicionarRelacao(elemento, "Local"));
            MenuItem relObjeto = new MenuItem("Objeto");
            relObjeto.setOnAction(e -> adicionarRelacao(elemento, "Objeto"));
            MenuItem relPersonagem = new MenuItem("Personagem");
            relPersonagem.setOnAction(e -> adicionarRelacao(elemento, "Personagem"));
            adicionarRelacao.getItems().addAll(relEvento, relLocal, relObjeto, relPersonagem);
        } else if (elemento instanceof Local) {
            MenuItem relEvento = new MenuItem("Evento");
            relEvento.setOnAction(e -> adicionarRelacao(elemento, "Evento"));
            MenuItem relPersonagem = new MenuItem("Personagem");
            relPersonagem.setOnAction(e -> adicionarRelacao(elemento, "Personagem"));
            MenuItem relObjeto = new MenuItem("Objeto");
            relObjeto.setOnAction(e -> adicionarRelacao(elemento, "Objeto"));
            MenuItem relLocal = new MenuItem("Local");
            relLocal.setOnAction(e -> adicionarRelacao(elemento, "Local"));
            adicionarRelacao.getItems().addAll(relEvento, relPersonagem, relObjeto, relLocal);
        } else if (elemento instanceof Objeto) {
            MenuItem relEvento = new MenuItem("Evento");
            relEvento.setOnAction(e -> adicionarRelacao(elemento, "Evento"));
            MenuItem relPersonagem = new MenuItem("Personagem");
            relPersonagem.setOnAction(e -> adicionarRelacao(elemento, "Personagem"));
            MenuItem relLocal = new MenuItem("Local");
            relLocal.setOnAction(e -> adicionarRelacao(elemento, "Local"));
            MenuItem relObjeto = new MenuItem("Objeto");
            relObjeto.setOnAction(e -> adicionarRelacao(elemento, "Objeto"));
            adicionarRelacao.getItems().addAll(relEvento, relPersonagem, relLocal, relObjeto);
        }

        MenuItem editar = new MenuItem("Editar");
        editar.setOnAction(e -> editarElemento(elemento));

        MenuItem remover = new MenuItem("Remover");
        remover.setOnAction(e -> {
            try {
                removerElemento(elemento, treeView);
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Erro ao remover: " + ex.getMessage());
                alert.showAndWait();
            }
        });

        MenuItem descricao = new MenuItem("Descrição");
        descricao.setOnAction(e -> mostrarDescricao(elemento));

        return new ContextMenu(descricao, adicionarRelacao, editar, remover);
    }

    private void mostrarDescricao(Object elemento) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/me/gui/timeline.css").toExternalForm());
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
        if (elemento == null || tipo == null)
            return;

        // Personagem <-> Personagem
        if (elemento instanceof Personagem p && tipo.equals("Personagem")) {
            List<Personagem> disponiveis = new ArrayList<>();
            for (Personagem outro : personagens) {
                if (!outro.getId().equals(p.getId()) && !p.getPersonagensRelacionados().contains(outro)) {
                    disponiveis.add(outro);
                }
            }
            if (disponiveis.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Não há personagens disponíveis para relacionar.");
                alert.showAndWait();
                return;
            }
            ChoiceDialog<Personagem> dialog = new ChoiceDialog<>(disponiveis.get(0), disponiveis);
            dialog.setTitle("Relacionar Personagem");
            dialog.setHeaderText("Escolha um personagem para relacionar:");
            dialog.getDialogPane().getStyleClass().add("dialog-pane");
            dialog.getDialogPane().getStylesheets()
                    .add(getClass().getResource("/me/gui/timeline.css").toExternalForm());
            dialog.showAndWait().ifPresent(personagemSelecionado -> {
                try {
                    gerenciadorPersonagem.adicionarRelacaoPersonagemPersonagem(p.getId(), personagemSelecionado.getId());
                } catch (Exception ex) {
                    mostrarErro("Erro ao salvar relação: " + ex.getMessage());
                }
                recarregarDados();
                setTreeView(treeView, c, gerenciadorEvento, gerenciadorPersonagem, gerenciadorLocal, gerenciadorObjeto);
                mostrarRelacoes(treeView, p);
            });
            return;
        }

        // Evento <-> Evento
        if (elemento instanceof Evento e && tipo.equals("Evento")) {
            List<Evento> disponiveis = new ArrayList<>();
            for (Evento outro : eventos) {
                if (!outro.getId().equals(e.getId()) && !e.getEventosRelacionados().contains(outro)) {
                    disponiveis.add(outro);
                }
            }
            if (disponiveis.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Não há eventos disponíveis para relacionar.");
                alert.showAndWait();
                return;
            }
            ChoiceDialog<Evento> dialog = new ChoiceDialog<>(disponiveis.get(0), disponiveis);
            dialog.setTitle("Relacionar Evento");
            dialog.setHeaderText("Escolha um evento para relacionar:");
            dialog.getDialogPane().getStyleClass().add("dialog-pane");
            dialog.getDialogPane().getStylesheets()
                    .add(getClass().getResource("/me/gui/timeline.css").toExternalForm());
            dialog.showAndWait().ifPresent(eventoSelecionado -> {
                try {
                    gerenciadorEvento.adicionarRelacaoEventoEvento(e.getId(), eventoSelecionado.getId());
                } catch (Exception ex) {
                    mostrarErro("Erro ao salvar relação: " + ex.getMessage());
                }
                recarregarDados();
                setTreeView(treeView, c, gerenciadorEvento, gerenciadorPersonagem, gerenciadorLocal, gerenciadorObjeto);
                mostrarRelacoes(treeView, e);
            });
            return;
        }

        // Local <-> Local
        if (elemento instanceof Local l && tipo.equals("Local")) {
            List<Local> disponiveis = new ArrayList<>();
            for (Local outro : locais) {
                if (!outro.getId().equals(l.getId()) && !l.getLocaisRelacionados().contains(outro)) {
                    disponiveis.add(outro);
                }
            }
            if (disponiveis.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Não há locais disponíveis para relacionar.");
                alert.showAndWait();
                return;
            }
            ChoiceDialog<Local> dialog = new ChoiceDialog<>(disponiveis.get(0), disponiveis);
            dialog.setTitle("Relacionar Local");
            dialog.setHeaderText("Escolha um local para relacionar:");
            dialog.getDialogPane().getStyleClass().add("dialog-pane");
            dialog.getDialogPane().getStylesheets()
                    .add(getClass().getResource("/me/gui/timeline.css").toExternalForm());
            dialog.showAndWait().ifPresent(localSelecionado -> {
                try {
                    gerenciadorLocal.adicionarRelacaoLocal(l.getId(), localSelecionado.getId());
                } catch (Exception ex) {
                    mostrarErro("Erro ao salvar relação: " + ex.getMessage());
                }
                recarregarDados();
                setTreeView(treeView, c, gerenciadorEvento, gerenciadorPersonagem, gerenciadorLocal, gerenciadorObjeto);
                mostrarRelacoes(treeView, l);
            });
            return;
        }

        // Objeto <-> Objeto
        if (elemento instanceof Objeto o && tipo.equals("Objeto")) {
            List<Objeto> disponiveis = new ArrayList<>();
            for (Objeto outro : objetos) {
                if (!outro.getId().equals(o.getId()) && !o.getObjetosRelacionados().contains(outro)) {
                    disponiveis.add(outro);
                }
            }
            if (disponiveis.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Não há objetos disponíveis para relacionar.");
                alert.showAndWait();
                return;
            }
            ChoiceDialog<Objeto> dialog = new ChoiceDialog<>(disponiveis.get(0), disponiveis);
            dialog.setTitle("Relacionar Objeto");
            dialog.setHeaderText("Escolha um objeto para relacionar:");
            dialog.getDialogPane().getStyleClass().add("dialog-pane");
            dialog.getDialogPane().getStylesheets()
                    .add(getClass().getResource("/me/gui/timeline.css").toExternalForm());
            dialog.showAndWait().ifPresent(objetoSelecionado -> {
                try {
                    gerenciadorObjeto.adicionarRelacaoObjetoObjeto(o.getId(), objetoSelecionado.getId());
                } catch (Exception ex) {
                    mostrarErro("Erro ao salvar relação: " + ex.getMessage());
                }
                recarregarDados();
                setTreeView(treeView, c, gerenciadorEvento, gerenciadorPersonagem, gerenciadorLocal, gerenciadorObjeto);
                mostrarRelacoes(treeView, o);
            });
            return;
        }

        // Relações cruzadas
        if (elemento instanceof Evento e) {
            if (tipo.equals("Personagem")) {
                List<Personagem> disponiveis = new ArrayList<>();
                for (Personagem p : personagens) {
                    if (!e.getPersonagensRelacionados().contains(p)) {
                        disponiveis.add(p);
                    }
                }
                if (disponiveis.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Não há personagens disponíveis para relacionar.");
                    alert.getDialogPane().getStylesheets().add(getClass().getResource("/me/gui/timeline.css").toExternalForm());
                    alert.showAndWait();
                    return;
                }
                ChoiceDialog<Personagem> dialog = new ChoiceDialog<>(disponiveis.get(0), disponiveis);
                dialog.setTitle("Relacionar Personagem");
                dialog.setHeaderText("Escolha um personagem para relacionar:");
                dialog.getDialogPane().getStyleClass().add("dialog-pane");
                dialog.getDialogPane().getStylesheets()
                        .add(getClass().getResource("/me/gui/timeline.css").toExternalForm());
                dialog.showAndWait().ifPresent(personagemSelecionado -> {
                    try {
                        gerenciadorEvento.adicionarRelacaoEventoPersonagem(e.getId(), personagemSelecionado.getId());
                    } catch (Exception ex) {
                        mostrarErro("Erro ao salvar relação: " + ex.getMessage());
                    }
                    recarregarDados();
                    setTreeView(treeView, c, gerenciadorEvento, gerenciadorPersonagem, gerenciadorLocal, gerenciadorObjeto);
                    mostrarRelacoes(treeView, e);
                });
                return;
            }
            if (tipo.equals("Local")) {
                List<Local> disponiveis = new ArrayList<>();
                for (Local l : locais) {
                    if (!e.getLocaisRelacionados().contains(l)) {
                        disponiveis.add(l);
                    }
                }
                if (disponiveis.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Não há locais disponíveis para relacionar.");
                    alert.getDialogPane().getStylesheets().add(getClass().getResource("/me/gui/timeline.css").toExternalForm());
                    alert.showAndWait();
                    return;
                }
                ChoiceDialog<Local> dialog = new ChoiceDialog<>(disponiveis.get(0), disponiveis);
                dialog.setTitle("Relacionar Local");
                dialog.setHeaderText("Escolha um local para relacionar:");
                dialog.getDialogPane().getStyleClass().add("dialog-pane");
                dialog.getDialogPane().getStylesheets()
                        .add(getClass().getResource("/me/gui/timeline.css").toExternalForm());
                dialog.showAndWait().ifPresent(localSelecionado -> {
                    try {
                        gerenciadorEvento.adicionarRelacaoEventoLocal(e.getId(), localSelecionado.getId());
                    } catch (Exception ex) {
                        mostrarErro("Erro ao salvar relação: " + ex.getMessage());
                    }
                    recarregarDados();
                    setTreeView(treeView, c, gerenciadorEvento, gerenciadorPersonagem, gerenciadorLocal, gerenciadorObjeto);
                    mostrarRelacoes(treeView, e);
                });
                return;
            }
            if (tipo.equals("Objeto")) {
                List<Objeto> disponiveis = new ArrayList<>();
                for (Objeto o : objetos) {
                    if (!e.getObjetosRelacionados().contains(o)) {
                        disponiveis.add(o);
                    }
                }
                if (disponiveis.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Não há objetos disponíveis para relacionar.");
                    alert.getDialogPane().getStylesheets().add(getClass().getResource("/me/gui/timeline.css").toExternalForm());
                    alert.showAndWait();
                    return;
                }
                ChoiceDialog<Objeto> dialog = new ChoiceDialog<>(disponiveis.get(0), disponiveis);
                dialog.setTitle("Relacionar Objeto");
                dialog.setHeaderText("Escolha um objeto para relacionar:");
                dialog.getDialogPane().getStyleClass().add("dialog-pane");
                dialog.getDialogPane().getStylesheets()
                        .add(getClass().getResource("/me/gui/timeline.css").toExternalForm());
                dialog.showAndWait().ifPresent(objetoSelecionado -> {
                    try {
                        gerenciadorEvento.adicionarRelacaoEventoObjeto(e.getId(), objetoSelecionado.getId());
                    } catch (Exception ex) {
                        mostrarErro("Erro ao salvar relação: " + ex.getMessage());
                    }
                    recarregarDados();
                    setTreeView(treeView, c, gerenciadorEvento, gerenciadorPersonagem, gerenciadorLocal, gerenciadorObjeto);
                    mostrarRelacoes(treeView, e);
                });
                return;
            }
        }

        if (elemento instanceof Personagem p) {
            if (tipo.equals("Evento")) {
                List<Evento> disponiveis = new ArrayList<>();
                for (Evento e : eventos) {
                    if (!p.getEventosRelacionados().contains(e)) {
                        disponiveis.add(e);
                    }
                }
                if (disponiveis.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Não há eventos disponíveis para relacionar.");
                    alert.getDialogPane().getStylesheets().add(getClass().getResource("/me/gui/timeline.css").toExternalForm());
                    alert.showAndWait();
                    return;
                }
                ChoiceDialog<Evento> dialog = new ChoiceDialog<>(disponiveis.get(0), disponiveis);
                dialog.setTitle("Relacionar Evento");
                dialog.setHeaderText("Escolha um evento para relacionar:");
                dialog.getDialogPane().getStyleClass().add("dialog-pane");
                dialog.getDialogPane().getStylesheets()
                        .add(getClass().getResource("/me/gui/timeline.css").toExternalForm());
                dialog.showAndWait().ifPresent(eventoSelecionado -> {
                    try {
                        gerenciadorPersonagem.adicionarRelacaoPersonagemEvento(p.getId(), eventoSelecionado.getId());
                    } catch (Exception ex) {
                        mostrarErro("Erro ao salvar relação: " + ex.getMessage());
                    }
                    recarregarDados();
                    setTreeView(treeView, c, gerenciadorEvento, gerenciadorPersonagem, gerenciadorLocal, gerenciadorObjeto);
                    mostrarRelacoes(treeView, p);
                });
                return;
            }
            if (tipo.equals("Local")) {
                List<Local> disponiveis = new ArrayList<>();
                for (Local l : locais) {
                    if (!p.getLocaisRelacionados().contains(l)) {
                        disponiveis.add(l);
                    }
                }
                if (disponiveis.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Não há locais disponíveis para relacionar.");
                    alert.showAndWait();
                    return;
                }
                ChoiceDialog<Local> dialog = new ChoiceDialog<>(disponiveis.get(0), disponiveis);
                dialog.setTitle("Relacionar Local");
                dialog.setHeaderText("Escolha um local para relacionar:");
                dialog.getDialogPane().getStyleClass().add("dialog-pane");
                dialog.getDialogPane().getStylesheets()
                        .add(getClass().getResource("/me/gui/timeline.css").toExternalForm());
                dialog.showAndWait().ifPresent(localSelecionado -> {
                    try {
                        gerenciadorPersonagem.adicionarRelacaoPersonagemLocal(p.getId(), localSelecionado.getId());
                    } catch (Exception ex) {
                        mostrarErro("Erro ao salvar relação: " + ex.getMessage());
                    }
                    recarregarDados();
                    setTreeView(treeView, c, gerenciadorEvento, gerenciadorPersonagem, gerenciadorLocal, gerenciadorObjeto);
                    mostrarRelacoes(treeView, p);
                });
                return;
            }
            if (tipo.equals("Objeto")) {
                List<Objeto> disponiveis = new ArrayList<>();
                for (Objeto o : objetos) {
                    if (!p.getObjetosRelacionados().contains(o)) {
                        disponiveis.add(o);
                    }
                }
                if (disponiveis.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Não há objetos disponíveis para relacionar.");
                    alert.getDialogPane().getStylesheets().add(getClass().getResource("/me/gui/timeline.css").toExternalForm());
                    alert.showAndWait();
                    return;
                }
                ChoiceDialog<Objeto> dialog = new ChoiceDialog<>(disponiveis.get(0), disponiveis);
                dialog.setTitle("Relacionar Objeto");
                dialog.setHeaderText("Escolha um objeto para relacionar:");
                dialog.getDialogPane().getStyleClass().add("dialog-pane");
                dialog.getDialogPane().getStylesheets()
                        .add(getClass().getResource("/me/gui/timeline.css").toExternalForm());
                dialog.showAndWait().ifPresent(objetoSelecionado -> {
                    try {
                        gerenciadorPersonagem.adicionarRelacaoPersonagemObjeto(p.getId(), objetoSelecionado.getId());
                    } catch (Exception ex) {
                        mostrarErro("Erro ao salvar relação: " + ex.getMessage());
                    }
                    recarregarDados();
                    setTreeView(treeView, c, gerenciadorEvento, gerenciadorPersonagem, gerenciadorLocal, gerenciadorObjeto);
                    mostrarRelacoes(treeView, p);
                });
                return;
            }
        }

        if (elemento instanceof Local l) {
            if (tipo.equals("Evento")) {
                List<Evento> disponiveis = new ArrayList<>();
                for (Evento e : eventos) {
                    if (!l.getEventosRelacionados().contains(e)) {
                        disponiveis.add(e);
                    }
                }
                if (disponiveis.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Não há eventos disponíveis para relacionar.");
                    alert.showAndWait();
                    return;
                }
                ChoiceDialog<Evento> dialog = new ChoiceDialog<>(disponiveis.get(0), disponiveis);
                dialog.setTitle("Relacionar Evento");
                dialog.setHeaderText("Escolha um evento para relacionar:");
                dialog.getDialogPane().getStyleClass().add("dialog-pane");
                dialog.getDialogPane().getStylesheets()
                        .add(getClass().getResource("/me/gui/timeline.css").toExternalForm());
                dialog.showAndWait().ifPresent(eventoSelecionado -> {
                    try {
                        gerenciadorLocal.adicionarRelacaoEvento(l.getId(), eventoSelecionado.getId());
                    } catch (Exception ex) {
                        mostrarErro("Erro ao salvar relação: " + ex.getMessage());
                    }
                    recarregarDados();
                    setTreeView(treeView, c, gerenciadorEvento, gerenciadorPersonagem, gerenciadorLocal, gerenciadorObjeto);
                    mostrarRelacoes(treeView, l);
                });
                return;
            }
            if (tipo.equals("Personagem")) {
                List<Personagem> disponiveis = new ArrayList<>();
                for (Personagem p : personagens) {
                    if (!l.getPersonagensRelacionados().contains(p)) {
                        disponiveis.add(p);
                    }
                }
                if (disponiveis.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Não há personagens disponíveis para relacionar.");
                    alert.getDialogPane().getStylesheets().add(getClass().getResource("/me/gui/timeline.css").toExternalForm());
                    alert.showAndWait();
                    return;
                }
                ChoiceDialog<Personagem> dialog = new ChoiceDialog<>(disponiveis.get(0), disponiveis);
                dialog.setTitle("Relacionar Personagem");
                dialog.setHeaderText("Escolha um personagem para relacionar:");
                dialog.getDialogPane().getStyleClass().add("dialog-pane");
                dialog.getDialogPane().getStylesheets()
                        .add(getClass().getResource("/me/gui/timeline.css").toExternalForm());
                dialog.showAndWait().ifPresent(personagemSelecionado -> {
                    try {
                        gerenciadorLocal.adicionarRelacaoPersonagem(l.getId(), personagemSelecionado.getId());
                    } catch (Exception ex) {
                        mostrarErro("Erro ao salvar relação: " + ex.getMessage());
                    }
                    recarregarDados();
                    setTreeView(treeView, c, gerenciadorEvento, gerenciadorPersonagem, gerenciadorLocal, gerenciadorObjeto);
                    mostrarRelacoes(treeView, l);
                });
                return;
            }
            if (tipo.equals("Objeto")) {
                List<Objeto> disponiveis = new ArrayList<>();
                for (Objeto o : objetos) {
                    if (!l.getObjetosRelacionados().contains(o)) {
                        disponiveis.add(o);
                    }
                }
                if (disponiveis.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Não há objetos disponíveis para relacionar.");
                    alert.getDialogPane().getStylesheets().add(getClass().getResource("/me/gui/timeline.css").toExternalForm());
                    alert.showAndWait();
                    return;
                }
                ChoiceDialog<Objeto> dialog = new ChoiceDialog<>(disponiveis.get(0), disponiveis);
                dialog.setTitle("Relacionar Objeto");
                dialog.setHeaderText("Escolha um objeto para relacionar:");
                dialog.getDialogPane().getStyleClass().add("dialog-pane");
                dialog.getDialogPane().getStylesheets()
                        .add(getClass().getResource("/me/gui/timeline.css").toExternalForm());
                dialog.showAndWait().ifPresent(objetoSelecionado -> {
                    try {
                        gerenciadorLocal.adicionarRelacaoObjeto(l.getId(), objetoSelecionado.getId());
                    } catch (Exception ex) {
                        mostrarErro("Erro ao salvar relação: " + ex.getMessage());
                    }
                    recarregarDados();
                    setTreeView(treeView, c, gerenciadorEvento, gerenciadorPersonagem, gerenciadorLocal, gerenciadorObjeto);
                    mostrarRelacoes(treeView, l);
                });
                return;
            }
        }

        if (elemento instanceof Objeto o) {
            if (tipo.equals("Evento")) {
                List<Evento> disponiveis = new ArrayList<>();
                for (Evento e : eventos) {
                    if (!o.getEventosRelacionados().contains(e)) {
                        disponiveis.add(e);
                    }
                }
                if (disponiveis.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Não há eventos disponíveis para relacionar.");
                    alert.getDialogPane().getStylesheets().add(getClass().getResource("/me/gui/timeline.css").toExternalForm());
                    alert.showAndWait();
                    return;
                }
                ChoiceDialog<Evento> dialog = new ChoiceDialog<>(disponiveis.get(0), disponiveis);
                dialog.setTitle("Relacionar Evento");
                dialog.setHeaderText("Escolha um evento para relacionar:");
                dialog.getDialogPane().getStyleClass().add("dialog-pane");
                dialog.getDialogPane().getStylesheets()
                        .add(getClass().getResource("/me/gui/timeline.css").toExternalForm());
                dialog.showAndWait().ifPresent(eventoSelecionado -> {
                    try {
                        gerenciadorObjeto.adicionarRelacaoObjetoEvento(o.getId(), eventoSelecionado.getId());
                    } catch (Exception ex) {
                        mostrarErro("Erro ao salvar relação: " + ex.getMessage());
                    }
                    recarregarDados();
                    setTreeView(treeView, c, gerenciadorEvento, gerenciadorPersonagem, gerenciadorLocal, gerenciadorObjeto);
                    mostrarRelacoes(treeView, o);
                });
                return;
            }
            if (tipo.equals("Personagem")) {
                List<Personagem> disponiveis = new ArrayList<>();
                for (Personagem p : personagens) {
                    if (!o.getPersonagensRelacionados().contains(p)) {
                        disponiveis.add(p);
                    }
                }
                if (disponiveis.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION,
                            "Não há personagens disponíveis para relacionar.");
                    alert.getDialogPane().getStylesheets().add(getClass().getResource("/me/gui/timeline.css").toExternalForm());
                    alert.showAndWait();
                    return;
                }
                ChoiceDialog<Personagem> dialog = new ChoiceDialog<>(disponiveis.get(0), disponiveis);
                dialog.setTitle("Relacionar Personagem");
                dialog.setHeaderText("Escolha um personagem para relacionar:");
                dialog.getDialogPane().getStyleClass().add("dialog-pane");
                dialog.getDialogPane().getStylesheets()
                        .add(getClass().getResource("/me/gui/timeline.css").toExternalForm());
                dialog.showAndWait().ifPresent(personagemSelecionado -> {
                    try {
                        gerenciadorObjeto.adicionarRelacaoObjetoPersonagem(o.getId(), personagemSelecionado.getId());
                    } catch (Exception ex) {
                        mostrarErro("Erro ao salvar relação: " + ex.getMessage());
                    }
                    recarregarDados();
                    setTreeView(treeView, c, gerenciadorEvento, gerenciadorPersonagem, gerenciadorLocal, gerenciadorObjeto);
                    mostrarRelacoes(treeView, o);
                });
                return;
            }
            if (tipo.equals("Local")) {
                List<Local> disponiveis = new ArrayList<>();
                for (Local l : locais) {
                    if (!o.getLocaisRelacionados().contains(l)) {
                        disponiveis.add(l);
                    }
                }
                if (disponiveis.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Não há locais disponíveis para relacionar.");
                    alert.getDialogPane().getStylesheets().add(getClass().getResource("/me/gui/timeline.css").toExternalForm());
                    alert.showAndWait();
                    return;
                }
                ChoiceDialog<Local> dialog = new ChoiceDialog<>(disponiveis.get(0), disponiveis);
                dialog.setTitle("Relacionar Local");
                dialog.setHeaderText("Escolha um local para relacionar:");
                dialog.getDialogPane().getStyleClass().add("dialog-pane");
                dialog.getDialogPane().getStylesheets()
                        .add(getClass().getResource("/me/gui/timeline.css").toExternalForm());
                dialog.showAndWait().ifPresent(localSelecionado -> {
                    try {
                        gerenciadorObjeto.adicionarRelacaoObjetoLocal(o.getId(), localSelecionado.getId());
                    } catch (Exception ex) {
                        mostrarErro("Erro ao salvar relação: " + ex.getMessage());
                    }
                    recarregarDados();
                    setTreeView(treeView, c, gerenciadorEvento, gerenciadorPersonagem, gerenciadorLocal, gerenciadorObjeto);
                    mostrarRelacoes(treeView, o);
                });
                return;
            }
        }
    }

    private void editarElemento(Object elemento) {
        if (elemento instanceof Evento e) {
            editarEvento(e);
        } else if (elemento instanceof ElementoNarrativo en) {
            editarElementoNarrativo(en);
        }
        treeView.setRoot(rootCampanha);
    }

    private void editarElementoNarrativo(ElementoNarrativo en) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/me/gui/timeline.css").toExternalForm());
        dialog.setTitle("Edit " + en.getClass().getSimpleName());

        TextField nameField = new TextField(en.getNome());
        TextArea descriptionArea = new TextArea(en.getDescricao());

        VBox content = new VBox(10,
                new Label("Name:"), nameField,
                new Label("Description:"), descriptionArea);

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
                    mostrarErro("Erro ao atualizar: " + ex.getMessage());
                }
            }
            return null;
        });

        dialog.showAndWait();
        recarregarDados();
        setTreeView(treeView, c, gerenciadorEvento, gerenciadorPersonagem, gerenciadorLocal, gerenciadorObjeto);
    }

    @FXML
    private void onSearch() {
        String termo = searchField.getText().trim().toLowerCase();
        if (termo.isEmpty())
            return;

        List<Object> resultados = new ArrayList<>();

        for (Evento e : eventos)
            if (e.getNome().toLowerCase().contains(termo))
                resultados.add(e);
        for (Personagem p : personagens)
            if (p.getNome().toLowerCase().contains(termo))
                resultados.add(p);
        for (Local l : locais)
            if (l.getNome().toLowerCase().contains(termo))
                resultados.add(l);
        for (Objeto o : objetos)
            if (o.getNome().toLowerCase().contains(termo))
                resultados.add(o);

        if (resultados.isEmpty()) {
            Alert alerta = new Alert(Alert.AlertType.INFORMATION, "No results found.");
            alerta.getDialogPane().getStylesheets().add(getClass().getResource("/me/gui/timeline.css").toExternalForm());
            alerta.setHeaderText(null);
            alerta.show();
            return;
        }

        ChoiceDialog<Object> dialog = new ChoiceDialog<>(resultados.get(0), resultados);
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/me/gui/timeline.css").toExternalForm());
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
                gerenciadorEvento.criarEvento(novoEvento.getTitulo(), novoEvento.getDescricao(), novoEvento.getCampanhaid());
                recarregarDados();
                setTreeView(treeView, c, gerenciadorEvento, gerenciadorPersonagem, gerenciadorLocal, gerenciadorObjeto);
                editarEvento(novoEvento);
            }
            case "Character" -> {
                Personagem novoPersonagem = new Personagem("New Character");
                novoPersonagem.setCampanhaId(c.getId());
                gerenciadorPersonagem.salvarNoBanco(novoPersonagem);
                recarregarDados();
                setTreeView(treeView, c, gerenciadorEvento, gerenciadorPersonagem, gerenciadorLocal, gerenciadorObjeto);
                editarElementoNarrativo(novoPersonagem);
            }
            case "Location" -> {
                Local novoLocal = new Local("New Location");
                novoLocal.setCampanhaId(c.getId());
                gerenciadorLocal.salvarNoBanco(novoLocal);
                recarregarDados();
                setTreeView(treeView, c, gerenciadorEvento, gerenciadorPersonagem, gerenciadorLocal, gerenciadorObjeto);
                editarElementoNarrativo(novoLocal);
            }
            case "Object" -> {
                Objeto novoObjeto = new Objeto("New Object");
                novoObjeto.setCampanhaId(c.getId());
                gerenciadorObjeto.salvarNoBanco(novoObjeto);
                recarregarDados();
                setTreeView(treeView, c, gerenciadorEvento, gerenciadorPersonagem, gerenciadorLocal, gerenciadorObjeto);
                editarElementoNarrativo(novoObjeto);
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
            Scene novaCena = new Scene(root, (int) treeView.getScene().getWidth(),
                    (int) treeView.getScene().getHeight());
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

    private void mostrarErro(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR, mensagem);
        alert.showAndWait();
    }

    private void removerElemento(Object elemento, TreeView<Object> treeView) throws SQLException {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.getDialogPane().getStylesheets().add(getClass().getResource("/me/gui/timeline.css").toExternalForm());
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText("Are you sure you want to delete this element?");
        confirm.setContentText(elemento.toString());

        confirm.showAndWait().ifPresent(resposta -> {
            if (resposta != ButtonType.OK)
                return;

            try {
                if (elemento instanceof Evento e) {
                    // Remove todas as relações do evento
                    for (Personagem p : new ArrayList<>(e.getPersonagensRelacionados())) {
                        gerenciadorEvento.removerRelacaoEventoPersonagem(e.getId(), p.getId());
                    }
                    for (Local l : new ArrayList<>(e.getLocaisRelacionados())) {
                        gerenciadorEvento.removerRelacaoEventoLocal(e.getId(), l.getId());
                    }
                    for (Objeto o : new ArrayList<>(e.getObjetosRelacionados())) {
                        gerenciadorEvento.removerRelacaoEventoObjeto(e.getId(), o.getId());
                    }
                    for (Evento ev : new ArrayList<>(e.getEventosRelacionados())) {
                        gerenciadorEvento.removerRelacaoEventoEvento(e.getId(), ev.getId());
                    }
                    gerenciadorEvento.remover(e.getId());
                } else if (elemento instanceof Personagem p) {
                    for (Evento e : new ArrayList<>(p.getEventosRelacionados())) {
                        gerenciadorPersonagem.removerRelacaoPersonagemEvento(p.getId(), e.getId());
                    }
                    for (Local l : new ArrayList<>(p.getLocaisRelacionados())) {
                        gerenciadorPersonagem.removerRelacaoPersonagemLocal(p.getId(), l.getId());
                    }
                    for (Objeto o : new ArrayList<>(p.getObjetosRelacionados())) {
                        gerenciadorPersonagem.removerRelacaoPersonagemObjeto(p.getId(), o.getId());
                    }
                    for (Personagem outro : new ArrayList<>(p.getPersonagensRelacionados())) {
                        gerenciadorPersonagem.removerRelacaoPersonagemPersonagem(p.getId(), outro.getId());
                    }
                    gerenciadorPersonagem.deletarDoBanco(p.getId());
                } else if (elemento instanceof Local l) {
                    for (Evento e : new ArrayList<>(l.getEventosRelacionados())) {
                        gerenciadorLocal.removerRelacaoEvento(l.getId(), e.getId());
                    }
                    for (Personagem p : new ArrayList<>(l.getPersonagensRelacionados())) {
                        gerenciadorLocal.removerRelacaoPersonagem(l.getId(), p.getId());
                    }
                    for (Objeto o : new ArrayList<>(l.getObjetosRelacionados())) {
                        gerenciadorLocal.removerRelacaoObjeto(l.getId(), o.getId());
                    }
                    for (Local outro : new ArrayList<>(l.getLocaisRelacionados())) {
                        gerenciadorLocal.removerRelacaoLocal(l.getId(), outro.getId());
                    }
                    gerenciadorLocal.deletarDoBanco(l.getId());
                } else if (elemento instanceof Objeto o) {
                    for (Evento e : new ArrayList<>(o.getEventosRelacionados())) {
                        gerenciadorObjeto.removerRelacaoObjetoEvento(o.getId(), e.getId());
                    }
                    for (Personagem p : new ArrayList<>(o.getPersonagensRelacionados())) {
                        gerenciadorObjeto.removerRelacaoObjetoPersonagem(o.getId(), p.getId());
                    }
                    for (Local l : new ArrayList<>(o.getLocaisRelacionados())) {
                        gerenciadorObjeto.removerRelacaoObjetoLocal(o.getId(), l.getId());
                    }
                    for (Objeto outro : new ArrayList<>(o.getObjetosRelacionados())) {
                        gerenciadorObjeto.removerRelacaoObjetoObjeto(o.getId(), outro.getId());
                    }
                    gerenciadorObjeto.deletarDoBanco(o.getId());
                }
            } catch (Exception ex) {
                mostrarErro("Erro ao remover: " + ex.getMessage());
            }

            recarregarDados();
            setTreeView(treeView, c, gerenciadorEvento, gerenciadorPersonagem, gerenciadorLocal, gerenciadorObjeto);
        });
    }

    private void editarEvento(Evento evento) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/me/gui/timeline.css").toExternalForm());
        dialog.setTitle("Edit Event");
        TextField nameField = new TextField(evento.getNome());
        TextArea descriptionArea = new TextArea(evento.getDescricao());

        VBox relationsBox = new VBox(10,
            criarSeletorDeRelacoesGenerico("Personagens Relacionados", evento, evento.getPersonagensRelacionados(), personagens,
                (e, p) -> {
                    try {
                        gerenciadorEvento.adicionarRelacaoEventoPersonagem(e.getId(), p.getId());
                    } catch (SQLException ex) {
                        mostrarErro("Erro ao adicionar relação Evento-Personagem: " + ex.getMessage());
                    }
                },
                (e, p) -> {
                    try {
                        gerenciadorEvento.removerRelacaoEventoPersonagem(e.getId(), p.getId());
                    } catch (SQLException ex) {
                        mostrarErro("Erro ao remover relação Evento-Personagem: " + ex.getMessage());
                    }
                }
            ),
            criarSeletorDeRelacoesGenerico("Locais Relacionados", evento, evento.getLocaisRelacionados(), locais,
                (e, l) -> {
                    try {
                        gerenciadorEvento.adicionarRelacaoEventoLocal(e.getId(), l.getId());
                    } catch (SQLException ex) {
                        mostrarErro("Erro ao adicionar relação Evento-Local: " + ex.getMessage());
                    }
                },
                (e, l) -> {
                    try {
                        gerenciadorEvento.removerRelacaoEventoLocal(e.getId(), l.getId());
                    } catch (SQLException ex) {
                        mostrarErro("Erro ao remover relação Evento-Local: " + ex.getMessage());
                    }
                }
            ),
            criarSeletorDeRelacoesGenerico("Objetos Relacionados", evento, evento.getObjetosRelacionados(), objetos,
                (e, o) -> {
                    try {
                        gerenciadorEvento.adicionarRelacaoEventoObjeto(e.getId(), o.getId());
                    } catch (SQLException ex) {
                        mostrarErro("Erro ao adicionar relação Evento-Objeto: " + ex.getMessage());
                    }
                },
                (e, o) -> {
                    try {
                        gerenciadorEvento.removerRelacaoEventoObjeto(e.getId(), o.getId());
                    } catch (SQLException ex) {
                        mostrarErro("Erro ao remover relação Evento-Objeto: " + ex.getMessage());
                    }
                }
            ),
            criarSeletorDeRelacoesGenerico("Eventos Relacionados", evento, evento.getEventosRelacionados(), eventos,
                (e, ev2) -> {
                    try {
                        gerenciadorEvento.adicionarRelacaoEventoEvento(e.getId(), ev2.getId());
                    } catch (SQLException ex) {
                        mostrarErro("Erro ao adicionar relação Evento-Evento: " + ex.getMessage());
                    }
                },
                (e, ev2) -> {
                    try {
                        gerenciadorEvento.removerRelacaoEventoEvento(e.getId(), ev2.getId());
                    } catch (SQLException ex) {
                        mostrarErro("Erro ao remover relação Evento-Evento: " + ex.getMessage());
                    }
                }
            )
        );

        VBox content = new VBox(10, new Label("Name:"), nameField, new Label("Description:"), descriptionArea, new Label("Relations:"), relationsBox);
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
                    mostrarErro("Erro ao atualizar evento: " + ex.getMessage());
                }
            }
            return null;
        });
        dialog.showAndWait();
        recarregarDados();
        setTreeView(treeView, c, gerenciadorEvento, gerenciadorPersonagem, gerenciadorLocal, gerenciadorObjeto);
    }

    private <T, E> VBox criarSeletorDeRelacoesGenerico(
            String titulo,
            E elemento,
            List<T> relacionados,
            List<T> todosDisponiveis,
            BiConsumer<E, T> adicionar,
            BiConsumer<E, T> remover
    ) {
        Label label = new Label(titulo);
        FlowPane flow = new FlowPane(10, 10);

        for (T rel : relacionados) {
            HBox item = new HBox(5, new Label(rel.toString()), new Button("❌"));
            ((Button) item.getChildren().get(1)).setOnAction(e -> {
                remover.accept(elemento, rel);
                recarregarDados();
                setTreeView(treeView, c, gerenciadorEvento, gerenciadorPersonagem, gerenciadorLocal, gerenciadorObjeto);
                mostrarRelacoes(treeView, elemento);
            });
            flow.getChildren().add(item);
        }

        ComboBox<T> combo = new ComboBox<>();
        combo.getItems().addAll(todosDisponiveis);
        Button btnAdd = new Button("➕");
        btnAdd.setOnAction(e -> {
            T sel = combo.getValue();
            if (sel != null && !relacionados.contains(sel)) {
                adicionar.accept(elemento, sel);
                recarregarDados();
                setTreeView(treeView, c, gerenciadorEvento, gerenciadorPersonagem, gerenciadorLocal, gerenciadorObjeto);
                mostrarRelacoes(treeView, elemento);
            }
        });

        return new VBox(5, label, flow, new HBox(5, combo, btnAdd));
    }
}