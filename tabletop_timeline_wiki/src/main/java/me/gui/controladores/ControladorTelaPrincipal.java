package me.gui.controladores;

import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import me.controle.GerenciadorCampanha;
import me.modelo.entidades.Campanha;

public class ControladorTelaPrincipal {
    @FXML private TextField campoNomeCampanha;
    @FXML private TextArea campoDescricaoCampanha;
    @FXML private Label labelMensagem;
    @FXML private ListView<Campanha> listaCampanhas;

    private final GerenciadorCampanha gerenciadorCampanha = new GerenciadorCampanha();

    @FXML
    public void initialize() {
        atualizarListaCampanhas();

        listaCampanhas.setOnMouseClicked((MouseEvent e) -> {
            if (e.getClickCount() == 2) {
                abrirCampanha();
            }
        });
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
        listaCampanhas.getItems().setAll(campanhas);
    }
}
