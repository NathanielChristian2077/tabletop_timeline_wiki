package me.controle;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import me.persistencia.DAOEvento;
import me.modelo.entidades.Evento;
import me.modelo.exceptions.ElementoNaoEncontradoException;
import me.persistencia.ConexaoBD;

/**
 * Controla a criação e associação entre eventos dentro da linha do tempo da campanha.
 * Permite definir sequências temporais e relacionar eventos posteriores e anteriores.
 * Demonstra composição e lógica temporal entre objetos.
 */
public class GerenciadorEvento extends GerenciadorEntradaDiario<Evento> {
    private final DAOEvento daoEvento;

    public GerenciadorEvento() {
        this.daoEvento = new DAOEvento();
    }

    public DAOEvento getDaoEvento() { return daoEvento; }

    public Evento criarEvento(String titulo, String descricao, String campanhaId) throws SQLException {
        Evento e = new Evento(titulo, descricao);
        e.setCampanhaid(campanhaId);
        adicionar(e);
        try (Connection conn = ConexaoBD.getConnection()) {
            daoEvento.salvar(e, campanhaId, conn);
        }
        return e;
    }

    @Override
    public Evento buscarPorId(String id) throws ElementoNaoEncontradoException {
        try {
            return super.buscarPorId(id);
        } catch (ElementoNaoEncontradoException e) {
            try (Connection conn = ConexaoBD.getConnection()) {
                Evento evento = daoEvento.buscarPorId(id, conn);
                if (evento != null) {
                    adicionar(evento);
                    return evento;
                } else {
                    throw new ElementoNaoEncontradoException("Evento com ID " + id + " não encontrado no banco.");
                }
            } catch (SQLException ex) {
                throw new ElementoNaoEncontradoException("Erro ao buscar no banco: " + ex.getMessage());
            }
        }
    }

    @Override
    public void remover(String id) throws ElementoNaoEncontradoException {
        try (Connection conn = ConexaoBD.getConnection()) {
            daoEvento.deletar(id, conn);
            super.remover(id);
        } catch (SQLException e) {
            throw new ElementoNaoEncontradoException("Erro ao deletar do banco: " + e.getMessage());
        }
    }

    @Override
    public void atualizarTitulo(String id, String novoTitulo) throws ElementoNaoEncontradoException {
        super.atualizarTitulo(id, novoTitulo);
        try (Connection conn = ConexaoBD.getConnection()) {
            daoEvento.atualizar(buscarPorId(id), conn);
        } catch (SQLException e) {
            throw new ElementoNaoEncontradoException("Erro ao atualizar título no banco: " + e.getMessage());
        }
    }

    @Override
    public void atualizarDescricao(String id, String novaDescricao) throws ElementoNaoEncontradoException {
        super.atualizarDescricao(id, novaDescricao);
        try (Connection conn = ConexaoBD.getConnection()) {
            daoEvento.atualizar(buscarPorId(id), conn);
        } catch (SQLException e) {
            throw new ElementoNaoEncontradoException("Erro ao atualizar descrição no banco: " + e.getMessage());
        }
    }

    public void atualizar(Evento evento) throws SQLException {
        try (Connection conn = ConexaoBD.getConnection()) {
            daoEvento.atualizar(evento, conn);
        }
    }

    public void carregarTodosDaCampanha(String campanhaId) throws SQLException {
        entradas.clear();
        try (Connection conn = ConexaoBD.getConnection()) {
            List<Evento> eventos = daoEvento.listarPorCampanha(campanhaId, conn);
            for (Evento evento : eventos) {
                adicionar(evento);
            }
        }
    }

    public List<Evento> listarPorCampanha(String campanhaId) throws SQLException {
        try (Connection conn = ConexaoBD.getConnection()) {
            return daoEvento.listarPorCampanha(campanhaId, conn);
        }
    }

    public void associarAnteriorPosterior(String idAnterior, String idPosterior) throws ElementoNaoEncontradoException {
        Evento anterior = buscarPorId(idAnterior);
        Evento posterior = buscarPorId(idPosterior);
        anterior.adicionarEventoPosterior(posterior);
    }

    public void adicionarRelacaoEventoEvento(String eventoId1, String eventoId2) throws SQLException {
        try (Connection conn = ConexaoBD.getConnection()) {
            daoEvento.adicionarRelacaoEvento(eventoId1, eventoId2, conn);
        }
    }

    public void removerRelacaoEventoEvento(String eventoId1, String eventoId2) throws SQLException {
        try (Connection conn = ConexaoBD.getConnection()) {
            daoEvento.removerRelacaoEvento(eventoId1, eventoId2, conn);
        }
    }

    public void adicionarRelacaoEventoPersonagem(String eventoId, String personagemId) throws SQLException {
        try (Connection conn = ConexaoBD.getConnection()) {
            daoEvento.adicionarRelacaoPersonagem(eventoId, personagemId, conn);
        }
    }

    public void removerRelacaoEventoPersonagem(String eventoId, String personagemId) throws SQLException {
        try (Connection conn = ConexaoBD.getConnection()) {
            daoEvento.removerRelacaoPersonagem(eventoId, personagemId, conn);
        }
    }

    public void adicionarRelacaoEventoLocal(String eventoId, String localId) throws SQLException {
        try (Connection conn = ConexaoBD.getConnection()) {
            daoEvento.adicionarRelacaoLocal(eventoId, localId, conn);
        }
    }

    public void removerRelacaoEventoLocal(String eventoId, String localId) throws SQLException {
        try (Connection conn = ConexaoBD.getConnection()) {
            daoEvento.removerRelacaoLocal(eventoId, localId, conn);
        }
    }

    public void adicionarRelacaoEventoObjeto(String eventoId, String objetoId) throws SQLException {
        try (Connection conn = ConexaoBD.getConnection()) {
            daoEvento.adicionarRelacaoObjeto(eventoId, objetoId, conn);
        }
    }

    public void removerRelacaoEventoObjeto(String eventoId, String objetoId) throws SQLException {
        try (Connection conn = ConexaoBD.getConnection()) {
            daoEvento.removerRelacaoObjeto(eventoId, objetoId, conn);
        }
    }
}
