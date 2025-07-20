package org.trab.demo.repository;

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

            String sql = "SELECT * FROM consultas WHERE data=?";

            PreparedStatement statem = Conexao.getConn().prepareStatement(sql);
            statem.setDate(1,data);

            ResultSet result = statem.executeQuery();

            while(result.next()) {
                Consulta con = new Consulta();
                con.setId(result.getLong("id"));

                con.setPaciente(UserRepository.getPacienteId(result.getLong("id_paciente")));

                con.setData(result.getDate("data"));
                con.setHorario(result.getTime("horario"));
                consultas.add(con);
            }

            return consultas;

        } catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }
    }

}
