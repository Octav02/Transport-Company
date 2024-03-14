package ro.mpp2024.repository.dbrepostiory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ro.mpp2024.model.Trip;
import ro.mpp2024.repository.TripRepository;
import ro.mpp2024.utils.dbutils.JdbcUtils;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class TripDBRepository implements TripRepository {
    private JdbcUtils dbUtils;
    private static final Logger logger = LogManager.getLogger();
    private static final String ADD_TRIP_SQL = "INSERT INTO TRIPS(DESTINATION, DEPARTURE_TIME, NUMBER_OF_SEATS) VALUES (?,?,?)";
    private static final String DELETE_TRIP_SQL = "DELETE FROM TRIPS WHERE ID=?";
    private static final String UPDATE_TRIP_SQL = "UPDATE TRIPS SET DESTINATION=?, DEPARTURE_TIME=?, NUMBER_OF_SEATS=? WHERE ID=?";
    private static final String FIND_TRIP_BY_ID_SQL = "SELECT * FROM TRIPS WHERE ID=?";
    private static final String FIND_ALL_TRIPS_SQL = "SELECT * FROM TRIPS";
    private static final String FIND_TRIP_BY_DESTINATION_AND_DEPARTURE_TIME_SQL = "SELECT * FROM TRIPS WHERE DESTINATION=? AND DEPARTURE_TIME=?";


    public TripDBRepository(Properties props) {
        logger.info("Initializing TripDBRepository with properties: {} ", props);
        dbUtils = new JdbcUtils(props);
    }


    @Override
    public void add(Trip elem) {
        logger.traceEntry("Adding trip {}", elem);
        try(Connection connection = dbUtils.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(ADD_TRIP_SQL);
            preparedStatement.setString(1, elem.getDestination());
            preparedStatement.setTimestamp(2, Timestamp.valueOf(elem.getDepartureTime()));
            preparedStatement.setInt(3, elem.getNumberOfSeats());


            int result = preparedStatement.executeUpdate();
            if (result == 0) {
                logger.error("Error adding trip");
            }

        } catch (SQLException e) {
            logger.throwing(e);
        }
    }

    @Override
    public void delete(Trip elem) {
        logger.traceEntry("Deleting trip {}", elem);
        try(Connection connection = dbUtils.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_TRIP_SQL);
            preparedStatement.setLong(1, elem.getId());

            int result = preparedStatement.executeUpdate();
            if (result == 0) {
                logger.error("Error deleting trip");
            }
        } catch (SQLException e) {
            logger.throwing(e);
        }
    }

    @Override
    public void update(Trip elem, Long id) {
        logger.traceEntry("Updating trip {}", elem);
        try(Connection connection = dbUtils.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_TRIP_SQL);
            preparedStatement.setString(1, elem.getDestination());
            preparedStatement.setTimestamp(2, Timestamp.valueOf(elem.getDepartureTime()));
            preparedStatement.setInt(3, elem.getNumberOfSeats());
            preparedStatement.setLong(4, id);

            int result = preparedStatement.executeUpdate();
            if (result == 0) {
                logger.error("Error updating trip");
            }
        } catch (SQLException e) {
            logger.throwing(e);
        }
    }

    @Override
    public Trip findById(Long id) {
        logger.traceEntry("Finding trip by id {}", id);
        Trip trip = null;

        try (Connection connection = dbUtils.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_TRIP_BY_ID_SQL);
            preparedStatement.setLong(1, id);

            ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                trip = getTripFromResultSet(result);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        logger.traceExit(trip);
        return trip;
    }

    private Trip getTripFromResultSet(ResultSet resultSet) {
       logger.traceEntry("Getting trip from resultSet {}", resultSet);

       Trip trip = new Trip();
        try {
            Long id = resultSet.getLong("id");
            String destination = resultSet.getString("destination");
            LocalDateTime departureTime = resultSet.getTimestamp("departure_time").toLocalDateTime();
            int numberOfSeats = resultSet.getInt("number_of_seats");

            trip = new Trip(id, destination, departureTime, numberOfSeats);
        } catch (SQLException e) {
            logger.throwing(e);
        }
        logger.traceExit(trip);
        return trip;
    }

    @Override
    public Iterable<Trip> findAll() {
        logger.traceEntry("Finding all trips");
        Set<Trip> trips = new HashSet<>();

        try (Connection connection = dbUtils.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_TRIPS_SQL);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                trips.add(getTripFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            logger.throwing(e);
        }

        logger.traceExit(trips);
        return trips;
    }

    @Override
    public Collection<Trip> getAll() {
        logger.traceEntry("Finding all trips");
        List<Trip> trips = new ArrayList<>();

        try (Connection connection = dbUtils.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_TRIPS_SQL);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                trips.add(getTripFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            logger.throwing(e);
        }

        logger.traceExit(trips);
        return trips;
    }

    @Override
    public List<Trip> getTripsByDestinationAndDepartureTime(String destination, LocalDateTime departureTime) {
        logger.traceEntry("Finding trips by destination {} and departure time {}", destination, departureTime);
        List<Trip> trips = new ArrayList<>();

        try (Connection connection = dbUtils.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_TRIP_BY_DESTINATION_AND_DEPARTURE_TIME_SQL);
            preparedStatement.setString(1, destination);
            preparedStatement.setTimestamp(2, Timestamp.valueOf(departureTime));

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                trips.add(getTripFromResultSet(resultSet));
            }

        } catch (SQLException e) {
            logger.throwing(e);
        }

        logger.traceExit(trips);
        return trips;
    }
}
