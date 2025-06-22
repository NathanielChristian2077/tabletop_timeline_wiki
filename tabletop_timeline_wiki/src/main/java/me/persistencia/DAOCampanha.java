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
            stmt.setObject(1, java.util.UUID.fromString(c.getId())); // <-- ajuste aqui
            stmt.setString(2, c.getNome());
            stmt.setString(3, c.getDescricao());
            String imagePath = c.getImagePath();
            stmt.setString(4,
                    (imagePath == null || imagePath.isBlank()) ? "/me/gui/images/nullPlaceholder.jpg" : imagePath);
            stmt.executeUpdate();
        }
    }

    public Campanha buscarPorId(String id) throws SQLException {
        String sql = "SELECT * FROM campanha WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, java.util.UUID.fromString(id));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Campanha c = new Campanha(
                    rs.getString("id"),
                    rs.getString("nome"),
                    rs.getString("descricao")
                );
                String imagePath = rs.getString("image_path");
                c.setCaminhoImagem(
                    (imagePath == null || imagePath.isBlank()) ? "/me/gui/images/nullPlaceholder.jpg" : imagePath);
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
                Campanha c = new Campanha(
                    rs.getString("id"),
                    rs.getString("nome"),
                    rs.getString("descricao")
                );
                String imagePath = rs.getString("image_path");
                c.setCaminhoImagem(
                    (imagePath == null || imagePath.isBlank()) ? "/me/gui/images/nullPlaceholder.jpg" : imagePath);
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
            String imagePath = c.getImagePath();
            stmt.setString(3, (imagePath == null || imagePath.isBlank()) ? "/me/gui/images/nullPlaceholder.jpg" : imagePath);
            stmt.setObject(4, java.util.UUID.fromString(c.getId())); // CORREÇÃO AQUI
            stmt.executeUpdate();
        }
    }

    public void deletar(String id) throws SQLException {
        String sql = "DELETE FROM campanha WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, java.util.UUID.fromString(id)); // CORREÇÃO
            stmt.executeUpdate();
        }
    }
}
