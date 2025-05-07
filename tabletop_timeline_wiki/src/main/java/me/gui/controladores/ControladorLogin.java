package me.gui.controladores;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

import javafx.animation.*;
import me.controle.GerenciadorUsuario;
import me.modelo.entidades.Usuario;
import me.modelo.enums.TipoUsuario;
import me.modelo.exceptions.ElementoNaoEncontradoException;

public class ControladorLogin {
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
    @FXML private Button botaoTrocarLogin;
    @FXML private Button botaoTrocarCadastro;
    @FXML private PasswordField campoSenhaConfirm;

    private final GerenciadorUsuario gerenciadorUsuario = new GerenciadorUsuario();
    private boolean showingLogin = true;

    @FXML
    public void initialize() {
        mostrarLoginAnimado();
        choiceTipo.getItems().addAll(TipoUsuario.values());

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

    public void realizarLogin() {
        String nome = campoNomeLogin.getText().trim();
        String senha = campoSenhaLogin.getText();

        if (nome.isEmpty() || senha.isEmpty()) {
            labelMensagem.setStyle("-fx-text-fill: red;");
            labelMensagem.setText("Todos os campos devem ser preenchidos.");
            return;
        }

        try {
            Usuario u = gerenciadorUsuario.buscarPorNome(nome);
            if (u.autenticar(senha)) {
                labelMensagem.setStyle("-fx-text-fill : green;");
                labelMensagem.setText("Bem-vindo " + u.getNome() + "!");
                // UsuarioSession.getInstance().setUsuario(u); // opcional
                irParaTelaPrincipal();
            }
        } catch (ElementoNaoEncontradoException e) {
            labelMensagem.setStyle("-fx-text-fill: red;");
            labelMensagem.setText("Usuário ou senha inválido.");
        }
    }

    @FXML
    public void realizarCadastro() {
        String nome = campoNomeCadastro.getText();
        String senha = campoSenhaCadastro.getText();
        String senhaConfirm = campoSenhaConfirm.getText();
        TipoUsuario tipo = choiceTipo.getValue();

        if (nome.isEmpty() || senha.isEmpty() || tipo == null || senhaConfirm.isEmpty()) {
            labelMensagem.setStyle("-fx-text-fill: red;");
            labelMensagem.setText("Todos os campos devem ser informados.");
            return;
        }

        if (!senha.equals(senhaConfirm)) {
            labelMensagem.setText("As senhas são diferentes.");
            return;
        }

        Usuario novo = new Usuario(nome, senha, tipo);
        gerenciadorUsuario.adicionarUsuario(novo);
        labelMensagem.setStyle("-fx-text-fill: green;");
        labelMensagem.setText("Cadastro realizado com sucesso.");
        mostrarLoginAnimado();
    }

    private void irParaTelaPrincipal() {
        try {
            System.out.println(getClass().getResource("/me/gui/TelaPrincipal.fxml"));
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/me/gui/TelaPrincipal.fxml"));
            Parent root = loader.load();
            Scene novaCena = new Scene(root, 800, 600);

            Stage stage = (Stage) campoNomeLogin.getScene().getWindow();
            stage.setScene(novaCena);
            stage.setTitle("RPG Campaign Manager - Tela Principal");
            stage.setResizable(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            motrarAlerta("Erro", "Não foi possível carregar a tela principal.");
        }
    }

    private void motrarAlerta(String titulo, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
