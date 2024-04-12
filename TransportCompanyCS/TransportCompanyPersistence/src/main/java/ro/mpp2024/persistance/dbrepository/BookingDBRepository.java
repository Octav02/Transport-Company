package ro.mpp2024.persistance.dbrepository;



import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ro.mpp2024.model.Booking;
import ro.mpp2024.persistance.BookingRepository;
import ro.mpp2024.persistance.TripRepository;
import ro.mpp2024.persistance.dbrepository.utils.JdbcUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

public class BookingDBRepository implements BookingRepository {

    private static final Logger logger = LogManager.getLogger();
    private final JdbcUtils dbUtils;
    private final TripRepository tripDBRepository;
    private static final String ADD_BOOKING_SQL = "INSERT INTO BOOKINGS(CLIENT_NAME,TRIP_ID) VALUES (?,?)";
    private static final String ADD_BOOKING_DEAILS_SQL = "INSERT INTO SEATS(BOOKING_ID,SEAT_NUMBER) VALUES (?,?)";
    private static final String DELETE_BOOKING_SQL = "DELETE FROM BOOKINGS WHERE ID=?";
    private static final String DELETE_BOOKING_DETAILS_SQL = "DELETE FROM SEATS WHERE BOOKING_ID=? AND SEAT_NUMBER=?";
    private static final String UPDATE_BOOKING_SQL = "UPDATE BOOKINGS SET CLIENT_NAME=?, TRIP_ID=? WHERE ID=?";
    private static final String FIND_BOOKING_BY_ID_SQL = "SELECT * FROM BOOKINGS WHERE ID=?";
    private static final String FIND_ALL_BOOKINGS_SQL = "SELECT * FROM BOOKINGS";
    private static final String FIND_BOOKING_BY_CLIENT_NAME_AND_TRIP_ID_SQL = "SELECT * FROM BOOKINGS WHERE CLIENT_NAME=? AND TRIP_ID=?";
    private static final String FIND_BOOKING_DETAILS_BY_BOOKING_ID_SQL = "SELECT * FROM SEATS WHERE BOOKING_ID=?";
    private static final String GET_NUMBER_OF_SEATS_OCCUPIED_FOR_TRIP_SQL = "SELECT COUNT(*) FROM SEATS WHERE BOOKING_ID IN (SELECT ID FROM BOOKINGS WHERE TRIP_ID=?)";
    private static final String GET_BOOKING_SEAT_CLIENT_FOR_A_TRIP_SQL = "SELECT S.SEAT_NUMBER, B.CLIENT_NAME FROM SEATS S JOIN BOOKINGS B ON S.BOOKING_ID=B.ID WHERE B.TRIP_ID=?";
    public BookingDBRepository(Properties properties, TripRepository tripDBRepository) {
        logger.info("Initiating BookingDBRepository");
        dbUtils = new JdbcUtils(properties);
        this.tripDBRepository = tripDBRepository;
    }

    @Override
    public List<Booking> getBookingsForAClientOnATrip(String clientName, Long tripId) {
        logger.traceEntry("Getting bookings for client {} on trip {}", clientName, tripId);
        Connection connection = dbUtils.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(FIND_BOOKING_BY_CLIENT_NAME_AND_TRIP_ID_SQL)) {
            preparedStatement.setString(1, clientName);
            preparedStatement.setLong(2, tripId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                List<Booking> bookings = new ArrayList<>();
                while (resultSet.next()) {
                    Booking booking = getBookingFromResultSet(resultSet);
                    bookings.add(booking);
                }
            }
        } catch (Exception e) {
            logger.error("DB Error getting bookings for client on trip" + e.getMessage());
            throw new RuntimeException("Error getting bookings for client on trip" + e.getMessage());
        }

