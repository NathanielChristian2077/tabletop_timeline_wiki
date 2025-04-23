package me.controle;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import me.modelo.entidades.Evento;
import me.modelo.exceptions.ElementoNaoEncontradoException;

public class GerenciadorEvento {
    private List<Evento> eventos;

    public GerenciadorEvento() {
        this.eventos = new ArrayList<>();
    }

    public Evento criarEvento(String titulo, String descricao, LocalDate data) {
        Evento novo = new Evento(titulo, descricao, data);
        eventos.add(novo);
        return novo;
    }

    public List<Evento> listarEventos() {
        return eventos;
    }

    public Evento buscarPorTitulo(String titulo) throws ElementoNaoEncontradoException {
        return eventos.stream()
                .filter(e -> e.getTitulo()
                .equalsIgnoreCase(titulo))
                .findFirst()
                .orElseThrow(() -> new ElementoNaoEncontradoException("Evento com titulo '" + titulo + "' não encontrado."));
    }

    public void removerEvento(String titulo) throws ElementoNaoEncontradoException {
        Evento e = buscarPorTitulo(titulo);
        eventos.remove(e);
    }

    public void atualizarDescricao(String titulo, String novaDescricao) throws ElementoNaoEncontradoException {
        Evento e = buscarPorTitulo(titulo);
        e.setDescricao(novaDescricao);
    }

    public void associarAnteriorPosterior(String tituloAnterior, String tituloPosterior) throws ElementoNaoEncontradoException {
        Evento anterior = buscarPorTitulo(tituloAnterior);
        Evento posterior = buscarPorTitulo(tituloPosterior);
        posterior.adicionarEventoAnterior(anterior);
    }
}