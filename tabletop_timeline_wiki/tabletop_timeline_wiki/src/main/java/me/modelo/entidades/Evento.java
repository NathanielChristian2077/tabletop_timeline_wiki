package me.modelo.entidades;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import me.modelo.abstracts.EntradaDiario;
import me.modelo.interfaces.Associavel;
import me.modelo.interfaces.Exportavel;

public class Evento extends EntradaDiario implements Associavel, Exportavel {
    private List<Personagem> personagensRelacionados;
    private List<Local> locaisRelacionados;
    private List<Objeto> objetosRelacionados;

    private List<Evento> eventosAnteriores;
    private List<Evento> eventosPosteriores;
    
    public Evento(String titulo, String descricao, LocalDate date) {
        super(titulo, descricao, date);
        this.personagensRelacionados = new ArrayList<>();
        this.locaisRelacionados = new ArrayList<>();
        this.objetosRelacionados = new ArrayList<>();
        this.eventosAnteriores = new ArrayList<>();
        this.eventosPosteriores = new ArrayList<>();
    }

    public List<Evento> getEventosAnteriores() {
        return eventosAnteriores;
    }

    public List<Evento> getEventosPosteriores() {
        return eventosPosteriores;
    }
    
    public void adicionarEventoAnterior(Evento e) {
        if (!eventosAnteriores.contains(e)) {
            eventosAnteriores.add(e);
            e.adicionarEventoPosterior(this);
        }
    }
}
