package org.trab.demo.repository;

import org.trab.demo.model.Agenda;
import org.trab.demo.model.Consulta;
import org.trab.demo.util.Conexao;
import org.trab.demo.util.StatusConsultaEnum;

import java.sql.*;
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

    /*
        Método responsavel por obter todas as consultas do paciente durante a sua sessão
     */
    public static List<Consulta> getConsultasByPaciente(long idPaciente) throws SQLException {
        List<Consulta> consultas = new ArrayList<>();
        String sql = "SELECT c.id, a.id as id_agenda, a.data, a.hora, a.id_psicologo " +
                "FROM consultas c " +
                "JOIN agendas a ON c.id_agenda = a.id " +
                "WHERE c.id_paciente = ? AND c.status = 'AGENDADO'";

        try (Connection conn = Conexao.getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, idPaciente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Agenda agenda = new Agenda(
                            rs.getLong("id_agenda"),
                            rs.getDate("data"),
                            rs.getTime("hora"),
                            rs.getLong("id_psicologo")
                    );

                    Consulta consulta = new Consulta();
                    consulta.setId(rs.getLong("id"));
                    consulta.setHorarioConsulta(agenda);

                    consultas.add(consulta);
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Erro ao buscar consultas: " + e.getMessage());
        }
        return consultas;
    }

}
