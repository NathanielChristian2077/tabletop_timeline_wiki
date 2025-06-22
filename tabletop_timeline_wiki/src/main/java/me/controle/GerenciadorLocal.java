package me.controle;

import java.sql.SQLException;
import java.util.List;

import me.modelo.entidades.Local;
import me.persistencia.DAOLocal;

public class GerenciadorLocal extends GerenciadorNarrativo<Local> {
    private final DAOLocal dao;

    public GerenciadorLocal() throws SQLException {
        this.dao = new DAOLocal();
    }

    @Override
    protected void salvarNoBanco(Local l) throws SQLException {
        dao.salvar(l, l.getCampanhaId());
    }

    @Override
    protected void deletarDoBanco(String id) throws SQLException {
        dao.deletar(id);
    }

    public List<Local> listarPorCampanha(String campanhaId) throws SQLException {
        return dao.listarPorCampanha(campanhaId);
    }

    public void atualizar(Local local) throws SQLException {
        dao.atualizar(local);
    }
}
