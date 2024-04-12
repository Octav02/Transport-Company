package ro.mpp2024.persistance;


import ro.mpp2024.model.Trip;

import java.time.LocalDateTime;
import java.util.List;

public interface TripRepository extends Repository<Trip, Long>{

    List<Trip> getTripsByDestinationAndDepartureTime(String destination, LocalDateTime departureTime);
}
