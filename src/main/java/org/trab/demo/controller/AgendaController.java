package org.trab.demo.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.trab.demo.model.Agenda;
import org.trab.demo.model.Consulta;
import org.trab.demo.model.Paciente;
import org.trab.demo.repository.AgendaRepository;
import org.trab.demo.repository.ConsultaRepository;
import org.trab.demo.util.Telas;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class AgendaController implements Initializable {
    @FXML
    private Button btn_agenda;

    @FXML
    private Button btn_cadHorarios;

    @FXML
    private Button btn_cancelar;

    @FXML
    private Button btn_dash;

    @FXML
    private Button btn_deletar;

    @FXML
    private Button btn_mostraHorarios;

    @FXML
    private Button btn_perfil;

    @FXML
    private DatePicker data_picker;

    @FXML
    private Label lb_data;

    @FXML
    private Label lb_semHorario;

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
            LocalDate data = this.data_picker.getValue();
            Date dateSql = Date.valueOf(data);
            List<Consulta> consultas = ConsultaRepository.getConsultasData(dateSql);

            resetaCampos();

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

                    if(consultas.get(i).getPaciente() != null) {
                        button.setUserData(consultas.get(i));

                        Label label = new Label(consultas.get(i).getPaciente().getNome());
                        label.setStyle("-fx-font-size:18");
                        this.grid_horarios.add(label, 1, i);
                    } else {
                        this.btn_deletar.setUserData(consultas.get(i).getHorarioConsulta());
                    }

                    this.grid_horarios.add(button, 0, i);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
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
            this.btn_cancelar.setDisable(false);
            this.btn_cancelar.setUserData(consulta);
        } else {
            this.btn_deletar.setDisable(false);
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

        System.out.println("horario consulta selecionado: "+consulta.getHorarioConsulta().getHora());
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

    private void resetaCampos()
    {
        this.grid_horarios.getChildren().clear();
        this.lb_semHorario.setVisible(false);
        this.data_picker.getEditor().clear();
        this.tf_nome.clear();
        this.tf_telefone.clear();
        this.tf_email.clear();
        this.btn_cancelar.setDisable(true);
        this.btn_deletar.setDisable(true);

    }
}
