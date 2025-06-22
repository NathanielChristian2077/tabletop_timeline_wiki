package me.persistencia;

import me.modelo.entidades.Usuario;
import me.modelo.enums.TipoUsuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class DAOUsuario {

    public void salvar(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuario (id, nome, senha, tipo) VALUES (?, ?, ?, ?)";
        try (Connection con = ConexaoBD.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setObject(1, UUID.fromString(usuario.getId()));
            stmt.setString(2, usuario.getNome());
            stmt.setString(3, usuario.getSenha());
            stmt.setString(4, usuario.getTipo().name());
            stmt.executeUpdate();
        }
    }

    public Optional<Usuario> buscarPorId(String id) throws SQLException {
        String sql = "SELECT * FROM usuario WHERE id = ?";
        try (Connection con = ConexaoBD.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setObject(1, UUID.fromString(id));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapearUsuario(rs));
            }
            return Optional.empty();
        }
    }

    public Optional<Usuario> buscarPorNome(String nome) throws SQLException {
        String sql = "SELECT * FROM usuario WHERE nome = ?";
        try (Connection con = ConexaoBD.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, nome);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapearUsuario(rs));
            }
            return Optional.empty();
        }
    }

    public List<Usuario> listarPorTipo(TipoUsuario tipo) throws SQLException {
        String sql = "SELECT * FROM usuario WHERE tipo = ?";
        List<Usuario> usuarios = new ArrayList<>();
        try (Connection con = ConexaoBD.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, tipo.name());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }
        }
        return usuarios;
    }

    public boolean atualizar(Usuario usuario) throws SQLException {
        String sql = "UPDATE usuario SET nome = ?, senha = ?, tipo = ? WHERE id = ?";
        try (Connection con = ConexaoBD.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getSenha());
            stmt.setString(3, usuario.getTipo().name());
            stmt.setObject(4, UUID.fromString(usuario.getId()));
            int linhas = stmt.executeUpdate();
            return linhas > 0;
        }
    }

    public boolean removerPorId(String id) throws SQLException {
        String sql = "DELETE FROM usuario WHERE id = ?";
        try (Connection con = ConexaoBD.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setObject(1, java.util.UUID.fromString(id)); // CORREÇÃO
            int linhas = stmt.executeUpdate();
            return linhas > 0;
        }
    }

    // Se quiser manter o método deletar:
    public void deletar(String id) throws SQLException {
        String sql = "DELETE FROM usuario WHERE id = ?";
        try (Connection con = ConexaoBD.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setObject(1, java.util.UUID.fromString(id)); // CORREÇÃO
            stmt.executeUpdate();
        }
    }

    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String nome = rs.getString("nome");
        String senha = rs.getString("senha");
        TipoUsuario tipo = TipoUsuario.valueOf(rs.getString("tipo"));
        return new Usuario(id, nome, senha, tipo); // Usa o id do banco!
    }
}
