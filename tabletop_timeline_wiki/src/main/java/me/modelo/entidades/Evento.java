package me.modelo.entidades;

import java.util.ArrayList;
import java.util.List;

import me.modelo.abstracts.EntradaDiario;
import me.modelo.interfaces.Associavel;
import me.modelo.interfaces.Exportavel;

/**
 * Representa um evento temporal dentro de uma campanha.
 * Estende EntradaDiario e implementa comportamento associativo entre locais,
 * personagens, objetos e outros eventos.
 * Demonstra composição, encapsulamento e comportamento relacional.
 */
public class Evento extends EntradaDiario implements Associavel, Exportavel {
    private List<Personagem> personagensRelacionados = new ArrayList<>();
    private List<Local> locaisRelacionados = new ArrayList<>();
    private List<Objeto> objetosRelacionados = new ArrayList<>();
    private List<Evento> eventosAnteriores = new ArrayList<>();
    private List<Evento> eventosPosteriores = new ArrayList<>();

    public Evento(String titulo, String descricao) {
        super(titulo, descricao);
        setCampanhaid(campanhaid);
    }

    public Evento(String id, String campanhaid, String titulo, String descricao) {
        super(id, campanhaid, titulo, descricao);
    }

    public void adicionarEventoAnterior(Evento e) {
        if (!eventosAnteriores.contains(e)) {
            eventosAnteriores.add(e);
            e.adicionarEventoPosterior(this);
        }
    }

    public Evento getAnterior() {
        return eventosAnteriores.get(eventosAnteriores.size() - 1);
    }

    public void setAnterior(Evento e) {
        if (e != null && !eventosAnteriores.contains(e)) {
            eventosAnteriores.add(e);
            e.setPosterior(this);
        }
    }

    public void adicionarEventoPosterior(Evento e) {
        if (!eventosPosteriores.contains(e)) {
            eventosPosteriores.add(e);
        }
    }

    public Evento getPosterior() {
        return eventosPosteriores.get(0);
    }

    public void setPosterior(Evento e) {
        if (e != null && !eventosPosteriores.contains(e)) {
            eventosPosteriores.add(e);
            e.setAnterior(this);
        }
    }

    @Override
    public void adicionarEvento(Evento e) {
        adicionarEventoAnterior(e);
    }

    @Override
    public void adicionarLocal(Local l) {
        if (!locaisRelacionados.contains(l))
            locaisRelacionados.add(l);
    }

    @Override
    public void adicionarObjeto(Objeto o) {
        if (!objetosRelacionados.contains(o))
            objetosRelacionados.add(o);
    }

    @Override
    public void adicionarPersonagem(Personagem p) {
        if (!personagensRelacionados.contains(p))
            personagensRelacionados.add(p);
    }

    @Override
    public void removerEvento(Evento e) {
        eventosAnteriores.remove(e);
        eventosPosteriores.remove(e);
    }

    @Override
    public void removerLocal(Local l) {
        locaisRelacionados.remove(l);
    }

    @Override
    public void removerObjeto(Objeto o) {
        objetosRelacionados.remove(o);
    }

    @Override
    public void removerPersonagem(Personagem p) {
        personagensRelacionados.remove(p);
    }

    @Override
    public List<Evento> getEventosRelacionados() {
        List<Evento> relacionados = new ArrayList<>();
        relacionados.addAll(eventosAnteriores);
        relacionados.addAll(eventosPosteriores);
        return relacionados;
    }

    @Override
    public List<Local> getLocaisRelacionados() {
        return locaisRelacionados;
    }

    @Override
    public List<Objeto> getObjetosRelacionados() {
        return objetosRelacionados;
    }

    @Override
    public List<Personagem> getPersonagensRelacionados() {
        return personagensRelacionados;
    }

    @Override
    public String getNome() {
        return titulo;
    }

    @Override
    public String exportar() {
        return String.format("Evento: %s (%s)\n%s", titulo, descricao);
    }

    @Override
    public String resumo() {
        return titulo;
    }
}