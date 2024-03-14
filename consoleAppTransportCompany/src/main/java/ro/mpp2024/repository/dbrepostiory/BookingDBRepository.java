package ro.mpp2024.repository.dbrepostiory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ro.mpp2024.model.Booking;
import ro.mpp2024.model.Trip;
import ro.mpp2024.repository.BookingRepository;
import ro.mpp2024.utils.dbutils.JdbcUtils;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class BookingDBRepository implements BookingRepository{
    private JdbcUtils dbUtils;
    private static final Logger logger = LogManager.getLogger();
    private static final String ADD_BOOKING_SQL = "INSERT INTO BOOKINGS(CLIENT_NAME,TRIP_ID) VALUES (?,?)";
    private static final String ADD_BOOKING_DEAILS_SQL = "INSERT INTO SEATS(BOOKING_ID,SEAT_NUMBER) VALUES (?,?)";
    private static final String DELETE_BOOKING_SQL = "DELETE FROM BOOKINGS WHERE ID=?";
     private static final String DELETE_BOOKING_DETAILS_SQL = "DELETE FROM SEATS WHERE BOOKING_ID=? AND SEAT_NUMBER=?";
    private static final String UPDATE_BOOKING_SQL = "UPDATE BOOKINGS SET CLIENT_NAME=?, TRIP_ID=? WHERE ID=?";
    private static final String FIND_BOOKING_BY_ID_SQL = "SELECT * FROM BOOKINGS WHERE ID=?";
    private static final String FIND_ALL_BOOKINGS_SQL = "SELECT * FROM BOOKINGS";
    private static final String FIND_BOOKING_BY_CLIENT_NAME_AND_TRIP_ID_SQL = "SELECT * FROM BOOKINGS WHERE CLIENT_NAME=? AND TRIP_ID=?";
    private static final String FIND_BOOKING_DETAILS_BY_BOOKING_ID_SQL = "SELECT * FROM SEATS WHERE BOOKING_ID=?";


    public BookingDBRepository(Properties props) {
        logger.info("Initializing BookingDBRepository with properties: {} ", props);

        dbUtils = new JdbcUtils(props);
    }


    @Override
    public List<Booking> getBookingsForAClientOnATrip(String clientName, Long tripId) {
        logger.traceEntry("Getting bookings for client {} on trip with id {}", clientName, tripId);
        List<Booking> bookings = new ArrayList<>();

        try(Connection connection = dbUtils.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_BOOKING_BY_CLIENT_NAME_AND_TRIP_ID_SQL);
            preparedStatement.setString(1, clientName);
            preparedStatement.setLong(2, tripId);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                logger.trace("Adding booking {}", getBookingFromResultSet(resultSet));
                bookings.add(getBookingFromResultSet(resultSet));
            }

        } catch (SQLException e) {
            logger.throwing(e);
        }

        logger.traceExit(bookings);
        return bookings;
    }

    @Override
    public void add(Booking elem) {
        logger.traceEntry("Adding booking {}", elem);
        try(Connection connection = dbUtils.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(ADD_BOOKING_SQL, Statement.RETURN_GENERATED_KEYS);
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

        } catch (SQLException e) {
            logger.throwing(e);
        }
    }

    private void addBookingDetails(Booking elem, Connection connection) {
        logger.traceEntry("Adding booking details {}", elem);
        try {
            for (Integer seat : elem.getReservedSeats()) {
                logger.trace("Adding seat {}", seat);
                PreparedStatement preparedStatement = connection.prepareStatement(ADD_BOOKING_DEAILS_SQL);
                preparedStatement.setLong(1, elem.getId());
                preparedStatement.setInt(2, seat);

                int result = preparedStatement.executeUpdate();
                if (result == 0) {
                    logger.error("Error adding booking details");
                }

            }
        } catch (SQLException e) {
            logger.throwing(e);
        }
        logger.traceExit();
    }

    @Override
    public void delete(Booking elem) {
        logger.traceEntry("Deleting booking {}", elem);
        try(Connection connection = dbUtils.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_BOOKING_SQL);
            preparedStatement.setLong(1, elem.getId());

            int result = preparedStatement.executeUpdate();
            if (result == 0) {
                logger.error("Error deleting booking");
            }

        } catch (SQLException e) {
            logger.throwing(e);
        }
        logger.traceExit();
    }

    @Override
    public void update(Booking elem, Long id) {
        logger.traceEntry("Updating booking {}", elem);
        try(Connection connection = dbUtils.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_BOOKING_SQL);
            preparedStatement.setString(1, elem.getClientName());
            preparedStatement.setLong(2, elem.getTrip().getId());
            preparedStatement.setLong(3, id);

            int result = preparedStatement.executeUpdate();
            if (result == 0) {
                logger.error("Error updating booking");
            }

            deleteBookingDetails(elem, connection);
            addBookingDetails(elem, connection);


        } catch (SQLException e) {
            logger.throwing(e);
        }
        logger.traceExit();
    }

    private void deleteBookingDetails(Booking elem, Connection connection) {
        logger.traceEntry("Deleting booking details {}", elem);
        try {
            for (Integer seat : elem.getReservedSeats()) {
                logger.trace("Deleting seat {}", seat);
                PreparedStatement preparedStatement = connection.prepareStatement(DELETE_BOOKING_DETAILS_SQL);
                preparedStatement.setLong(1, elem.getId());
                preparedStatement.setInt(2, seat);

                int result = preparedStatement.executeUpdate();
                if (result == 0) {
                    logger.error("Error deleting booking details");
                }

            }
        } catch (SQLException e) {
            logger.throwing(e);
        }
        logger.traceExit();
    }

    @Override
    public Booking findById(Long id) {
        logger.traceEntry("Finding booking with id {}", id);
        Booking booking = null;

        try(Connection connection = dbUtils.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_BOOKING_BY_ID_SQL);
            preparedStatement.setLong(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                booking = getBookingFromResultSet(resultSet);
            }
            else {
                logger.error("No booking found with id {}", id);
            }

        } catch (SQLException e) {
            logger.throwing(e);
        }

        logger.traceExit(booking);
        return booking;
    }

    private Booking getBookingFromResultSet(ResultSet resultSet) {
        logger.traceEntry("Getting booking from result set {}", resultSet);
        Booking booking = null;
        try {
            Long id = resultSet.getLong("ID");
            String clientName = resultSet.getString("CLIENT_NAME");
            Long tripId = resultSet.getLong("TRIP_ID");
            List<Integer> reservedSeats = getBookingDetails(id);
            Trip trip = getTripFromId(tripId);
            booking = new Booking(id, clientName,reservedSeats, trip);
        } catch (SQLException e) {
            logger.throwing(e);
        }

        logger.traceExit(booking);
        return booking;

    }

    private Trip getTripFromId(Long tripId) {
        Trip trip = null;
        try (Connection connection = dbUtils.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM TRIPS WHERE ID=?");
            preparedStatement.setLong(1, tripId);

            ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                logger.trace("Getting trip from result set {}", result);
                trip =  getTripFromResultSet(result);

            }
            else {
                logger.error("No trip found with id {}", tripId);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return trip;
    }

    private Trip getTripFromResultSet(ResultSet result) {
        Trip trip = null;
        try {
            Long id = result.getLong("id");
            String destination = result.getString("destination");
            LocalDateTime departureTime = result.getTimestamp("departure_time").toLocalDateTime();
            int numberOfSeats = result.getInt("number_of_seats");

            logger.trace("Creating trip with id {}, destination {}, departureTime {}, numberOfSeats {}", id, destination, departureTime, numberOfSeats);
            trip = new Trip(id, destination, departureTime, numberOfSeats);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return trip;
    }

    private List<Integer> getBookingDetails(Long id) {
        logger.traceEntry("Getting booking details for booking with id {}", id);
        List<Integer> reservedSeats = null;

        try(Connection connection = dbUtils.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_BOOKING_DETAILS_BY_BOOKING_ID_SQL);
            preparedStatement.setLong(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                logger.trace("Adding seat {}", resultSet.getInt("SEAT_NUMBER"));
                reservedSeats.add(resultSet.getInt("SEAT_NUMBER"));
            }

        } catch (SQLException e) {
            logger.throwing(e);
        }
        logger.traceExit(reservedSeats);
        return reservedSeats;

    }

    @Override
    public Iterable<Booking> findAll() {
        logger.traceEntry("Finding all bookings");
        Set<Booking> bookings = new HashSet<>();

        try(Connection connection = dbUtils.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_BOOKINGS_SQL);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                logger.trace("Adding booking {}", getBookingFromResultSet(resultSet));
                bookings.add(getBookingFromResultSet(resultSet));
            }

        } catch (SQLException e) {
            logger.throwing(e);
        }

        logger.traceExit(bookings);
        return bookings;
    }

    @Override
    public Collection<Booking> getAll() {
        logger.traceEntry("Finding all bookings");
        List<Booking> bookings = new ArrayList<>();
        try(Connection connection = dbUtils.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_BOOKINGS_SQL);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                logger.trace("Adding booking {}", getBookingFromResultSet(resultSet));
                bookings.add(getBookingFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        logger.traceExit(bookings);
        return bookings;
    }
}
