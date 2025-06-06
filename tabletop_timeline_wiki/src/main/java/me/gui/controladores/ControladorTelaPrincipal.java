package me.gui.controladores;

import java.io.File;
import java.util.List;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
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
import me.controle.GerenciadorUsuario;
import me.modelo.entidades.Campanha;
import me.modelo.entidades.Usuario;
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

    private final double bgFactorX = 1.25;
    private final double bgFactorY = 0.75;
    private final double fgFactorX = -10;
    private final double fgFactorY = -5;

    private double maxImageOffsetX;
    private double maxImageOffsetY;

    private double targetOffsetX = 0;
    private double targetOffsetY = 0;

    @FXML private StackPane popupContainer;
    @FXML private VBox popupForm;
    @FXML private Label popupTitle;

    @FXML private Label labelImagemSelecionada;

    private File imagemSelecionada = null;

    private ContextMenu menuAtual;
    private Campanha campanhaEditando = null;
    @FXML private Button createButton;

    @FXML private Button profileButton;
    @FXML private StackPane popupEditarPerfil;
    @FXML private TextField campoNovoNome;
    @FXML private PasswordField campoSenhaAtual;
    @FXML private PasswordField campoNovaSenha;

    private final GerenciadorCampanha gerenciadorCampanha = new GerenciadorCampanha();
    private final GerenciadorUsuario gerenciadorUsuario = new GerenciadorUsuario();
    private Usuario usuario;

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

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

        gerenciadorCampanha.criarCampanha("Campanha de Teste", "Descrição de teste que nem sequer da pra ver na real.");
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
        StackPane cartao = new StackPane();
        cartao.getStyleClass().add("campanha-card");
        cartao.setAlignment(Pos.TOP_CENTER);
        cartao.setPrefSize(larguraCard, alturaCard);
        cartao.setMaxSize(440, 270);

        StackPane imageWrapper = new StackPane();
        imageWrapper.setPrefSize(larguraCard, alturaCard);

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
        nome.setText(c.getNome().length() > 29 ? c.getNome().substring(0, 29) : c.getNome());
        nome.getStyleClass().add("campanha-label");
        nome.setStyle("-fx-font-family:"+"Constantia"+";-fx-font-size:" + (28 * larguraCard / 440) + "px;");
        StackPane.setAlignment(nome, Pos.BOTTOM_CENTER);
        StackPane.setMargin(nome, new Insets(0, 0, 10, 0));

        cartao.setOnContextMenuRequested(e -> abrirMenuContextual(e, c));
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
        if (menuAtual != null && menuAtual.isShowing()) {
            menuAtual.hide();
        }

        ContextMenu menu = new ContextMenu();
        menu.getStyleClass().add("menu-context");
        menu.setStyle("-fx-font-family: "+"Constantia"+"; -fx-font-size:"+ (20 * larguraCard / 440) + "px;");



        MenuItem editar = new MenuItem("Edit " + campanha.getNome());
        editar.getStyleClass().add("menu-item");
        editar.setOnAction(e -> {
            popupTitle.setText(campanha.getNome());
            campanhaEditando = campanha;
            campoNomeCampanha.setText(campanha.getNome());
            campoDescricaoCampanha.setText(campanha.getDescricao());
            labelImagemSelecionada.setText(campanha.imageExists() ? new File(campanha.getImagePath()).getName():"Empty");
            imagemSelecionada = campanha.imageExists() ? new File(campanha.getImagePath()) : null;
            createButton.setText("Edit");
            popupContainer.setVisible(true);
        });

        MenuItem excluir = new MenuItem("Delete");
        excluir.getStyleClass().add("menu-item");
        excluir.setStyle("-fx-text-fill: red;");
        excluir.setOnAction(e -> {
            try {
                // TODO: Substituir ou implementar no DAO
                gerenciadorCampanha.removerCapanha(campanha);
                carregarCampanhas();
            } catch (ElementoNaoEncontradoException ex) {
                throw new RuntimeException(ex);
            }
        });

        menu.getItems().addAll(editar, excluir);
        menu.show((Node) event.getSource(), event.getScreenX(), event.getScreenY());
        menuAtual = menu;
    }

    @FXML
    private void mostrarPopupCriacao(MouseEvent event) {
        double mouseX = event.getSceneX();
        double mouseY = event.getSceneY();
        popupTitle.setText("New Campaign");
        popupContainer.setVisible(true);
        popupForm.setVisible(true);
        labelMensagem.setText("");
        campoNomeCampanha.clear();
        campoDescricaoCampanha.clear();
        labelImagemSelecionada.setText("Empty");
        createButton.setText("Create");
        imagemSelecionada = null;

        popupContainer.setLayoutX(mouseX);
        popupContainer.setLayoutY(mouseY);
    }


    @FXML
    private void fecharPopup() {
        campanhaEditando = null;
        imagemSelecionada = null;
        campoNomeCampanha.clear();
        campoDescricaoCampanha.clear();
        labelImagemSelecionada.setText("Empty");
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
        campoNomeCampanha.textProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue.length() > 29) {
                campoNomeCampanha.setText(oldValue);
            }
        });
        String descricao = campoDescricaoCampanha.getText().trim();

        if (nome.isEmpty() || descricao.isEmpty()) {
            labelMensagem.setText("All fields must be filled.");
            return;
        }

        if (campanhaEditando != null) {
            campanhaEditando.setNome(nome);
            campanhaEditando.setDescricao(descricao);
            if (imagemSelecionada != null) {
                campanhaEditando.setCaminhoImagem(imagemSelecionada.getAbsolutePath());
            }
            campanhaEditando = null;
        } else {
            Campanha nova = new Campanha(nome, descricao);
            if (imagemSelecionada != null) {
                nova.setCaminhoImagem(imagemSelecionada.getAbsolutePath());
            }
            gerenciadorCampanha.criarCampanha(nova.getNome(), nova.getDescricao());
            gridCampanhas.getChildren().add(criarCartaoCampanha(nova));
        }


        fecharPopup();
        carregarCampanhas();
    }

    @FXML
    private void abrirMenuPerfil(MouseEvent event) {
        if (usuario == null) return;
        if (menuAtual != null && menuAtual.isShowing()) {
            menuAtual.hide();
        }

        ContextMenu menuPerfil = new ContextMenu();
        menuPerfil.getStyleClass().add("menu-context");
        menuPerfil.setStyle("-fx-font-family: "+"Constantia"+"; -fx-font-size:"+ (20 * larguraCard / 440) + "px;");

        MenuItem editar = new MenuItem("Edit Profile");
        editar.getStyleClass().add("menu-item");
        editar.setOnAction(e -> mostrarPopupEditarPerfil(event));

        MenuItem excluir = new MenuItem("Delete Account");
        excluir.getStyleClass().add("menu-item");
        excluir.setStyle("-fx-text-fill: red;");
        excluir.setOnAction(e -> confirmarDeleteAccount());

        menuPerfil.getItems().addAll(editar, excluir);
        menuPerfil.show((Node) event.getSource(), event.getScreenX(), event.getScreenY());
        menuAtual = menuPerfil;
    }

    @FXML
    private void mostrarPopupEditarPerfil(MouseEvent event){
        double mouseX = event.getSceneX();
        double mouseY = event.getSceneY();
        popupEditarPerfil.setVisible(true);
        labelMensagem.setText("");
        campoNovoNome.clear();
        campoSenhaAtual.clear();
        campoNovaSenha.clear();
        popupEditarPerfil.setLayoutX(mouseX);
        popupEditarPerfil.setLayoutY(mouseY);
    }

    @FXML
    private void fecharPopupPerfil() {
        popupEditarPerfil.setVisible(false);
        campoNovoNome.clear();
        campoSenhaAtual.clear();
        campoNovaSenha.clear();
    }

    @FXML
    private void salvarEdicaoPerfil() {
        String novoNome = campoNovoNome.getText().trim();
        String senhaAtual = campoSenhaAtual.getText().trim();
        String novaSenha = campoNovaSenha.getText().trim();

        if (!usuario.getSenha().equals(senhaAtual)) {
            labelMensagem.setText("Incorrect current password.");
            labelMensagem.setTextFill(Color.RED);
            return;
        }

        if (!novoNome.isEmpty()) {
            usuario.setNome(novoNome);
        }

        if (!novaSenha.isEmpty()) {
            usuario.setSenha(novaSenha);
        }

        //  TODO: atualizar no DAO

        labelMensagem.setText("Profile updated.");
        labelMensagem.setTextFill(Color.GREEN);
        fecharPopupPerfil();
    }


    private void confirmarDeleteAccount() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Account");
        alert.setHeaderText("Are you sure?");
        alert.setContentText("This action is irreversible.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                gerenciadorUsuario.getUsuarios().remove(usuario);
                System.exit(0);
            }
        });
    }
}
