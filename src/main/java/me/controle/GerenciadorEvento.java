package me.controle;

import me.modelo.entidades.Evento;
import me.modelo.exceptions.ElementoNaoEncontradoException;

import java.time.LocalDate;
/**
 * Controla a criação e associação entre eventos dentro da linha do tempo da campanha.
 * Permite definir sequências temporais e relacionar eventos posteriores e anteriores.
 * Demonstra composição e lógica temporal entre objetos.
 */
public class GerenciadorEvento extends GerenciadorEntradaDiario<Evento> {

    public Evento criarEvento(String titulo, String descricao, LocalDate data) {
        Evento e = new Evento(titulo, descricao, data);
        adicionar(e);
        return e;
    }

    public void associarAnteriorPosterior(String idAnterior, String idPosterior) throws ElementoNaoEncontradoException {
        Evento anterior = buscarPorId(idAnterior);
        Evento posterior = buscarPorId(idPosterior);
        anterior.adicionarEventoPosterior(posterior);
    }
}
