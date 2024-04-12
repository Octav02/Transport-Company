package ro.mpp2024;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import ro.mpp2024.model.User;
import ro.mpp2024.services.TransportCompanyService;

import java.io.IOException;

public class LoginController {
    private Parent parent;
    @FXML
    public TextField usernameTextField;
    @FXML
    public PasswordField passwordTextField;
    private MainController mainController;
    private TransportCompanyService service;
    private User user;
    public void handleLogin(ActionEvent event) {
        String username = usernameTextField.getText();
        String password = passwordTextField.getText();
        if (service.login(username, password, mainController)) {
            user = service.getUserByUsername(username);
            System.out.println("Login successful");
            goToMainView(event);
        } else {
            System.out.println("Login failed");
        }
    }

    private void goToMainView(ActionEvent event) {

        Stage stage = new Stage();
        Scene scene = new Scene(parent);
        stage.setScene(scene);
        mainController.setService(service, user);
        stage.show();

    }

    public void setService(TransportCompanyService service, MainController mainController) {
        this.service = service;
        this.mainController = mainController;
    }

    public void setParent(Parent root2) {
        parent = root2;
    }
}
