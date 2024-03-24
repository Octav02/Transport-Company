package ro.mpp2024.transportcompany.repository.dbrepository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ro.mpp2024.transportcompany.model.Trip;
import ro.mpp2024.transportcompany.repository.TripRepository;
import ro.mpp2024.transportcompany.utils.dbutils.JdbcUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public class TripDbRepository implements TripRepository {

    private static final Logger logger = LogManager.getLogger();
    private final JdbcUtils dbUtils;

    private static final String INSERT_TRIP_SQL = "insert into Trips(destination,departure_time,number_of_seats) values (?,?,?)";
    private static final String DELETE_TRIP_SQL = "DELETE FROM TRIPS WHERE ID=?";
    private static final String UPDATE_TRIP_SQL = "UPDATE TRIPS SET DESTINATION=?, DEPARTURE_TIME=?, NUMBER_OF_SEATS=? WHERE ID=?";
    private static final String FIND_TRIP_BY_ID_SQL = "SELECT * FROM TRIPS WHERE ID=?";
    private static final String FIND_ALL_TRIPS_SQL = "SELECT * FROM TRIPS";
    private static final String FIND_TRIP_BY_DESTINATION_AND_DEPARTURE_TIME_SQL = "SELECT * FROM TRIPS WHERE DESTINATION=? AND DATE(DEPARTURE_TIME)=?";


    public TripDbRepository(Properties properties) {
        logger.info("Initiating TripDbRepository");
        dbUtils = new JdbcUtils(properties);
    }

    @Override
    public void add(Trip elem) {
        logger.traceEntry("Adding trip {}", elem);
        Connection connection = dbUtils.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_TRIP_SQL)) {
            preparedStatement.setString(1, elem.getDestination());
            preparedStatement.setTimestamp(2, Timestamp.valueOf(elem.getDepartureTime()));
            preparedStatement.setInt(3, elem.getTotalNumberOfSeats());

            int result = preparedStatement.executeUpdate();
            if (result == 0) {
                logger.error("Error adding trip");
            }
        } catch (Exception e) {
            logger.error("DB Error adding trip" + e.getMessage());
            throw new RuntimeException("Error adding trip" + e.getMessage());
        }
        logger.traceExit();
    }

    @Override
    public void delete(Trip elem) {
        logger.traceEntry("Deleting trip {}", elem);
        Connection connection = dbUtils.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_TRIP_SQL)) {
            preparedStatement.setLong(1, elem.getId());

            int result = preparedStatement.executeUpdate();
            if (result == 0) {
                logger.error("Error deleting trip");
            }
        } catch (Exception e) {
            logger.error("DB Error deleting trip" + e.getMessage());
            throw new RuntimeException("Error deleting trip" + e.getMessage());
        }
        logger.traceExit();

    }

    @Override
    public void update(Trip elem, Long id) {
        logger.traceEntry("Updating trip {}", elem);
        Connection connection = dbUtils.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_TRIP_SQL)) {
            preparedStatement.setString(1, elem.getDestination());
            preparedStatement.setTimestamp(2, Timestamp.valueOf(elem.getDepartureTime()));
            preparedStatement.setInt(3, elem.getTotalNumberOfSeats());
            preparedStatement.setLong(4, id);

            int result = preparedStatement.executeUpdate();
            if (result == 0) {
                logger.error("Error updating trip");
            }
        } catch (Exception e) {
            logger.error("DB Error updating trip" + e.getMessage());
            throw new RuntimeException("Error updating trip" + e.getMessage());
        }

        logger.traceExit();
    }

    @Override
    public Trip findById(Long id) {
        logger.traceEntry("Finding trip with id {}", id);
        Connection connection = dbUtils.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement(FIND_TRIP_BY_ID_SQL)) {
            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Trip trip = getTripFromResultSet(resultSet);
                    logger.traceExit();
                    return trip;
                }
            }
        } catch (Exception e) {
            logger.error("DB Error finding trip with id" + e.getMessage());
            throw new RuntimeException("Error finding trip with id" + e.getMessage());
        }
        logger.traceExit("Trip not found");
        return null;
    }

    private Trip getTripFromResultSet(ResultSet resultSet) {
        logger.traceEntry("Getting trip from result set");
        try {
            Long id = resultSet.getLong("id");
            String destination = resultSet.getString("destination");
            LocalDateTime departureTime = resultSet.getTimestamp("departure_time").toLocalDateTime();
            int numberOfSeats = resultSet.getInt("number_of_seats");

            logger.traceExit();
            return new Trip(id, destination, departureTime, numberOfSeats);
        } catch (Exception e) {
            logger.error("DB Error getting trip from result set" + e.getMessage());
            throw new RuntimeException("Error getting trip from result set" + e.getMessage());
        }
    }

    @Override
    public Iterable<Trip> findAll() {
        logger.traceEntry("Finding all trips");
        Connection connection = dbUtils.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_TRIPS_SQL)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                Set<Trip> trips = new HashSet<>();
                while (resultSet.next()) {
                    Trip trip = getTripFromResultSet(resultSet);
                    trips.add(trip);
                }

                logger.traceExit();
                return trips;
            }
        } catch (Exception e) {
            logger.error("DB Error finding all trips" + e.getMessage());
            throw new RuntimeException("Error finding all trips" + e.getMessage());
        }

    }

    @Override
    public List<Trip> getTripsByDestinationAndDepartureTime(String destination, LocalDateTime departureTime) {
        logger.traceEntry("Finding trips by destination {} and departure time {}", destination, departureTime);
        Connection connection = dbUtils.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement(FIND_TRIP_BY_DESTINATION_AND_DEPARTURE_TIME_SQL)) {
            preparedStatement.setString(1, destination);
            preparedStatement.setTimestamp(2, Timestamp.valueOf(departureTime));

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                Set<Trip> trips = new HashSet<>();
                while (resultSet.next()) {
                    Trip trip = getTripFromResultSet(resultSet);
                    trips.add(trip);
                }

                logger.traceExit();
                return List.copyOf(trips);
            }
        } catch (Exception e) {
            logger.error("DB Error finding trips by destination and departure time" + e.getMessage());
            throw new RuntimeException("Error finding trips by destination and departure time" + e.getMessage());
        }
    }
}
