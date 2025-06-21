package me.persistencia;

import me.modelo.entidades.Usuario;
import me.modelo.enums.TipoUsuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DAOUsuario {

    private final Connection conn;

    public DAOUsuario() throws SQLException {
        this.conn = ConexaoBD.getConnection();
    }

    public void salvar(Usuario u) throws SQLException {
        String sql = "INSERT INTO usuario (id, nome, senha, tipo) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, u.getId());
            stmt.setString(2, u.getNome());
            stmt.setString(3, u.getSenha());
            stmt.setString(4, u.getTipo().name());
            stmt.executeUpdate();
        }
    }

    public Usuario buscarPorId(String id) throws SQLException {
        String sql = "SELECT * FROM usuario WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Usuario(
                    rs.getString("nome"),
                    rs.getString("senha"),
                    TipoUsuario.valueOf(rs.getString("tipo"))
                );
            }
        }
        return null;
    }

    public Usuario buscarPorNome(String nome) throws SQLException {
        String sql = "SELECT * FROM usuario WHERE nome = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nome);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Usuario(
                    rs.getString("nome"),
                    rs.getString("senha"),
                    TipoUsuario.valueOf(rs.getString("tipo"))
                );
            }
        }
        return null;
    }

    public List<Usuario> listarTodos() throws SQLException {
        String sql = "SELECT * FROM usuario";
        List<Usuario> lista = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Usuario u = new Usuario(
                    rs.getString("nome"),
                    rs.getString("senha"),
                    TipoUsuario.valueOf(rs.getString("tipo"))
                );
                lista.add(u);
            }
        }
        return lista;
    }

    public void atualizar(Usuario u) throws SQLException {
        String sql = "UPDATE usuario SET nome = ?, senha = ?, tipo = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, u.getNome());
            stmt.setString(2, u.getSenha());
            stmt.setString(3, u.getTipo().name());
            stmt.setString(4, u.getId());
            stmt.executeUpdate();
        }
    }

    public void deletar(String id) throws SQLException {
        String sql = "DELETE FROM usuario WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        }
    }
}
