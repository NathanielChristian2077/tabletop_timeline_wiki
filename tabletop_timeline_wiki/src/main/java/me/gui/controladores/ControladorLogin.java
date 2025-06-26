package me.gui.controladores;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.SQLException;

import javafx.animation.*;
import me.controle.GerenciadorUsuario;
import me.modelo.entidades.Usuario;
import me.modelo.enums.TipoUsuario;
import me.modelo.exceptions.ElementoNaoEncontradoException;

public class ControladorLogin {

    @FXML private AnchorPane root;
    @FXML private ImageView backgroundImage;
    @FXML private Label titulo1;
    @FXML private Label titulo2;
    @FXML private HBox botaoTrocaBox;
    @FXML private TextField campoNomeLogin;
    @FXML private TextField campoNomeCadastro;
    @FXML private PasswordField campoSenhaLogin;
    @FXML private PasswordField campoSenhaCadastro;
    @FXML private ChoiceBox<TipoUsuario> choiceTipo;
    @FXML private Button botaoLogin;
    @FXML private Button botaoCadastrar;
    @FXML private Label labelMensagem;
    @FXML private VBox formLogin;
    @FXML private VBox formCadastro;
    @FXML private StackPane container;
    @FXML private Button botaoTrocarLogin;
    @FXML private Button botaoTrocarCadastro;
    @FXML private PasswordField campoSenhaConfirm;

    private Usuario usuarioLogado = null;

    private final GerenciadorUsuario gerenciadorUsuario;
    private boolean showingLogin = true;
    private double targetOffsetX = 0;
    private double targetOffsetY = 0;

    private double bgFactorX = 1.25;
    private double bgFactorY = .75;
    private double fgFactorX = -10;
    private double fgFactorY = -5;

    private double maxImageOffsetX;
    private double maxImageOffsetY;

    public ControladorLogin() {
        try {
            this.gerenciadorUsuario = new GerenciadorUsuario();
        } catch (SQLException e) {
            throw new RuntimeException("Error initializing user manager", e);
        }
    }

