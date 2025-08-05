package org.trab.demo.controller;



import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import org.trab.demo.model.Consulta;
import org.trab.demo.repository.ConsultaRepository;
import org.trab.demo.util.Sessao;
import org.trab.demo.util.Telas;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DashboardAdm implements Initializable {
    @FXML
    private Label lb_dia;
    @FXML
    private Label lb_sem_consulta;
    @FXML
    private Button btn_perfil;
    @FXML
    private Button btn_cadastra_horario;
    @FXML
    private Button btn_agenda;
    @FXML
    private Button btn_historicoConsultas;
    @FXML
    private Button btn_deslogar;
    @FXML
    private GridPane grid_consulta_dia;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Date currentDate = Calendar.getInstance().getTime();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
            dateFormat.setTimeZone(TimeZone.getTimeZone("America/Sao_Paulo"));
            String formattedDate = dateFormat.format(currentDate);
            this.lb_dia.setText(formattedDate);

            List<Consulta> consultas = ConsultaRepository.getConsultasData(new java.sql.Date(currentDate.getTime()));

            if(consultas.isEmpty()) {
                this.lb_sem_consulta.setVisible(true);
            } else {
                for(int i = 0; i < consultas.size(); i++) {
                    if(consultas.get(i).getPaciente() != null) {
                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                        String formattedTime = timeFormat.format(consultas.get(i).getHorarioConsulta().getHora());

                        Button button = new Button(formattedTime);
                        button.setPrefWidth(85);
                        button.setPrefHeight(26);
                        button.setStyle("-fx-font-size:14");
                        this.grid_consulta_dia.add(button,0,i);

                        Label label = new Label(consultas.get(i).getPaciente().getNome());
                        label.setStyle("-fx-font-size:18");
                        this.grid_consulta_dia.add(label,1,i);
                    }
                }
            }

            if(this.grid_consulta_dia.getChildren().isEmpty()) {
                this.lb_sem_consulta.setVisible(true);
            };

        } catch (SQLException e) {
            System.out.println(e.getMessage());
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

    public void telaPerfil() throws IOException
    {
        try {
            Telas.getTelaEditPsi();
        } catch (IOException e) {
            throw new IOException(e.getMessage());
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

    public void telaAgendaPsicologo() throws IOException
    {
        try {
            Telas.getTelaAgendaPsicologo();
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


}
