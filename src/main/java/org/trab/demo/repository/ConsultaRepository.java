package org.trab.demo.repository;

import org.trab.demo.model.Consulta;
import org.trab.demo.model.Agenda;
import org.trab.demo.util.Conexao;
import org.trab.demo.enums.StatusConsultaEnum;
import org.trab.demo.model.Paciente;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConsultaRepository {

    public static List<Consulta> getConsultasHorariosData(Date data) throws SQLException
    {
        try {

            List<Consulta> consultas = new ArrayList<>();

            List<Agenda> horarios = AgendaRepository.getHorariosData(data);

            for(int i = 0; i < horarios.size(); i++) {
                Consulta con = new Consulta();

                if (horarios.get(i).getStatus().equals(StatusConsultaEnum.AGENDADO.toString()) ||
                        horarios.get(i).getStatus().equals(StatusConsultaEnum.CONCLUIDO.toString()) ||
                        horarios.get(i).getStatus().equals(StatusConsultaEnum.NAO_REALIZADO.toString())) {

                    String sql = "SELECT * FROM consultas WHERE id_agenda=?";

                    PreparedStatement statem = Conexao.getConn().prepareStatement(sql);
                    statem.setLong(1, horarios.get(i).getId());

                    ResultSet result = statem.executeQuery();

                    if (result.next()) {
                        con.setId(result.getLong("id"));

                        con.setPaciente(UserRepository.getPacienteId(result.getLong("id_paciente")));

                        con.setHorarioConsulta(horarios.get(i));
                        consultas.add(con);
                    }
                } else {
                    con.setHorarioConsulta(horarios.get(i));
                    consultas.add(con);
                }
            }

            return consultas;

        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }
    public static List<Consulta> getConsultasData(Date data) throws SQLException {
        try {
            List<Consulta> consultas = new ArrayList<>();
            String sql = "SELECT c.id, c.id_paciente, c.id_agenda, a.*, u.nome, u.telefone, u.email " +
                    "FROM consultas c " +
                    "JOIN agendas a ON c.id_agenda = a.id " +
                    "JOIN usuarios u ON c.id_paciente = u.id " +
                    "WHERE a.data = ?";

            PreparedStatement statement = Conexao.getConn().prepareStatement(sql);
            statement.setDate(1, data);
            ResultSet result = statement.executeQuery();

            while (result.next()) {

                System.out.println("id agenda: "+result.getLong("a.id"));

                Consulta consulta = new Consulta();
                consulta.setId(result.getLong("c.id"));
                consulta.setIdPaciente(result.getLong("c.id_paciente"));
                consulta.setIdAgenda(result.getLong("c.id_agenda"));

                // Criar objeto Agenda
                Agenda agenda = new Agenda();
                agenda.setId(result.getLong("a.id"));
                agenda.setData(result.getDate("a.data"));
                agenda.setHora(result.getTime("a.hora"));
                agenda.setIdPsicologo(result.getLong("a.id_psicologo"));
                agenda.setStatus(result.getString("a.status"));

                // Criar objeto Paciente
                Paciente paciente = new Paciente();
                paciente.setId(result.getLong("c.id_paciente"));
                paciente.setNome(result.getString("u.nome"));
                paciente.setTelefone(result.getString("u.telefone"));
                paciente.setEmail(result.getString("u.email"));

                consulta.setHorarioConsulta(agenda);
                consulta.setPaciente(paciente);

                consultas.add(consulta);
            }
            return consultas;
        } catch (SQLException e) {
            throw new SQLException("Erro ao buscar consultas por data: " + e.getMessage());
        }
    }

    public static List<Consulta> getConsultasFinalizadas() throws SQLException {
        try {
            List<Consulta> consultas = new ArrayList<>();
            String sql = "SELECT c.*, a.*, u.* " +
                    "FROM consultas c " +
                    "JOIN agendas a ON c.id_agenda = a.id " +
                    "JOIN usuarios u ON c.id_paciente = u.id " +
                    "WHERE a.status IN (?, ?)";

            PreparedStatement statement = Conexao.getConn().prepareStatement(sql);
            statement.setString(1, StatusConsultaEnum.CONCLUIDO.toString());
            statement.setString(2, StatusConsultaEnum.NAO_REALIZADO.toString());
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                Consulta consulta = new Consulta();
                consulta.setId(result.getLong("c.id"));
                consulta.setIdPaciente(result.getLong("c.id_paciente"));
                consulta.setIdAgenda(result.getLong("c.id_agenda"));

                // Criar objeto Agenda
                Agenda agenda = new Agenda();
                agenda.setId(result.getLong("a.id"));
                agenda.setData(result.getDate("a.data"));
                agenda.setHora(result.getTime("a.hora"));
                agenda.setIdPsicologo(result.getLong("a.id_psicologo"));
                agenda.setStatus(result.getString("a.status"));

                // Criar objeto Paciente
                Paciente paciente = new Paciente();
                paciente.setId(result.getLong("u.id"));
                paciente.setNome(result.getString("u.nome"));
                paciente.setTelefone(result.getString("u.telefone"));
                paciente.setEmail(result.getString("u.email"));
                paciente.setDataNascimento(result.getDate("u.data_nascimento"));
                paciente.setCpf(result.getString("u.cpf"));

                consulta.setHorarioConsulta(agenda);
                consulta.setPaciente(paciente);

                consultas.add(consulta);
            }
            return consultas;
        } catch (SQLException e) {
            throw new SQLException("Erro ao buscar consultas finalizadas: " + e.getMessage());
        }
    }

    public static void cancelarConsulta(Long idConsulta) throws SQLException {
        Connection conn = null;
        PreparedStatement deleteConsultaStmt = null;
        PreparedStatement updateAgendaStmt = null;

        try {
            conn = Conexao.getConn();
            conn.setAutoCommit(false); // Inicialização do cancelamento

            // 1. Obtém id_agenda da consulta
            String selectSql = "SELECT id_agenda FROM consultas WHERE id = ?";
            deleteConsultaStmt = conn.prepareStatement(selectSql);
            deleteConsultaStmt.setLong(1, idConsulta);
            ResultSet rs = deleteConsultaStmt.executeQuery();

            if (!rs.next()) {
                throw new SQLException("Consulta não encontrada");
            }

            Long idAgenda = rs.getLong("id_agenda");

            // 2. Apaga a consulta do SQL
            String deleteSql = "DELETE FROM consultas WHERE id = ?";
            deleteConsultaStmt = conn.prepareStatement(deleteSql);
            deleteConsultaStmt.setLong(1, idConsulta);
            deleteConsultaStmt.executeUpdate();

            // 3. Atualização status da agenda para LIVRE
            String updateSql = "UPDATE agendas SET status = ? WHERE id = ?";
            updateAgendaStmt = conn.prepareStatement(updateSql);
            updateAgendaStmt.setString(1, StatusConsultaEnum.LIVRE.toString());
            updateAgendaStmt.setLong(2, idAgenda);
            updateAgendaStmt.executeUpdate();

            conn.commit(); // Finalização do cancelamento

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Reverter em caso de erro
                } catch (SQLException ex) {
                    throw new SQLException("Erro ao reverter transação: " + ex.getMessage());
                }
            }
            throw new SQLException("Erro ao cancelar consulta: " + e.getMessage());
        } finally {

            if (deleteConsultaStmt != null) deleteConsultaStmt.close();
            if (updateAgendaStmt != null) updateAgendaStmt.close();
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    // Log error
                }
            }
        }
    }
    public static void agendarConsulta(Consulta consulta) throws SQLException {
        String sql = "INSERT INTO consultas (id_paciente, id_agenda) VALUES (?, ?)";
        try (PreparedStatement statement = Conexao.getConn().prepareStatement(sql)) {
            statement.setLong(1, consulta.getIdPaciente());
            statement.setLong(2, consulta.getIdAgenda());
            statement.executeUpdate();
        }
    }

    public static List<Consulta> getConsultasByPaciente(Long idPaciente) throws SQLException {
        List<Consulta> consultas = new ArrayList<>();
        String sql = "SELECT c.id, c.id_paciente, c.id_agenda, a.* " +
                "FROM consultas c " +
                "JOIN agendas a ON c.id_agenda = a.id " +
                "WHERE c.id_paciente = ?";

        try (PreparedStatement statement = Conexao.getConn().prepareStatement(sql)) {
            statement.setLong(1, idPaciente);
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                Consulta consulta = new Consulta();
                consulta.setId(result.getLong("c.id"));
                consulta.setIdPaciente(result.getLong("c.id_paciente"));
                consulta.setIdAgenda(result.getLong("c.id_agenda"));

                Agenda agenda = new Agenda();
                agenda.setId(result.getLong("a.id"));
                agenda.setData(result.getDate("a.data"));
                agenda.setHora(result.getTime("a.hora"));
                agenda.setStatus(result.getString("a.status"));

                consulta.setHorarioConsulta(agenda);
                consultas.add(consulta);
            }
        }
        return consultas;
    }
}