package me.modelo.entidades;

import me.modelo.abstracts.ElementoNarrativo;

public class Personagem extends ElementoNarrativo {
    public Personagem(String nome) {
        super(nome);
    }

    public Personagem(String id, String campanhaid, String nome, String descricao) {
        super(id, campanhaid, nome, descricao);
    }

    @Override
    public String exportar() {
        return String.format("Personagem: %s\n%s", nome, descricao != null ? descricao : "Sem descrição");
    }

    @Override
    public String toString() {
        return this.nome;
    }
}