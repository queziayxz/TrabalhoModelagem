package org.trab.demo.model;

import java.sql.Time;
import java.util.Date;

public class Agenda {
    private Long id;
    private Date data;
    private Time hora;
    private Long id_psicologo;

    public Agenda(Long id, Date data, Time hora, Long id_psicologo) {
        this.id = id;
        this.data = data;
        this.hora = hora;
        this.id_psicologo = id_psicologo;
    }

    public Agenda() {
    }

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
        return id_psicologo;
    }

    public void setIdPsicologo(Long id_psicologo) {
        this.id_psicologo = id_psicologo;
    }
}
