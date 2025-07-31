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

    public static void atualizarUsuario(User user) throws SQLException {
        String sql = "UPDATE usuarios SET nome=?, email=?, telefone=?, data_nascimento=?, senha=? WHERE id=?";
        try (PreparedStatement stmt = Conexao.getConn().prepareStatement(sql)) {
            stmt.setString(1, user.getNome());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getTelefone());
            if (user.getDataNascimento() != null) {
                stmt.setDate(4, new Date(user.getDataNascimento().getTime()));
            } else {
                stmt.setDate(4, null);
            }
            stmt.setString(5, user.getSenha());
            stmt.setLong(6, user.getId());

            int updated = stmt.executeUpdate();
            if (updated == 0) {
                throw new SQLException("Nenhum registro atualizado para o usuário ID " + user.getId());
            }
        }
    }
}

}
