package unit;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.trab.demo.controller.AgendaController;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;

public class AgendaTest {

    @Test
    public void validaDataHoraDataAntesTest()
    {
        AgendaController agenda = new AgendaController();

        assertTrue(agenda.validaDataHora(Date.valueOf(LocalDate.of(1995, 1, 12)),
                LocalTime.now()));
    }

    @Test
    public void validaDataHoraTesteHoraDepoisTest()
    {
        AgendaController agenda = new AgendaController();
        assertFalse(agenda.validaDataHora(Date.valueOf(LocalDate.now()),LocalTime.of(23,10)));
    }

    @Test
    public void validaDataHoraTesteHoraAntesTest()
    {
        AgendaController agenda = new AgendaController();
        assertTrue(agenda.validaDataHora(Date.valueOf(LocalDate.now()),LocalTime.of(07,10)));

    }

    @Test
    public void validaDataHoraTesteDataDepoisTest()
    {
        AgendaController agenda = new AgendaController();
        assertFalse(agenda.validaDataHora(Date.valueOf(LocalDate.of(2027,12,01)),LocalTime.now()));

    }
}
