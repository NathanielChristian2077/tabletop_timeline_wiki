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

public class DAOEvento {

    public DAOEvento() {
    }

    public void salvar(Evento e, String campanhaId, Connection conn) throws SQLException {
        String sql = "INSERT INTO evento (id, titulo, descricao, anterior_id, posterior_id, campanha_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(e.getId()));
            stmt.setString(2, e.getTitulo());
            stmt.setString(3, e.getDescricao());
            stmt.setObject(4, e.getAnterior() != null ? UUID.fromString(e.getAnterior().getId()) : null);
            stmt.setObject(5, e.getPosterior() != null ? UUID.fromString(e.getPosterior().getId()) : null);
            stmt.setObject(6, UUID.fromString(campanhaId));
            stmt.executeUpdate();
        }
    }

    public Evento buscarPorId(String id, Connection conn) throws SQLException {
        String sql = "SELECT * FROM evento WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(id));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Evento e = new Evento(
                            rs.getString("id"),
                            rs.getString("campanha_id"),
                            rs.getString("titulo"),
                            rs.getString("descricao"));
                    e.setId(rs.getString("id"));
                    return e;
                }
            }
        }
        return null;
    }

    public Evento buscarPorId(String id, Connection conn, Map<String, Associavel> visitados) throws SQLException {
        if (visitados.containsKey(id)) return (Evento) visitados.get(id);

        Evento e = buscarPorId(id, conn);
        if (e == null) return null;

        visitados.put(id, e);

        DAOEvento daoEvento = new DAOEvento();
        DAOPersonagem daoPersonagem = new DAOPersonagem();
        DAOLocal daoLocal = new DAOLocal();
        DAOObjeto daoObjeto = new DAOObjeto();

        for (String pid : listarPersonagensRelacionados(e.getId(), conn)) {
            Personagem p = daoPersonagem.buscarPorId(pid, conn, visitados);
            if (p != null)
                e.adicionarPersonagem(p);
        }

        for (String lid : listarLocaisRelacionados(e.getId(), conn)) {
            Local l = daoLocal.buscarPorId(lid, conn, visitados);
            if (l != null)
                e.adicionarLocal(l);
        }

        for (String oid : listarObjetosRelacionados(e.getId(), conn)) {
            Objeto o = daoObjeto.buscarPorId(oid, conn, visitados);
            if (o != null)
                e.adicionarObjeto(o);
        }

        for (String eid : listarEventosRelacionados(e.getId(), conn)) {
            if (!eid.equals(e.getId())) {
                Evento ev = daoEvento.buscarPorId(eid, conn, visitados);
                if (ev != null)
                    e.adicionarEvento(ev);
            }
        }

        return e;
    }

    public Evento buscarPorIdComRelacionados(String id, Connection conn) throws SQLException {
        return buscarPorId(id, conn, new HashMap<>());
    }

    public List<Evento> listarTodos(Connection conn) throws SQLException {
        String sql = "SELECT * FROM evento";
        List<Evento> lista = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Evento e = new Evento(
                        rs.getString("id"),
                        rs.getString("campanha_id"),
                        rs.getString("titulo"),
                        rs.getString("descricao"));
                e.setId(rs.getString("id"));
                lista.add(e);
            }
        }
        return lista;
    }

    public List<Evento> listarPorCampanha(String campanhaId, Connection conn) throws SQLException {
        List<Evento> lista = new ArrayList<>();
        String sql = "SELECT * FROM evento WHERE campanha_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, java.util.UUID.fromString(campanhaId));
            try (ResultSet rs = stmt.executeQuery()) {
                DAOPersonagem daoPersonagem = new DAOPersonagem();
                DAOLocal daoLocal = new DAOLocal();
                DAOObjeto daoObjeto = new DAOObjeto();
                while (rs.next()) {
                    Evento e = new Evento(
                            rs.getString("id"),
                            rs.getString("campanha_id"),
                            rs.getString("titulo"),
                            rs.getString("descricao"));
                    e.setId(rs.getString("id"));
                    e.setCampanhaid(campanhaId);

                    // Carregar personagens relacionados
                    List<String> personagensIds = listarPersonagensRelacionados(e.getId(), conn);
                    for (String pid : personagensIds) {
                        Personagem p = daoPersonagem.buscarPorId(pid, conn);
                        if (p != null)
                            e.adicionarPersonagem(p);
                    }

                    // Carregar locais relacionados
                    List<String> locaisIds = listarLocaisRelacionados(e.getId(), conn);
                    for (String lid : locaisIds) {
                        Local l = daoLocal.buscarPorId(lid, conn);
                        if (l != null)
                            e.adicionarLocal(l);
                    }

                    // Carregar objetos relacionados
                    List<String> objetosIds = listarObjetosRelacionados(e.getId(), conn);
                    for (String oid : objetosIds) {
                        Objeto o = daoObjeto.buscarPorId(oid, conn);
                        if (o != null)
                            e.adicionarObjeto(o);
                    }

                    // Carregar eventos relacionados (auto-relação bidirecional)
                    List<String> eventosIds = listarEventosRelacionados(e.getId(), conn);
                    for (String eid : eventosIds) {
                        if (!eid.equals(e.getId())) {
                            Evento ev = buscarPorId(eid, conn);
                            if (ev != null)
                                e.adicionarEvento(ev);
                        }
                    }

                    lista.add(e);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return lista;
    }

    public void atualizar(Evento e, Connection conn) throws SQLException {
        String sql = "UPDATE evento SET titulo = ?, descricao = ?, anterior_id = ?, posterior_id = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, e.getTitulo());
            stmt.setString(2, e.getDescricao());
            stmt.setObject(3, e.getAnterior() != null ? java.util.UUID.fromString(e.getAnterior().getId()) : null);
            stmt.setObject(4, e.getPosterior() != null ? java.util.UUID.fromString(e.getPosterior().getId()) : null);
            stmt.setObject(5, java.util.UUID.fromString(e.getId()));
            stmt.executeUpdate();
        }
    }

    public void deletar(String id, Connection conn) throws SQLException {
        String sql = "DELETE FROM evento WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, java.util.UUID.fromString(id));
            stmt.executeUpdate();
        }
    }

    // Relacionamentos

    // Evento-Evento (auto-relação bidirecional)
    public void adicionarRelacaoEvento(String eventoId1, String eventoId2, Connection conn) throws SQLException {
        String idA = eventoId1.compareTo(eventoId2) < 0 ? eventoId1 : eventoId2;
        String idB = eventoId1.compareTo(eventoId2) < 0 ? eventoId2 : eventoId1;
        String checkSql = "SELECT 1 FROM evento_evento WHERE evento_id1 = ? AND evento_id2 = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setObject(1, java.util.UUID.fromString(idA));
            checkStmt.setObject(2, java.util.UUID.fromString(idB));
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next())
                    return; // Já existe
            }
        }
        String sql = "INSERT INTO evento_evento (evento_id1, evento_id2) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, java.util.UUID.fromString(idA));
            stmt.setObject(2, java.util.UUID.fromString(idB));
            stmt.executeUpdate();
        }
    }

    public void removerRelacaoEvento(String eventoId1, String eventoId2, Connection conn) throws SQLException {
        String idA = eventoId1.compareTo(eventoId2) < 0 ? eventoId1 : eventoId2;
        String idB = eventoId1.compareTo(eventoId2) < 0 ? eventoId2 : eventoId1;
        String sql = "DELETE FROM evento_evento WHERE evento_id1 = ? AND evento_id2 = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, java.util.UUID.fromString(idA));
            stmt.setObject(2, java.util.UUID.fromString(idB));
            stmt.executeUpdate();
        }
    }

    public List<String> listarEventosRelacionados(String eventoId, Connection conn) throws SQLException {
        String sql = "SELECT evento_id1, evento_id2 FROM evento_evento WHERE evento_id1 = ? OR evento_id2 = ?";
        List<String> ids = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, java.util.UUID.fromString(eventoId));
            stmt.setObject(2, java.util.UUID.fromString(eventoId));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String id1 = rs.getString("evento_id1");
                    String id2 = rs.getString("evento_id2");
                    if (!id1.equals(eventoId))
                        ids.add(id1);
                    if (!id2.equals(eventoId))
                        ids.add(id2);
                }
            }
        }
        return ids;
    }

    // Evento-Personagem
    public void adicionarRelacaoPersonagem(String eventoId, String personagemId, Connection conn) throws SQLException {
        String sql = "INSERT INTO evento_personagem (evento_id, personagem_id) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, java.util.UUID.fromString(eventoId));
            stmt.setObject(2, java.util.UUID.fromString(personagemId));
            stmt.executeUpdate();
        }
    }

    public void removerRelacaoPersonagem(String eventoId, String personagemId, Connection conn) throws SQLException {
        String sql = "DELETE FROM evento_personagem WHERE evento_id = ? AND personagem_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, java.util.UUID.fromString(eventoId));
            stmt.setObject(2, java.util.UUID.fromString(personagemId));
            stmt.executeUpdate();
        }
    }

    public List<String> listarPersonagensRelacionados(String eventoId, Connection conn) throws SQLException {
        String sql = "SELECT personagem_id FROM evento_personagem WHERE evento_id = ?";
        List<String> ids = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, java.util.UUID.fromString(eventoId));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getString("personagem_id"));
                }
            }
        }
        return ids;
    }

    // Evento-Objeto
    public void adicionarRelacaoObjeto(String eventoId, String objetoId, Connection conn) throws SQLException {
        String sql = "INSERT INTO evento_objeto (evento_id, objeto_id) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, java.util.UUID.fromString(eventoId));
            stmt.setObject(2, java.util.UUID.fromString(objetoId));
            stmt.executeUpdate();
        }
    }

    public void removerRelacaoObjeto(String eventoId, String objetoId, Connection conn) throws SQLException {
        String sql = "DELETE FROM evento_objeto WHERE evento_id = ? AND objeto_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, java.util.UUID.fromString(eventoId));
            stmt.setObject(2, java.util.UUID.fromString(objetoId));
            stmt.executeUpdate();
        }
    }

    public List<String> listarObjetosRelacionados(String eventoId, Connection conn) throws SQLException {
        String sql = "SELECT objeto_id FROM evento_objeto WHERE evento_id = ?";
        List<String> ids = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, java.util.UUID.fromString(eventoId));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getString("objeto_id"));
                }
            }
        }
        return ids;
    }

    // Evento-Local
    public void adicionarRelacaoLocal(String eventoId, String localId, Connection conn) throws SQLException {
        String sql = "INSERT INTO evento_local (evento_id, local_id) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, java.util.UUID.fromString(eventoId));
            stmt.setObject(2, java.util.UUID.fromString(localId));
            stmt.executeUpdate();
        }
    }

    public void removerRelacaoLocal(String eventoId, String localId, Connection conn) throws SQLException {
        String sql = "DELETE FROM evento_local WHERE evento_id = ? AND local_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, java.util.UUID.fromString(eventoId));
            stmt.setObject(2, java.util.UUID.fromString(localId));
            stmt.executeUpdate();
        }
    }

    public List<String> listarLocaisRelacionados(String eventoId, Connection conn) throws SQLException {
        String sql = "SELECT local_id FROM evento_local WHERE evento_id = ?";
        List<String> ids = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, java.util.UUID.fromString(eventoId));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getString("local_id"));
                }
            }
        }
        return ids;
    }
}
