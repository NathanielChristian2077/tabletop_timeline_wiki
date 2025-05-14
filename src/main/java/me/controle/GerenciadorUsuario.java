package me.controle;

import me.modelo.entidades.Usuario;
import me.modelo.enums.TipoUsuario;
import me.modelo.exceptions.ElementoNaoEncontradoException;

import java.util.ArrayList;
import java.util.List;

public class GerenciadorUsuario {
    private final List<Usuario> usuarios = new ArrayList<>();

    public void adicionarUsuario(Usuario usuario) {
        usuarios.add(usuario);
    }

    public Usuario buscarPorId(String id) throws ElementoNaoEncontradoException {
        return usuarios.stream()
            .filter(u -> u.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new ElementoNaoEncontradoException("Usuário não encontrado."));
    }

    public Usuario buscarPorNome(String nome) throws ElementoNaoEncontradoException {
        return usuarios.stream()
            .filter(u -> u.getNome().equalsIgnoreCase(nome))
            .findFirst()
            .orElseThrow(() -> new ElementoNaoEncontradoException("Usuário não encontrado."));
    }

    public List<Usuario> listarPorTipo(TipoUsuario tipo) {
        List<Usuario> resultado = new ArrayList<>();
        for (Usuario u : usuarios) {
            if (u.getTipo() == tipo) {
                resultado.add(u);
            }
        }
        return resultado;
    }

    public void removerUsuarioPorId(String id) throws ElementoNaoEncontradoException {
        Usuario u = buscarPorId(id);
        usuarios.remove(u);
    }
}
