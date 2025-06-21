package me.persistencia;

import me.modelo.entidades.Evento;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DAOEvento {

    private final Connection conn;

    public DAOEvento() throws SQLException {
        this.conn = ConexaoBD.getConnection();
    }

    public void salvar(Evento e, String campanhaId) throws SQLException {
        String sql = "INSERT INTO evento (id, titulo, descricao, anterior_id, posterior_id, campanha_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, e.getId());
            stmt.setString(2, e.getTitulo());
            stmt.setString(3, e.getDescricao());
            stmt.setString(4, e.getAnterior() != null ? e.getAnterior().getId() : null);
            stmt.setString(5, e.getPosterior() != null ? e.getPosterior().getId() : null);
            stmt.setString(6, campanhaId);
            stmt.executeUpdate();
        }
    }

    public Evento buscarPorId(String id) throws SQLException {
        String sql = "SELECT * FROM evento WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Evento e = new Evento(rs.getString("titulo"), rs.getString("descricao"));
                e.setId(rs.getString("id"));
                // Relacionamentos serão resolvidos depois se necessário
                return e;
            }
        }
        return null;
    }

    public List<Evento> listarTodos() throws SQLException {
        String sql = "SELECT * FROM evento";
        List<Evento> lista = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Evento e = new Evento(rs.getString("titulo"), rs.getString("descricao"));
                e.setId(rs.getString("id"));
                lista.add(e);
            }
        }
        return lista;
    }

    public void atualizar(Evento e) throws SQLException {
        String sql = "UPDATE evento SET titulo = ?, descricao = ?, anterior_id = ?, posterior_id = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, e.getTitulo());
            stmt.setString(2, e.getDescricao());
            stmt.setString(3, e.getAnterior() != null ? e.getAnterior().getId() : null);
            stmt.setString(4, e.getPosterior() != null ? e.getPosterior().getId() : null);
            stmt.setString(5, e.getId());
            stmt.executeUpdate();
        }
    }

    public void deletar(String id) throws SQLException {
        String sql = "DELETE FROM evento WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        }
    }
}
