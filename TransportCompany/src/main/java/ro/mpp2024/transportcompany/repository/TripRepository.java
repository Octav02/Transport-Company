package ro.mpp2024.transportcompany.repository;

import ro.mpp2024.transportcompany.model.Trip;

import java.time.LocalDateTime;
import java.util.List;

public interface TripRepository extends Repository<Trip, Long>{

    List<Trip> getTripsByDestinationAndDepartureTime(String destination, LocalDateTime departureTime);
}
