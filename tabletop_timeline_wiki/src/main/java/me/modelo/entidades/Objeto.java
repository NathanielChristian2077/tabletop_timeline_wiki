package me.modelo.entidades;

import me.modelo.abstracts.ElementoNarrativo;

public class Objeto extends ElementoNarrativo {
    public Objeto(String nome) {
        super(nome);
    }

    public Objeto(String id, String campanhaid, String nome, String descricao) {
        super(id, campanhaid, nome, descricao);
    }

    @Override
    public String exportar() {
        return String.format("Objeto: %s\n%s", nome, descricao != null ? descricao : "Sem descrição");
    }
}