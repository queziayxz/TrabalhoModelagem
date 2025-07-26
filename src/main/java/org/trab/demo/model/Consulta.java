package org.trab.demo.model;

import java.sql.Time;
import java.util.Date;
import java.text.SimpleDateFormat;

public class Consulta {
    private Long id;
    private Paciente paciente;
    private Agenda horarioConsulta;

    public Consulta(Long id, Paciente paciente, Agenda horarioConsulta) {
        this.id = id;
        this.paciente = paciente;
        this.horarioConsulta = horarioConsulta;
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

    public Agenda getHorarioConsulta() {
        return this.horarioConsulta;
    }

    public void setHorarioConsulta(Agenda horarioConsulta) {
        this.horarioConsulta = horarioConsulta;
    }

    // Pega as Datas formatadas
    public String getDataFormatada() {
        return new SimpleDateFormat("dd/MM/yyyy").format(horarioConsulta.getData());
    }

    public String getHoraFormatada() {
        return new SimpleDateFormat("HH:mm").format(horarioConsulta.getHora());
    }
}
