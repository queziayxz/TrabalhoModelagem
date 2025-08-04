package org.trab.demo.model;

import java.sql.Time;
import java.sql.Date;

public class Agenda {
    private Long id;
    private Date data;
    private Time hora;
    private Long idPsicologo;  // Corresponde a coluna id_psicologo no SQL
    private String status;      // Corresponde a coluna status no SQL

    // Construtor completo alinhado com SQL
    public Agenda(Long id, Date data, Time hora, Long idPsicologo, String status) {
        this.id = id;
        this.data = data;
        this.hora = hora;
        this.idPsicologo = idPsicologo;
        this.status = status;
    }

    // Construtor parcial mantido para compatibilidade
    public Agenda(Long id, Date data, Time hora, Long idPsicologo) {
        this(id, data, hora, idPsicologo, null);
    }

    public Agenda() {
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Time getHora() {
        return hora;
    }

    public void setHora(Time hora) {
        this.hora = hora;
    }

    public Long getIdPsicologo() {
        return idPsicologo;
    }

    public void setIdPsicologo(Long idPsicologo) {
        this.idPsicologo = idPsicologo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}