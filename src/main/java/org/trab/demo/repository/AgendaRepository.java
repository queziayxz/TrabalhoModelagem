package org.trab.demo.repository;

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

                horarios.add(horario);
            }

            return horarios;

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
