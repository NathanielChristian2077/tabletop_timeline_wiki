package me.persistencia;

import me.modelo.entidades.Objeto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DAOObjeto {

    private final Connection conn;

    public DAOObjeto() throws SQLException {
        this.conn = ConexaoBD.getConnection();
    }

    public void salvar(Objeto objeto, String campanhaId) throws SQLException {
        String sql = "INSERT INTO objeto (id, nome, descricao, campanha_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, java.util.UUID.fromString(objeto.getId())); // CORRIGIDO
            stmt.setString(2, objeto.getNome());
            stmt.setString(3, objeto.getDescricao());
            stmt.setString(4, campanhaId);
            stmt.executeUpdate();
        }
    }

    public List<Objeto> listarPorCampanha(String campanhaId) throws SQLException {
        String sql = "SELECT * FROM objeto WHERE campanha_id = ?";
        List<Objeto> objetos = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, java.util.UUID.fromString(campanhaId)); // CORRIGIDO
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Objeto objeto = new Objeto(
                    rs.getString("id"),
                    rs.getString("campanha_id"),
                    rs.getString("nome"),
                    rs.getString("descricao")
                );
                objetos.add(objeto);
            }
        }
        return objetos;
    }

    public void atualizar(Objeto objeto) throws SQLException {
        String sql = "UPDATE objeto SET nome = ?, descricao = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, objeto.getNome());
            stmt.setString(2, objeto.getDescricao());
            stmt.setObject(3, java.util.UUID.fromString(objeto.getId())); // CORRIGIDO
            stmt.executeUpdate();
        }
    }

    public void deletar(String id) throws SQLException {
        String sql = "DELETE FROM objeto WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, java.util.UUID.fromString(id)); // CORRIGIDO
            stmt.executeUpdate();
        }
    }
}
