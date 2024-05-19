package ro.mpp2024;

import org.springframework.web.client.RestTemplate;
import ro.mpp2024.model.Trip;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

public class ClientTest {
    private static final String URL = "http://localhost:8080/transport-company/trips";

    private RestTemplate restTemplate = new RestTemplate();

    public List<Trip> getAllTrips() {
        return execute(() -> {
            Trip[] trips = restTemplate.getForObject(URL, Trip[].class);
            return trips == null ? new ArrayList<>() : Arrays.asList(trips);
        });
    }

    public Trip getTripById(Long id) {
        return execute(() -> restTemplate.getForObject(URL + "/" + id, Trip.class));
    }

    public Trip createTrip(Trip trip) {
        return execute(() -> restTemplate.postForObject(URL, trip, Trip.class));
    }

    public Trip updateTrip(Trip trip, Long id) {
        return execute(() -> {
            restTemplate.put(URL + "/" + id, trip);
            return trip;
        });
    }

    public Trip deleteTrip(Long id) {
        return execute(() -> {
            Trip trip = restTemplate.getForObject(URL + "/" + id, Trip.class);
            restTemplate.delete(URL + "/" + id);
            return trip;
        });
    }

    public String getDestinationOfTrip(Long id) {
        return execute(() -> restTemplate.getForObject(URL + "/" + id + "/destination", String.class));
    }


    private <T> T execute(Callable<T> callable) {
        try {
            return callable.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
