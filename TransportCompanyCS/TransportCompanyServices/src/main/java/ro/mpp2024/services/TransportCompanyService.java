package ro.mpp2024.services;

import ro.mpp2024.dto.*;
import ro.mpp2024.model.Trip;
import ro.mpp2024.model.User;

import java.util.List;

public interface TransportCompanyService {
    boolean login(String username, String password, TransportCompanyObserver client);
    void    logout(User user, TransportCompanyObserver client);

    List<TripSeatsDTO> getAllTripsWithAvailableSeats();

    Trip getTripById(Long id);

    List<BookingSeatDTO> getBookingsSeatsForTrip(Long id);

    void addBooking(String clientName, Trip trip, List<Integer> seatsToBook, TransportCompanyObserver client);

    User getUserByUsername(String username);
}
