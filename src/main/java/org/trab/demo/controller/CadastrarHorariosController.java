package org.trab.demo.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.trab.demo.util.Telas;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CadastrarHorariosController implements Initializable {
    @FXML
    private Button btn_agenda;

    @FXML
    private Button btn_cadHorarios;

    @FXML
    private Button btn_dash;

    @FXML
    private Button btn_mostrarHorarios;

    @FXML
    private Button btn_perfil;

    @FXML
    private DatePicker data_picker;

    @FXML
    private Label lb_data;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btn_cadHorarios.setDisable(true);
    }

    public void telaAgenda() throws IOException
    {
        try {
            Telas.getTelaAgendaPsicologo();
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
}
