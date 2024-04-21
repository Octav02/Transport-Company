package ro.mpp2024.dto;

import ro.mpp2024.model.Trip;

import java.io.Serializable;
import java.util.List;

public class BookingDTO implements Serializable {
    private String clientName;
    private Trip trip;
    private List<Integer> seats;

    public BookingDTO() {
    }

    public BookingDTO(String clientName, List<Integer> seats, Trip trip) {
        this.clientName = clientName;
        this.trip = trip;
        this.seats = seats;
    }

    public String getClientName() {
        return clientName;
    }

    public Trip getTrip() {
        return trip;
    }

    public List<Integer> getSeats() {
        return seats;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public void setSeats(List<Integer> seats) {
        this.seats = seats;
    }

    @Override
    public String toString() {
        return "BookingDTO{" +
                "clientName='" + clientName + '\'' +
                ", trip=" + trip +
                ", seats=" + seats +
                '}';
    }


}
