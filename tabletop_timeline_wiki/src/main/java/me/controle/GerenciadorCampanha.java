package me.controle;

import java.sql.SQLException;
import java.util.List;

import me.modelo.entidades.Campanha;
import me.modelo.exceptions.ElementoNaoEncontradoException;
import me.persistencia.DAOCampanha;

public class GerenciadorCampanha {
    private DAOCampanha daoCampanha;

    public GerenciadorCampanha() throws SQLException {
        this.daoCampanha = new DAOCampanha();
    }

    public void criarCampanha(String nome, String descricao) throws SQLException {
        Campanha nova = new Campanha(nome, descricao);
        daoCampanha.salvar(nova);
    }

    public List<Campanha> listarCampanhas() throws SQLException {
        return daoCampanha.listarTodas();
    }

    public Campanha buscarCampanhaPorNome(String nome) throws SQLException, ElementoNaoEncontradoException {
        List<Campanha> campanhas = daoCampanha.listarTodas();
        return campanhas.stream()
                .filter(c -> c.getNome().equalsIgnoreCase(nome))
                .findFirst()
                .orElseThrow(() -> new ElementoNaoEncontradoException("Campanha '" + nome + "' nao encontrada."));
    }

    public void removerCampanha(Campanha c) throws SQLException {
        daoCampanha.deletar(c.getId());
    }

    public void atualizarCampanha(Campanha c) throws SQLException {
        daoCampanha.atualizar(c);
    }
}
