package me.gui.controladores;

import java.io.File;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import me.controle.GerenciadorCampanha;
import me.modelo.entidades.Campanha;
import me.modelo.exceptions.ElementoNaoEncontradoException;

public class ControladorTelaPrincipal {
    @FXML private TextField campoNomeCampanha;
    @FXML private TextArea campoDescricaoCampanha;
    @FXML private Label labelMensagem;
    @FXML private ListView<Campanha> listaCampanhas;

    @FXML private FlowPane gridCampanhas;
    @FXML private Label labelSaudacao;

    private final GerenciadorCampanha gerenciadorCampanha = new GerenciadorCampanha();

    @FXML
    public void initialize() {
        labelSaudacao.setText("Bem-vindo!");
        carregarCampanhas();
    }

    private void carregarCampanhas() {
        gridCampanhas.getChildren().clear();
        List<Campanha> campanhas = gerenciadorCampanha.listarCampanhas();

        if (campanhas != null && !campanhas.isEmpty()) {
            for (Campanha c : listaCampanhas.getItems()) {
                gridCampanhas.getChildren().add(criarCartaoCampanha(c));
            }
        }
        gridCampanhas.getChildren().add(criarCartaoNovaCampanha());
    }

    private Node criarCartaoCampanha(Campanha c) {
        VBox cartao = new VBox();
        cartao.getStyleClass().add("campanha-card");

        ImageView capa = new ImageView();
        Image imagem;
        if (c.imageExists()) {
            imagem = new Image(new File(c.getImagePath()).toURI().toString());
        } else {
            imagem = new Image(getClass().getResource("/me/gui/images/placeholder.jpg").toExternalForm());
        }

        capa.setImage(imagem);
        capa.setFitWidth(180);
        capa.setFitHeight(120);
        capa.setPreserveRatio(true);
        capa.setSmooth(true);

        Label nome = new Label(c.getNome());

        cartao.setOnContextMenuRequested(e -> abrirMenuContextual(e, c));
        cartao.getChildren().addAll(capa, nome);
        return cartao;
    }

    private Node criarCartaoNovaCampanha() {
        StackPane novo = new StackPane();
        novo.getStyleClass().add("campanha-card");

        ImageView placeholder = new ImageView(new Image(getClass().getResource("/me/gui/images/placeholder.jpg").toExternalForm()));
        placeholder.setFitWidth(180);
        placeholder.setFitHeight(120);
        placeholder.setPreserveRatio(true);
        placeholder.setOpacity(0.3);

        Label mais = new Label("+");
        mais.getStyleClass().add("plus-icon");
        novo.getChildren().addAll(placeholder, mais);

        novo.setOnMouseClicked(e -> criarCampanha());
        return novo;
    }

    @FXML
    private void criarCampanha() {
        String nome = campoNomeCampanha.getText().trim();
        String descricao = campoDescricaoCampanha.getText().trim();

        if (nome.isEmpty() || descricao.isEmpty()) {
            labelMensagem.setText("Todos os campos devem estar preenchidos.");
            return;
        }

        Campanha nova =  new Campanha(nome,descricao);
        gerenciadorCampanha.criarCampanha(nova.getNome(), nova.getDescricao());
        campoNomeCampanha.clear();
        campoDescricaoCampanha.clear();
        atualizarListaCampanhas();
    }

    @FXML
    private void abrirCampanha() {
        Campanha selecionada = listaCampanhas.getSelectionModel().getSelectedItem();
        
        if (selecionada == null) {
            labelMensagem.setText("Nenhuma campanha selecionada.");
        }

        //TODO: Carregar interface da Campanha
    }

    private void atualizarListaCampanhas() {
        List<Campanha> campanhas = gerenciadorCampanha.listarCampanhas();
        if (campanhas == null || campanhas.isEmpty()) {
            listaCampanhas.getItems().clear();
        } else {
            listaCampanhas.getItems().setAll(campanhas);
        }
    }

    private void abrirMenuContextual(ContextMenuEvent event, Campanha campanha) {
        ContextMenu menu = new ContextMenu();

        MenuItem editar = new MenuItem("Editar");
        editar.setOnAction(e -> {
            // TODO: abrir tela de edição de campanha
            labelMensagem.setText("Editar " + campanha.getNome());
        });

        MenuItem excluir = new MenuItem("Excluir");
        excluir.setOnAction(e -> {
            try {
                gerenciadorCampanha.removerCapanha(campanha.getId());
                atualizarListaCampanhas();
            } catch (ElementoNaoEncontradoException ex) {
                throw new RuntimeException(ex);
            }
        });

        menu.getItems().addAll(editar, excluir);
        menu.show((Node) event.getSource(), event.getScreenX(), event.getScreenY());
    }
}
