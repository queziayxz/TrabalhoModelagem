package org.trab.demo.repository;

import org.trab.demo.model.Agenda;
import org.trab.demo.model.Consulta;
import org.trab.demo.util.Conexao;

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
                String sql = "SELECT * FROM consultas WHERE id_agenda=?";

                PreparedStatement statem = Conexao.getConn().prepareStatement(sql);
                statem.setLong(1,horarios.get(i).getId());

                ResultSet result = statem.executeQuery();

                while(result.next()) {
                    Consulta con = new Consulta();
                    con.setId(result.getLong("id"));

                    con.setPaciente(UserRepository.getPacienteId(result.getLong("id_paciente")));

                    con.setHorarioConsulta(horarios.get(i));
                    consultas.add(con);
                }
            }

            return consultas;

        } catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }
    }

}
