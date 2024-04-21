package ro.mpp2024;

import ro.mpp2024.dto.BookingSeatDTO;
import ro.mpp2024.dto.TripSeatsDTO;
import ro.mpp2024.model.Booking;
import ro.mpp2024.model.Trip;
import ro.mpp2024.model.User;
import ro.mpp2024.persistance.BookingRepository;
import ro.mpp2024.persistance.TripRepository;
import ro.mpp2024.persistance.UserRepository;
import ro.mpp2024.services.TransportCompanyObserver;
import ro.mpp2024.services.TransportCompanyService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TransportCompanyServerImplementation implements TransportCompanyService {
    private final UserRepository userRepository;
    private final TripRepository tripRepository;
    private final BookingRepository bookingRepository;
    private final Map<String, TransportCompanyObserver> loggedClients;
    private final int defaultNoOfThreads = 5;

    public TransportCompanyServerImplementation(UserRepository userRepository, TripRepository tripRepository, BookingRepository bookingRepository) {
        this.userRepository = userRepository;
        this.tripRepository = tripRepository;
        this.bookingRepository = bookingRepository;
        loggedClients = new ConcurrentHashMap<>();
    }

    @Override
    public synchronized boolean login(String username, String password, TransportCompanyObserver client) {
        User user = userRepository.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            System.out.println(loggedClients.size());
            System.out.println(loggedClients.containsKey(username) + " " + username);
            if (loggedClients.containsKey(username) && loggedClients.get(username) != null && !loggedClients.get(username).equals(client)){
                return false;
            }
            loggedClients.put(username, client);
            return true;
        }
        return false;
    }

    @Override
    public void logout(User user, TransportCompanyObserver client) {
        System.out.println(loggedClients.size());
        TransportCompanyObserver localClient = loggedClients.remove(user.getUsername());
        if (localClient == null) {
            System.out.println("Nasol");
            throw new RuntimeException("User " + user.getUsername() + " is not logged in.");
        }
        System.out.println(loggedClients.size() + "after logout");

    }

    @Override
    public List<TripSeatsDTO> getAllTripsWithAvailableSeats() {
        Set<Trip> trips = (Set<Trip>) tripRepository.findAll();
        List<TripSeatsDTO> tripSeatsDTOS = new ArrayList<>();
        for (Trip trip : trips) {
            System.out.println(trip);
            int availableSeats = trip.getTotalNumberOfSeats() - bookingRepository.getNumberOfBookingsForTrip(trip.getId());
            tripSeatsDTOS.add(new TripSeatsDTO(trip.getId(), trip.getDestination(), trip.getDepartureTime(), availableSeats));
        }
        System.out.println(tripSeatsDTOS.size());
        return tripSeatsDTOS;
    }

    @Override
    public Trip getTripById(Long id) {
        return tripRepository.findById(id);
    }

    @Override
    public List<BookingSeatDTO> getBookingsSeatsForTrip(Long id) {

        Map<Integer,String> mapSeatToClient = bookingRepository.getBookingSeatDTOsForTrip(id);
        List<BookingSeatDTO> bookingSeatDTOS = new ArrayList<>();
        Trip trip = tripRepository.findById(id);

        for (int i = 0; i < trip.getTotalNumberOfSeats(); i++) {
            String clientName = mapSeatToClient.getOrDefault(i,"-");
            bookingSeatDTOS.add(new BookingSeatDTO(i, clientName));
        }

        return bookingSeatDTOS;
    }

    @Override
    public synchronized void addBooking(String clientName, Trip trip, List<Integer> seatsToBook, TransportCompanyObserver client) {

        Booking booking = new Booking(clientName, seatsToBook, trip);
        validateNewBooking(booking);
        bookingRepository.add(booking);
        notifyClients();
    }

    private void notifyClients() {
        ExecutorService executor = Executors.newFixedThreadPool(defaultNoOfThreads);
        for (var client: loggedClients.values()) {
            if (client == null) {
                continue;
            }
            executor.execute(client::bookingAdded);

        }
        executor.shutdown();
    }

    private void validateNewBooking(Booking booking) {
        Map<Integer,String> mapSeatToClient = bookingRepository.getBookingSeatDTOsForTrip(booking.getTrip().getId());
        for (int seat : booking.getReservedSeats()) {
            if (mapSeatToClient.containsKey(seat)) {
                throw new RuntimeException("Seat " + seat + " is already booked");
            }
        }
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
