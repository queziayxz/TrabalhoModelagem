package org.trab.demo.repository;

import org.trab.demo.model.Paciente;
import org.trab.demo.model.Psicologo;
import org.trab.demo.util.Conexao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

public class UserRepository {

    public static void verifyLogin(String email, String senha) throws SQLException
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
                        break;
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
                        break;
                }
            } else {
                System.out.println("usuario nao existe");
            }

        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }

}
