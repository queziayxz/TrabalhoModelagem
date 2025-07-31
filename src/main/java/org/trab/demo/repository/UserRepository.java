package org.trab.demo.repository;

import org.trab.demo.model.Paciente;
import org.trab.demo.model.Psicologo;
import org.trab.demo.model.User;
import org.trab.demo.util.Conexao;
import org.trab.demo.util.Sessao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepository {

    public static User autenticate(String email, String senha) throws SQLException
    {
        try {

            String sql = "SELECT * FROM usuarios WHERE email=? AND senha=?";

            PreparedStatement statem = Conexao.getConn().prepareStatement(sql);
            statem.setString(1, email);
            statem.setString(2, senha);

            ResultSet result = statem.executeQuery();

            if(result.next()) {
                switch(result.getString("is_psicologo")) {
                    case "1":
                        Psicologo psi = new Psicologo();
                        psi.setId(result.getLong("id"));
                        psi.setNome(result.getString("nome"));
                        psi.setTelefone(result.getString("telefone"));
                        psi.setEmail(result.getString("email"));
                        psi.setDataNascimento(result.getDate("data_nascimento"));
                        psi.setCpf(result.getString("cpf"));
                        psi.setCrp(result.getString("crp"));
                        psi.setSenha(result.getString("senha"));
                        psi.setIsPsicologo(result.getBoolean("is_psicologo"));

                        return psi;

                    case "0":
                        Paciente paci = new Paciente();
                        paci.setId(result.getLong("id"));
                        paci.setNome(result.getString("nome"));
                        paci.setTelefone(result.getString("telefone"));
                        paci.setEmail(result.getString("email"));
                        paci.setDataNascimento(result.getDate("data_nascimento"));
                        paci.setCpf(result.getString("cpf"));
                        paci.setSenha(result.getString("senha"));
                        paci.setIsPsicologo(result.getBoolean("is_psicologo"));

                        return paci;
                }

            } else {
                System.out.println("usuario nao existe");
            }

            return null;

        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    public static Paciente getPacienteId(Long id_paciente) throws SQLException
    {
        try {

            Paciente paciente = new Paciente();

            String sql = "SELECT * FROM usuarios WHERE id=?";
            PreparedStatement statm = Conexao.getConn().prepareStatement(sql);
            statm.setLong(1, id_paciente);

            ResultSet result = statm.executeQuery();

            if(result.next()) {
                paciente.setId(result.getLong("id"));
                paciente.setNome(result.getString("nome"));
                paciente.setTelefone(result.getString("telefone"));
                paciente.setEmail(result.getString("email"));
                paciente.setDataNascimento(result.getDate("data_nascimento"));
                paciente.setCpf(result.getString("cpf"));
                paciente.setSenha(result.getString("senha"));
                paciente.setIsPsicologo(result.getBoolean("is_psicologo"));

                return paciente;
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }
    }

    public static void updatePsicologo(Psicologo psicologo) throws SQLException
    {
        try {
            String sql = "UPDATE usuarios SET nome=?,telefone=?,email=?,data_nascimento=?,cpf=?,crp=? WHERE id=?";
            PreparedStatement statement = Conexao.getConn().prepareStatement(sql);
            statement.setString(1,psicologo.getNome());
            statement.setString(2,psicologo.getTelefone());
            statement.setString(3,psicologo.getEmail());
            statement.setString(4,psicologo.getDataNascimento().toString());
            statement.setString(5,psicologo.getCpf());
            statement.setString(6,psicologo.getCrp());
            statement.setLong(7,psicologo.getId());

            statement.execute();
        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }
}
