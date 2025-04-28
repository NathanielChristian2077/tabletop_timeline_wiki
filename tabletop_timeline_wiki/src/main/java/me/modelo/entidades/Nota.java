package me.modelo.entidades;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import me.modelo.abstracts.EntradaDiario;
import me.modelo.enums.TipoNota;

public class Nota extends EntradaDiario {
    private TipoNota visibilidade;
    private Set<String> idsDestinatarios = new HashSet<>();

    public Nota(String titulo, String descricao, LocalDate date, TipoNota visibilidade) {
        super(titulo, descricao, date);
        this.visibilidade = visibilidade;
    }

    public TipoNota getVisibilidade() {
        return visibilidade;
    }

    public void setVisibilidade(TipoNota visibilidade) {
        this.visibilidade = visibilidade;
    }

    public void adicionarDestinatario(String idJogador) {
        idsDestinatarios.add(idJogador);
    }

    public void removerDestinatario(String idJogador) {
        idsDestinatarios.remove(idJogador);
    }

    public Set<String> getDestinatarios() {
        return idsDestinatarios;
    }

    @Override
    public String resumo() {
        return titulo + " (Nota - " + visibilidade + ")";
    }
}
