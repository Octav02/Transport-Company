package ro.mpp2024;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ro.mpp2024.dto.BookingSeatDTO;
import ro.mpp2024.dto.TripSeatsDTO;
import ro.mpp2024.model.Trip;
import ro.mpp2024.model.User;
import ro.mpp2024.services.TransportCompanyObserver;
import ro.mpp2024.services.TransportCompanyService;
import javafx.util.Duration;
import javafx.animation.PauseTransition;

import java.time.LocalDateTime;
import java.util.List;

public class MainController implements TransportCompanyObserver {
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

    private TransportCompanyService service;
    private User loggedInUser;
    public void initialize() {
        initializeTripsTableView();
        initializeBookingTripTableView();
        addFilterListeners();
    }

    private void initializeBookingTripTableView() {
        tripInformationTableView.setItems(bookingModel);
        seatNumberTableColumn.setCellValueFactory(new PropertyValueFactory<>("seatNumber"));
        reservedForTableColumn.setCellValueFactory(new PropertyValueFactory<>("reservedFor"));

    }

    private void addFilterListeners() {
        desinationTextField.textProperty().addListener((observable, oldValue, newValue) -> filterTrips());
        departureDateDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> filterTrips());
        departureHourTextField.textProperty().addListener((observable, oldValue, newValue) -> filterTrips());
    }

    private void filterTrips() {
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
        tripsTableView.setItems(tripsModel);
        destinationTableColumn.setCellValueFactory(new PropertyValueFactory<>("destination"));
        departureTimeTableColumn.setCellValueFactory(new PropertyValueFactory<>("departureTime"));
        availableSeatsTableColumn.setCellValueFactory(new PropertyValueFactory<>("availableSeats"));

        tripsTableView.getSelectionModel().selectFirst();

        tripsTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                currentTrip = service.getTripById(newSelection.getId());
                currentTrip.setId(newSelection.getId());
                bookingTripLabel.setText("Booking trip: " + currentTrip.getDestination() + " " + currentTrip.getDepartureTime());
                initBookingAvailableData();
            }
        });
        tripsTableView.getSelectionModel().selectFirst();


    }

    private void initBookingAvailableData() {

        List<BookingSeatDTO> bookingSeatDTOS = service.getBookingsSeatsForTrip(currentTrip.getId());
        bookingModel.setAll(bookingSeatDTOS);
    }

    public void handleCreateNewBooking(ActionEvent event) {
        String clientName = clientNameTextField.getText();
        String seatsToBook = seatsForBookingTextField.getText();
        if (clientName.isEmpty() || seatsToBook.isEmpty()) {
            return;
        }
        try {
            List<Integer> seats = List.of(seatsToBook.split(",")).stream().map(Integer::parseInt).toList();
            service.addBooking(clientName, currentTrip, seats, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleLogOut(ActionEvent event) {
        service.logout(loggedInUser, this);
    }

    public void setService(TransportCompanyService service, User user) {
        this.service = service;
        this.loggedInUser = user;
        initData();
    }

    private void initData() {
        List<TripSeatsDTO> trips = service.getAllTripsWithAvailableSeats();
        System.out.println(trips);
        tripsModel.clear();
        tripsModel.setAll(trips);

        if (currentTrip != null) {
            initBookingAvailableData();
        }
    }

    @Override
    public void bookingAdded() {
        Platform.runLater(() -> {
            try {
                initData();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
