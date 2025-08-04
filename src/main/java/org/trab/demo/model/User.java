package org.trab.demo.model;

import java.sql.Date;

public abstract class User {
    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private Date data_nascimento;
    private String cpf;
    private String senha;
    private boolean is_psicologo;

    public User(Long id, String nome, String email, String telefone, Date data_nascimento, String cpf, String senha, boolean is_psicologo) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.data_nascimento = data_nascimento;
        this.cpf = cpf;
        this.senha = senha;
        this.is_psicologo = is_psicologo;
    }

    public User() {

    }

    public static void validarCPF(String cpf) throws IllegalArgumentException {
        cpf = cpf.replaceAll("[^\\d]+", "");

        // Elimina CPFs inválidos conhecidos
        if (cpf.length() != 11 ||
                cpf.equals("00000000000") ||
                cpf.equals("11111111111") ||
                cpf.equals("22222222222") ||
                cpf.equals("33333333333") ||
                cpf.equals("44444444444") ||
                cpf.equals("55555555555") ||
                cpf.equals("66666666666") ||
                cpf.equals("77777777777") ||
                cpf.equals("88888888888") ||
                cpf.equals("99999999999")) {

            throw new IllegalArgumentException("CPF Inválido");

        }

        // Valida 1o digito
        int add = 0;
        for (int i = 0; i < 9; i++) {
            add += Integer.parseInt(String.valueOf(cpf.charAt(i))) * (10 - i);
        }
        int rev = 11 - (add % 11);
        if (rev == 10 || rev == 11) {
            rev = 0;
        }
        if (rev != Integer.parseInt(String.valueOf(cpf.charAt(9)))) {
            throw new IllegalArgumentException("CPF Inválido");
        }

        // Valida 2o digito
        add = 0;
        for (int i = 0; i < 10; i++) {
            add += Integer.parseInt(String.valueOf(cpf.charAt(i))) * (11 - i);
        }
        rev = 11 - (add % 11);
        if (rev == 10 || rev == 11) {
            rev = 0;
        }
        if (rev != Integer.parseInt(String.valueOf(cpf.charAt(10)))) {
            throw new IllegalArgumentException("CPF Inválido");
        }
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return this.nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return this.telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Date getDataNascimento() {
        return this.data_nascimento;
    }

    public void setDataNascimento(Date data_nascimento) {
        this.data_nascimento = data_nascimento;
    }

    public String getCpf() {
        return this.cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getSenha() {
        return this.senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public boolean getIsPsicologo() {
        return this.is_psicologo;
    }

    public void setIsPsicologo(boolean is_psicologo) {
        this.is_psicologo = is_psicologo;
    }
}
