package unit;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.trab.demo.util.Conexao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@ExtendWith(MockitoExtension.class)
public class ConexaoTest {

    @InjectMocks
    private Conexao conn;
    @Test
    public void getConnTest()
    {
        Connection mockConn = Mockito.mock(Connection.class);

        try(MockedStatic<Conexao> mockedConfig = Mockito.mockStatic(Conexao.class)) {
            mockedConfig.when(Conexao::getConn).thenReturn(mockConn);
            Connection connection = Conexao.getConn();
            assertNotNull(connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
