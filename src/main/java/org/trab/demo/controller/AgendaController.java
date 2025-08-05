package org.trab.demo.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.trab.demo.enums.StatusConsultaEnum;
import org.trab.demo.model.Agenda;
import org.trab.demo.model.Consulta;
import org.trab.demo.repository.AgendaRepository;
import org.trab.demo.repository.ConsultaRepository;
import org.trab.demo.util.Sessao;
import org.trab.demo.util.Telas;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.ResourceBundle;
import java.util.TimeZone;

public class AgendaController implements Initializable {
    @FXML
    private Button btn_agenda;

    @FXML
    private Button btn_cancelarConsulta;

    @FXML
    private Button btn_deletarHorario;
    @FXML
    private Button btn_consultaRealizada;
    @FXML
    private Button btn_consultaNaoRealizada;

    @FXML
    private Button btn_mostraHorarios;

    @FXML
    private DatePicker data_picker;

    @FXML
    private Label lb_data;

    @FXML
    private Label lb_semHorario;
    @FXML
    private Label lb_selectData;
    @FXML
    private Label lb_dataSelecionada;

    @FXML
    private GridPane grid_horarios;

    @FXML
    private TextField tf_email;

    @FXML
    private TextField tf_telefone;

    @FXML
    private TextField tf_nome;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        btn_agenda.setDisable(true);
    }

    public void mostrarHorariosDia()
    {
        try {
            this.validaCampos();

            LocalDate data = this.data_picker.getValue();
            Date dateSql = Date.valueOf(data);
            List<Consulta> consultas = ConsultaRepository.getConsultasData(dateSql);

            resetaCampos();

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
            dateFormat.setTimeZone(TimeZone.getTimeZone("America/Sao_Paulo"));
            String formattedDate = dateFormat.format(dateSql);
            this.lb_dataSelecionada.setText(formattedDate);
            this.lb_dataSelecionada.setVisible(true);

            this.lb_selectData.setVisible(false);

            if(consultas.isEmpty()) {
                this.lb_semHorario.setVisible(true);
            } else {
                for(int i = 0; i < consultas.size(); i++) {
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                    String formattedTime = timeFormat.format(consultas.get(i).getHorarioConsulta().getHora());

                    Button button = new Button(formattedTime);
                    button.setPrefWidth(85);
                    button.setPrefHeight(26);
                    button.setStyle("-fx-font-size:18");

                    button.setOnAction(this::selecionaHorario);

                    Label label = new Label();
                    label.setStyle("-fx-font-size:18");

                    if(consultas.get(i).getPaciente() != null) {
                        button.setUserData(consultas.get(i));

                        switch (consultas.get(i).getHorarioConsulta().getStatus()) {
                            case "AGENDADO":
                                label.setText(consultas.get(i).getPaciente().getNome());
                                break;
                            case "CONCLUIDO":
                                label.setText("Consulta Realizada");
                                break;
                            case "NAO_REALIZADO":
                                label.setText("Consulta não Realizada");
                                break;
                        }
                    } else {
                        this.btn_deletarHorario.setUserData(consultas.get(i).getHorarioConsulta());
                        label.setText("Horário Livre");
                    }

                    this.grid_horarios.add(button, 0, i);
                    this.grid_horarios.add(label, 1, i);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (IllegalArgumentException e) {
            Alert dialogoInfo = new Alert(Alert.AlertType.WARNING);
            dialogoInfo.setTitle("Error");
            dialogoInfo.setHeaderText(e.getMessage());
            dialogoInfo.showAndWait();
        }
    }

    public void selecionaHorario(ActionEvent event)
    {
        Button button = (Button) event.getSource();
        Consulta consulta = (Consulta) button.getUserData();

        if(consulta != null) {
            this.tf_nome.setText(consulta.getPaciente().getNome());
            this.tf_telefone.setText(consulta.getPaciente().getTelefone());
            this.tf_email.setText(consulta.getPaciente().getEmail());
            this.btn_deletarHorario.setDisable(true);

            if(consulta.getHorarioConsulta().getStatus().equals(StatusConsultaEnum.AGENDADO.toString())) {
                this.btn_cancelarConsulta.setDisable(false);
                this.btn_cancelarConsulta.setUserData(consulta);

                this.btn_consultaRealizada.setDisable(false);
                this.btn_consultaRealizada.setUserData(consulta);

                this.btn_consultaNaoRealizada.setDisable(false);
                this.btn_consultaNaoRealizada.setUserData(consulta);
            } else {
                this.btn_cancelarConsulta.setDisable(true);
                this.btn_consultaRealizada.setDisable(true);
                this.btn_consultaNaoRealizada.setDisable(true);
            }

        } else {
            this.tf_nome.clear();
            this.tf_telefone.clear();
            this.tf_email.clear();
            this.btn_deletarHorario.setDisable(false);
            this.btn_cancelarConsulta.setDisable(true);
        }
    }

    public void concluirConsulta(ActionEvent event)
    {
        Button button = (Button) event.getSource();
        Consulta consulta = (Consulta) button.getUserData();

        LocalTime horarioAgora = consulta.getHorarioConsulta().getHora().toLocalTime();

        if(consulta.getHorarioConsulta().getData().after(new java.util.Date()) || horarioAgora.isAfter(LocalTime.now())) {
            Alert dialogoExe = new Alert(Alert.AlertType.INFORMATION);
            dialogoExe.setTitle("Erro Finalização Consulta");
            dialogoExe.setHeaderText("Não foi possível marcar a consulta como concluída!");
            dialogoExe.setContentText("Foi selecionado uma consulta em datas futuras.");
            dialogoExe.showAndWait();
        } else {
            Alert dialogoExe = new Alert(Alert.AlertType.CONFIRMATION);
            ButtonType btnRealizado = new ButtonType("Marcar como Realizado");
            ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            String dateF = dateFormat.format(consulta.getHorarioConsulta().getData());
            String timeF = timeFormat.format(consulta.getHorarioConsulta().getHora());

            dialogoExe.setTitle("Finalizando Consulta");
            dialogoExe.setContentText("Tem certeza que deseja marcar como realizado a consulta no dia "+dateF+" as "+timeF+"?");
            dialogoExe.getButtonTypes().setAll(btnRealizado, btnCancelar);
            dialogoExe.showAndWait().ifPresent(b -> {
                if (b == btnRealizado) {
                    try {
                        AgendaRepository.finalizaConsulta(consulta.getHorarioConsulta().getId(), StatusConsultaEnum.CONCLUIDO.toString());

                        Alert dialogoInfo = new Alert(Alert.AlertType.INFORMATION);
                        dialogoInfo.setContentText("Consulta Finalizada com Sucesso!");
                        dialogoInfo.showAndWait();
                        resetaCampos();
                    } catch (SQLException e) {
                        Alert dialogoInfo = new Alert(Alert.AlertType.WARNING);
                        dialogoInfo.setTitle("Error");
                        dialogoInfo.setHeaderText("Não foi possível marcar como concluída a consulta selecionada!");
                        dialogoInfo.showAndWait();
                    }
                }
            });
        }

    }

    public void marcarNaoRealizadaConsulta(ActionEvent event)
    {
        Button button = (Button) event.getSource();
        Consulta consulta = (Consulta) button.getUserData();

        LocalTime horarioAgora = consulta.getHorarioConsulta().getHora().toLocalTime();

        if(consulta.getHorarioConsulta().getData().after(new java.util.Date()) || horarioAgora.isAfter(LocalTime.now())) {
            Alert dialogoExe = new Alert(Alert.AlertType.INFORMATION);
            dialogoExe.setTitle("Erro Finalização Consulta");
            dialogoExe.setHeaderText("Não foi possível marcar a consulta como não concluída!");
            dialogoExe.setContentText("Foi selecionado uma consulta em datas futuras.");
            dialogoExe.showAndWait();
        } else {
            Alert dialogoExe = new Alert(Alert.AlertType.CONFIRMATION);
            ButtonType btnRealizado = new ButtonType("Marcar como Não Realizado");
            ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            String dateF = dateFormat.format(consulta.getHorarioConsulta().getData());
            String timeF = timeFormat.format(consulta.getHorarioConsulta().getHora());

            dialogoExe.setTitle("Deletando Horário");
            dialogoExe.setContentText("Tem certeza que deseja marcar como não realizado a consulta no dia "+dateF+" as "+timeF+"?");
            dialogoExe.getButtonTypes().setAll(btnRealizado, btnCancelar);
            dialogoExe.showAndWait().ifPresent(b -> {
                if (b == btnRealizado) {
                    try {
                        AgendaRepository.finalizaConsulta(consulta.getHorarioConsulta().getId(), StatusConsultaEnum.NAO_REALIZADO.toString());

                        Alert dialogoInfo = new Alert(Alert.AlertType.INFORMATION);
                        dialogoInfo.setContentText("Consulta Finalizada com Sucesso!");
                        dialogoInfo.showAndWait();
                        resetaCampos();
                    } catch (SQLException e) {
                        Alert dialogoInfo = new Alert(Alert.AlertType.WARNING);
                        dialogoInfo.setTitle("Error");
                        dialogoInfo.setHeaderText("Não foi possível marcar como não concluída a consulta selecionada!");
                        dialogoInfo.showAndWait();
                    }
                }
            });
        }

    }

    public void deletarHorario(ActionEvent event)
    {
        Button button = (Button) event.getSource();
        Agenda horario = (Agenda) button.getUserData();

        Alert dialogoExe = new Alert(Alert.AlertType.CONFIRMATION);
        ButtonType btnDeletar = new ButtonType("Deletar");
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String dateF = dateFormat.format(horario.getData());
        String timeF = timeFormat.format(horario.getHora());

        dialogoExe.setTitle("Deletando Horário");
        dialogoExe.setContentText("Tem certeza que deseja detelar o horário de "+timeF+" no dia "+dateF+"?");
        dialogoExe.getButtonTypes().setAll(btnDeletar, btnCancelar);
        dialogoExe.showAndWait().ifPresent(b -> {
            if (b == btnDeletar) {
                try {
                    AgendaRepository.deleteHorario(horario.getId());

                    Alert dialogoInfo = new Alert(Alert.AlertType.INFORMATION);
                    dialogoInfo.setContentText("Horário Deletado com Sucesso!");
                    dialogoInfo.showAndWait();
                    resetaCampos();
                } catch (SQLException e) {
                    Alert dialogoInfo = new Alert(Alert.AlertType.WARNING);
                    dialogoInfo.setTitle("Error");
                    dialogoInfo.setHeaderText("Não foi possível deletar o horário selecionado!");
                    dialogoInfo.showAndWait();
                }
            }
        });
    }

    public void cancelarConsulta(ActionEvent event)
    {
        Button button = (Button) event.getSource();
        Consulta consulta = (Consulta) button.getUserData();

        Alert dialogoExe = new Alert(Alert.AlertType.CONFIRMATION);
        ButtonType btnSim = new ButtonType("Sim");
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String dateF = dateFormat.format(consulta.getHorarioConsulta().getData());
        String timeF = timeFormat.format(consulta.getHorarioConsulta().getHora());

        dialogoExe.setTitle("Cancelando Consulta");
        dialogoExe.setContentText("Tem certeza que deseja cancelar a consulta de "+timeF+" no dia "+dateF+"?");
        dialogoExe.getButtonTypes().setAll(btnSim, btnCancelar);
        dialogoExe.showAndWait().ifPresent(b -> {
            if (b == btnSim) {
                try {
                    ConsultaRepository.cancelarConsulta(consulta.getId());

                    Alert dialogoInfo = new Alert(Alert.AlertType.INFORMATION);
                    dialogoInfo.setContentText("Consulta Cancelada com Sucesso!");
                    dialogoInfo.showAndWait();
                    resetaCampos();
                } catch (SQLException e) {
                    Alert dialogoInfo = new Alert(Alert.AlertType.WARNING);
                    dialogoInfo.setTitle("Error");
                    dialogoInfo.setHeaderText("Não foi possível cancelar a consulta selecionada!");
                    dialogoInfo.showAndWait();
                }
            }
        });
    }

    private void validaCampos() throws IllegalArgumentException
    {
        if(this.data_picker.getEditor().getText().isEmpty()) {
            throw new IllegalArgumentException("Selecione uma Data!");
        }
    }

    public void delogarSistema() throws IOException
    {
        try {
            Sessao.getInstance().deslogar();
            Telas.getTelaLogin(null);
        } catch (IOException e) {

        }
    }

    public void telaCadHorarios() throws IOException
    {
        try {
            Telas.getTelaCadHorarios();
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }

    public void telaPerfilPsi() throws IOException
    {
        try {
            Telas.getTelaEditPsi();
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }

    public void telaDashPsicologo() throws IOException
    {
        try {
            Telas.getTelaDashPsi();
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }

    public void telaHistoricoConsultas() throws IOException
    {
        try {
            Telas.getTelaHistoricoConsultas();
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }

    private void resetaCampos()
    {
        this.grid_horarios.getChildren().clear();
        this.lb_semHorario.setVisible(false);
        this.data_picker.getEditor().clear();
        this.tf_nome.clear();
        this.tf_telefone.clear();
        this.tf_email.clear();
        this.btn_cancelarConsulta.setDisable(true);
        this.btn_deletarHorario.setDisable(true);
        this.btn_consultaRealizada.setDisable(true);
        this.btn_consultaNaoRealizada.setDisable(true);

    }
}
