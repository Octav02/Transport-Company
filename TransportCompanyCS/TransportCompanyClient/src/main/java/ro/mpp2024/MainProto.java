package ro.mpp2024;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ro.mpp2024.protobuffprotocol.ProtoProxy;
import ro.mpp2024.rpcprotocol.ServiceRpcProxy;
import ro.mpp2024.services.TransportCompanyService;

import java.io.IOException;

public class MainProto extends Application {
    private static Stage primaryStage;
    private MainController mainController;

    private static String defaultServer = "localhost";
    private static int defaultPort = 55555;

    @Override
    public void start(Stage stage) throws Exception {
        System.out.println("Starting client");
        primaryStage = stage;

        TransportCompanyService service = new ProtoProxy(defaultServer, defaultPort);

        loadStage(service);

    }

    private void loadStage(TransportCompanyService service) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("login-view.fxml"));
            Parent root = loader.load();
            primaryStage.setScene(new Scene(root));
            LoginController loginController = loader.getController();

            FXMLLoader loader2 = new FXMLLoader(getClass().getClassLoader().getResource("main-view.fxml"));
            Parent root2 = loader2.load();
            mainController = loader2.getController();

            loginController.setService(service,mainController);
            loginController.setParent(root2);
            primaryStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
