package me.gui.controladores;

import java.io.File;
import java.util.List;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import me.controle.GerenciadorCampanha;
import me.modelo.entidades.Campanha;
import me.modelo.exceptions.ElementoNaoEncontradoException;

public class ControladorTelaPrincipal {
    @FXML private AnchorPane root;
    @FXML private ImageView backgroundImage;
    @FXML private TextField campoNomeCampanha;
    @FXML private TextArea campoDescricaoCampanha;
    @FXML private Label labelMensagem;
    @FXML private Label labelTitle;
    @FXML private GridPane gridCampanhas;
    @FXML private VBox sidebar;

    private double larguraCard = 440;
    private double alturaCard = 270;

    private double bgFactorX = 1.25;
    private double bgFactorY = 0.75;
    private double fgFactorX = -10;
    private double fgFactorY = -5;

    private double maxImageOffsetX;
    private double maxImageOffsetY;

    private double targetOffsetX = 0;
    private double targetOffsetY = 0;

    @FXML private StackPane popupContainer;
    @FXML private VBox popupForm;

    @FXML private Label labelImagemSelecionada;

    private File imagemSelecionada = null;

    private final GerenciadorCampanha gerenciadorCampanha = new GerenciadorCampanha();

    @FXML
    public void initialize() {
        root.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                backgroundImage.fitWidthProperty().bind(newScene.widthProperty());
                backgroundImage.fitHeightProperty().bind(newScene.heightProperty());

                maxImageOffsetX = bgFactorX;
                maxImageOffsetY = bgFactorY;

                newScene.setOnMouseMoved(event -> {
                    double centerX = newScene.getWidth() / 2;
                    double centerY = newScene.getHeight() / 2;
                    targetOffsetX = (event.getSceneX() - centerX) / centerX;
                    targetOffsetY = (event.getSceneY() - centerY) / centerY;
                });

                startParallax();
            }
        });
        root.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double escala = newWidth.doubleValue() / 1920.0;
            labelTitle.setStyle("-fx-font-family:"+"Constantia"+"; -fx-font-size:" + (96 * escala) + "px;");
            larguraCard = 440 * escala;
            alturaCard = 270 * escala;
            carregarCampanhas();
        });
        gridCampanhas.prefWidthProperty().bind(root.widthProperty().subtract(100));
        gridCampanhas.prefHeightProperty().bind(root.heightProperty().subtract(200));
        backgroundImage.fitWidthProperty().bind(root.widthProperty());
        backgroundImage.fitHeightProperty().bind(root.heightProperty());
        sidebar.prefHeightProperty().bind(root.heightProperty());
    }

    private void startParallax() {
        Timeline parallaxAnim = new Timeline(new KeyFrame(Duration.millis(16), e -> {
            // Fundo
            double currentX = backgroundImage.getTranslateX();
            double currentY = backgroundImage.getTranslateY();

            double nextX = currentX + (targetOffsetX * bgFactorX - currentX) * 0.08;
            double nextY = currentY + (targetOffsetY * bgFactorY - currentY) * 0.08;

            double minTranslateX = -maxImageOffsetX * root.getWidth() / 2;
            double maxTranslateX = maxImageOffsetX * root.getWidth() / 2;
            double minTranslateY = -maxImageOffsetY * root.getHeight() / 2;
            double maxTranslateY = maxImageOffsetY * root.getHeight() / 2;

            nextX = Math.max(Math.min(nextX, maxTranslateX), minTranslateX);
            nextY = Math.max(Math.min(nextY, maxTranslateY), minTranslateY);

            backgroundImage.setTranslateX(nextX);
            backgroundImage.setTranslateY(nextY);

            // Campanhas (fg)
            double gridX = gridCampanhas.getTranslateX();
            double gridY = gridCampanhas.getTranslateY();

            double nextGridX = gridX + (targetOffsetX * fgFactorX - gridX) * 0.08;
            double nextGridY = gridY + (targetOffsetY * fgFactorY - gridY) * 0.08;
            gridCampanhas.setTranslateX(nextGridX);
            gridCampanhas.setTranslateY(nextGridY);

            // Título
            labelTitle.setTranslateX(labelTitle.getTranslateX() + (targetOffsetX * -10 - labelTitle.getTranslateX()) * 0.08);
            labelTitle.setTranslateY(labelTitle.getTranslateY() + (targetOffsetY * -5 - labelTitle.getTranslateY()) * 0.08);
        }));
        parallaxAnim.setCycleCount(Animation.INDEFINITE);
        parallaxAnim.play();
    }

    private void carregarCampanhas() {
        gridCampanhas.getChildren().clear();
        List<Campanha> campanhas = gerenciadorCampanha.listarCampanhas();
        int MAX_COLUNAS = 3;
        int col = 0;
        int row = 0;

        if (campanhas != null && !campanhas.isEmpty()) {
            for (Campanha c : campanhas) {
                Node card = criarCartaoCampanha(c);
                gridCampanhas.add(card, col, row);

                col++;
                if (col >= MAX_COLUNAS) {
                    col = 0;
                    row++;
                }
            }
        }

        Node novoCard = criarCartaoNovaCampanha();
        gridCampanhas.add(novoCard, col, row);
    }

    private Node criarCartaoCampanha(Campanha c) {
        VBox cartao = new VBox(10);
        cartao.getStyleClass().add("campanha-card");
        cartao.setAlignment(Pos.CENTER);

        Image imagem;
        if (c.imageExists()) {
            imagem = new Image(new File(c.getImagePath()).toURI().toString());
        } else {
            imagem = new Image(getClass().getResource("/me/gui/images/nullPlaceholder.jpg").toExternalForm());
        }
        ImageView capa = new ImageView(imagem);
        capa.setFitWidth(larguraCard);
        capa.setFitHeight(alturaCard);
        capa.setPreserveRatio(false);

        Rectangle clip = new Rectangle(larguraCard, alturaCard);
        clip.setArcWidth(10);
        clip.setArcHeight(10);
        capa.setClip(clip);

        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        WritableImage image = capa.snapshot(parameters, null);
        capa.setClip(null);
        capa.setImage(image);

        Label nome = new Label(c.getNome());
        nome.getStyleClass().add("campanha-label");
        nome.setStyle("-fx-font-family:"+"Constantia"+";-fx-font-size:" + (24 * larguraCard / 440) + "px;");
        StackPane.setAlignment(nome, Pos.BOTTOM_CENTER);
        StackPane.setMargin(nome, new Insets(0, 0, 8, 0));

        cartao.setOnContextMenuRequested(e -> abrirMenuContextual(e, c));
        cartao.setMaxHeight(270);
        cartao.getChildren().addAll(capa, nome);
        return cartao;
    }

    private Node criarCartaoNovaCampanha() {
        StackPane novo = new StackPane();
        novo.getStyleClass().add("campanha-card");
        ImageView placeholder = new ImageView(new Image(getClass().getResource("/me/gui/images/createCampaignPlaceholder.jpg").toExternalForm()));
        placeholder.setFitWidth(larguraCard);
        placeholder.setFitHeight(alturaCard);
        placeholder.setPreserveRatio(false);
        Rectangle clip = new Rectangle(larguraCard, alturaCard);
        clip.setArcWidth(10);
        clip.setArcHeight(10);
        placeholder.setClip(clip);

        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        WritableImage image = placeholder.snapshot(parameters, null);
        placeholder.setClip(null);
        placeholder.setImage(image);

        double proporcaoBotao = 0.16;
        double tamanhoBotao = larguraCard * proporcaoBotao;
        double fontSize = tamanhoBotao * 0.75;

        Button mais = new Button("+");
        mais.setMinSize(tamanhoBotao, tamanhoBotao);
        mais.setMaxSize(tamanhoBotao, tamanhoBotao);
        mais.setPrefSize(tamanhoBotao, tamanhoBotao);
        mais.getStyleClass().add("plus-icon");
        mais.setStyle("-fx-font-size: " + fontSize + "px;");
        mais.setFocusTraversable(true);
        mais.setOnMouseClicked(this::mostrarPopupCriacao);

        StackPane.setAlignment(mais, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(mais, new Insets(0, 10, 10, 0));

        novo.getChildren().addAll(placeholder, mais);
        return novo;
    }

    private void abrirMenuContextual(ContextMenuEvent event, Campanha campanha) {
        ContextMenu menu = new ContextMenu();

        MenuItem editar = new MenuItem("Editar");
        editar.setOnAction(e -> {
            editar.setText("Editar " + campanha.getNome());
            // TODO: implementar edição
        });

        MenuItem excluir = new MenuItem("Excluir");
        excluir.setOnAction(e -> {
            try {
                gerenciadorCampanha.removerCapanha(campanha.getId());
                carregarCampanhas();
            } catch (ElementoNaoEncontradoException ex) {
                throw new RuntimeException(ex);
            }
        });

        menu.getItems().addAll(editar, excluir);
        menu.show((Node) event.getSource(), event.getScreenX(), event.getScreenY());
    }

    @FXML
    private void mostrarPopupCriacao(MouseEvent event) {
        double mouseX = event.getSceneX();
        double mouseY = event.getSceneY();

        popupContainer.setVisible(true);
        popupForm.setVisible(true);
        labelMensagem.setText("");
        campoNomeCampanha.clear();
        campoDescricaoCampanha.clear();
        labelImagemSelecionada.setText("Nenhuma imagem");
        imagemSelecionada = null;

        popupContainer.setLayoutX(mouseX);
        popupContainer.setLayoutY(mouseY);
    }


    @FXML
    private void fecharPopup() {
        popupContainer.setVisible(false);
    }

    @FXML
    private void selecionarImagem() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose an image");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imagens", "*.jpg", "*.png", "*.jpeg"));
        File file = fileChooser.showOpenDialog(popupContainer.getScene().getWindow());
        if (file != null) {
            imagemSelecionada = file;
            labelImagemSelecionada.setText(file.getName());
        }
    }

    @FXML
    private void confirmarCriacao() {
        String nome = campoNomeCampanha.getText().trim();
        String descricao = campoDescricaoCampanha.getText().trim();

        if (nome.isEmpty() || descricao.isEmpty()) {
            labelMensagem.setText("All fields must be filled.");
            return;
        }

        Campanha nova = new Campanha(nome, descricao);
        if (imagemSelecionada != null) {
            nova.setCaminhoImagem(imagemSelecionada.getAbsolutePath());
        }

        gerenciadorCampanha.criarCampanha(nova.getNome(), nova.getDescricao());
        gridCampanhas.getChildren().add(criarCartaoCampanha(nova));
        fecharPopup();
        carregarCampanhas();
    }
}
