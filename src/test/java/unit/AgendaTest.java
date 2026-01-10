package unit;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.trab.demo.controller.AgendaController;

import java.sql.Connection;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;

public class AgendaTest {
//    @InjectMocks
//    private AgendaController agenda;

    @Test
    public void validaDataHoraTest()
    {
        AgendaController agenda = new AgendaController();
        LocalDate localDate = LocalDate.of(1995, 1, 12);
        LocalTime localTime = LocalTime.of(10,10);
        Date date = Date.valueOf(localDate);

        assertTrue(agenda.validaDataHora(date,localTime));
        assertFalse(agenda.validaDataHora(Date.valueOf(LocalDate.now()),LocalTime.of(20,10)));
        assertTrue(agenda.validaDataHora(Date.valueOf(LocalDate.now()),LocalTime.of(07,10)));
        assertFalse(agenda.validaDataHora(Date.valueOf(LocalDate.of(2027,12,01)),LocalTime.of(20,10)));
    }
}
