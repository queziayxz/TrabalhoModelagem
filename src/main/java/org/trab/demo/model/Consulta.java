package org.trab.demo.model;

import java.sql.Time;
import java.util.Date;

public class Consulta {
    private Long id;
    private Paciente paciente;
    private Date data;
    private Time horario;

    public Consulta(Long id, Paciente paciente, Date data, Time horario) {
        this.id = id;
        this.paciente = paciente;
        this.data = data;
        this.horario = horario;
    }

    public Consulta() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Time getHorario() {
        return horario;
    }

    public void setHorario(Time horario) {
        this.horario = horario;
    }
}
