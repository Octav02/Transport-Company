package ro.mpp2024.model;

import jakarta.persistence.*;
import jakarta.persistence.Entity;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "trips")
public class Trip  implements ro.mpp2024.model.Entity<Long>, Serializable {
    @Column(name = "destination")
    private String destination;
    @Column(name = "departure_time")
    private LocalDateTime departureTime;
    @Column(name = "number_of_seats")
    private int totalNumberOfSeats;
    @Id
    @GeneratedValue(generator = "increment")
    @Column(name = "id")
    private Long id;

    public Trip() {
        id = 0L;
    }

    public Trip(String destination, LocalDateTime departureTime, int totalNumberOfSeats) {
        id = 0L;
        this.destination = destination;
        this.departureTime = departureTime;
        this.totalNumberOfSeats = totalNumberOfSeats;
    }

    public Trip(Long id, String destination, LocalDateTime departureTime, int totalNumberOfSeats) {
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

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
