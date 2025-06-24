package me.persistencia;

import me.modelo.entidades.Objeto;
import me.modelo.entidades.Evento;
import me.modelo.entidades.Local;
import me.modelo.entidades.Personagem;

import java.sql.*;
import java.util.*;

public class DAOObjeto {

    public DAOObjeto() {}

    public void salvar(Objeto objeto, String campanhaId, Connection conn) throws SQLException {
        String sql = "INSERT INTO objeto (id, nome, descricao, campanha_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(objeto.getId()));
            stmt.setString(2, objeto.getNome());
            stmt.setString(3, objeto.getDescricao());
            stmt.setObject(4, UUID.fromString(campanhaId));
            stmt.executeUpdate();
        }
    }

    public List<Objeto> listarPorCampanha(String campanhaId, Connection conn) throws SQLException {
        String sql = "SELECT id FROM objeto WHERE campanha_id = ?";
        List<Objeto> objetos = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(campanhaId));
            try (ResultSet rs = stmt.executeQuery()) {
                Set<String> visitados = new HashSet<>();
                while (rs.next()) {
                    Objeto objeto = buscarPorId(rs.getString("id"), conn, visitados);
                    if (objeto != null) {
                        objetos.add(objeto);
                    }
                }
            }
        }
        return objetos;
    }

    public void atualizar(Objeto objeto, Connection conn) throws SQLException {
        String sql = "UPDATE objeto SET nome = ?, descricao = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, objeto.getNome());
            stmt.setString(2, objeto.getDescricao());
            stmt.setObject(3, UUID.fromString(objeto.getId()));
            stmt.executeUpdate();
        }
    }

    public void deletar(String id, Connection conn) throws SQLException {
        String sql = "DELETE FROM objeto WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(id));
            stmt.executeUpdate();
        }
    }

    public Objeto buscarPorId(String id, Connection conn) throws SQLException {
        return buscarPorId(id, conn, new HashSet<>());
    }

    public Objeto buscarPorId(String id, Connection conn, Set<String> visitados) throws SQLException {
        if (visitados.contains(id)) return null;
        visitados.add(id);

        String sql = "SELECT * FROM objeto WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(id));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Objeto o = new Objeto(
                        rs.getString("id"),
                        rs.getString("campanha_id"),
                        rs.getString("nome"),
                        rs.getString("descricao")
                    );

                    DAOEvento daoEvento = new DAOEvento();
                    DAOLocal daoLocal = new DAOLocal();
                    DAOPersonagem daoPersonagem = new DAOPersonagem();

                    for (String eid : listarEventosRelacionados(o.getId(), conn)) {
                        Evento e = daoEvento.buscarPorId(eid, conn);
                        if (e != null) o.adicionarEvento(e);
                    }

                    for (String lid : listarLocaisRelacionados(o.getId(), conn)) {
                        Local l = daoLocal.buscarPorId(lid, conn);
                        if (l != null) o.adicionarLocal(l);
                    }

                    for (String pid : listarPersonagensRelacionados(o.getId(), conn)) {
                        Personagem p = daoPersonagem.buscarPorId(pid, conn);
                        if (p != null) o.adicionarPersonagem(p);
                    }

                    for (String oid : listarRelacionamentosObjeto(o.getId(), conn)) {
                        if (!oid.equals(o.getId())) {
                            Objeto obj = buscarPorId(oid, conn, visitados);
                            if (obj != null) o.adicionarObjeto(obj);
                        }
                    }

                    return o;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    // --- RELACIONAMENTOS ---

    public void adicionarRelacaoEvento(String objetoId, String eventoId, Connection conn) throws SQLException {
        String sql = "INSERT INTO evento_objeto (objeto_id, evento_id) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(objetoId));
            stmt.setObject(2, UUID.fromString(eventoId));
            stmt.executeUpdate();
        }
    }

    public void removerRelacaoEvento(String objetoId, String eventoId, Connection conn) throws SQLException {
        String sql = "DELETE FROM evento_objeto WHERE objeto_id = ? AND evento_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(objetoId));
            stmt.setObject(2, UUID.fromString(eventoId));
            stmt.executeUpdate();
        }
    }

    public List<String> listarEventosRelacionados(String objetoId, Connection conn) throws SQLException {
        String sql = "SELECT evento_id FROM evento_objeto WHERE objeto_id = ?";
        List<String> lista = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(objetoId));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(rs.getString("evento_id"));
                }
            }
        }
        return lista;
    }

    public void adicionarRelacaoLocal(String objetoId, String localId, Connection conn) throws SQLException {
        String sql = "INSERT INTO objeto_local (objeto_id, local_id) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(objetoId));
            stmt.setObject(2, UUID.fromString(localId));
            stmt.executeUpdate();
        }
    }

    public void removerRelacaoLocal(String objetoId, String localId, Connection conn) throws SQLException {
        String sql = "DELETE FROM objeto_local WHERE objeto_id = ? AND local_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(objetoId));
            stmt.setObject(2, UUID.fromString(localId));
            stmt.executeUpdate();
        }
    }

    public List<String> listarLocaisRelacionados(String objetoId, Connection conn) throws SQLException {
        String sql = "SELECT local_id FROM objeto_local WHERE objeto_id = ?";
        List<String> lista = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(objetoId));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(rs.getString("local_id"));
                }
            }
        }
        return lista;
    }

    public void adicionarRelacaoPersonagem(String objetoId, String personagemId, Connection conn) throws SQLException {
        String sql = "INSERT INTO personagem_objeto (objeto_id, personagem_id) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(objetoId));
            stmt.setObject(2, UUID.fromString(personagemId));
            stmt.executeUpdate();
        }
    }

    public void removerRelacaoPersonagem(String objetoId, String personagemId, Connection conn) throws SQLException {
        String sql = "DELETE FROM personagem_objeto WHERE objeto_id = ? AND personagem_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(objetoId));
            stmt.setObject(2, UUID.fromString(personagemId));
            stmt.executeUpdate();
        }
    }

    public List<String> listarPersonagensRelacionados(String objetoId, Connection conn) throws SQLException {
        String sql = "SELECT personagem_id FROM personagem_objeto WHERE objeto_id = ?";
        List<String> lista = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(objetoId));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(rs.getString("personagem_id"));
                }
            }
        }
        return lista;
    }

    public void adicionarRelacaoObjeto(String objetoId1, String objetoId2, Connection conn) throws SQLException {
        if (objetoId1.equals(objetoId2)) return;
        String idA = objetoId1.compareTo(objetoId2) < 0 ? objetoId1 : objetoId2;
        String idB = objetoId1.compareTo(objetoId2) < 0 ? objetoId2 : objetoId1;
        String checkSql = "SELECT 1 FROM objeto_objeto WHERE objeto1_id = ? AND objeto2_id = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setObject(1, UUID.fromString(idA));
            checkStmt.setObject(2, UUID.fromString(idB));
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) return;
            }
        }
        String sql = "INSERT INTO objeto_objeto (objeto1_id, objeto2_id) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(idA));
            stmt.setObject(2, UUID.fromString(idB));
            stmt.executeUpdate();
        }
    }

    public void removerRelacaoObjeto(String objetoId1, String objetoId2, Connection conn) throws SQLException {
        String idA = objetoId1.compareTo(objetoId2) < 0 ? objetoId1 : objetoId2;
        String idB = objetoId1.compareTo(objetoId2) < 0 ? objetoId2 : objetoId1;
        String sql = "DELETE FROM objeto_objeto WHERE objeto1_id = ? AND objeto2_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(idA));
            stmt.setObject(2, UUID.fromString(idB));
            stmt.executeUpdate();
        }
    }

    public List<String> listarRelacionamentosObjeto(String objetoId, Connection conn) throws SQLException {
        String sql = "SELECT objeto1_id, objeto2_id FROM objeto_objeto WHERE objeto1_id = ? OR objeto2_id = ?";
        List<String> lista = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(objetoId));
            stmt.setObject(2, UUID.fromString(objetoId));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String id1 = rs.getString("objeto1_id");
                    String id2 = rs.getString("objeto2_id");
                    if (!id1.equals(objetoId)) lista.add(id1);
                    if (!id2.equals(objetoId)) lista.add(id2);
                }
            }
        }
        return lista;
    }
}
