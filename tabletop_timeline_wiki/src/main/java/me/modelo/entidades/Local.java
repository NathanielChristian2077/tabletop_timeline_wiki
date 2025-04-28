package me.modelo.entidades;

import me.modelo.abstracts.ElementoNarrativo;

public class Local extends ElementoNarrativo {
    public Local(String nome) {
        super(nome);
    }

    @Override
    public String exportar() {
        return String.format("Local: %s\n%s", nome, descricao != null ? descricao : "Sem descrição");
    }
}
