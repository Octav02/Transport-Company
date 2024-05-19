package ro.mpp2024.model;

import java.io.Serializable;
import java.util.List;

public class Booking implements Entity<Long>, Serializable {
    private Long id;
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

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }
}
