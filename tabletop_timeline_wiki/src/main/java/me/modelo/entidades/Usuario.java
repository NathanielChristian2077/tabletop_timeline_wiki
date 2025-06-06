package me.modelo.entidades;

import java.util.UUID;

import me.modelo.enums.TipoUsuario;

public class Usuario {
    private final String id;
    private String nome;
    private String senha;
    private TipoUsuario tipo;
    /**
 * Representa um usuário do sistema, podendo ser mestre ou jogador.
 * Controla permissões e autoria, sendo base para validação de interações e filtros de acesso.
 * Demonstra encapsulamento e uso de enum para modelar papéis.
 */
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

    public void setNome(String nome) { this.nome = nome; }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) { this.senha = senha; }

    public TipoUsuario getTipo() {
        return tipo;
    }

    public void setTipo(TipoUsuario tipo) { this.tipo = tipo; }

    public boolean isMestre() {
        return tipo == TipoUsuario.MESTRE;
    }

    public boolean autenticar(String senhaInformada) {
        return this.senha.equals(senhaInformada);
    }
}
