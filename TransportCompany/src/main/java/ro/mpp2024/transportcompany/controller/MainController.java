package ro.mpp2024.transportcompany.controller;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ro.mpp2024.transportcompany.dtos.BookingSeatDTO;
import ro.mpp2024.transportcompany.dtos.TripSeatsDTO;
import ro.mpp2024.transportcompany.model.Trip;
import ro.mpp2024.transportcompany.model.User;
import ro.mpp2024.transportcompany.service.Service;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainController {

    private static final Logger logger = LogManager.getLogger();
    public TableView<TripSeatsDTO> tripsTableView;
    public TableColumn<TripSeatsDTO,String> destinationTableColumn;
    public TableColumn<TripSeatsDTO, LocalDateTime> departureTimeTableColumn;
    public TableColumn<TripSeatsDTO, Integer> availableSeatsTableColumn;
    public DatePicker departureDateDatePicker;
    public TextField desinationTextField;
    public TextField departureHourTextField;
    public Label bookingTripLabel;
    public TextField clientNameTextField;
    public TableView<BookingSeatDTO> tripInformationTableView;
    public TableColumn<BookingSeatDTO,Integer> seatNumberTableColumn;
    public TableColumn<BookingSeatDTO,String> reservedForTableColumn;
    public TextField seatsForBookingTextField;

    private Trip currentTrip;
    private ObservableList<TripSeatsDTO> tripsModel = FXCollections.observableArrayList();
    private ObservableList<BookingSeatDTO> bookingModel = FXCollections.observableArrayList();

    private Service service;
    private User loggedInUser;

    public MainController() {
        logger.info("Creating MainController");
    }

    public void initialize() {
        logger.info("Initializing MainController");
        initializeTripsTableView();
        initializeBookingTripTableView();
        addFilterListeners();
    }

    private void initializeBookingTripTableView() {
        logger.info("Initializing booking trip table view");
        tripInformationTableView.setItems(bookingModel);
        seatNumberTableColumn.setCellValueFactory(new PropertyValueFactory<>("seatNumber"));
        reservedForTableColumn.setCellValueFactory(new PropertyValueFactory<>("reservedFor"));

    }

    private void addFilterListeners() {
        logger.info("Adding filter listeners");
        desinationTextField.textProperty().addListener((observable, oldValue, newValue) -> filterTrips());
        departureDateDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> filterTrips());
        departureHourTextField.textProperty().addListener((observable, oldValue, newValue) -> filterTrips());
    }

    private void filterTrips() {
        logger.info("Filtering trips");
        String destination = desinationTextField.getText();
        LocalDateTime departureDate = departureDateDatePicker.getValue() != null ? departureDateDatePicker.getValue().atStartOfDay() : null;
        int departureHour = departureHourTextField.getText().isEmpty() ? -1 : Integer.parseInt(departureHourTextField.getText());

        List<TripSeatsDTO> trips = service.getAllTripsWithAvailableSeats();
        trips = trips.stream()
                .filter(trip -> destination.isEmpty() || trip.getDestination().contains(destination))
                .filter(trip -> departureDate == null || trip.getDepartureTime().toLocalDate().equals(departureDate.toLocalDate()))
                .filter(trip -> departureHour == -1 || trip.getDepartureTime().getHour() == departureHour)
                .toList();

        tripsModel.setAll(trips);
    }

    private void initializeTripsTableView() {
        logger.info("Initializing trips table view");
        tripsTableView.setItems(tripsModel);
        destinationTableColumn.setCellValueFactory(new PropertyValueFactory<>("destination"));
        departureTimeTableColumn.setCellValueFactory(new PropertyValueFactory<>("departureTime"));
        availableSeatsTableColumn.setCellValueFactory(new PropertyValueFactory<>("availableSeats"));

        tripsTableView.getSelectionModel().selectFirst();

        tripsTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                currentTrip = service.getTripById(newSelection.getId());
                bookingTripLabel.setText("Booking trip: " + currentTrip.getDestination() + " " + currentTrip.getDepartureTime());
                initBookingAvailableData();
            }
        });
        tripsTableView.getSelectionModel().selectFirst();


    }

    private void initBookingAvailableData() {

        List<BookingSeatDTO> bookingSeatDTOS = service.getBookingSeatDTOsForTrip(currentTrip.getId());
        bookingModel.setAll(bookingSeatDTOS);
        }


    @FXML
    public void handleCreateNewBooking(ActionEvent actionEvent) {

        try {
            String[] seats = seatsForBookingTextField.getText().split(",");
            List<Integer> seatsToBook = new ArrayList<>();
            for (String seat : seats) {
                seatsToBook.add(Integer.parseInt(seat));
            }

            String clientName = clientNameTextField.getText();
            service.createBooking(clientName, currentTrip.getId(), seatsToBook);

            initData();
        }
        catch (Exception e) {
            logger.error(e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error creating booking");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }


    }

    public void setService(Service service, User loggedInUser) {
        logger.info("Setting service");
        this.service = service;
        logger.info("Setting logged in user as {}", loggedInUser);
        this.loggedInUser = loggedInUser;

        initData();
    }

    private void initData() {
        logger.info("Initializing data");
        List<TripSeatsDTO> trips = service.getAllTripsWithAvailableSeats();
        tripsModel.setAll(trips);

        if (currentTrip != null) {
            initBookingAvailableData();
        }
    }

    public void handleLogOut(ActionEvent event) {
        logger.info("Logging out");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Logout");
        alert.setHeaderText("Logout successful");
        alert.setContentText("You have been logged out");
        alert.showAndWait();

        goToLoginView(event);
    }

    private void goToLoginView(ActionEvent event) {
        logger.info("Going to login view");

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainController.class.getResource("/ro/mpp2024/transportcompany/login-view.fxml"));
            AnchorPane root = fxmlLoader.load();
            LoginController loginController = fxmlLoader.getController();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);

            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    System.out.println("Stage is closing");
                }
            });

            loginController.setService(service);

            stage.show();
        } catch (IOException e) {
            logger.error(e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error going to login view");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
}
