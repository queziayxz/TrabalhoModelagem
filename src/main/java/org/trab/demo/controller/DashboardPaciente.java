package org.trab.demo.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.trab.demo.enums.StatusConsultaEnum;
import org.trab.demo.model.Agenda;
import org.trab.demo.model.Consulta;
import org.trab.demo.model.Paciente;
import org.trab.demo.repository.ConsultaRepository;
import org.trab.demo.util.Sessao;
import org.trab.demo.util.Telas;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;

import static org.trab.demo.util.Telas.*;
import static org.trab.demo.util.Telas.getTelaCancelamento;

public class DashboardPaciente implements Initializable {

    @FXML private Label lbNome;
    @FXML private Label lbProxConsulta;
    @FXML private Button btnAcessarPerfil;
    @FXML private Button btnAgendar;
    @FXML private Button btnRemarcar;
    @FXML private Button btnCancelar;
    @FXML private Button btnDeslogar;
    @FXML private TableView<Consulta> tvConsultas;
    @FXML private TableColumn<Consulta, String> colDia;
    @FXML private TableColumn<Consulta, String> colHora;

    private Paciente paciente;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            // Obtém paciente da sessão
            paciente = Sessao.getInstance().getUser(Paciente.class);

            // Configura nome do paciente
            lbNome.setText(paciente.getNome());

            // Configurar as colunas da tabela
            colDia.setCellValueFactory(cellData -> {
                Agenda agenda = cellData.getValue().getHorarioConsulta();
                return new SimpleStringProperty(formatDate(agenda.getData()));
            });

            colHora.setCellValueFactory(cellData -> {
                Agenda agenda = cellData.getValue().getHorarioConsulta();
                return new SimpleStringProperty(formatTime(agenda.getHora()));
            });

            // Carrega consultas
            carregarConsultas();

        } catch (Exception e) {
            e.printStackTrace();
            lbProxConsulta.setText("Erro ao carregar dados do paciente");
        }
    }

    private void carregarConsultas() {
                try {List<Consulta> consultas = ConsultaRepository.getConsultasByPaciente(paciente.getId());

            tvConsultas.getItems().setAll(consultas);

            if (!consultas.isEmpty()) {
                Date hoje = new Date(Calendar.getInstance().getTimeInMillis());
                Consulta proxima = null;

                for (Consulta c : consultas) {
                    Date dataConsulta = c.getHorarioConsulta().getData();

                    // Considerar apenas consultas futuras ou do dia atual
                    if (dataConsulta.after(hoje) || dataConsulta.equals(hoje)) {
                        if (proxima == null || dataConsulta.before(proxima.getHorarioConsulta().getData())) {
                            proxima = c;
                        }
                    }
                }

                if (proxima != null) {
                    Agenda agendaProxima = proxima.getHorarioConsulta();
                    lbProxConsulta.setText("Próxima consulta: " +
                            formatDate(agendaProxima.getData()) +
                            " às " +
                            formatTime(agendaProxima.getHora()));
                } else {
                    lbProxConsulta.setText("Sua proxima consulta: ");
                }
            } else {
                lbProxConsulta.setText("Nenhuma consulta agendada");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            lbProxConsulta.setText("Erro ao carregar consultas");
        }
    }

    private String formatDate(Date date) {
        return new SimpleDateFormat("dd/MM/yyyy").format(date);
    }

    private String formatTime(Time time) {
        return new SimpleDateFormat("HH:mm").format(time);
    }

    @FXML
    private void telaPerfil() throws IOException {
        getTelaPerfil();
    }

    @FXML
    private void telaAgendamento() throws IOException {
        getTelaAgendamento();
    }

    @FXML
    private void telaRemarcacao() throws IOException {
        getTelaRemarcacao();
    }

    @FXML
    private void telaCancelamento() throws IOException {
        getTelaCancelamento();
    }

    @FXML
    private void deslogar() throws IOException {
        Sessao.getInstance().setUser(null);
        getTelaLogin(null);
    }
}