        logger.traceExit("No bookings found");
        return null;
    }

    @Override
    public int getNumberOfBookingsForTrip(Long id) {
        logger.traceEntry("Getting number of bookings for trip with id {}", id);
        Connection connection = dbUtils.getConnection();
        try(PreparedStatement preparedStatement = connection.prepareStatement(GET_NUMBER_OF_SEATS_OCCUPIED_FOR_TRIP_SQL)) {
            preparedStatement.setLong(1, id);

            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                if(resultSet.next()) {
                    int numberOfBookings = resultSet.getInt(1);
                    logger.traceExit();
                    return numberOfBookings;
                }
            }
        } catch (Exception e) {
            logger.error("DB Error getting number of bookings for trip" + e.getMessage());
            throw new RuntimeException("Error getting number of bookings for trip" + e.getMessage());
        }

        logger.traceExit("No bookings found");
        return 0;
    }

    @Override
    public Map<Integer, String> getBookingSeatDTOsForTrip(Long id) {
        logger.traceEntry("Getting booking seat DTOs for trip with id {}", id);
        Connection connection = dbUtils.getConnection();
        try(PreparedStatement preparedStatement = connection.prepareStatement(GET_BOOKING_SEAT_CLIENT_FOR_A_TRIP_SQL)) {
            preparedStatement.setLong(1, id);

            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                Map<Integer, String> bookingSeatDTOs = new HashMap<>();
                while(resultSet.next()) {
                    bookingSeatDTOs.put(resultSet.getInt("SEAT_NUMBER"), resultSet.getString("CLIENT_NAME"));
                }
                logger.traceExit();
                return bookingSeatDTOs;
            }
        } catch (Exception e) {
            logger.error("DB Error getting booking seat DTOs for trip" + e.getMessage());
            throw new RuntimeException("Error getting booking seat DTOs for trip" + e.getMessage());
        }

    }

    private Booking getBookingFromResultSet(ResultSet resultSet) {
        logger.traceEntry("Getting booking from result set");
        try {
            Long id = resultSet.getLong("ID");
            String clientName = resultSet.getString("CLIENT_NAME");
            Long tripId = resultSet.getLong("TRIP_ID");
            List<Integer> seats = getBookingDetails(id);
            return new Booking(id, clientName, seats, tripDBRepository.findById(tripId));
        } catch (Exception e) {
            logger.error("DB Error getting booking from result set" + e.getMessage());
            throw new RuntimeException("Error getting booking from result set" + e.getMessage());
        }

    }

    private List<Integer> getBookingDetails(Long id) {
        logger.traceEntry("Getting booking details for booking with id {}", id);
        Connection connection = dbUtils.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(FIND_BOOKING_DETAILS_BY_BOOKING_ID_SQL)) {
            preparedStatement.setLong(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                List<Integer> seats = new ArrayList<>();

                while (resultSet.next()) {
                    seats.add(resultSet.getInt("SEAT_NUMBER"));
                }
                logger.traceExit();
                return seats;
            }
        } catch (Exception e) {
            logger.error("DB Error getting booking details" + e.getMessage());
            throw new RuntimeException("Error getting booking details" + e.getMessage());
        }

    }

    @Override
    public void add(Booking elem) {
        logger.traceEntry("Adding booking {}", elem);
        Connection connection = dbUtils.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(ADD_BOOKING_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, elem.getClientName());
            preparedStatement.setLong(2, elem.getTrip().getId());

            int result = preparedStatement.executeUpdate();
            if (result == 0) {
                logger.error("Error adding booking");
            }

            //Add id as last generated key
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                elem.setId(generatedKeys.getLong(1));
            } else {
                logger.error("Error adding booking, no ID obtained.");
            }

            addBookingDetails(elem, connection);

        } catch (Exception e) {
            logger.error("DB Error adding booking" + e.getMessage());
            throw new RuntimeException("Error adding booking" + e.getMessage());
        }
        logger.traceExit();

    }

    private void addBookingDetails(Booking elem, Connection connection) {
        logger.traceEntry("Adding booking details for booking {}", elem);
        try (PreparedStatement preparedStatement = connection.prepareStatement(ADD_BOOKING_DEAILS_SQL)) {
            for (Integer seat : elem.getReservedSeats()) {
                preparedStatement.setLong(1, elem.getId());
                preparedStatement.setInt(2, seat);

                int result = preparedStatement.executeUpdate();
                if (result == 0) {
                    logger.error("Error adding booking details");
                }
            }
        } catch (Exception e) {
            logger.error("DB Error adding booking details" + e.getMessage());
            throw new RuntimeException("Error adding booking details" + e.getMessage());
        }
        logger.traceExit();
    }

    @Override
    public void delete(Booking elem) {
        logger.traceEntry("Deleting booking {}", elem);
        Connection connection = dbUtils.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_BOOKING_SQL)) {
            preparedStatement.setLong(1, elem.getId());

            int result = preparedStatement.executeUpdate();
            if (result == 0) {
                logger.error("Error deleting booking");
            }
        } catch (Exception e) {
            logger.error("DB Error deleting booking" + e.getMessage());
            throw new RuntimeException("Error deleting booking" + e.getMessage());
        }
        logger.traceExit();
    }

    @Override
    public void update(Booking elem, Long id) {
        logger.traceEntry("Updating booking {}", elem);
        Connection connection = dbUtils.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_BOOKING_SQL)) {
            preparedStatement.setString(1, elem.getClientName());
            preparedStatement.setLong(2, elem.getTrip().getId());
            preparedStatement.setLong(3, id);

            int result = preparedStatement.executeUpdate();
            if (result == 0) {
                logger.error("Error updating booking");
            }

            deleteBookingDetails(elem, connection);
            addBookingDetails(elem, connection);
        } catch (Exception e) {
            logger.error("DB Error updating booking" + e.getMessage());
            throw new RuntimeException("Error updating booking" + e.getMessage());
        }
        logger.traceExit();
    }

    private void deleteBookingDetails(Booking elem, Connection connection) {
        logger.traceEntry("Deleting booking details for booking {}", elem);
        try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_BOOKING_DETAILS_SQL)) {
            preparedStatement.setLong(1, elem.getId());

            int result = preparedStatement.executeUpdate();
            if (result == 0) {
                logger.error("Error deleting booking details");
            }
        } catch (Exception e) {
            logger.error("DB Error deleting booking details" + e.getMessage());
            throw new RuntimeException("Error deleting booking details" + e.getMessage());
        }
        logger.traceExit();
    }

    @Override
    public Booking findById(Long id) {
        logger.traceEntry("Finding booking with id {}", id);
        Connection connection = dbUtils.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(FIND_BOOKING_BY_ID_SQL)) {
            preparedStatement.setLong(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Booking booking = getBookingFromResultSet(resultSet);
                    logger.traceExit();
                    return booking;
                }
            }
        } catch (Exception e) {
            logger.error("DB Error finding booking" + e.getMessage());
            throw new RuntimeException("Error finding booking" + e.getMessage());
        }
        logger.traceExit("No booking found");
        return null;
    }

    @Override
    public Iterable<Booking> findAll() {
        logger.traceEntry("Finding all bookings");
        Connection connection = dbUtils.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_BOOKINGS_SQL)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                Set<Booking> bookings = new HashSet<>();
                while (resultSet.next()) {
                    Booking booking = getBookingFromResultSet(resultSet);
                    bookings.add(booking);
                }
                logger.traceExit();
                return bookings;
            }
        } catch (Exception e) {
            logger.error("DB Error finding all bookings" + e.getMessage());
            throw new RuntimeException("Error finding all bookings" + e.getMessage());
        }
    }
}
