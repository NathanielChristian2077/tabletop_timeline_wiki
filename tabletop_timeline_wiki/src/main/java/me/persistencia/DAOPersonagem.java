package me.persistencia;

import me.modelo.entidades.Evento;
import me.modelo.entidades.Local;
import me.modelo.entidades.Objeto;
import me.modelo.entidades.Personagem;
import me.modelo.interfaces.Associavel;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DAOPersonagem {

    public DAOPersonagem() {
    }

    public void salvar(Personagem personagem, String campanhaId, Connection conn) throws SQLException {
        String sql = "INSERT INTO personagem (id, nome, descricao, campanha_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, java.util.UUID.fromString(personagem.getId()));
            stmt.setString(2, personagem.getNome());
            stmt.setString(3, personagem.getDescricao());
            stmt.setObject(4, java.util.UUID.fromString(campanhaId));
            stmt.executeUpdate();
        }
    }

    public List<Personagem> listarPorCampanha(String campanhaId, Connection conn) throws SQLException {
        String sql = "SELECT id FROM personagem WHERE campanha_id = ?";
        List<Personagem> personagens = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, java.util.UUID.fromString(campanhaId));
            try (ResultSet rs = stmt.executeQuery()) {
                Map<String, Associavel> visitados = new HashMap<>();
                while (rs.next()) {
                    Personagem personagem = buscarPorId(rs.getString("id"), conn, visitados);
                    if (personagem != null) {
                        personagens.add(personagem);
                    }
                }
            }
        }
        return personagens;
    }

    public void atualizar(Personagem personagem, Connection conn) throws SQLException {
        String sql = "UPDATE personagem SET nome = ?, descricao = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, personagem.getNome());
            stmt.setString(2, personagem.getDescricao());
            stmt.setObject(3, java.util.UUID.fromString(personagem.getId()));
            stmt.executeUpdate();
        }
    }

    public void deletar(String id, Connection conn) throws SQLException {
        String sql = "DELETE FROM personagem WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, java.util.UUID.fromString(id));
            stmt.executeUpdate();
        }
    }

    public Personagem buscarPorId(String id, Connection conn) throws SQLException {
        return buscarPorId(id, conn, new HashMap<>());
    }

    public Personagem buscarPorId(String id, Connection conn, Map<String, Associavel> visitados) throws SQLException {
        if (visitados.containsKey(id)) return (Personagem) visitados.get(id);

        String sql = "SELECT * FROM personagem WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(id));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Personagem p = new Personagem(
                            rs.getString("id"),
                            rs.getString("campanha_id"),
                            rs.getString("nome"),
                            rs.getString("descricao")
                    );

                    visitados.put(id, p);

                    DAOObjeto daoObjeto = new DAOObjeto();
                    DAOEvento daoEvento = new DAOEvento();
                    DAOLocal daoLocal = new DAOLocal();

                    // Relacionamentos com objetos
                    for (String oid : listarObjetosRelacionados(p.getId(), conn)) {
                        Objeto obj = daoObjeto.buscarPorId(oid, conn, visitados);
                        if (obj != null)
                            p.adicionarObjeto(obj);
                    }

                    // Relacionamentos com eventos
                    for (String eid : listarEventosRelacionados(p.getId(), conn)) {
                        Evento ev = daoEvento.buscarPorId(eid, conn, visitados);
                        if (ev != null)
                            p.adicionarEvento(ev);
                    }

                    // Relacionamentos com locais
                    for (String lid : listarLocaisRelacionados(p.getId(), conn)) {
                        Local loc = daoLocal.buscarPorId(lid, conn, visitados);
                        if (loc != null)
                            p.adicionarLocal(loc);
                    }

                    // Relação com outros personagens
                    for (String pid : listarRelacionamentosPersonagem(p.getId(), conn)) {
                        if (!pid.equals(p.getId())) {
                            Personagem per = buscarPorId(pid, conn, visitados); // reutiliza o mesmo Set
                            if (per != null)
                                p.adicionarPersonagem(per);
                        }
                    }

                    return p;
                }
            }
        }
        return null;
    }

    // --- RELACIONAMENTOS ---

    // Objeto
    public void adicionarRelacaoObjeto(String personagemId, String objetoId, Connection conn) throws SQLException {
        String sql = "INSERT INTO personagem_objeto (personagem_id, objeto_id) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, java.util.UUID.fromString(personagemId));
            stmt.setObject(2, java.util.UUID.fromString(objetoId));
            stmt.executeUpdate();
        }
    }

    public void removerRelacaoObjeto(String personagemId, String objetoId, Connection conn) throws SQLException {
        String sql = "DELETE FROM personagem_objeto WHERE personagem_id = ? AND objeto_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, java.util.UUID.fromString(personagemId));
            stmt.setObject(2, java.util.UUID.fromString(objetoId));
            stmt.executeUpdate();
        }
    }

    public List<String> listarObjetosRelacionados(String personagemId, Connection conn) throws SQLException {
        String sql = "SELECT objeto_id FROM personagem_objeto WHERE personagem_id = ?";
        List<String> ids = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, java.util.UUID.fromString(personagemId));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getString("objeto_id"));
                }
            }
        }
        return ids;
    }

    // Evento
    public void adicionarRelacaoEvento(String personagemId, String eventoId, Connection conn) throws SQLException {
        String sql = "INSERT INTO evento_personagem (personagem_id, evento_id) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, java.util.UUID.fromString(personagemId));
            stmt.setObject(2, java.util.UUID.fromString(eventoId));
            stmt.executeUpdate();
        }
    }

    public void removerRelacaoEvento(String personagemId, String eventoId, Connection conn) throws SQLException {
        String sql = "DELETE FROM evento_personagem WHERE personagem_id = ? AND evento_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, java.util.UUID.fromString(personagemId));
            stmt.setObject(2, java.util.UUID.fromString(eventoId));
            stmt.executeUpdate();
        }
    }

    public List<String> listarEventosRelacionados(String personagemId, Connection conn) throws SQLException {
        String sql = "SELECT evento_id FROM evento_personagem WHERE personagem_id = ?";
        List<String> ids = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, java.util.UUID.fromString(personagemId));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getString("evento_id"));
                }
            }
        }
        return ids;
    }

    // Local
    public void adicionarRelacaoLocal(String personagemId, String localId, Connection conn) throws SQLException {
        String sql = "INSERT INTO personagem_local (personagem_id, local_id) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, java.util.UUID.fromString(personagemId));
            stmt.setObject(2, java.util.UUID.fromString(localId));
            stmt.executeUpdate();
        }
    }

    public void removerRelacaoLocal(String personagemId, String localId, Connection conn) throws SQLException {
        String sql = "DELETE FROM personagem_local WHERE personagem_id = ? AND local_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, java.util.UUID.fromString(personagemId));
            stmt.setObject(2, java.util.UUID.fromString(localId));
            stmt.executeUpdate();
        }
    }

    public List<String> listarLocaisRelacionados(String personagemId, Connection conn) throws SQLException {
        String sql = "SELECT local_id FROM personagem_local WHERE personagem_id = ?";
        List<String> ids = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, java.util.UUID.fromString(personagemId));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getString("local_id"));
                }
            }
        }
        return ids;
    }

    // Personagem-Personagem (auto-relação bidirecional)
    public void adicionarRelacaoPersonagem(String personagemId1, String personagemId2, Connection conn)
            throws SQLException {
        if (personagemId1.equals(personagemId2))
            return; // Evita auto-relação redundante
        String idA = personagemId1.compareTo(personagemId2) < 0 ? personagemId1 : personagemId2;
        String idB = personagemId1.compareTo(personagemId2) < 0 ? personagemId2 : personagemId1;
        // Verifica se já existe
        String checkSql = "SELECT 1 FROM personagem_personagem WHERE personagem1_id = ? AND personagem2_id = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setObject(1, java.util.UUID.fromString(idA));
            checkStmt.setObject(2, java.util.UUID.fromString(idB));
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next())
                    return; // Já existe
            }
        }
        String sql = "INSERT INTO personagem_personagem (personagem1_id, personagem2_id) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, java.util.UUID.fromString(idA));
            stmt.setObject(2, java.util.UUID.fromString(idB));
            stmt.executeUpdate();
        }
    }

    public void removerRelacaoPersonagem(String personagemId1, String personagemId2, Connection conn)
            throws SQLException {
        String idA = personagemId1.compareTo(personagemId2) < 0 ? personagemId1 : personagemId2;
        String idB = personagemId1.compareTo(personagemId2) < 0 ? personagemId2 : personagemId1;
        String sql = "DELETE FROM personagem_personagem WHERE personagem1_id = ? AND personagem2_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, java.util.UUID.fromString(idA));
            stmt.setObject(2, java.util.UUID.fromString(idB));
            stmt.executeUpdate();
        }
    }

    public List<String> listarRelacionamentosPersonagem(String personagemId, Connection conn) throws SQLException {
        String sql = "SELECT personagem1_id, personagem2_id FROM personagem_personagem WHERE personagem1_id = ? OR personagem2_id = ?";
        List<String> ids = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, java.util.UUID.fromString(personagemId));
            stmt.setObject(2, java.util.UUID.fromString(personagemId));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String id1 = rs.getString("personagem1_id");
                    String id2 = rs.getString("personagem2_id");
                    if (!id1.equals(personagemId))
                        ids.add(id1);
                    if (!id2.equals(personagemId))
                        ids.add(id2);
                }
            }
        }
        return ids;
    }
}
