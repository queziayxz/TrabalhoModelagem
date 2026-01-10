package unit;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.trab.demo.model.User;

@ExtendWith(MockitoExtension.class)
public class UserTest {

    @Test
    public void validarCpfTest()
    {
        assertThrows(IllegalArgumentException.class, () -> User.validarCPF("123"));
        assertThrows(IllegalArgumentException.class, () -> User.validarCPF("12345678912"));
        assertDoesNotThrow(() -> User.validarCPF("37107107070"));
    }
}
