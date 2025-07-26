package org.trab.demo.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.trab.demo.model.Consulta;
import org.trab.demo.model.Paciente;
import org.trab.demo.repository.ConsultaRepository;
import org.trab.demo.util.Sessao;
import org.trab.demo.util.Telas;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;

public class DashboardPaciente implements Initializable {

    @FXML private Label lbNome;
    @FXML private Label lbProxConsulta;
    @FXML private Button btnAcessarPerfil;
    @FXML private Button btnAgendar;
    @FXML private Button btnRemarcar;
    @FXML private Button btnCancelar;
    @FXML private Button btnAcessarPerfil1; // Botão Deslogar
    @FXML private TableView<Consulta> tvConsultas;
    @FXML private TableColumn<Consulta, String> colDia;
    @FXML private TableColumn<Consulta, String> colHora;

    private Paciente paciente;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Obtém paciente da sessão
        paciente = Sessao.getInstance().getUser(Paciente.class);

        // Configura nome do paciente
        lbNome.setText(paciente.getNome());

        colDia.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDataFormatada()));

        colHora.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getHoraFormatada()));

        // Carrega consultas
        carregarConsultas();
    }

    private void carregarConsultas() {
        try {
            List<Consulta> consultas = ConsultaRepository.getConsultasByPaciente(paciente.getId());
            tvConsultas.getItems().setAll(consultas);

            if (!consultas.isEmpty()) {
                Date hoje = new Date(Calendar.getInstance().getTimeInMillis());
                Consulta proxima = null;

                for (Consulta c : consultas) {
                    if (c.getHorarioConsulta().getData().after(hoje) ||
                            c.getHorarioConsulta().getData().equals(hoje)) {

                        if (proxima == null ||
                                c.getHorarioConsulta().getData().before(proxima.getHorarioConsulta().getData())) {
                            proxima = c;
                        }
                    }
                }

                if (proxima != null) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                    lbProxConsulta.setText("Próximas consultas: " +
                            dateFormat.format(proxima.getHorarioConsulta().getData()) +
                            " às " +
                            timeFormat.format(proxima.getHorarioConsulta().getHora()));
                } else {
                    lbProxConsulta.setText("Próxima consulta: ");
                }
            } else {
                lbProxConsulta.setText("Nenhuma consulta agendada");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            lbProxConsulta.setText("Erro ao carregar consultas");
        }
    }

    @FXML
    private void telaPerfil() throws IOException {
        Telas.getTelaPerfil();
    }

    @FXML
    private void telaAgendamento() throws IOException {
        Telas.getTelaAgendamento();
    }

    @FXML
    private void telaRemarcacao() throws IOException {
        Telas.getTelaRemarcacao();
    }

    @FXML
    private void telaCancelamento() throws IOException {
        Telas.getTelaCancelamento();
    }

    @FXML
    private void deslogar() throws IOException {
        Sessao.getInstance().setUser(null); // Limpa a sessão
        Telas.getTelaLogin(null); // Volta para tela de login
    }
}