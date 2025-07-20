package org.trab.demo.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.trab.demo.util.Telas;

import java.io.IOException;
import java.net.URL;
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
    private TextField tf_email;

    @FXML
    private TextField tf_telefone;

    @FXML
    private TextField tx_nome;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        btn_agenda.setDisable(true);
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
}
