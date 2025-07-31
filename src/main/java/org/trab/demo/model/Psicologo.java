package org.trab.demo.model;

import java.util.Calendar;
import java.sql.Date;

public class Psicologo extends User {
    private String crp;

    public Psicologo(Long id, String nome, String email, String telefone, Date data_nascimento, String cpf, String crp, String senha, boolean is_psicologo) {
        super(id, nome, email, telefone, data_nascimento, cpf, senha, is_psicologo);
        this.crp = crp;
    }

    public Psicologo() {

    }

    public String getCrp() {
        return crp;
    }

    public void setCrp(String crp) {
        this.crp = crp;
    }
}
