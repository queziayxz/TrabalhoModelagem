package org.trab.demo.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import org.trab.demo.model.Agenda;
import org.trab.demo.model.Consulta;
import org.trab.demo.repository.ConsultaRepository;

import java.sql.SQLException;
import java.time.LocalDate;

public class CancelamentoController extends BaseConsultaController {

    @FXML private TableView<Consulta> tableViewConsultas;
    @FXML private TableColumn<Consulta, String> colDia;
    @FXML private TableColumn<Consulta, String> colHora;
    @FXML private Button btnCancelarConsulta;
    @FXML private Text textAjuda;
    @FXML private Button btnCancelarConsulta1;

    private ObservableList<Consulta> consultasList;
    private Consulta consultaSelecionada;

    @FXML
    public void initialize() {
        // Configurar as colunas
        colDia.setCellValueFactory(cellData -> {
            Agenda agenda = cellData.getValue().getHorarioConsulta();
            return new SimpleStringProperty(formatDate(agenda.getData()));
        });

        colHora.setCellValueFactory(cellData -> {
            Agenda agenda = cellData.getValue().getHorarioConsulta();
            return new SimpleStringProperty(formatTime(agenda.getHora()));
        });

        tableViewConsultas.getSelectionModel().selectedItemProperty().addListener((obs, old, newSelection) -> {
            consultaSelecionada = newSelection;
            verificarSelecao();
        });

        // Configurar clique no texto de ajuda
        if (textAjuda != null) {
            textAjuda.setOnMouseClicked(event -> onAjuda());
        }

        carregarConsultas();
    }

    @Override
    protected void verificarSelecao() {
        if (consultaSelecionada != null) {
            LocalDate dataConsulta = consultaSelecionada.getHorarioConsulta().getData().toLocalDate();
            boolean podeCancelar = podeCancelarOuRemarcar(dataConsulta);
            btnCancelarConsulta.setDisable(!podeCancelar);
        } else {
            btnCancelarConsulta.setDisable(true);
        }
    }

    private void carregarConsultas() {
        try {
            consultasList = carregarConsultasPaciente();
            tableViewConsultas.setItems(consultasList);

            if (consultasList.isEmpty()) {
                showAlert("Informação", "Você não tem consultas agendadas para cancelar.");
            }
        } catch (SQLException e) {
            showAlert("Erro", "Erro ao buscar suas consultas: " + e.getMessage());
        }
    }

    @FXML
    private void cancelarConsulta() {
        if (consultaSelecionada == null) {
            showAlert("Atenção", "Por favor, selecione uma consulta para cancelar.");
            return;
        }

        LocalDate dataConsulta = consultaSelecionada.getHorarioConsulta().getData().toLocalDate();
        if (!podeCancelarOuRemarcar(dataConsulta)) {
            showAlert("Atenção", "Você só pode cancelar consultas com pelo menos 1 dia de antecedência.");
            return;
        }

        try {
            ConsultaRepository.cancelarConsulta(consultaSelecionada.getId());
            showAlert("Sucesso", "Sua consulta foi cancelada com sucesso!");
            reabrirTela();
        } catch (SQLException e) {
            showAlert("Erro", "Não foi possível cancelar sua consulta: " + e.getMessage());
        }
    }

    // Método de ajuda com texto amigável
    @FXML
    private void onAjuda() {
        showAlert("Como cancelar uma consulta",
                "Passo a passo para cancelar uma consulta:\n\n" +
                        "1. Na tabela abaixo, você verá todas as suas consultas agendadas.\n" +
                        "2. Clique em uma consulta para selecioná-la.\n" +
                        "3. O botão 'Cancelar' será liberado se a consulta puder ser cancelada.\n" +
                        "4. Clique em 'Cancelar' para confirmar o cancelamento.\n\n" +
                        "Importante: Você só pode cancelar consultas com pelo menos 1 dia de antecedência.\n" +
                        "Consultas de hoje não podem ser canceladas através do sistema.");
    }

    // Implementação do método reabrirTela
    @Override
    protected void reabrirTela() {
        // Limpar seleção
        tableViewConsultas.getSelectionModel().clearSelection();
        consultaSelecionada = null;

        // Recarregar consultas
        carregarConsultas();

        // Desabilitar botão
        btnCancelarConsulta.setDisable(true);
    }

    // Sobrescreve apenas o método específico da tela atual
    @Override
    public void onCancelar(MouseEvent mouseEvent) {
        showAlert("Cancelamento", "Você já está na tela de cancelamento de consultas");
    }
}