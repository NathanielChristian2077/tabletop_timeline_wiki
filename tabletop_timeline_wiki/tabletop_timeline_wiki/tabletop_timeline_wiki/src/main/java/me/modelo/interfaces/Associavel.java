package me.modelo.interfaces;

import java.util.List;

import me.modelo.entidades.Evento;
import me.modelo.entidades.Local;
import me.modelo.entidades.Objeto;
import me.modelo.entidades.Personagem;

public interface Associavel {
    String getId();
    String getNome();
    String getDescricao();

    List<Evento> getEventosRelacionados();
    List<Local> getLocaisRelacionados();
    List<Objeto> getObjetosRelacionados();
    List<Personagem> getPersonagensRelacionados();

    void adicionarEvento(Evento e);
    void adicionarLocal(Local l);
    void adicionarObjeto(Objeto o);
    void adicionarPersonagem(Personagem p);

    void removerEvento(Evento e);
    void removerLocal(Local l);
    void removerObjeto(Objeto o);
    void removerPersonagem(Personagem p);
}
