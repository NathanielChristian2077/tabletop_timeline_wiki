package me.persistencia;

import me.modelo.entidades.Personagem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DAOPersonagem {

    private final Connection conn;

    public DAOPersonagem() throws SQLException {
        this.conn = ConexaoBD.getConnection();
    }

    public void salvar(Personagem personagem, String campanhaId) throws SQLException {
        String sql = "INSERT INTO personagem (id, nome, descricao, campanha_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, java.util.UUID.fromString(personagem.getId())); // CORRIGIDO
            stmt.setString(2, personagem.getNome());
            stmt.setString(3, personagem.getDescricao());
            stmt.setString(4, campanhaId);
            stmt.executeUpdate();
        }
    }

    public List<Personagem> listarPorCampanha(String campanhaId) throws SQLException {
        String sql = "SELECT * FROM personagem WHERE campanha_id = ?";
        List<Personagem> personagens = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, campanhaId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Personagem personagem = new Personagem(
                    rs.getString("id"),
                    rs.getString("campanha_id"),
                    rs.getString("nome"),
                    rs.getString("descricao")
                );
                personagens.add(personagem);
            }
        }
        return personagens;
    }

    public void atualizar(Personagem personagem) throws SQLException {
        String sql = "UPDATE personagem SET nome = ?, descricao = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, personagem.getNome());
            stmt.setString(2, personagem.getDescricao());
            stmt.setObject(3, java.util.UUID.fromString(personagem.getId()));
            stmt.executeUpdate();
        }
    }

    public void deletar(String id) throws SQLException {
        String sql = "DELETE FROM personagem WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, java.util.UUID.fromString(id));
            stmt.executeUpdate();
        }
    }
}