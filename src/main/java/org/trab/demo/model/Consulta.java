package org.trab.demo.model;

import java.sql.Time;
import java.sql.Date;

public class Consulta {
    private Long id;
    private Long idPaciente;  // Novo campo adicionado
    private Long idAgenda;    // Novo campo adicionado
    private Paciente paciente;
    private Agenda horarioConsulta;

    public Consulta(Long id, Long idPaciente, Long idAgenda, Paciente paciente, Agenda horarioConsulta) {
        this.id = id;
        this.idPaciente = idPaciente;
        this.idAgenda = idAgenda;
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

    public Long getIdPaciente() {
        return idPaciente;
    }

    public void setIdPaciente(Long idPaciente) {
        this.idPaciente = idPaciente;
    }

    public Long getIdAgenda() {
        return idAgenda;
    }

    public void setIdAgenda(Long idAgenda) {
        this.idAgenda = idAgenda;
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
}