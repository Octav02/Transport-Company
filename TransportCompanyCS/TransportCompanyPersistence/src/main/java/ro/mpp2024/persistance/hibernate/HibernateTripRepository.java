package ro.mpp2024.persistance.hibernate;

import ro.mpp2024.model.Trip;
import ro.mpp2024.persistance.TripRepository;

import java.time.LocalDateTime;
import java.util.List;

public class HibernateTripRepository implements TripRepository {
    @Override
    public List<Trip> getTripsByDestinationAndDepartureTime(String destination, LocalDateTime departureTime) {
        return List.of();
    }

    @Override
    public void add(Trip elem) {

    }

    @Override
    public void delete(Trip elem) {

    }

    @Override
    public void update(Trip elem, Long id) {

    }

    @Override
    public Trip findById(Long id) {
        return null;
    }

    @Override
    public Iterable<Trip> findAll() {
        return null;
    }
}
