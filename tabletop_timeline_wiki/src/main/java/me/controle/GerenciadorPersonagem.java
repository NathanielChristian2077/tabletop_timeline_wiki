package me.controle;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import me.modelo.entidades.Personagem;
import me.persistencia.ConexaoBD;
import me.persistencia.DAOPersonagem;

public class GerenciadorPersonagem extends GerenciadorNarrativo<Personagem> {
    private final DAOPersonagem dao;

    public GerenciadorPersonagem() {
        this.dao = new DAOPersonagem();
    }

    public DAOPersonagem getDao() { return dao; }

    @Override
    public void salvarNoBanco(Personagem p) throws SQLException {
        try (Connection conn = ConexaoBD.getConnection()) {
            dao.salvar(p, p.getCampanhaId(), conn);
        }
    }

    @Override
    public void deletarDoBanco(String id) throws SQLException {
        try (Connection conn = ConexaoBD.getConnection()) {
            dao.deletar(id, conn);
        }
    }

    public List<Personagem> listarPorCampanha(String campanhaId) throws SQLException {
        try (Connection conn = ConexaoBD.getConnection()) {
            return dao.listarPorCampanha(campanhaId, conn);
        }
    }

    public void atualizar(Personagem personagem) throws SQLException {
        try (Connection conn = ConexaoBD.getConnection()) {
            dao.atualizar(personagem, conn);
        }
    }

    // --- Métodos de relacionamento ---

    public void adicionarRelacaoPersonagemObjeto(String personagemId, String objetoId) throws SQLException {
        try (Connection conn = ConexaoBD.getConnection()) {
            dao.adicionarRelacaoObjeto(personagemId, objetoId, conn);
        }
    }

    public void removerRelacaoPersonagemObjeto(String personagemId, String objetoId) throws SQLException {
        try (Connection conn = ConexaoBD.getConnection()) {
            dao.removerRelacaoObjeto(personagemId, objetoId, conn);
        }
    }

    public void adicionarRelacaoPersonagemEvento(String personagemId, String eventoId) throws SQLException {
        try (Connection conn = ConexaoBD.getConnection()) {
            dao.adicionarRelacaoEvento(personagemId, eventoId, conn);
        }
    }

    public void removerRelacaoPersonagemEvento(String personagemId, String eventoId) throws SQLException {
        try (Connection conn = ConexaoBD.getConnection()) {
            dao.removerRelacaoEvento(personagemId, eventoId, conn);
        }
    }

    public void adicionarRelacaoPersonagemLocal(String personagemId, String localId) throws SQLException {
        try (Connection conn = ConexaoBD.getConnection()) {
            dao.adicionarRelacaoLocal(personagemId, localId, conn);
        }
    }

    public void removerRelacaoPersonagemLocal(String personagemId, String localId) throws SQLException {
        try (Connection conn = ConexaoBD.getConnection()) {
            dao.removerRelacaoLocal(personagemId, localId, conn);
        }
    }

    public void adicionarRelacaoPersonagemPersonagem(String personagemId1, String personagemId2) throws SQLException {
        try (Connection conn = ConexaoBD.getConnection()) {
            dao.adicionarRelacaoPersonagem(personagemId1, personagemId2, conn);
        }
    }

    public void removerRelacaoPersonagemPersonagem(String personagemId1, String personagemId2) throws SQLException {
        try (Connection conn = ConexaoBD.getConnection()) {
            dao.removerRelacaoPersonagem(personagemId1, personagemId2, conn);
        }
    }
}
