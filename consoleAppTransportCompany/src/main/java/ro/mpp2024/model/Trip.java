package ro.mpp2024.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Trip extends Entity<Long> implements Serializable {
    private String destination;
    private LocalDateTime departureTime;
    private int numberOfSeats;

    public Trip() {
    }

    public Trip(String destination, LocalDateTime departureTime, int numberOfSeats) {
        super();
        this.destination = destination;
        this.departureTime = departureTime;
        this.numberOfSeats = numberOfSeats;
    }

    public Trip(Long id, String destination, LocalDateTime departureTime, int numberOfSeats) {
        super(id);
        this.destination = destination;
        this.departureTime = departureTime;
        this.numberOfSeats = numberOfSeats;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public int getNumberOfSeats() {
        return numberOfSeats;
    }

    public void setNumberOfSeats(int numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }

    @Override
    public String toString() {
        return "Trip{" +
                "destination='" + destination + '\'' +
                ", departureTime=" + departureTime +
                ", totalNumberOfSeats=" + numberOfSeats +
                ", id=" + id +
                '}';
    }
}
