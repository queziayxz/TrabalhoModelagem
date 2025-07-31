package org.trab.demo.repository;

import org.trab.demo.enums.StatusConsultaEnum;
import org.trab.demo.model.Agenda;
import org.trab.demo.util.Conexao;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AgendaRepository {

    public static List<Agenda> getHorariosData(Date data) throws SQLException
    {
        try {
            List<Agenda> horarios = new ArrayList<>();
            String sql = "SELECT * FROM agendas WHERE data=?";
            PreparedStatement statem = Conexao.getConn().prepareStatement(sql);
            statem.setDate(1, data);

            ResultSet result = statem.executeQuery();

            while(result.next()) {
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
            throw new SQLException(e.getMessage());
        }
    }

    public static List<Agenda> getHorariosStatus() throws SQLException
    {
        try {
            List<Agenda> horarios = new ArrayList<>();

            String sql = "SELECT * FROM agendas WHERE status=? OR status=? ORDER BY data ASC";
            PreparedStatement statement = Conexao.getConn().prepareStatement(sql);
            statement.setString(1,StatusConsultaEnum.CONCLUIDO.toString());
            statement.setString(2,StatusConsultaEnum.NAO_REALIZADO.toString());
            ResultSet result = statement.executeQuery();

            while(result.next()) {
                Agenda horario = new Agenda();
                horario.setId(result.getLong("id"));
                horario.setIdPsicologo(result.getLong("id_psicologo"));
                horario.setData(result.getDate("data"));
                horario.setHora(result.getTime("hora"));
                horario.setStatus(result.getString("status"));

                horarios.add(horario);
            }

            return horarios;

        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    public static void cadastraHorario(Agenda horario) throws SQLException
    {
        System.out.println("data: "+horario.getData());
        System.out.println("hora: "+horario.getHora());
        System.out.println("id_psi: "+horario.getIdPsicologo());
        try {
            String sql = "INSERT INTO agendas (data,hora,id_psicologo,status) VALUES (?,?,?,?)";
            PreparedStatement statement = Conexao.getConn().prepareStatement(sql);
            statement.setDate(1, horario.getData());
            statement.setTime(2, horario.getHora());
            statement.setLong(3, horario.getIdPsicologo());
            statement.setString(4, StatusConsultaEnum.LIVRE.toString());

            statement.execute();
        } catch (SQLException e) {
            throw new SQLException();
        }
    }

    public static void finalizaConsulta(Long idHorario, String status) throws SQLException
    {
        try {
            String sql = "UPDATE agendas SET status=? WHERE id=?";

            PreparedStatement statement = Conexao.getConn().prepareStatement(sql);
            statement.setString(1, status);
            statement.setLong(2, idHorario);
            statement.execute();

        } catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }
    }

    public static void deleteHorario(Long idHorario) throws SQLException
    {
        try {
            String sql = "DELETE FROM agendas WHERE id=?";

            PreparedStatement statement = Conexao.getConn().prepareStatement(sql);
            statement.setLong(1, idHorario);
            statement.execute();

        } catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }
    }

}
