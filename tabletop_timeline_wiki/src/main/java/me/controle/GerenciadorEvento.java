package me.controle;

import java.sql.SQLException;
import java.util.List;

import me.modelo.entidades.Evento;
import me.modelo.exceptions.ElementoNaoEncontradoException;
import me.persistencia.DAOEvento;

/**
 * Controla a criação e associação entre eventos dentro da linha do tempo da campanha.
 * Permite definir sequências temporais e relacionar eventos posteriores e anteriores.
 * Demonstra composição e lógica temporal entre objetos.
 */
public class GerenciadorEvento extends GerenciadorEntradaDiario<Evento> {
    private final DAOEvento daoEvento;

    public GerenciadorEvento() throws SQLException {
        this.daoEvento = new DAOEvento();
    }

    public Evento criarEvento(String titulo, String descricao) throws SQLException {
        Evento e = new Evento(titulo, descricao);
        adicionar(e);
        daoEvento.salvar(e, e.getCampanhaid());
        return e;
    }

    @Override
    public Evento buscarPorId(String id) throws ElementoNaoEncontradoException {
        try {
            return super.buscarPorId(id);
        } catch (ElementoNaoEncontradoException e) {
            try {
                Evento evento = daoEvento.buscarPorId(id);
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
        try {
            daoEvento.deletar(id);
            super.remover(id);
        } catch (SQLException e) {
            throw new ElementoNaoEncontradoException("Erro ao deletar do banco: " + e.getMessage());
        }
    }


    @Override
    public void atualizarTitulo(String id, String novoTitulo) throws ElementoNaoEncontradoException {
        super.atualizarTitulo(id, novoTitulo);
        try {
            daoEvento.atualizar(buscarPorId(id));
        } catch (SQLException e) {
            throw new ElementoNaoEncontradoException("Erro ao atualizar título no banco: " + e.getMessage());
        }
    }

    @Override
    public void atualizarDescricao(String id, String novaDescricao) throws ElementoNaoEncontradoException {
        super.atualizarDescricao(id, novaDescricao);
        try {
            daoEvento.atualizar(buscarPorId(id));
        } catch (SQLException e) {
            throw new ElementoNaoEncontradoException("Erro ao atualizar descrição no banco: " + e.getMessage());
        }
    }

    
    public void atualizar(Evento evento) throws SQLException {
        daoEvento.atualizar(evento);
    }

    public void carregarTodosDaCampanha(String campanhaId) throws SQLException {
        entradas.clear();
        List<Evento> eventos = daoEvento.listarPorCampanha(campanhaId);
        for (Evento evento : eventos) {
            adicionar(evento);
        }
    } 

    public List<Evento> listarPorCampanha(String campanhaId) throws SQLException {
        return daoEvento.listarPorCampanha(campanhaId);
    }

    public void associarAnteriorPosterior(String idAnterior, String idPosterior) throws ElementoNaoEncontradoException {
        Evento anterior = buscarPorId(idAnterior);
        Evento posterior = buscarPorId(idPosterior);
        anterior.adicionarEventoPosterior(posterior);
    }
}
