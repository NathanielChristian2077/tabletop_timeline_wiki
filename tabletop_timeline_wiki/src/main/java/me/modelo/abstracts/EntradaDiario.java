package me.modelo.abstracts;

import java.util.Objects;
import java.util.UUID;

public abstract class EntradaDiario {
    protected String id;
    protected String campanhaid;
    protected String titulo;
    protected String descricao;

    public EntradaDiario(String titulo, String descricao) {
        this.id = UUID.randomUUID().toString();
        this.titulo = titulo;
        this.descricao = descricao;
    }

    public EntradaDiario(String id, String campanhaid, String titulo, String descricao) {
        this.id = id;
        this.campanhaid = campanhaid;
        this.titulo = titulo;
        this.descricao = descricao;
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

    public String getCampanhaid() {
        return campanhaid;
    }

    public void setCampanhaid(String campanhaid) {
        this.campanhaid = campanhaid;
    }

    public String getDescricao() {
        return descricao;
    }

    

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        EntradaDiario other = (EntradaDiario) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public abstract String resumo();
}
