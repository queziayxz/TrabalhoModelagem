package org.trab.demo.repository;

import org.trab.demo.model.Agenda;
import org.trab.demo.model.Consulta;
import org.trab.demo.util.Conexao;
import org.trab.demo.util.StatusConsultaEnum;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class ConsultaRepository {

    public static List<Consulta> getConsultasData(Date data) throws SQLException
    {
        try {

            List<Consulta> consultas = new ArrayList<>();

            List<Agenda> horarios = AgendaRepository.getHorariosData(data);

            for(int i = 0; i < horarios.size(); i++) {
                Consulta con = new Consulta();

                String sql = "SELECT * FROM consultas WHERE id_agenda=? AND status=?";

                PreparedStatement statem = Conexao.getConn().prepareStatement(sql);
                statem.setLong(1,horarios.get(i).getId());
                statem.setString(2, StatusConsultaEnum.AGENDADO.toString());

                ResultSet result = statem.executeQuery();

                if(result.next()) {
                    con.setId(result.getLong("id"));

                    con.setPaciente(UserRepository.getPacienteId(result.getLong("id_paciente")));

                    con.setHorarioConsulta(horarios.get(i));
                    consultas.add(con);
                } else {
                    con.setHorarioConsulta(horarios.get(i));
                    consultas.add(con);
                }
            }
            return consultas;

        } catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }
    }

    public static void cancelarConsulta(Long idContulta) throws SQLException
    {
        try {
            String sql = "UPDATE consultas SET status=? WHERE id=?";
            PreparedStatement statement = Conexao.getConn().prepareStatement(sql);
            statement.setString(1,StatusConsultaEnum.CANCELADO.toString());
            statement.setLong(2,idContulta);
            statement.execute();

        } catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }
    }

}
