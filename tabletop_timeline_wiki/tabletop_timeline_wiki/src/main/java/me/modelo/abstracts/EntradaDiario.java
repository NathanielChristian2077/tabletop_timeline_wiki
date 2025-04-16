package me.modelo.abstracts;

import java.time.LocalDate;
import java.util.UUID;

public abstract class EntradaDiario {
    protected String id;
    protected String titulo;
    protected String descricao;
    protected LocalDate date;
    
    public EntradaDiario(String titulo, String descricao, LocalDate date) {
        this.id = UUID.randomUUID().toString();
        this.titulo = titulo;
        this.descricao = descricao;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public abstract String resumo();
}
