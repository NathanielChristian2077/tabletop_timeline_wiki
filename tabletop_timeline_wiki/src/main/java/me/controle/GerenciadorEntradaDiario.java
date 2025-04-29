package me.controle;

import me.modelo.abstracts.EntradaDiario;
import me.modelo.exceptions.ElementoNaoEncontradoException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.time.LocalDate;

/**
 * Classe abstrata que centraliza a lógica de gerenciamento de entradas do diário (Evento e Nota).
 * Permite listagem por data, atualização e busca genérica.
 * Demonstra uso avançado de herança com generics para reaproveitar funcionalidades.
 */
public class GerenciadorEntradaDiario<T extends EntradaDiario> {
    protected List<T> entradas = new ArrayList<>();

    public void adicionar(T entrada) {
        entradas.add(entrada);
    }

    public void remover(String id) throws ElementoNaoEncontradoException {
        T entrada = buscarPorId(id);
        entradas.remove(entrada);
    }

    public List<T> listarTodas() {
        return new ArrayList<>(entradas);
    }

    public List<T> listarOrdenadoPorData() {
        return entradas.stream()
            .sorted(Comparator.comparing(EntradaDiario::getDate))
            .toList();
    }

    public List<T> listarEntreDatas(LocalDate inicio, LocalDate fim) {
        return entradas.stream()
            .filter(e -> !e.getDate().isBefore(inicio) && !e.getDate().isAfter(fim))
            .toList();
    }

    public T buscarPorId(String id) throws ElementoNaoEncontradoException {
        return entradas.stream()
            .filter(e -> e.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new ElementoNaoEncontradoException("Entrada com ID não encontrada: " + id));
    }

    public List<T> buscarPorTitulo(String titulo) {
        return entradas.stream()
            .filter(e -> e.getTitulo().equalsIgnoreCase(titulo))
            .toList();
    }

    public void atualizarDescricao(String id, String novaDescricao) throws ElementoNaoEncontradoException {
        T entrada = buscarPorId(id);
        entrada.setDescricao(novaDescricao);
    }

    public void atualizarTitulo(String id, String novoTitulo) throws ElementoNaoEncontradoException {
        T entrada = buscarPorId(id);
        entrada.setTitulo(novoTitulo);
    }
}
