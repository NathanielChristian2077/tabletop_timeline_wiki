package me.gui.controladores;

import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
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

    @FXML
    public void init() {
        choiceTipo.getItems().addAll(TipoUsuario.values());

        botaoTrocarLogin.setOnAction(e -> mostrarLoginAnimado());
        botaoTrocarCadastro.setOnAction(e -> mostrarCadastroAnimado());
    }

    private void mostrarLoginAnimado() {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), formCadastro);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        TranslateTransition slideIn = new TranslateTransition(Duration.millis(300), formLogin);
        slideIn.setFromX(-50);
        slideIn.setToX(0);

        fadeOut.setOnFinished(event -> {
            formLogin.setVisible(true);
            formLogin.setManaged(true);
            formCadastro.setVisible(false);
            formCadastro.setManaged(false);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(200), formLogin);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);

            new SequentialTransition(slideIn, fadeIn).play();
        });

        fadeOut.play();
        labelMensagem.setText("");
    }

    private void mostrarCadastroAnimado() {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), formLogin);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        TranslateTransition slideIn = new TranslateTransition(Duration.millis(300), formCadastro);
        slideIn.setFromX(50);
        slideIn.setToX(0);

        fadeOut.setOnFinished(event -> {
            formLogin.setVisible(false);
            formLogin.setManaged(false);
            formCadastro.setVisible(true);
            formCadastro.setManaged(true);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(200), formCadastro);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);

            new SequentialTransition(slideIn, fadeIn).play();
        });

        fadeOut.play();
        labelMensagem.setText("");
    }

    public void realizarLogin() {
        String nome = campoNomeLogin.getText();
        String senha = campoSenhaLogin.getText();

        try {
            Usuario u = gerenciadorUsuario.buscarPorNome(nome);
            if (u.autenticar(senha)) {
                labelMensagem.setText("Bem-vindo " + u.getNome());
                // UsuarioSession.getInstance().setUsuario(u); // opcional
            } else {
                labelMensagem.setText("Senha Incorreta.");
            }
        } catch (ElementoNaoEncontradoException e) {
            labelMensagem.setText("Usuário não encontrado.");
        }
    }

    @FXML
    public void realizarCadastro() {
        String nome = campoNomeCadastro.getText();
        String senha = campoSenhaCadastro.getText();
        String senhaConfirm = campoSenhaConfirm.getText();
        TipoUsuario tipo = choiceTipo.getValue();

        if (nome.isEmpty() || senha.isEmpty() || tipo == null || senhaConfirm.isEmpty()) {
            labelMensagem.setText("Todos os campos devem ser informados.");
            return;
        }

        if (!senha.equals(senhaConfirm)) {
            labelMensagem.setText("As senhas são diferentes.");
            return;
        }

        Usuario novo = new Usuario(nome, senha, tipo);
        gerenciadorUsuario.adicionarUsuario(novo);
        labelMensagem.setText("Cadastro bem sucedido.");
    }
}
