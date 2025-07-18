package me.modelo.entidades;

import java.util.HashSet;
import java.util.Set;

import me.modelo.abstracts.EntradaDiario;
import me.modelo.enums.TipoNota;
/**
 * Representa uma anotação do jogador ou mestre, com controle de visibilidade.
 * Pode ser pública, restrita (a certos usuários) ou privada.
 * Usa encapsulamento para proteger autor e visibilidade, e aplica herança de EntradaDiario.
 */
public class Nota extends EntradaDiario {
    private final Usuario autor;
    private TipoNota tipo;
    private Set<String> destinatarios;

    public Nota(String titulo, String descricao, TipoNota tipo, Usuario autor) {
        super(titulo, descricao);
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
