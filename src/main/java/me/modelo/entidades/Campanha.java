package me.modelo.entidades;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import me.modelo.abstracts.ElementoNarrativo;
import me.modelo.exceptions.ElementoNaoEncontradoException;
/**
 * Representa uma campanha de RPG, contendo todos os eventos e gerenciada por um mestre.
 * Aplica agregação e controle de propriedade sobre os elementos narrativos associados.
 * Permite centralizar a estrutura da narrativa.
 */
public class Campanha {
    private String id;
    private String nome;
    private String descricao;

    private Map<String, ElementoNarrativo> elementos;
    private List<Evento> eventos;

    public Campanha(String nome, String descricao) {
        this.id = UUID.randomUUID().toString();
        this.nome = nome;
        this.descricao = descricao;
        this.elementos = new HashMap<>();
        this.eventos = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void adicionarElemento(ElementoNarrativo e) {
        elementos.put(e.getId(), e);
    }

    public void removerElemento(String id) throws ElementoNaoEncontradoException {
        if (!elementos.containsKey(id)) {
            throw new ElementoNaoEncontradoException("Elemento com ID " + id + " nao encontrado na campanha.");
        }
        elementos.remove(id);
    }

    public ElementoNarrativo buscarElemento(String id) throws ElementoNaoEncontradoException {
        if (!elementos.containsKey(id)) {
            throw new ElementoNaoEncontradoException("Elemento com ID " + id + " nao encontrado.");
        }
        return elementos.get(id);
    }

    public void adicionarEvento(Evento evento) {
        eventos.add(evento);
    }

    public List<Evento> getEventos() {
        return eventos;
    }

    public List<ElementoNarrativo> listarTodosElementos() {
        return new ArrayList<>(elementos.values());
    }

    public List<Personagem> getPersonagens() {
        List<Personagem> personagens = new ArrayList<>();
        for (ElementoNarrativo e : elementos.values()) {
            if (e instanceof Personagem) {
                personagens.add((Personagem) e);
            }
        }
        return personagens;
    }

    public List<Objeto> getObjetos() {
        List<Objeto> objetos = new ArrayList<>();
        for (ElementoNarrativo e : elementos.values()) {
            if (e instanceof Objeto) {
                objetos.add((Objeto) e);
            }
        }
        return objetos;
    }

    public List<Local> getLocais() {
        List<Local> locais = new ArrayList<>();
        for (ElementoNarrativo e : elementos.values()) {
            if (e instanceof Local) {
                locais.add((Local) e);
            }
        }
        return locais;
    }

    public List<Personagem> buscarPersonagensPorNome(String nome) {
        List<Personagem> encontrados = new ArrayList<>();
        for (Personagem p : getPersonagens()) {
            if (p.getNome().equalsIgnoreCase(nome)) encontrados.add(p);
        }
        return encontrados;
    }
}