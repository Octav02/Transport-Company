package ro.mpp2024.persistance.dbrepository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ro.mpp2024.model.User;
import ro.mpp2024.persistance.UserRepository;
import ro.mpp2024.persistance.dbrepository.utils.JdbcUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class UserDBRepository implements UserRepository {
    private static final Logger logger = LogManager.getLogger();
    private final JdbcUtils dbUtils;

    private static final String INSERT_USER_SQL = "insert into Users(first_name,last_name,username,password) values (?,?,?,?)";
    private static final String DELETE_USER_SQL = "delete from Users where id=?";
    private static final String UPDATE_USER_SQL = "update Users set first_name=?,last_name=?,username=?,password=? where id=?";
    private static final String FIND_USER_BY_ID_SQL = "select * from Users where id=?";
    private static final String FIND_ALL_USERS_SQL = "select * from Users";
    private static final String FIND_USER_BY_USERNAME_SQL = "select * from Users where username=?";

    public UserDBRepository(Properties properties) {
        logger.info("Initiating UserDBRepository with properties : {}", properties);
        dbUtils = new JdbcUtils(properties);
    }

    @Override
    public void add(User elem) {
        logger.traceEntry("Adding user {}", elem);
        Connection connection = dbUtils.getConnection();
        try(PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER_SQL)) {
            preparedStatement.setString(1, elem.getFirstName());
            preparedStatement.setString(2, elem.getLastName());
            preparedStatement.setString(3, elem.getUsername());
            preparedStatement.setString(4, elem.getPassword());
            int result = preparedStatement.executeUpdate();
            if (result == 0) {
                logger.error("Error adding user");
            }
        } catch (Exception e) {
            logger.error("DB Error adding user" + e.getMessage());
            throw new RuntimeException("Error adding user" + e.getMessage());
        }
        logger.traceExit();
    }
    @Override
    public void delete(User elem) {
        logger.traceEntry("Deleting user {}", elem);
        Connection connection = dbUtils.getConnection();
        try(PreparedStatement preparedStatement = connection.prepareStatement(DELETE_USER_SQL)) {
            preparedStatement.setLong(1, elem.getId());
            int result = preparedStatement.executeUpdate();
            if (result == 0) {
                logger.error("Error deleting user");
            }
        } catch (Exception e) {
            logger.error("DB Error deleting user" + e.getMessage());
            throw new RuntimeException("Error deleting user" + e.getMessage());
        }
        logger.traceExit();

    }

    @Override
    public void update(User elem, Long id) {
        logger.traceEntry("Updating user {}", elem);
        Connection connection = dbUtils.getConnection();
        try(PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_USER_SQL)) {
            preparedStatement.setString(1, elem.getFirstName());
            preparedStatement.setString(2, elem.getLastName());
            preparedStatement.setString(3, elem.getUsername());
            preparedStatement.setString(4, elem.getPassword());
            preparedStatement.setLong(5, id);
            int result = preparedStatement.executeUpdate();
            if (result == 0) {
                logger.error("Error updating user");
            }
        } catch (Exception e) {
            logger.error("DB Error updating user" + e.getMessage());
            throw new RuntimeException("Error updating user" + e.getMessage());
        }
        logger.traceExit();
    }

    @Override
    public User findById(Long id) {
        logger.traceEntry("Finding user with id {}", id);
        Connection connection = dbUtils.getConnection();
        try(PreparedStatement preparedStatement = connection.prepareStatement(FIND_USER_BY_ID_SQL)) {
            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    User user = getUserFromResultSet(resultSet);
                    logger.traceExit();
                    return user;
                }
            }
        } catch (Exception e) {
            logger.error("DB Error finding user" + e.getMessage());
            throw new RuntimeException("Error finding user" + e.getMessage());
        }

        logger.traceExit("User not found");
        return null;
    }

    private User getUserFromResultSet(ResultSet resultSet) {
        logger.traceEntry();
        try {
            Long id = resultSet.getLong("id");
            String firstName = resultSet.getString("first_name");
            String lastName = resultSet.getString("last_name");
            String username = resultSet.getString("username");
            String password = resultSet.getString("password");

            logger.traceExit();
            return new User(id, firstName, lastName, username, password);
        }
        catch (SQLException e) {
            logger.error("DB Error getting user from result set " + e.getMessage());
            throw new RuntimeException("Error getting user from result set" + e.getMessage());
        }
    }

    @Override
    public Iterable<User> findAll() {
        logger.traceEntry("Finding all users");
        Connection connection = dbUtils.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_USERS_SQL)) {
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                Set<User> users = new HashSet<>();
                while (resultSet.next()) {
                    User user = getUserFromResultSet(resultSet);
                    users.add(user);
                }
                logger.traceExit();
                return users;
            }
        }
        catch (SQLException e) {
            logger.error("DB Error finding all users" + e.getMessage());
            throw new RuntimeException("Error finding all users" + e.getMessage());
        }
    }



    @Override
    public User findByUsername(String username) {
        logger.traceEntry("Finding user by username {}", username);
        Connection connection = dbUtils.getConnection();
        try(PreparedStatement preparedStatement = connection.prepareStatement(FIND_USER_BY_USERNAME_SQL)) {
            preparedStatement.setString(1, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    User user = getUserFromResultSet(resultSet);
                    logger.traceExit();
                    return user;
                }
            }
        } catch (Exception e) {
            logger.error("DB Error finding user by username" + e.getMessage());
            throw new RuntimeException("Error finding user by username" + e.getMessage());
        }
        logger.traceExit("DB User not found");
        return null;

    }
}
