package me.modelo.entidades;

import java.util.UUID;

import me.modelo.enums.TipoUsuario;

public class Usuario {
    private final String id;
    private final String nome;
    private final String senha;
    private final TipoUsuario tipo;

    public Usuario(String nome, String senha, TipoUsuario tipo) {
        this.id = UUID.randomUUID().toString();
        this.nome = nome;
        this.senha = senha;
        this.tipo = tipo;
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getSenha() {
        return senha;
    }

    public TipoUsuario getTipo() {
        return tipo;
    }

    public boolean isMestre() {
        return tipo == TipoUsuario.MESTRE;
    }

    public boolean autenticar(String senhaInformada) {
        return this.senha.equals(senhaInformada);
    }
}
