package me.persistencia;

import me.modelo.entidades.Campanha;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DAOCampanha {

    private final Connection conn;

    public DAOCampanha() throws SQLException {
        this.conn = ConexaoBD.getConnection();
    }

    public void salvar(Campanha c) throws SQLException {
        String sql = "INSERT INTO campanha (id, nome, descricao, image_path) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, c.getId());
            stmt.setString(2, c.getNome());
            stmt.setString(3, c.getDescricao());
            stmt.setString(4, c.getImagePath());
            stmt.executeUpdate();
        }
    }

    public Campanha buscarPorId(String id) throws SQLException {
        String sql = "SELECT * FROM campanha WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Campanha c = new Campanha(rs.getString("nome"), rs.getString("descricao"));
                c.setCaminhoImagem(rs.getString("image_path"));
                return c;
            }
        }
        return null;
    }

    public List<Campanha> listarTodas() throws SQLException {
        String sql = "SELECT * FROM campanha";
        List<Campanha> lista = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Campanha c = new Campanha(rs.getString("nome"), rs.getString("descricao"));
                c.setCaminhoImagem(rs.getString("image_path"));
                lista.add(c);
            }
        }
        return lista;
    }

    public void atualizar(Campanha c) throws SQLException {
        String sql = "UPDATE campanha SET nome = ?, descricao = ?, image_path = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, c.getNome());
            stmt.setString(2, c.getDescricao());
            stmt.setString(3, c.getImagePath());
            stmt.setString(4, c.getId());
            stmt.executeUpdate();
        }
    }

    public void deletar(String id) throws SQLException {
        String sql = "DELETE FROM campanha WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        }
    }
}
