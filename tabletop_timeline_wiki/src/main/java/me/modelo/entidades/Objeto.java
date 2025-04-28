package me.modelo.entidades;

import me.modelo.abstracts.ElementoNarrativo;

public class Objeto extends ElementoNarrativo {
    public Objeto(String nome) {
        super(nome);
    }

    @Override
    public String exportar() {
        return String.format("Objeto: %s\n%s", nome, descricao != null ? descricao : "Sem descrição");
    }
}