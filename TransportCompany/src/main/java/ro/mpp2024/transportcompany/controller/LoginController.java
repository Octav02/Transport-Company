package ro.mpp2024.transportcompany.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ro.mpp2024.transportcompany.MainApplication;
import ro.mpp2024.transportcompany.model.User;
import ro.mpp2024.transportcompany.service.Service;

import java.io.IOException;

public class LoginController {
    @FXML
    public TextField usernameTextField;
    @FXML
    public PasswordField passwordTextField;
    private static final Logger logger = LogManager.getLogger();
    private Service service;
    private User user;
    public LoginController() {
        logger.info("Creating LoginController");
    }

    public void setService(Service service) {
        logger.info("Setting service");
        this.service = service;

    }


    public void handleLogin(ActionEvent actionEvent) {
        logger.traceEntry("Handling login");
        String username = usernameTextField.getText();
        String password = passwordTextField.getText();
        try {
            if (service.login(username, password)) {
                user = service.getUserByUsername(username);
                logger.info("Login successful");
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Login");
                alert.setHeaderText("Login successful");
                alert.setContentText("Welcome " + username);
                alert.showAndWait();
                
                goToMainView(actionEvent);
            } else {
                logger.info("Login failed");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Login");
                alert.setHeaderText("Login failed");
                alert.setContentText("Invalid username or password");
                alert.showAndWait();
            }
        } catch (Exception e) {
            logger.error("Error logging in " + e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login");
            alert.setHeaderText("Login failed");
            alert.setContentText("Error logging in");
            alert.showAndWait();
        }
        
    }

    private void goToMainView(ActionEvent event) {
        logger.traceEntry("Going to main view");

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ro/mpp2024/transportcompany/main-view.fxml"));
            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();

            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);

            MainController mainController = loader.getController();
            mainController.setService(service,user);

            stage.show();

        }
        catch (IOException e) {
            logger.error("Error loading main view " + e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error loading main view");
            alert.setContentText("Error loading main view");
            alert.showAndWait();

        }
    }
}
