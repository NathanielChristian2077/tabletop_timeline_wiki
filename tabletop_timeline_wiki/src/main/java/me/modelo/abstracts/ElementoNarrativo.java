package me.modelo.abstracts;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import me.modelo.entidades.Evento;
import me.modelo.entidades.Local;
import me.modelo.entidades.Objeto;
import me.modelo.entidades.Personagem;
import me.modelo.interfaces.Associavel;
import me.modelo.interfaces.Exportavel;

/**
 * Classe abstrata que representa um elemento narrativo do sistema (como personagem, objeto, local).
 * Implementa as interfaces Associavel e Exportavel para garantir integração com outros elementos da narrativa.
 * Aplica herança, reutilização de código e polimorfismo.
 */
public abstract class ElementoNarrativo implements Associavel, Exportavel {
    protected String id;
    protected String nome;
    protected String descricao;

    protected List<Evento> eventosRelacionados = new ArrayList<>();
    protected List<Local> locaisRelacionados = new ArrayList<>();
    protected List<Objeto> objetosRelacionados = new ArrayList<>();
    protected List<Personagem> personagensRelacionados = new ArrayList<>();

    public ElementoNarrativo(String nome) {
        this.id = UUID.randomUUID().toString();
        this.nome = nome;
        this.descricao = null;
    }

    @Override
    public void adicionarEvento(Evento e) {
        if (!eventosRelacionados.contains(e)) 
            eventosRelacionados.add(e);
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
    public List<Evento> getEventosRelacionados() {
        return eventosRelacionados;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public List<Local> getLocaisRelacionados() {
        return locaisRelacionados;
    }

    @Override
    public String getNome() {
        return nome;
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
    public void removerEvento(Evento e) {
        eventosRelacionados.remove(e);        
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
    public abstract String exportar();

}
