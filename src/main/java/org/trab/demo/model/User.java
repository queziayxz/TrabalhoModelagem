package org.trab.demo.model;

import java.util.Date;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Date getDataNascimento() {
        return data_nascimento;
    }

    public void setDataNascimento(Date data_nascimento) {
        this.data_nascimento = data_nascimento;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public boolean getIsPsicologo() {
        return is_psicologo;
    }

    public void setIsPsicologo(boolean is_psicologo) {
        this.is_psicologo = is_psicologo;
    }
}
