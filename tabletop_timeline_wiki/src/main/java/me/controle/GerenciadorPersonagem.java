package me.controle;

import java.sql.SQLException;
import java.util.List;

import me.modelo.entidades.Personagem;
import me.persistencia.DAOPersonagem;

public class GerenciadorPersonagem extends GerenciadorNarrativo<Personagem> {
    private final DAOPersonagem dao;

    public GerenciadorPersonagem() throws SQLException {
        this.dao = new DAOPersonagem();
    }

    @Override
    protected void salvarNoBanco(Personagem p) throws SQLException {
        dao.salvar(p, p.getCampanhaId());
    }

    @Override
    protected void deletarDoBanco(String id) throws SQLException {
        dao.deletar(id);
    }

    public List<Personagem> listarPorCampanha(String campanhaId) throws SQLException {
        return dao.listarPorCampanha(campanhaId);
    }

    public void atualizar(Personagem personagem) throws SQLException {
        dao.atualizar(personagem);
    }
}
