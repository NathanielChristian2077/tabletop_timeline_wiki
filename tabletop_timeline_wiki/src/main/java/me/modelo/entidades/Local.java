package me.modelo.entidades;

import me.modelo.abstracts.ElementoNarrativo;

public class Local extends ElementoNarrativo {
    public Local(String nome) {
        super(nome);
    }

    public Local(String id, String campanhaid, String nome, String descricao) {
        super(id, campanhaid, nome, descricao);
    }

    @Override
    public String exportar() {
        return String.format("Local: %s\n%s", nome, descricao != null ? descricao : "Sem descrição");
    }

    @Override
    public String toString() {
        return this.nome;
    }
}
