package me.gui.controladores;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import me.controle.*;
import me.modelo.abstracts.ElementoNarrativo;
import me.modelo.entidades.*;
import me.modelo.interfaces.Associavel;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class ControladorTimeline {
    @FXML private TreeView<Object> treeView;
    @FXML private AnchorPane graphPane;
    private final Deque<Object> historicoRoots = new ArrayDeque<>();

    private GerenciadorEvento gerenciadorEvento = new GerenciadorEvento();
    private List<Evento> eventos = gerenciadorEvento.listarTodas();
    private GerenciadorPersonagem gerenciadorPersonagem = new GerenciadorPersonagem();
    private List<Personagem> personagens = gerenciadorPersonagem.listarTodos();
    private GerenciadorLocal gerenciadorLocal = new GerenciadorLocal();
    private List<Local> locais = gerenciadorLocal.listarTodos();
    private GerenciadorObjeto gerenciadorObjeto = new GerenciadorObjeto();
    private List<Objeto> objetos = gerenciadorObjeto.listarTodos();

    private Campanha c;

    public void setCampanha(Campanha c) {
        this.c = c;
    }

    @FXML
    public void initialize() {
        setTreeView(treeView, c, gerenciadorEvento, gerenciadorPersonagem, gerenciadorLocal, gerenciadorObjeto);
        // TODO: adicionar botao que retorna para root campanha base
    }

    private void setTreeView(TreeView<Object> treeView, Campanha c, GerenciadorEvento gerenciadorEvento, GerenciadorPersonagem gerenciadorPersonagem, GerenciadorLocal gerenciadorLocal, GerenciadorObjeto gerenciadorObjeto) {
        TreeItem<Object> root = new TreeItem<>(c.getNome());
        root.setExpanded(true);

        root.getChildren().add(criarGrupo("Events", eventos));
        root.getChildren().add(criarGrupo("Characters", personagens));
        root.getChildren().add(criarGrupo("Locations", locais));
        root.getChildren().add(criarGrupo("Objects", objetos));

        treeView.setRoot(root);

        configExib(treeView);
        configSelec(treeView);
    }

    private <T> TreeItem<Object> criarGrupo(String nome, List<T> itens) {
        TreeItem<Object> grupo = new TreeItem<>(nome);
        grupo.setExpanded(false);
        for(T item : itens) {
            grupo.getChildren().add(new TreeItem<>(item));
        }
        return grupo;
    }

    private void configExib(TreeView<Object> treeView) {
        treeView.setCellFactory(tv -> new TreeCell<>() {
            @Override
            protected void updateItem(Object  item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("");
                } else if (item instanceof Evento e) {
                    setText(e.getNome());
                } else if (item instanceof Personagem p) {
                    setText(p.getNome());
                } else if (item instanceof Local l) {
                    setText(l.getNome());
                } else if (item instanceof Objeto o) {
                    setText(o.getNome());
                } else {
                    setText(item.toString());
                }
            }
        });
    }

    private void configSelec(TreeView<Object> treeView) {
        treeView.setOnMouseClicked( event -> {
            TreeItem<Object> selecionado = treeView.getSelectionModel().getSelectedItem();
            Object valor = selecionado.getValue();

            if (event.getButton() == MouseButton.PRIMARY) {
                if (event.getClickCount() == 2) {
                    mostrarRelacoes(treeView, valor);
                } else if (event.getClickCount() == 1) {
                    //TODO: Highlight sem ação
                }
            } else if (event.getButton() == MouseButton.SECONDARY) {
                ContextMenu menu = criarMenuContextual(valor, treeView);
                if ( menu != null) {
                    menu.show(treeView, event.getScreenX(), event.getScreenY());
                }
            }
        });
    }

    private void mostrarRelacoes(TreeView<Object> treeView, Object elemento) {
        if (treeView.getRoot() != null) {
            historicoRoots.push(treeView.getRoot().getValue());
        }

        TreeItem<Object> newRoot = new TreeItem<>(elemento);
        newRoot.setExpanded(true);

        //Botao Voltar
        if (!historicoRoots.isEmpty()) {
            Button voltarButton = new Button("Back");
            voltarButton.setOnAction(e -> {
                Object anterior = historicoRoots.pop();
                mostrarRelacoes(treeView, anterior);
            });
            TreeItem<Object> voltarItem = new TreeItem<>(voltarButton);
            newRoot.getChildren().add(voltarItem);
        }

        if (elemento instanceof Evento e) {
            TreeItem<Object> anterior = new TreeItem<>("<< Previous: " + e.getAnterior().getNome());
            TreeItem<Object> proximo = new TreeItem<>("Previous to: " + e.getPosterior().getNome() + ">>");

            //TODO: anterior e próximo podem serão buttons ou hyperlinks -> implementar
            newRoot.getChildren().add(anterior);
            newRoot.getChildren().add(criarGrupo("Characters", e.getPersonagensRelacionados()));
            newRoot.getChildren().add(criarGrupo("Locations", e.getLocaisRelacionados()));
            newRoot.getChildren().add(criarGrupo("Objects", e.getObjetosRelacionados()));
            newRoot.getChildren().add(proximo);
        } else {
            TreeItem<Object> personagens = criarGrupo("Characters", ((ElementoNarrativo) elemento).getPersonagensRelacionados());
            TreeItem<Object> locais = criarGrupo("Locations", ((ElementoNarrativo) elemento).getLocaisRelacionados());
            TreeItem<Object> objetos = criarGrupo("Objects", ((ElementoNarrativo) elemento).getObjetosRelacionados());
            TreeItem<Object> eventos = criarGrupo("Events", ((ElementoNarrativo) elemento).getEventosRelacionados());

            newRoot.getChildren().addAll(personagens, locais, objetos, eventos);
        }

        treeView.setRoot(newRoot);
        //TODO: Implementar botão voltar
    }

    private ContextMenu criarMenuContextual(Object elemento, TreeView<Object> treeView) {
        if (elemento == null) return null;
        //TODO: implementar mostrarDescricao
        MenuItem descricao = new MenuItem("Descrition");
        descricao.setOnAction(e -> mostrarDescricao(elemento));

        Menu adicionarRelacao = new Menu("Add links");

        MenuItem relEvento = new MenuItem("Event");
        MenuItem relPersonagem = new MenuItem("Character");
        MenuItem relLugar = new MenuItem("Location");
        MenuItem relObject = new MenuItem("Object");

        //TODO: Implementar adicionarRelacao(Object, String), editarElemento(Object) e removerElemento(Object, TreeView<Object>)
        relEvento.setOnAction(e -> adicionarRelacao(elemento, "Event"));
        relPersonagem.setOnAction(e -> adicionarRelacao(elemento, "Character"));
        relLugar.setOnAction(e -> adicionarRelacao(elemento, "Location"));
        relObject.setOnAction(e -> adicionarRelacao(elemento, "Object"));

        adicionarRelacao.getItems().addAll(relEvento, relPersonagem, relLugar, relObject);

        MenuItem editar = new MenuItem("Edit");
        editar.setOnAction(e -> editarElemento(elemento));

        MenuItem remover = new MenuItem("Delete");
        remover.setOnAction(e -> removerElemento(elemento, treeView));

        return new ContextMenu(descricao, adicionarRelacao, editar, remover);
    }

    private void mostrarDescricao(Object elemento) {

    }

    private void adicionarRelacao(Object elemento, String tipo) {

    }

    private void removerElemento(Object elemento, TreeView<Object> treeView) {

    }

    private void editarElemento(Object elemento) {

    }

}
