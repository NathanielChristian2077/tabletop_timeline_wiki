package me.gui.controladores;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import me.controle.GerenciadorUsuario;
import me.modelo.entidades.Usuario;
import me.modelo.enums.TipoUsuario;
import me.modelo.exceptions.ElementoNaoEncontradoException;

public class ControladorLogin {
    @FXML private ImageView backgroundImage;

    @FXML private HBox mainPane;
    @FXML private VBox loginPane;
    @FXML private VBox signUpPane;

    @FXML private TextField campoNomeLogin;
    @FXML private PasswordField campoSenhaLogin;
    @FXML private Button botaoLogin;
    @FXML private Button botaoTrocarParaCadastro;

    @FXML private TextField campoNomeCadastro;
    @FXML private PasswordField campoSenhaCadastro;
    @FXML private PasswordField campoSenhaConfirm;
    @FXML private ChoiceBox<TipoUsuario> choiceTipo;
    @FXML private Button botaoCadastrar;
    @FXML private Button botaoTrocarParaLogin;

    @FXML private Label labelMensagem;

    private final GerenciadorUsuario gerenciadorUsuario = new GerenciadorUsuario();

    @FXML
    public void initialize() {
        choiceTipo.getItems().addAll(TipoUsuario.values());

        backgroundImage.fitWidthProperty().bind(mainPane.widthProperty());
        backgroundImage.fitHeightProperty().bind(mainPane.heightProperty());

        botaoTrocarParaCadastro.setOnAction(e -> animarTransicaoCadastro());
        botaoTrocarParaLogin.setOnAction(e -> animarTransicaoLogin());
    }

    @FXML
    public void animarTransicaoCadastro() {
        TranslateTransition slide = new TranslateTransition(Duration.millis(500), mainPane);
        slide.setToX(-mainPane.getWidth()/2);
        slide.play();
    }

    @FXML
    public void animarTransicaoLogin() {
        TranslateTransition slide = new TranslateTransition(Duration.millis(500), mainPane);
        slide.setToX(0);
        slide.play();
    }

    @FXML
    public void realizarLogin() {
        String nome = campoNomeLogin.getText();
        String senha = campoSenhaLogin.getText();
        try {
            Usuario u = gerenciadorUsuario.buscarPorNome(nome);
            if (u.autenticar(senha)) {
                labelMensagem.setText("Hello " + nome + "!");
            } else {
                labelMensagem.setText("Username/Password authentication error. Try again.");
            }
        } catch (ElementoNaoEncontradoException e) {
            labelMensagem.setText("Username not found.");
        }
    }

    @FXML
    public void realizarCadastro () {
        String nome = campoNomeCadastro.getText();
        String senha = campoSenhaCadastro.getText();
        String confirm = campoSenhaConfirm.getText();
        TipoUsuario tipo = choiceTipo.getValue();

        if (!senha.equals(confirm)) {
            labelMensagem.setText("Different passwords.");
        } else {
            Usuario novo = new Usuario(nome, senha, tipo);
            gerenciadorUsuario.adicionarUsuario(novo);
            labelMensagem.setText("Sign up confimed. Welcome" + nome + "!");
            animarTransicaoLogin();
        }
    }
}
