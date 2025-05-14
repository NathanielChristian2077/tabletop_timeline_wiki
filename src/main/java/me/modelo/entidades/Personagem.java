package me.modelo.entidades;

import me.modelo.abstracts.ElementoNarrativo;

public class Personagem extends ElementoNarrativo {
    public Personagem(String nome) {
        super(nome);
    }

    @Override
    public String exportar() {
        return String.format("Personagem: %s\n%s", nome, descricao != null ? descricao : "Sem descrição");
    }
}