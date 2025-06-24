package me.controle;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import me.modelo.entidades.Objeto;
import me.persistencia.DAOObjeto;
import me.persistencia.ConexaoBD;

public class GerenciadorObjeto extends GerenciadorNarrativo<Objeto> {
    private final DAOObjeto dao;

    public GerenciadorObjeto() {
        this.dao = new DAOObjeto();
    }

    public DAOObjeto getDao() { return dao; }

    @Override
    public void salvarNoBanco(Objeto o) throws SQLException {
        try (Connection conn = ConexaoBD.getConnection()) {
            dao.salvar(o, o.getCampanhaId(), conn);
        }
    }

    @Override
    public void deletarDoBanco(String id) throws SQLException {
        try (Connection conn = ConexaoBD.getConnection()) {
            dao.deletar(id, conn);
        }
    }
    
    public List<Objeto> listarPorCampanha(String campanhaId) throws SQLException {
        try (Connection conn = ConexaoBD.getConnection()) {
            return dao.listarPorCampanha(campanhaId, conn);
        }
    }

    public void atualizar(Objeto objeto) throws SQLException {
        try (Connection conn = ConexaoBD.getConnection()) {
            dao.atualizar(objeto, conn);
        }
    }

    // --- Métodos de relacionamento ---

    public void adicionarRelacaoObjetoEvento(String objetoId, String eventoId) throws SQLException {
        try (Connection conn = ConexaoBD.getConnection()) {
            dao.adicionarRelacaoEvento(objetoId, eventoId, conn);
        }
    }

    public void removerRelacaoObjetoEvento(String objetoId, String eventoId) throws SQLException {
        try (Connection conn = ConexaoBD.getConnection()) {
            dao.removerRelacaoEvento(objetoId, eventoId, conn);
        }
    }

    public void adicionarRelacaoObjetoLocal(String objetoId, String localId) throws SQLException {
        try (Connection conn = ConexaoBD.getConnection()) {
            dao.adicionarRelacaoLocal(objetoId, localId, conn);
        }
    }

    public void removerRelacaoObjetoLocal(String objetoId, String localId) throws SQLException {
        try (Connection conn = ConexaoBD.getConnection()) {
            dao.removerRelacaoLocal(objetoId, localId, conn);
        }
    }

    public void adicionarRelacaoObjetoPersonagem(String objetoId, String personagemId) throws SQLException {
        try (Connection conn = ConexaoBD.getConnection()) {
            dao.adicionarRelacaoPersonagem(objetoId, personagemId, conn);
        }
    }

    public void removerRelacaoObjetoPersonagem(String objetoId, String personagemId) throws SQLException {
        try (Connection conn = ConexaoBD.getConnection()) {
            dao.removerRelacaoPersonagem(objetoId, personagemId, conn);
        }
    }

    public void adicionarRelacaoObjetoObjeto(String objetoId1, String objetoId2) throws SQLException {
        try (Connection conn = ConexaoBD.getConnection()) {
            dao.adicionarRelacaoObjeto(objetoId1, objetoId2, conn);
        }
    }

    public void removerRelacaoObjetoObjeto(String objetoId1, String objetoId2) throws SQLException {
        try (Connection conn = ConexaoBD.getConnection()) {
            dao.removerRelacaoObjeto(objetoId1, objetoId2, conn);
        }
    }
}
