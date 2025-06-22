package me.persistencia;

import me.modelo.entidades.Local;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DAOLocal {

    private final Connection conn;

    public DAOLocal() throws SQLException {
        this.conn = ConexaoBD.getConnection();
    }

    public void salvar(Local local, String campanhaId) throws SQLException {
        String sql = "INSERT INTO local (id, nome, descricao, campanha_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, java.util.UUID.fromString(local.getId())); // CORRIGIDO
            stmt.setString(2, local.getNome());
            stmt.setString(3, local.getDescricao());
            stmt.setString(4, campanhaId);
            stmt.executeUpdate();
        }
    }

    public List<Local> listarPorCampanha(String campanhaId) throws SQLException {
        String sql = "SELECT * FROM local WHERE campanha_id = ?";
        List<Local> locais = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, java.util.UUID.fromString(campanhaId)); // CORRIGIDO
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Local local = new Local(
                    rs.getString("id"),
                    rs.getString("campanha_id"),
                    rs.getString("nome"),
                    rs.getString("descricao")
                );
                locais.add(local);
            }
        }
        return locais;
    }

    public void atualizar(Local local) throws SQLException {
        String sql = "UPDATE local SET nome = ?, descricao = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, local.getNome());
            stmt.setString(2, local.getDescricao());
            stmt.setObject(3, java.util.UUID.fromString(local.getId())); // CORRIGIDO
            stmt.executeUpdate();
        }
    }

    public void deletar(String id) throws SQLException {
        String sql = "DELETE FROM local WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, java.util.UUID.fromString(id)); // CORRIGIDO
            stmt.executeUpdate();
        }
    }
}
