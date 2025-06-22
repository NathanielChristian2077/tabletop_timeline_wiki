package me.controle;

import java.sql.SQLException;
import java.util.List;

import me.modelo.entidades.Objeto;
import me.persistencia.DAOObjeto;

public class GerenciadorObjeto extends GerenciadorNarrativo<Objeto> {
    private final DAOObjeto dao;

    public GerenciadorObjeto() throws SQLException {
        this.dao = new DAOObjeto();
    }

    @Override
    protected void salvarNoBanco(Objeto o) throws SQLException {
        dao.salvar(o, o.getCampanhaId());
    }

    @Override
    protected void deletarDoBanco(String id) throws SQLException {
        dao.deletar(id);
    }
    
    public List<Objeto> listarPorCampanha(String campanhaId) throws SQLException {
        return dao.listarPorCampanha(campanhaId);
    }

    public void atualizar(Objeto objeto) throws SQLException {
        dao.atualizar(objeto);
    }
}
