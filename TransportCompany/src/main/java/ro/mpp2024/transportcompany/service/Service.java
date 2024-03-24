package ro.mpp2024.transportcompany.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ro.mpp2024.transportcompany.dtos.BookingSeatDTO;
import ro.mpp2024.transportcompany.dtos.TripSeatsDTO;
import ro.mpp2024.transportcompany.model.Booking;
import ro.mpp2024.transportcompany.model.Trip;
import ro.mpp2024.transportcompany.model.User;
import ro.mpp2024.transportcompany.repository.BookingRepository;
import ro.mpp2024.transportcompany.repository.TripRepository;
import ro.mpp2024.transportcompany.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Service {
    private static final Logger logger = LogManager.getLogger();
    private TripRepository tripRepository;
    private BookingRepository bookingRepository;
    private UserRepository userRepository;

    public Service(TripRepository tripRepository, BookingRepository bookingRepository, UserRepository userRepository) {
        logger.info("Creating Service");
        this.tripRepository = tripRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    public boolean login(String username, String password) {
        logger.traceEntry("Login with username {} and password");
        User user = userRepository.findByUsername(username);
        logger.traceExit("User is {}", user);
        return user != null && user.getPassword().equals(password);
    }

    public List<TripSeatsDTO> getAllTripsWithAvailableSeats() {
        logger.traceEntry("Getting all trips with available seats");

        Set<Trip> trips = (Set<Trip>) tripRepository.findAll();
        List<TripSeatsDTO> tripSeatsDTOS = new ArrayList<>();
        for (Trip trip : trips) {
            logger.info("Getting available seats for trip {}", trip);
            int availableSeats = trip.getTotalNumberOfSeats() - bookingRepository.getNumberOfBookingsForTrip(trip.getId());
            tripSeatsDTOS.add(new TripSeatsDTO(trip.getId(),trip.getDestination(), trip.getDepartureTime(), availableSeats));
        }

        logger.traceExit("Returning trips with available seats {}", tripSeatsDTOS);
        return tripSeatsDTOS;
    }

    public Trip getTripById(Long id) {
        logger.traceEntry("Getting trip with id {}", id);

        logger.traceExit("Returning trip with id {}", id);
        return tripRepository.findById(id);
    }

    public List<BookingSeatDTO> getBookingSeatDTOsForTrip(Long id) {
        logger.traceEntry("Getting booking seat dtos for trip with id {}", id);

        Map<Integer,String> mapSeatToClient = bookingRepository.getBookingSeatDTOsForTrip(id);
        List<BookingSeatDTO> bookingSeatDTOS = new ArrayList<>();
        Trip trip = tripRepository.findById(id);

        for (int i = 0; i < trip.getTotalNumberOfSeats(); i++) {
            String clientName = mapSeatToClient.getOrDefault(i,"-");
            logger.info("Adding booking seat dto with seat number {} and client name {}", i, clientName);
            bookingSeatDTOS.add(new BookingSeatDTO(i, clientName));
        }

        logger.traceExit("Returning booking seat dtos {}", bookingSeatDTOS);
        return bookingSeatDTOS;
    }

    public void createBooking(String clientName, Long tripId, List<Integer> seatsToBook) {
        logger.traceEntry("Creating booking for client {} on trip with tripId {} for seats {}", clientName, tripId, seatsToBook);


        Booking booking = new Booking(clientName, seatsToBook, tripRepository.findById(tripId));
        validateNewBooking(booking);
        bookingRepository.add(booking);
        logger.traceExit("Booking created");
    }

    private void validateNewBooking(Booking booking) {
        Map<Integer,String> mapSeatToClient = bookingRepository.getBookingSeatDTOsForTrip(booking.getTrip().getId());
        for (int seat : booking.getReservedSeats()) {
            if (mapSeatToClient.containsKey(seat)) {
                logger.error("Seat {} is already booked", seat);
                throw new RuntimeException("Seat " + seat + " is already booked");
            }
        }
    }
}
