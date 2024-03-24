package ro.mpp2024.transportcompany;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import ro.mpp2024.transportcompany.controller.LoginController;
import ro.mpp2024.transportcompany.repository.BookingRepository;
import ro.mpp2024.transportcompany.repository.TripRepository;
import ro.mpp2024.transportcompany.repository.UserRepository;
import ro.mpp2024.transportcompany.repository.dbrepository.BookingDBRepository;
import ro.mpp2024.transportcompany.repository.dbrepository.TripDbRepository;
import ro.mpp2024.transportcompany.repository.dbrepository.UserDBRepository;
import ro.mpp2024.transportcompany.service.Service;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {


        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("login-view.fxml"));
        AnchorPane root = fxmlLoader.load();
        LoginController loginController = fxmlLoader.getController();
        Scene scene = new Scene(root);
        stage.setScene(scene);

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.out.println("Stage is closing");
            }
        });

        loginController.setService(getNewService());

        stage.show();
    }

    private Service getNewService() {
        Properties props = new Properties();
        try {
            props.load(new FileReader("properties/db.properties"));
        } catch (IOException e) {
            System.out.println("Cannot find file " + e);
        }

        UserRepository userRepository = new UserDBRepository(props);
        TripRepository tripRepository = new TripDbRepository(props);
        BookingRepository bookingRepository = new BookingDBRepository(props, tripRepository);

        return new Service(tripRepository, bookingRepository, userRepository);
    }

    public static void main(String[] args) {
        launch();
    }
}