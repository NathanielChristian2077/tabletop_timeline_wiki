package me.controle;

import java.util.ArrayList;
import java.util.List;

import me.modelo.entidades.Campanha;
import me.modelo.exceptions.ElementoNaoEncontradoException;

public class GerenciadorCampanha {
    private List<Campanha> campanhas;

    public GerenciadorCampanha() {
        this.campanhas = new ArrayList<>();
    }

    public void criarCampanha(String nome, String descricao) {
        campanhas.add(new Campanha(nome, descricao));
    }

    public List<Campanha> listarCampanhas() {
        return campanhas;
    }

    public Campanha buscarCampanhaPorNome(String nome) throws ElementoNaoEncontradoException {
        return campanhas.stream()
                        .filter(c -> c.getNome()
                        .equalsIgnoreCase(nome))
                        .findFirst()
                        .orElseThrow(() -> new ElementoNaoEncontradoException("Campanha '"+ nome + "' nao encontrada."));
    }

    public void removerCapanha(Campanha c) throws ElementoNaoEncontradoException {
        campanhas.remove(c);
    }
}
