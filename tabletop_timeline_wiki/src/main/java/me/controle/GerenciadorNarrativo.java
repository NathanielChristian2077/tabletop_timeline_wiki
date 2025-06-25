package me.controle;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import me.modelo.abstracts.ElementoNarrativo;
import me.modelo.exceptions.ElementoNaoEncontradoException;
/**
 * Gerenciador genérico para elementos narrativos como Personagem, Local e Objeto.
 * Aplica polimorfismo parametrizado com generics e contém métodos padrão de CRUD e busca.
 * Exemplo de reutilização de lógica entre diferentes entidades.
 */
public abstract class GerenciadorNarrativo<T extends ElementoNarrativo> {
    protected List<T> elementos = new ArrayList<>();

    public void adicionar(T elemento) throws SQLException {
        elementos.add(elemento);
        salvarNoBanco(elemento);
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

    public void removerPorId(String id) throws ElementoNaoEncontradoException, SQLException {
        T elemento = buscarPorId(id);
        elementos.remove(elemento);
        deletarDoBanco(id);
    }

    public void remover(T t) throws SQLException {
        elementos.remove(t);
        deletarDoBanco(t.getId());
    }

    protected abstract void salvarNoBanco(T elemento) throws SQLException;

    protected abstract void deletarDoBanco(String id) throws SQLException;
}
