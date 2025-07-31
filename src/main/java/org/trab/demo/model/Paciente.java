package org.trab.demo.model;

import java.util.Calendar;
import java.sql.Date;

public class Paciente extends User {
    public Paciente(Long id, String nome, String email, String telefone, Date data_nascimento, String cpf, String senha, boolean is_psicologo) {
        super(id, nome, email, telefone, data_nascimento, cpf, senha, is_psicologo);
    }

    public Paciente() {
    }
}
