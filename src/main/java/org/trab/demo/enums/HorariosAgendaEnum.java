package org.trab.demo.enums;

public enum HorariosAgendaEnum {
    OITO("08:00:00"),
    NOVE("09:00:00"),
    DEZ("10:00:00"),
    ONZE("11:00:00"),
    TREZE("13:00:00"),
    QUATORZE("14:00:00"),
    QUINZE("15:00:00"),
    DEZESSEIS("16:00:00"),
    DEZESSETE("17:00:00"),
    DEZOITO("18:00:00"),
    DEZENOVE("19:00:00"),
    VINTE("20:00:00");

    private final String horario;
    HorariosAgendaEnum(String horario) {
        this.horario = horario;
    }

    public String getHorario() {
        return horario;
    }
}
