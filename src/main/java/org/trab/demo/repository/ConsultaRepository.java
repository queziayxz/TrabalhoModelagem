package org.trab.demo.repository;

import org.trab.demo.model.Agenda;
import org.trab.demo.model.Consulta;
import org.trab.demo.util.Conexao;
import org.trab.demo.enums.StatusConsultaEnum;

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

                if(horarios.get(i).getStatus().equals(StatusConsultaEnum.AGENDADO.toString()) ||
                horarios.get(i).getStatus().equals(StatusConsultaEnum.CONCLUIDO.toString()) ||
                horarios.get(i).getStatus().equals(StatusConsultaEnum.NAO_REALIZADO.toString())) {

                    String sql = "SELECT * FROM consultas WHERE id_agenda=?";

                    PreparedStatement statem = Conexao.getConn().prepareStatement(sql);
                    statem.setLong(1,horarios.get(i).getId());

                    ResultSet result = statem.executeQuery();

                    if(result.next()) {
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
            throw new SQLException(e.getMessage());
        }
    }

    public static List<Consulta> getConsultasFinalizadas() throws SQLException
    {
        try {
            List<Agenda> horarios = AgendaRepository.getHorariosStatus();
            List<Consulta> consultas = new ArrayList<>();

            for(Agenda horario : horarios) {
                String sql = "SELECT * FROM consultas WHERE id_agenda=?";
                PreparedStatement statement = Conexao.getConn().prepareStatement(sql);
                statement.setLong(1,horario.getId());
                ResultSet result = statement.executeQuery();
                if(result.next()) {
                    Consulta consulta = new Consulta();
                    consulta.setId(result.getLong("id"));
                    consulta.setHorarioConsulta(horario);
                    consulta.setPaciente(UserRepository.getPacienteId(result.getLong("id_paciente")));

                    consultas.add(consulta);
                }
            }

            return consultas;

        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    public static void cancelarConsulta(Long idContulta) throws SQLException
    {
        try {
            String sql = "UPDATE consultas SET status=? WHERE id=?";
            PreparedStatement statement = Conexao.getConn().prepareStatement(sql);
            statement.setString(1,StatusConsultaEnum.LIVRE.toString());
            statement.setLong(2,idContulta);
            statement.execute();

        } catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }
    }

}
