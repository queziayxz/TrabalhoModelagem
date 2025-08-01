package org.trab.demo.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import org.trab.demo.enums.StatusConsultaEnum;
import org.trab.demo.model.Consulta;
import org.trab.demo.model.Paciente;
import org.trab.demo.repository.AgendaRepository;
import org.trab.demo.repository.ConsultaRepository;
import org.trab.demo.util.Sessao;
import org.trab.demo.util.Telas;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.util.List;

public class CancelamentoController {

    @FXML private TableView<Consulta> tableViewConsultas;
    @FXML private TableColumn<Consulta, Date> colDia;
    @FXML private TableColumn<Consulta, Time> colHora;
    @FXML private Button btnCancelarConsulta;

    private ObservableList<Consulta> consultasList = FXCollections.observableArrayList();
    private Consulta consultaSelecionada;

    @FXML
    public void initialize() {
        // Configurar as colunas usando as propriedades aninhadas
        colDia.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getHorarioConsulta().getData()));
        colHora.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getHorarioConsulta().getHora()));

        tableViewConsultas.setItems(consultasList);
        btnCancelarConsulta.setDisable(true);

        tableViewConsultas.getSelectionModel().selectedItemProperty().addListener((obs, old, newSelection) -> {
            consultaSelecionada = newSelection;
            btnCancelarConsulta.setDisable(newSelection == null);
        });

        carregarConsultas();
    }

    private void carregarConsultas() {
        Sessao sessao = Sessao.getInstance();
        Paciente paciente = sessao.getUser(Paciente.class);

        try {
            List<Consulta> consultas = ConsultaRepository.getConsultasByPaciente(paciente.getId());
            consultasList.clear();

            for (Consulta consulta : consultas) {
                if (consulta.getHorarioConsulta().getStatus().equals(StatusConsultaEnum.AGENDADO.toString())) {
                    consultasList.add(consulta);
                }
            }

            if (consultasList.isEmpty()) {
                showAlert("Informação", "Você não tem consultas agendadas para cancelar.");
            }
        } catch (SQLException e) {
            showAlert("Erro", "Erro ao buscar consultas: " + e.getMessage());
        }
    }

    @FXML
    private void cancelarConsulta() {
        try {
            ConsultaRepository.cancelarConsulta(consultaSelecionada.getId());
            AgendaRepository.atualizarStatusHorario(
                    consultaSelecionada.getHorarioConsulta().getId(),
                    StatusConsultaEnum.LIVRE.toString()
            );

            showAlert("Sucesso", "Consulta cancelada com sucesso!");
            carregarConsultas();
            btnCancelarConsulta.setDisable(true);
        } catch (SQLException e) {
            showAlert("Erro", "Erro ao cancelar consulta: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    public void onDeslogar(MouseEvent mouseEvent) {
        try {
            Sessao.getInstance().setUser(null); //define paciente da sessão NULL;
            Telas.getTelaLogin(null); //chama tela de login
        } catch (IOException e) {
            showError("Erro de Navegação", "Não foi possível abrir a tela de login");
        }
    }

    public void onInicio(MouseEvent mouseEvent) {
        try {
            Telas.getTelaDashPaci();//chama tela de paciente
        } catch (IOException e) {
            showError("Erro de Navegação", "Não foi possível abrir a tela de login");
        }
    }

    public void onPerfil(MouseEvent mouseEvent) {
        try {
            Telas.getTelaPerfil(); //chama tela de Editar Perfil
        } catch (IOException e) {
            showError("Erro de Navegação", "Não foi possível abrir a tela de login");
        }
    }

    public void onAgendar(MouseEvent mouseEvent) {
        try {
            Telas.getTelaAgendamento(); //chama tela de Editar Perfil
        } catch (IOException e) {
            showError("Erro de Navegação", "Não foi possível abrir a tela de login");
        }
    }

    public void onRemarcar(MouseEvent mouseEvent) {
        try {
            Telas.getTelaRemarcacao(); //chama tela de Editar Perfil
        } catch (IOException e) {
            showError("Erro de Navegação", "Não foi possível abrir a tela de login");
        }
    }

    public void onCancelar(MouseEvent mouseEvent) {
        try {
            Telas.getTelaCancelamento(); //chama tela de Editar Perfil
        } catch (IOException e) {
            showError("Erro de Navegação", "Não foi possível abrir a tela de login");
        }
    }
}