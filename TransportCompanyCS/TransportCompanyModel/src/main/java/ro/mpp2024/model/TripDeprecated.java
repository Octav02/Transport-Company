package ro.mpp2024.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class TripDeprecated implements Entity<Long>, Serializable {
    private String destination;
    private LocalDateTime departureTime;
    private int totalNumberOfSeats;
    private Long id;

    public TripDeprecated() {
    }

    public TripDeprecated(String destination, LocalDateTime departureTime, int totalNumberOfSeats) {
        super();
        this.destination = destination;
        this.departureTime = departureTime;
        this.totalNumberOfSeats = totalNumberOfSeats;
    }

    public TripDeprecated(Long id, String destination, LocalDateTime departureTime, int totalNumberOfSeats) {
        this.id = id;
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

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }
}
