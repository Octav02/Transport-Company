package ro.mpp2024.transportcompany.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Trip extends Entity<Long> implements Serializable {
    private String destination;
    private LocalDateTime departureTime;
    private int totalNumberOfSeats;

    public Trip() {
    }

    public Trip(String destination, LocalDateTime departureTime, int totalNumberOfSeats) {
        super();
        this.destination = destination;
        this.departureTime = departureTime;
        this.totalNumberOfSeats = totalNumberOfSeats;
    }

    public Trip(Long id, String destination, LocalDateTime departureTime, int totalNumberOfSeats) {
        super(id);
        this.destination = destination;
        this.departureTime = departureTime;
        this.totalNumberOfSeats = totalNumberOfSeats;
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

    public int getTotalNumberOfSeats() {
        return totalNumberOfSeats;
    }

    public void setTotalNumberOfSeats(int totalNumberOfSeats) {
        this.totalNumberOfSeats = totalNumberOfSeats;
    }

    @Override
    public String toString() {
        return "Trip{" +
                "destination='" + destination + '\'' +
                ", departureTime=" + departureTime +
                ", totalNumberOfSeats=" + totalNumberOfSeats +
                ", id=" + id +
                '}';
    }
}
