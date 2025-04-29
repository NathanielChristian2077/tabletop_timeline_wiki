package me.controle;

import me.modelo.entidades.Nota;
import me.modelo.entidades.Usuario;
import me.modelo.enums.TipoNota;
import me.modelo.exceptions.ElementoNaoEncontradoException;
import me.modelo.exceptions.AssociacaoInvalidaException;

import java.time.LocalDate;
import java.util.List;

public class GerenciadorNota extends GerenciadorEntradaDiario<Nota> {

    public Nota criarNota(String titulo, String texto, LocalDate data, TipoNota tipo, Usuario autor) {
        Nota nota = new Nota(titulo, texto, data, tipo, autor);
        adicionar(nota);
        return nota;
    }

    public List<Nota> listarVisiveisPara(Usuario usuario) {
        return entradas.stream().filter(nota ->
            nota.getTipo() == TipoNota.PUBLICA ||
            (nota.getTipo() == TipoNota.RESTRITA && nota.getDestinatarios().contains(usuario.getId())) ||
            (nota.getTipo() == TipoNota.PRIVADA && nota.getAutor().getId().equals(usuario.getId()))
        ).toList();
    }

    public void atualizarDescricao(String id, String novaDescricao, Usuario solicitante)
            throws ElementoNaoEncontradoException, AssociacaoInvalidaException {
        Nota nota = buscarPorId(id);
        verificarAutoria(nota, solicitante);
        nota.setDescricao(novaDescricao);
    }

    public void atualizarVisibilidade(String id, TipoNota novoTipo, Usuario solicitante)
            throws ElementoNaoEncontradoException, AssociacaoInvalidaException {
        Nota nota = buscarPorId(id);
        verificarAutoria(nota, solicitante);
        nota.setTipo(novoTipo);
    }

    public void adicionarDestinatario(String id, String idDestinatario, Usuario solicitante)
            throws ElementoNaoEncontradoException, AssociacaoInvalidaException {
        Nota nota = buscarPorId(id);
        verificarAutoria(nota, solicitante);
        nota.adicionarDestinatario(idDestinatario);
    }

    public void remover(String id, Usuario solicitante)
            throws ElementoNaoEncontradoException, AssociacaoInvalidaException {
        Nota nota = buscarPorId(id);
        verificarAutoria(nota, solicitante);
        entradas.remove(nota);
    }

    private void verificarAutoria(Nota nota, Usuario solicitante) throws AssociacaoInvalidaException {
        if (!nota.getAutor().getId().equals(solicitante.getId())) {
            throw new AssociacaoInvalidaException("Usuário não autorizado a modificar esta nota.");
        }
    }
}
