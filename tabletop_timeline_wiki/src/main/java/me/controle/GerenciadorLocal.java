package me.controle;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import me.persistencia.DAOLocal;
import me.modelo.entidades.Local;
import me.persistencia.ConexaoBD;

public class GerenciadorLocal extends GerenciadorNarrativo<Local> {
    private final DAOLocal dao;

    public GerenciadorLocal() {
        this.dao = new DAOLocal();
    }

    public DAOLocal getDao() { return dao; }

    @Override
    public void salvarNoBanco(Local l) throws SQLException {
        try (Connection conn = ConexaoBD.getConnection()) {
            dao.salvar(l, l.getCampanhaId(), conn);
        }
    }

    @Override
    public void deletarDoBanco(String id) throws SQLException {
        try (Connection conn = ConexaoBD.getConnection()) {
            dao.deletar(id, conn);
        }
    }

    public List<Local> listarPorCampanha(String campanhaId) throws SQLException {
        try (Connection conn = ConexaoBD.getConnection()) {
            return dao.listarPorCampanha(campanhaId, conn);
        }
    }

    public void atualizar(Local local) throws SQLException {
        try (Connection conn = ConexaoBD.getConnection()) {
            dao.atualizar(local, conn);
        }
    }

    public void adicionarRelacaoPersonagem(String localId, String personagemId) throws SQLException {
        try (Connection conn = ConexaoBD.getConnection()) {
            dao.adicionarRelacaoPersonagem(localId, personagemId, conn);
        }
    }

    public void removerRelacaoPersonagem(String localId, String personagemId) throws SQLException {
        try (Connection conn = ConexaoBD.getConnection()) {
            dao.removerRelacaoPersonagem(localId, personagemId, conn);
        }
    }

    public void adicionarRelacaoObjeto(String localId, String objetoId) throws SQLException {
        try (Connection conn = ConexaoBD.getConnection()) {
            dao.adicionarRelacaoObjeto(localId, objetoId, conn);
        }
    }

    public void removerRelacaoObjeto(String localId, String objetoId) throws SQLException {
        try (Connection conn = ConexaoBD.getConnection()) {
            dao.removerRelacaoObjeto(localId, objetoId, conn);
        }
    }

    public void adicionarRelacaoEvento(String localId, String eventoId) throws SQLException {
        try (Connection conn = ConexaoBD.getConnection()) {
            dao.adicionarRelacaoEvento(localId, eventoId, conn);
        }
    }

    public void removerRelacaoEvento(String localId, String eventoId) throws SQLException {
        try (Connection conn = ConexaoBD.getConnection()) {
            dao.removerRelacaoEvento(localId, eventoId, conn);
        }
    }

    public void adicionarRelacaoLocal(String localId, String localId2) throws SQLException {
        try (Connection conn = ConexaoBD.getConnection()) {
            dao.adicionarRelacaoLocal(localId, localId2, conn);
        }
    }

    public void removerRelacaoLocal(String localId, String localId2) throws SQLException {
        try (Connection conn = ConexaoBD.getConnection()) {
            dao.removerRelacaoLocal(localId, localId2, conn);
        }
    }
}
