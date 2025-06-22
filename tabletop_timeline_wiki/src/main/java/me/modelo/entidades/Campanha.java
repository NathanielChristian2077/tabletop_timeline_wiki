package me.modelo.entidades;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import me.modelo.abstracts.ElementoNarrativo;
import me.modelo.exceptions.ElementoNaoEncontradoException;

/**
 * Representa uma campanha de RPG, contendo todos os eventos e gerenciada por um
 * mestre.
 * Aplica agregação e controle de propriedade sobre os elementos narrativos
 * associados.
 * Permite centralizar a estrutura da narrativa.
 */
public class Campanha {
    private String id;
    private String nome;
    private String descricao;

    private String imagePath;
    private static final String DEFAULT_IMAGE_PATH = "/me/gui/images/nullPlaceholder.jpg";

    private Map<String, ElementoNarrativo> elementos;
    private List<Evento> eventos;

    public Campanha(String nome, String descricao) {
        this.id = UUID.randomUUID().toString();
        this.nome = nome;
        this.descricao = descricao;
        this.elementos = new HashMap<>();
        this.eventos = new ArrayList<>();
        this.imagePath = DEFAULT_IMAGE_PATH;
    }

    public Campanha(String id, String nome, String descricao) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.elementos = new HashMap<>();
        this.eventos = new ArrayList<>();
        this.imagePath = DEFAULT_IMAGE_PATH;
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

    public String getImagePath() {
        return (imagePath == null || imagePath.isBlank()) ? DEFAULT_IMAGE_PATH : imagePath;
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
            if (p.getNome().equalsIgnoreCase(nome))
                encontrados.add(p);
        }
        return encontrados;
    }

    public void setCaminhoImagem(String imagePath) {
        if (imagePath == null || imagePath.isBlank()) {
            this.imagePath = DEFAULT_IMAGE_PATH;
        } else {
            this.imagePath = imagePath;
        }
    }

    public boolean imageExists() {
        String path = getImagePath();
        if (path.equals(DEFAULT_IMAGE_PATH))
            return true;
        return path != null && !path.isBlank() && new File(path).exists();
    }
}