    @FXML
    public void initialize() {
        mostrarLoginAnimado();
        choiceTipo.getItems().addAll(TipoUsuario.values());
        BoxBlur blur = new BoxBlur(0, 0, 1);
        backgroundImage.setEffect(blur);
        backgroundImage.setSmooth(true);

        backgroundImage.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                backgroundImage.fitWidthProperty().bind(newScene.widthProperty());
                backgroundImage.fitHeightProperty().bind(newScene.heightProperty());

                maxImageOffsetX = bgFactorX;
                maxImageOffsetY = bgFactorY;

                newScene.setOnMouseMoved(event -> {
                    double centerX = newScene.getWidth() / 2;
                    double centerY = newScene.getHeight() / 2;
                    double offsetX = (event.getSceneX() - centerX) / centerX;
                    double offsetY = (event.getSceneY() - centerY) / centerY;

                    targetOffsetX = offsetX;
                    targetOffsetY = offsetY;

                    double buttonCenterX = botaoLogin.localToScene(botaoLogin.getBoundsInLocal()).getCenterX();
                    double buttonCenterY = botaoLogin.localToScene(botaoLogin.getBoundsInLocal()).getCenterY();

                    double dx = buttonCenterX - event.getSceneX();
                    double dy = buttonCenterY - event.getSceneY();
                    double distance = Math.hypot(dx, dy);

                    double maxDistance = 720.0;

                    double blurStrength = Math.max(0, (1.0 - distance / maxDistance)) * 20.0;

                    blur.setWidth(blurStrength);
                    blur.setHeight(blurStrength);
                });

                startParallax();
            }
        });

        botaoTrocarLogin.setOnAction(e -> {
            if (!showingLogin) {
                mostrarLoginAnimado();
            }
        });
        botaoTrocarCadastro.setOnAction(e -> {
            if (showingLogin) {
                mostrarCadastroAnimado();
            }
        });
    }

    private void startParallax() {
        Timeline parallaxAnim = new Timeline(new KeyFrame(Duration.millis(16), e -> {
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

            double formX = container.getTranslateX();
            double formY = container.getTranslateY();

            double nextFormX = formX + (targetOffsetX * fgFactorX - formX) * 0.08;
            double nextFormY = formY + (targetOffsetY * fgFactorY - formY) * 0.08;
            container.setTranslateX(nextFormX);
            container.setTranslateY(nextFormY);

            titulo1.setTranslateX(titulo1.getTranslateX() + (targetOffsetX * -10 - titulo1.getTranslateX())* 0.08);
            titulo1.setTranslateY(titulo1.getTranslateY() + (targetOffsetY * -5 - titulo1.getTranslateY())* 0.08);

            titulo2.setTranslateX(titulo2.getTranslateX() + (targetOffsetX * -10 - titulo2.getTranslateX())* 0.08);
            titulo2.setTranslateY(titulo2.getTranslateY() + (targetOffsetY * -5 - titulo2.getTranslateY())* 0.08);

            botaoTrocaBox.setTranslateX(botaoTrocaBox.getTranslateX() + (targetOffsetX * -10 - botaoTrocaBox.getTranslateX()) * 0.08);
            botaoTrocaBox.setTranslateY(botaoTrocaBox.getTranslateY() + (targetOffsetY * -5 - botaoTrocaBox.getTranslateY()) * 0.08);
        }));
        parallaxAnim.setCycleCount(Animation.INDEFINITE);
        parallaxAnim.play();
    }

    public void realizarLogin() {
        String nome = campoNomeLogin.getText().trim();
        String senha = campoSenhaLogin.getText();

        if (nome.isEmpty()) {
            mostrarErro(campoNomeLogin, "* required");
            return;
        } else if (senha.isEmpty()) {
            mostrarErro(campoSenhaLogin, "* required");
            return;
        }

        try {
            Usuario u = gerenciadorUsuario.buscarPorNome(nome);
            if (u.autenticar(senha)) {
                labelMensagem.setStyle("-fx-text-fill : green;");
                labelMensagem.setText("Welcome " + u.getNome() + "!");
                usuarioLogado = u;
                irParaTelaPrincipal();
            } else {
                labelMensagem.setStyle("-fx-text-fill: red;");
                labelMensagem.setText("Invalid username or password.");
            }
        } catch (ElementoNaoEncontradoException e) {
            labelMensagem.setStyle("-fx-text-fill: red;");
            labelMensagem.setText("Invalid username or password.");
        } catch (SQLException e) {
            e.printStackTrace();
            labelMensagem.setText("Database error.");
        }
    }

    @FXML
    public void realizarCadastro() {
        String nome = campoNomeCadastro.getText();
        String senha = campoSenhaCadastro.getText();
        String senhaConfirm = campoSenhaConfirm.getText();
        TipoUsuario tipo = choiceTipo.getValue();

        if (nome.isEmpty()) {
            mostrarErro(campoNomeCadastro, "* required");
            return;
        } else if (senha.isEmpty()) {
            mostrarErro(campoSenhaCadastro, "* required");
            return;
        } else if (senhaConfirm.isEmpty()) {
            mostrarErro(campoSenhaConfirm, "* required");
            return;
        }

        if (!senha.equals(senhaConfirm)) {
            mostrarErro(campoSenhaConfirm, "Passwords do not match.");
            return;
        }

        try {
            gerenciadorUsuario.buscarPorNome(nome);
            mostrarErro(campoNomeCadastro, "User already exists.");
        } catch (ElementoNaoEncontradoException ex) {
            Usuario novo = new Usuario(nome, senha, tipo);
            try {
                gerenciadorUsuario.adicionarUsuario(novo);
                labelMensagem.setStyle("-fx-text-fill: green;");
                labelMensagem.setText("Registration successful.");
                mostrarLoginAnimado();
            } catch (SQLException e) {
                labelMensagem.setStyle("-fx-text-fill: red;");
                labelMensagem.setText("Error registering user.");
            }
        } catch (SQLException e) {
            labelMensagem.setStyle("-fx-text-fill: red;");
            labelMensagem.setText("Error checking user.");
        }
    }

    public void irParaTelaPrincipal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/me/gui/TelaPrincipal.fxml"));
            Parent root = loader.load();
            ControladorTelaPrincipal controlador = loader.getController();
            controlador.setUsuario(usuarioLogado);
            Scene novaCena = new Scene(root, (int)campoNomeLogin.getScene().getWidth(), (int)campoNomeLogin.getScene().getHeight());
            Stage stage = (Stage) campoNomeLogin.getScene().getWindow();
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

    private void mostrarErro(TextField campo, String msg) {
        campo.setStyle("-fx-border-color: red; -fx-border-radius: 10px;");
        labelMensagem.setStyle("-fx-text-fill: red;");
        labelMensagem.setText(msg);
    }

    private void mostrarLoginAnimado() {
        if (showingLogin) return;
        showingLogin = true;
        botaoTrocarLogin.setDisable(true);
        botaoTrocarCadastro.setDisable(true);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), formCadastro);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setInterpolator(Interpolator.EASE_BOTH);

        fadeOut.setOnFinished(e -> {
            formCadastro.setVisible(false);
            formCadastro.setManaged(false);

            formLogin.setTranslateX(-50);
            formLogin.setOpacity(0);
            formLogin.setScaleX(0.95);
            formLogin.setScaleY(0.95);
            formLogin.setVisible(true);
            formLogin.setManaged(true);

            TranslateTransition slideIn = new TranslateTransition(Duration.millis(350), formLogin);
            slideIn.setFromX(-50);
            slideIn.setToX(0);
            slideIn.setInterpolator(Interpolator.EASE_BOTH);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(350), formLogin);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.setInterpolator(Interpolator.EASE_BOTH);

            ScaleTransition scaleIn = new ScaleTransition(Duration.millis(350), formLogin);
            scaleIn.setFromX(0.95);
            scaleIn.setFromY(0.95);
            scaleIn.setToX(1.0);
            scaleIn.setToY(1.0);
            scaleIn.setInterpolator(Interpolator.EASE_BOTH);

            ParallelTransition show = new ParallelTransition(slideIn, fadeIn, scaleIn);
            show.setOnFinished(ev -> {
                botaoTrocarLogin.setDisable(false);
                botaoTrocarCadastro.setDisable(false);
            });
            show.play();
        });

        fadeOut.play();
    }

    private void mostrarCadastroAnimado() {
        if (!showingLogin) return;
        showingLogin = false;
        botaoTrocarLogin.setDisable(true);
        botaoTrocarCadastro.setDisable(true);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), formLogin);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setInterpolator(Interpolator.EASE_BOTH);

        fadeOut.setOnFinished(e -> {
            formLogin.setVisible(false);
            formLogin.setManaged(false);

            formCadastro.setTranslateX(50);
            formCadastro.setOpacity(0);
            formCadastro.setScaleX(0.95);
            formCadastro.setScaleY(0.95);
            formCadastro.setVisible(true);
            formCadastro.setManaged(true);

            TranslateTransition slideIn = new TranslateTransition(Duration.millis(350), formCadastro);
            slideIn.setFromX(50);
            slideIn.setToX(0);
            slideIn.setInterpolator(Interpolator.EASE_BOTH);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(350), formCadastro);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.setInterpolator(Interpolator.EASE_BOTH);

            ScaleTransition scaleIn = new ScaleTransition(Duration.millis(350), formCadastro);
            scaleIn.setFromX(0.95);
            scaleIn.setFromY(0.95);
            scaleIn.setToX(1.0);
            scaleIn.setToY(1.0);
            scaleIn.setInterpolator(Interpolator.EASE_BOTH);

            ParallelTransition show = new ParallelTransition(slideIn, fadeIn, scaleIn);
            show.setOnFinished(ev -> {
                botaoTrocarLogin.setDisable(false);
                botaoTrocarCadastro.setDisable(false);
            });
            show.play();
        });

        fadeOut.play();
    }
}
