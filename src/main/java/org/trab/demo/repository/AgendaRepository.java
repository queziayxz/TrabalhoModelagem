package org.trab.demo.repository;

import org.trab.demo.enums.StatusConsultaEnum;
import org.trab.demo.model.Agenda;
import org.trab.demo.util.Conexao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AgendaRepository {

    public static List<Agenda> getHorariosData(Date data) throws SQLException {
        try {
            List<Agenda> horarios = new ArrayList<>();
            String sql = "SELECT * FROM agendas WHERE data=? ORDER BY data,hora";
            PreparedStatement statement = Conexao.getConn().prepareStatement(sql);
            statement.setDate(1, data);
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                Agenda horario = new Agenda();
                horario.setId(result.getLong("id"));
                horario.setData(result.getDate("data"));
                horario.setHora(result.getTime("hora"));
                horario.setIdPsicologo(result.getLong("id_psicologo"));
                horario.setStatus(result.getString("status"));
                horarios.add(horario);
            }
            return horarios;
        } catch (SQLException e) {
            throw new SQLException("Erro ao buscar horários por data: " + e.getMessage());
        }
    }

    public static List<Agenda> getHorariosStatus() throws SQLException {
        try {
            List<Agenda> horarios = new ArrayList<>();
            String sql = "SELECT * FROM agendas WHERE status=? OR status=? ORDER BY data ASC";
            PreparedStatement statement = Conexao.getConn().prepareStatement(sql);
            statement.setString(1, StatusConsultaEnum.CONCLUIDO.toString());
            statement.setString(2, StatusConsultaEnum.NAO_REALIZADO.toString());
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                Agenda horario = new Agenda();
                horario.setId(result.getLong("id"));
                horario.setData(result.getDate("data"));
                horario.setHora(result.getTime("hora"));
                horario.setIdPsicologo(result.getLong("id_psicologo"));
                horario.setStatus(result.getString("status"));
                horarios.add(horario);
            }
            return horarios;
        } catch (SQLException e) {
            throw new SQLException("Erro ao buscar horários por status: " + e.getMessage());
        }
    }

    public static void cadastraHorario(Agenda horario) throws SQLException {
        try {
            String sql = "INSERT INTO agendas (data, hora, id_psicologo, status) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = Conexao.getConn().prepareStatement(sql);
            statement.setDate(1, horario.getData());
            statement.setTime(2, horario.getHora());
            statement.setLong(3, horario.getIdPsicologo());
            statement.setString(4, StatusConsultaEnum.LIVRE.toString());
            statement.execute();
        } catch (SQLException e) {
            throw new SQLException("Erro ao cadastrar horário: " + e.getMessage());
        }
    }

    public static void finalizaConsulta(Long idHorario, String status) throws SQLException {
        try {
            String sql = "UPDATE agendas SET status=? WHERE id=?";
            PreparedStatement statement = Conexao.getConn().prepareStatement(sql);
            statement.setString(1, status);
            statement.setLong(2, idHorario);
            statement.execute();
        } catch (SQLException e) {
            throw new SQLException("Erro ao finalizar consulta: " + e.getMessage());
        }
    }

    public static void deleteHorario(Long idHorario) throws SQLException {
        try {
            // Primeiro verifica se existe consulta associada
            if (existeConsultaAssociada(idHorario)) {
                throw new SQLException("Existe consulta associada a este horário");
            }

            String sql = "DELETE FROM agendas WHERE id=?";
            PreparedStatement statement = Conexao.getConn().prepareStatement(sql);
            statement.setLong(1, idHorario);
            statement.execute();
        } catch (SQLException e) {
            throw new SQLException("Erro ao deletar horário: " + e.getMessage());
        }
    }

    private static boolean existeConsultaAssociada(Long idAgenda) throws SQLException {
        String sql = "SELECT COUNT(*) FROM consultas WHERE id_agenda=?";
        PreparedStatement statement = Conexao.getConn().prepareStatement(sql);
        statement.setLong(1, idAgenda);
        ResultSet result = statement.executeQuery();

        if (result.next()) {
            return result.getInt(1) > 0;
        }
        return false;
    }
}