package me.modelo.entidades;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import me.modelo.abstracts.EntradaDiario;
import me.modelo.enums.TipoNota;

public class Nota extends EntradaDiario {
    private final Usuario autor;
    private TipoNota tipo;
    private Set<String> destinatarios;

    public Nota(String titulo, String descricao, LocalDate date, TipoNota tipo, Usuario autor) {
        super(titulo, descricao, date);
        this.tipo = tipo;
        this.autor = autor;
        this.destinatarios = new HashSet<>();
    }

    public Usuario getAutor() {
        return autor;
    }

    public TipoNota getTipo() {
        return tipo;
    }

    public void setTipo(TipoNota tipo) {
        this.tipo = tipo;
    }

    public Set<String> getDestinatarios() {
        return destinatarios;
    }

    public void adicionarDestinatario(String idUsuario) {
        destinatarios.add(idUsuario);
    }

    @Override
    public String resumo() {
        return titulo + " (Nota - " + tipo + ")";
    }
}
