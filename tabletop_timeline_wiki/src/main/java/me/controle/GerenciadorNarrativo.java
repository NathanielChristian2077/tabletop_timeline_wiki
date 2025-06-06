package me.controle;

import me.modelo.abstracts.ElementoNarrativo;
import me.modelo.exceptions.ElementoNaoEncontradoException;

import java.util.ArrayList;
import java.util.List;
/**
 * Gerenciador genérico para elementos narrativos como Personagem, Local e Objeto.
 * Aplica polimorfismo parametrizado com generics e contém métodos padrão de CRUD e busca.
 * Exemplo de reutilização de lógica entre diferentes entidades.
 */
public class GerenciadorNarrativo<T extends ElementoNarrativo> {
    protected List<T> elementos = new ArrayList<>();

    public void adicionar(T elemento) {
        elementos.add(elemento);
    }

    public List<T> listarTodos() {
        return new ArrayList<>(elementos);
    }

    public T buscarPorId(String id) throws ElementoNaoEncontradoException {
        return elementos.stream()
            .filter(e -> e.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new ElementoNaoEncontradoException("Elemento com ID não encontrado: " + id));
    }

    public List<T> buscarPorNome(String nome) {
        List<T> encontrados = new ArrayList<>();
        for (T e : elementos) {
            if (e.getNome().equalsIgnoreCase(nome)) {
                encontrados.add(e);
            }
        }
        return encontrados;
    }

    public void removerPorId(String id) throws ElementoNaoEncontradoException {
        T elemento = buscarPorId(id);
        elementos.remove(elemento);
    }

    public void remover(T t) {
        elementos.remove(t);
    }
}
