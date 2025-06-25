package me.persistencia;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import me.modelo.entidades.Evento;
import me.modelo.entidades.Local;
import me.modelo.entidades.Objeto;
import me.modelo.entidades.Personagem;
import me.modelo.interfaces.Associavel;

public class DAOLocal {

    public DAOLocal() {}

    public void salvar(Local local, String campanhaId, Connection conn) throws SQLException {
        String sql = "INSERT INTO local (id, nome, descricao, campanha_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(local.getId()));
            stmt.setString(2, local.getNome());
            stmt.setString(3, local.getDescricao());
            stmt.setObject(4, UUID.fromString(campanhaId));
            stmt.executeUpdate();
        }
    }

    public List<Local> listarPorCampanha(String campanhaId, Connection conn) throws SQLException {
        String sql = "SELECT id FROM local WHERE campanha_id = ?";
        List<Local> locais = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(campanhaId));
            try (ResultSet rs = stmt.executeQuery()) {
                Map<String, Associavel> visitados = new HashMap<>();
                while (rs.next()) {
                    Local local = buscarPorId(rs.getString("id"), conn, visitados);
                    if (local != null) {
                        locais.add(local);
                    }
                }
            }
        }
        return locais;
    }

    public void atualizar(Local local, Connection conn) throws SQLException {
        String sql = "UPDATE local SET nome = ?, descricao = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, local.getNome());
            stmt.setString(2, local.getDescricao());
            stmt.setObject(3, UUID.fromString(local.getId()));
            stmt.executeUpdate();
        }
    }

    public void deletar(String id, Connection conn) throws SQLException {
        String sql = "DELETE FROM local WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(id));
            stmt.executeUpdate();
        }
    }


    // Adiciona relação Local-Personagem
    public void adicionarRelacaoPersonagem(String localId, String personagemId, Connection conn) throws SQLException {
        String sql = "INSERT INTO personagem_local (local_id, personagem_id) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, java.util.UUID.fromString(localId));
            stmt.setObject(2, java.util.UUID.fromString(personagemId));
            stmt.executeUpdate();
        }
    }

    // Remove relação Local-Personagem
    public void removerRelacaoPersonagem(String localId, String personagemId, Connection conn) throws SQLException {
        String sql = "DELETE FROM personagem_local WHERE local_id = ? AND personagem_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, java.util.UUID.fromString(localId));
            stmt.setObject(2, java.util.UUID.fromString(personagemId));
            stmt.executeUpdate();
        }
    }

    // Lista personagens relacionados a um local
    public List<String> listarPersonagensRelacionados(String localId, Connection conn) throws SQLException {
        String sql = "SELECT personagem_id FROM personagem_local WHERE local_id = ?";
        List<String> ids = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, java.util.UUID.fromString(localId));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getString("personagem_id"));
                }
            }
        }
        return ids;
    }

    // Adiciona relação Local-Local (bidirecional, ordenando os IDs)
    public void adicionarRelacaoLocal(String localId1, String localId2, Connection conn) throws SQLException {
        if (localId1.equals(localId2)) return; // Evita auto-relação redundante
        String idMenor = localId1.compareTo(localId2) < 0 ? localId1 : localId2;
        String idMaior = localId1.compareTo(localId2) < 0 ? localId2 : localId1;
        String checkSql = "SELECT 1 FROM local_local WHERE local1_id = ? AND local2_id = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setObject(1, java.util.UUID.fromString(idMenor));
            checkStmt.setObject(2, java.util.UUID.fromString(idMaior));
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) return; // Já existe
            }
        }
        String sql = "INSERT INTO local_local (local1_id, local2_id) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, java.util.UUID.fromString(idMenor));
            stmt.setObject(2, java.util.UUID.fromString(idMaior));
            stmt.executeUpdate();
        }
    }

    // Remove relação Local-Local (bidirecional, remove ambas as direções)
    public void removerRelacaoLocal(String localId1, String localId2, Connection conn) throws SQLException {
        String idMenor = localId1.compareTo(localId2) < 0 ? localId1 : localId2;
        String idMaior = localId1.compareTo(localId2) < 0 ? localId2 : localId1;
        String sql = "DELETE FROM local_local WHERE (local1_id = ? AND local2_id = ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, java.util.UUID.fromString(idMenor));
            stmt.setObject(2, java.util.UUID.fromString(idMaior));
            stmt.executeUpdate();
        }
    }

    // Lista locais relacionados a um local (busca ambos os lados)
    public List<String> listarLocaisRelacionados(String localId, Connection conn) throws SQLException {
        List<String> ids = new ArrayList<>();
        String sql = "SELECT local1_id, local2_id FROM local_local WHERE local1_id = ? OR local2_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, java.util.UUID.fromString(localId));
            stmt.setObject(2, java.util.UUID.fromString(localId));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String id1 = rs.getString("local1_id");
                    String id2 = rs.getString("local2_id");
                    if (localId.equals(id1) && !localId.equals(id2)) {
                        ids.add(id2);
                    } else if (localId.equals(id2) && !localId.equals(id1)) {
                        ids.add(id1);
                    }
                }
            }
        }
        return ids;
    }

    // Adiciona relação Local-Objeto
    public void adicionarRelacaoObjeto(String localId, String objetoId, Connection conn) throws SQLException {
        String sql = "INSERT INTO objeto_local (local_id, objeto_id) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, java.util.UUID.fromString(localId));
            stmt.setObject(2, java.util.UUID.fromString(objetoId));
            stmt.executeUpdate();
        }
    }

    // Remove relação Local-Objeto
    public void removerRelacaoObjeto(String localId, String objetoId, Connection conn) throws SQLException {
        String sql = "DELETE FROM objeto_local WHERE local_id = ? AND objeto_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, java.util.UUID.fromString(localId));
            stmt.setObject(2, java.util.UUID.fromString(objetoId));
            stmt.executeUpdate();
        }
    }

    // Lista objetos relacionados a um local
    public List<String> listarObjetosRelacionados(String localId, Connection conn) throws SQLException {
        String sql = "SELECT objeto_id FROM objeto_local WHERE local_id = ?";
        List<String> ids = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, java.util.UUID.fromString(localId));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getString("objeto_id"));
                }
            }
        }
        return ids;
    }

    // Adiciona relação Local-Evento
    public void adicionarRelacaoEvento(String localId, String eventoId, Connection conn) throws SQLException {
        String sql = "INSERT INTO evento_local (local_id, evento_id) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, java.util.UUID.fromString(localId));
            stmt.setObject(2, java.util.UUID.fromString(eventoId));
            stmt.executeUpdate();
        }
    }

    // Remove relação Local-Evento
    public void removerRelacaoEvento(String localId, String eventoId, Connection conn) throws SQLException {
        String sql = "DELETE FROM evento_local WHERE local_id = ? AND evento_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, java.util.UUID.fromString(localId));
            stmt.setObject(2, java.util.UUID.fromString(eventoId));
            stmt.executeUpdate();
        }
    }

    // Lista eventos relacionados a um local
    public List<String> listarEventosRelacionados(String localId, Connection conn) throws SQLException {
        String sql = "SELECT evento_id FROM evento_local WHERE local_id = ?";
        List<String> ids = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, java.util.UUID.fromString(localId));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getString("evento_id"));
                }
            }
        }
        return ids;
    }

    public Local buscarPorId(String id, Connection conn) throws SQLException {
        return buscarPorId(id, conn, new HashMap<>());
    }

    public Local buscarPorId(String id, Connection conn, Map<String, Associavel> visitados) throws SQLException {
        if (visitados.containsKey(id)) return (Local) visitados.get(id);

        String sql = "SELECT * FROM local WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(id));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Local l = new Local(
                        rs.getString("id"),
                        rs.getString("campanha_id"),
                        rs.getString("nome"),
                        rs.getString("descricao")
                    );

                    visitados.put(id, l);

                    DAOPersonagem daoPersonagem = new DAOPersonagem();
                    DAOObjeto daoObjeto = new DAOObjeto();
                    DAOEvento daoEvento = new DAOEvento();

                    for (String pid : listarPersonagensRelacionados(l.getId(), conn)) {
                        Personagem p = daoPersonagem.buscarPorId(pid, conn, visitados);
                        if (p != null) l.adicionarPersonagem(p);
                    }

                    for (String oid : listarObjetosRelacionados(l.getId(), conn)) {
                        Objeto o = daoObjeto.buscarPorId(oid, conn, visitados);
                        if (o != null) l.adicionarObjeto(o);
                    }

                    for (String eid : listarEventosRelacionados(l.getId(), conn)) {
                        Evento e = daoEvento.buscarPorId(eid, conn, visitados);
                        if (e != null) l.adicionarEvento(e);
                    }

                    for (String lid : listarLocaisRelacionados(l.getId(), conn)) {
                        if (!lid.equals(l.getId())) {
                            Local loc = buscarPorId(lid, conn, visitados);
                            if (loc != null) l.adicionarLocal(loc);
                        }
                    }

                    return l;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
