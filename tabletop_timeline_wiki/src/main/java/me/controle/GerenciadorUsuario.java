package me.controle;

import me.modelo.entidades.Usuario;
import me.modelo.enums.TipoUsuario;
import me.modelo.exceptions.ElementoNaoEncontradoException;
import me.persistencia.DAOUsuario;

import java.sql.SQLException;
import java.util.List;

public class GerenciadorUsuario {
    private final DAOUsuario daoUsuario;

    public GerenciadorUsuario() throws SQLException {
        this.daoUsuario = new DAOUsuario();
    }

    public void adicionarUsuario(Usuario usuario) throws SQLException {
        daoUsuario.salvar(usuario);
    }

    public Usuario buscarPorId(String id) throws ElementoNaoEncontradoException, SQLException {
        return daoUsuario.buscarPorId(id)
                .orElseThrow(() -> new ElementoNaoEncontradoException("User not found."));
    }

    public Usuario buscarPorNome(String nome) throws ElementoNaoEncontradoException, SQLException {
        return daoUsuario.buscarPorNome(nome)
                .orElseThrow(() -> new ElementoNaoEncontradoException("User not found."));
    }

    public List<Usuario> listarPorTipo(TipoUsuario tipo) throws SQLException {
        return daoUsuario.listarPorTipo(tipo);
    }

    public void removerUsuarioPorId(String id) throws ElementoNaoEncontradoException, SQLException {
        if (!daoUsuario.removerPorId(id)) {
            throw new ElementoNaoEncontradoException("User not found.");
        }
    }

    public void remover(Usuario u) throws SQLException {
        daoUsuario.removerPorId(u.getId());
    }

    public void atualizarUsuario(Usuario usuarioAtualizado) throws ElementoNaoEncontradoException, SQLException {
        if (!daoUsuario.atualizar(usuarioAtualizado)) {
            throw new ElementoNaoEncontradoException("User with ID " + usuarioAtualizado.getId() + " not found.");
        }
    }
}
