package org.trab.demo.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import org.trab.demo.enums.StatusConsultaEnum;
import org.trab.demo.model.Consulta;
import org.trab.demo.repository.ConsultaRepository;
import org.trab.demo.util.Sessao;
import org.trab.demo.util.Telas;

import javafx.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;
import java.util.TimeZone;

public class HistoricoConsultasController implements Initializable {

    @FXML
    private Text btn_historicoConsultas;

    @FXML
    private GridPane grid_horarios;

    @FXML
    private RadioButton rb_pacienteCompareceu;

    @FXML
    private RadioButton rb_pacienteNaoCompareceu;

    @FXML
    private Label lb_semConsultas;

    @FXML
    private TextField tf_dia;

    @FXML
    private TextField tf_horario;

    @FXML
    private TextField tf_nome;

    @FXML
    private TextField tf_telefone;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.btn_historicoConsultas.setDisable(true);

        try {
            List<Consulta> consultas = ConsultaRepository.getConsultasFinalizadas();

            if(!consultas.isEmpty()) {
                this.lb_semConsultas.setVisible(false);
                String formatedTime;

                for(int i = 0; i < consultas.size(); i++) {
                    formatedTime = this.formattedTimeString(consultas.get(i).getHorarioConsulta().getHora());

                    Button button = new Button(formatedTime);
                    button.setStyle("-fx-font-size:18");
                    button.setUserData(consultas.get(i));
                    button.setOnAction(this::horarioSelecionado);

                    Label label = new Label(consultas.get(i).getPaciente().getNome());
                    label.setStyle("-fx-font-size:18");

                    this.grid_horarios.add(button,0,i);
                    this.grid_horarios.add(label,1,i);

                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    public void horarioSelecionado(ActionEvent event)
    {
        Button button = (Button) event.getSource();
        Consulta consulta = (Consulta) button.getUserData();

        String formatedDate = this.formattedDateString(consulta.getHorarioConsulta().getData());
        String formatedTime = this.formattedTimeString(consulta.getHorarioConsulta().getHora());

        if(consulta.getHorarioConsulta().getStatus().equals(StatusConsultaEnum.CONCLUIDO.toString())) {
            this.rb_pacienteCompareceu.setSelected(true);
        } else {
            this.rb_pacienteNaoCompareceu.setSelected(true);
        }

        this.tf_nome.setText(consulta.getPaciente().getNome());
        this.tf_telefone.setText(consulta.getPaciente().getTelefone());
        this.tf_dia.setText(formatedDate);
        this.tf_horario.setText(formatedTime);
    }

    private String formattedDateString(Date date)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
        dateFormat.setTimeZone(TimeZone.getTimeZone("America/Sao_Paulo"));
        String formattedDate = dateFormat.format(date);
        return formattedDate;
    }

    private String formattedTimeString(Time time)
    {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String formattedTime = timeFormat.format(time);

        return formattedTime;
    }

    public void delogarSistema(MouseEvent mouseEvent) throws IOException
    {
        try {
            Sessao.getInstance().deslogar();
            Telas.getTelaLogin(null);
        } catch (IOException e) {

        }
    }

    @FXML
    void telaAgenda(MouseEvent mouseEvent) throws IOException
    {
        try {
            Telas.getTelaAgendaPsicologo();
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }

    @FXML
    void telaCadHorarios(MouseEvent mouseEvent) throws IOException
    {
        try {
            Telas.getTelaCadHorarios();
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }

    @FXML
    void telaDashPsicologo(MouseEvent mouseEvent) throws IOException
    {
        try {
            Telas.getTelaDashPsi();
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }

    @FXML
    void telaPerfilPsi(MouseEvent mouseEvent) throws IOException
    {
        try {
            Telas.getTelaEditPsi();
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }
}
