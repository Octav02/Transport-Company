package ro.mpp2024;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import ro.mpp2024.model.Trip;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/transport-company/trips")
public class TripController {
    @Autowired
    private TripRepository tripRepository;

    @RequestMapping(method = RequestMethod.GET)
    public Iterable<Trip> getAllTrips() {
        return tripRepository.findAll();
    }

    @RequestMapping("/greeting")
    public String greeting() {
        return "Hello, World!";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Trip getTripById(@PathVariable Long id) {
        return tripRepository.findById(id) != null ? tripRepository.findById(id) : new Trip();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Trip deleteTrip(@PathVariable Long id) {
        Trip trip = tripRepository.findById(id);
        tripRepository.delete(trip);
        return trip != null ? trip : new Trip("null", LocalDateTime.now(),-6);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Trip updateTrip(@RequestBody Trip trip, @PathVariable Long id) {
        tripRepository.update(trip, id);
        return tripRepository.findById(id);
    }

    @RequestMapping(method = RequestMethod.POST)
    public Trip addTrip(@RequestBody Trip trip) {
        tripRepository.add(trip);
        return trip;
    }

    @RequestMapping("/{tripId}/destination")
    public String getDestination(@PathVariable Long tripId){
        return tripRepository.findById(tripId).getDestination();
    }
}
