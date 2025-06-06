package me.controle;

import me.modelo.entidades.Usuario;
import me.modelo.enums.TipoUsuario;
import me.modelo.exceptions.ElementoNaoEncontradoException;

import java.util.ArrayList;
import java.util.List;

public class GerenciadorUsuario {
    private List<Usuario> usuarios = new ArrayList<>();

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

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

    public void remover(Usuario u) {
        usuarios.remove(u);
    }

    public void atualizarUsuario(Usuario usuarioAtualizado) throws ElementoNaoEncontradoException {
        for (Usuario u : usuarios) {
            if (u.getId().equals(usuarioAtualizado.getId())) {
                u.setNome(usuarioAtualizado.getNome());
                u.setSenha(usuarioAtualizado.getSenha());
                return;
            }
        }
        throw new ElementoNaoEncontradoException("Usuário com ID " + usuarioAtualizado.getId() + " não encontrado.");
    }

}
