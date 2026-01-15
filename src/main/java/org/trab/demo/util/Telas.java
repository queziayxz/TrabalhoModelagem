package org.trab.demo.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Telas {

    private static Stage stageC;
    public static void getTelaLogin(Stage stage) throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(Telas.class.getResource("/org/trab/demo/login.fxml"));
        if (stage == null) {
            Parent root = fxmlLoader.load();
            stageC.getScene().setRoot(root);
            return;
        }

        stageC = stage;
        Scene scene = new Scene(fxmlLoader.load());

        stageC.setScene(scene);
        stageC.setMaximized(false);
        stageC.setMaximized(true);
        stageC.show();
    }
        public static Stage getStage() {
            return stageC;
        }

    public static void getTelaCadastro() throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(Telas.class.getResource("/org/trab/demo/cadastro.fxml"));
        Parent root = fxmlLoader.load();

        stageC.getScene().setRoot(root);
        stageC.show();
    }

    public static void getTelaDashPsi() throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(Telas.class.getResource("/org/trab/demo/dash_adm.fxml"));
        Parent root = fxmlLoader.load();
        stageC.getScene().setRoot(root);
        stageC.show();
    }

    public static void getTelaDashPaci() throws IOException
   {
        FXMLLoader fxmlLoader = new FXMLLoader(Telas.class.getResource("/org/trab/demo/dashPaciente.fxml"));
        Parent root = fxmlLoader.load();
        stageC.getScene().setRoot(root);
        stageC.show();
    }

    public static void getTelaEditPsi() throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(Telas.class.getResource("/org/trab/demo/edit_psicologo.fxml"));
        Parent root = fxmlLoader.load();
        stageC.getScene().setRoot(root);
        stageC.show();
    }

    public static void getTelaCadHorarios() throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(Telas.class.getResource("/org/trab/demo/cadastra_horario.fxml"));
        Parent root = fxmlLoader.load();
        stageC.getScene().setRoot(root);
        stageC.show();
    }

    public static void getTelaAgendaPsicologo() throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(Telas.class.getResource("/org/trab/demo/agenda.fxml"));
        Parent root = fxmlLoader.load();
        stageC.getScene().setRoot(root);
        stageC.show();
    }
    public static void getTelaHistoricoConsultas() throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(Telas.class.getResource("/org/trab/demo/historico_consultas.fxml"));
        Parent root = fxmlLoader.load();
        stageC.getScene().setRoot(root);
        stageC.show();
    }

    public static void getTelaPerfil() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Telas.class.getResource("/org/trab/demo/dashPerfilPaciente.fxml"));
        Parent root = fxmlLoader.load();
        stageC.getScene().setRoot(root);
        stageC.show();
    }

    public static void getTelaAgendamento() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Telas.class.getResource("/org/trab/demo/dashAgendamento.fxml"));
        Parent root = fxmlLoader.load();
        stageC.getScene().setRoot(root);
        stageC.show();
    }

    public static void getTelaRemarcacao() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Telas.class.getResource("/org/trab/demo/dashRemarcação.fxml"));
        Parent root = fxmlLoader.load();
        stageC.getScene().setRoot(root);
        stageC.show();
    }

    public static void getTelaCancelamento() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Telas.class.getResource("/org/trab/demo/dashCancelamento.fxml"));
        Parent root = fxmlLoader.load();
        stageC.getScene().setRoot(root);
        stageC.show();
    }

}
