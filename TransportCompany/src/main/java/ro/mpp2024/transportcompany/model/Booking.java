package ro.mpp2024.transportcompany.model;

import java.io.Serializable;
import java.util.List;

public class Booking extends Entity<Long> implements Serializable {
    private String clientName;
    private List<Integer> reservedSeats;
    private Trip trip;

    public Booking() {
    }

    public Booking(String clientName, List<Integer> reservedSeats, Trip trip) {
        this.clientName = clientName;
        this.reservedSeats = reservedSeats;
        this.trip = trip;
    }

    public Booking(Long id, String clientName, List<Integer> reservedSeats, Trip trip) {
        this.id = id;
        this.clientName = clientName;
        this.reservedSeats = reservedSeats;
        this.trip = trip;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public List<Integer> getReservedSeats() {
        return reservedSeats;
    }

    public void setReservedSeats(List<Integer> reservedSeats) {
        this.reservedSeats = reservedSeats;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "clientName='" + clientName + '\'' +
                ", reservedSeats=" + reservedSeats +
                ", trip=" + trip +
                ", id=" + id +
                '}';
    }
}
