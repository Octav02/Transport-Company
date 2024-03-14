package ro.mpp2024.repository.dbrepostiory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ro.mpp2024.model.User;
import ro.mpp2024.repository.UserRepository;
import ro.mpp2024.utils.dbutils.JdbcUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class UserDBRepository implements UserRepository {
    private final JdbcUtils dbUtils;

    private static final Logger logger = LogManager.getLogger();

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
        try (Connection connection = dbUtils.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER_SQL);
            preparedStatement.setString(1, elem.getFirstName());
            preparedStatement.setString(2, elem.getLastName());
            preparedStatement.setString(3, elem.getUsername());
            preparedStatement.setString(4, elem.getPassword());

            int result = preparedStatement.executeUpdate();
            if (result == 0) {
                logger.error("Error adding user");
            }

        } catch (Exception e) {
            logger.throwing(e);
        }
        logger.traceExit();
    }

    @Override
    public void delete(User elem) {
        logger.traceEntry("Deleting user {}", elem);
        try (Connection connection = dbUtils.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_USER_SQL);
            preparedStatement.setLong(1, elem.getId());
            int result = preparedStatement.executeUpdate();
            if (result == 0) {
                logger.error("Error deleting user");
            }
        } catch (Exception e) {
            logger.throwing(e);
        }
        logger.traceExit();
    }

    @Override
    public void update(User elem, Long id) {
        logger.traceEntry("Updating user {}", elem);
        try (Connection connection = dbUtils.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_USER_SQL);
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
            logger.throwing(e);
        }
        logger.traceExit();
    }

    @Override
    public User findById(Long id) {
        logger.traceEntry("Finding user by id {}", id);
        User user = null;
        try (Connection connection = dbUtils.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_USER_BY_ID_SQL);
            preparedStatement.setLong(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                user = getUserFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            logger.throwing(e);
        }
        logger.traceExit(user);
        return user;
    }

    private User getUserFromResultSet(ResultSet resultSet) {
        logger.traceEntry("Getting user from result set {}", resultSet);
        User user;
        try {
            logger.traceEntry("Getting user from result set {}", resultSet);
            String firstName = resultSet.getString("first_name");
            String lastName = resultSet.getString("last_name");
            String username = resultSet.getString("username");
            String password = resultSet.getString("password");
            long id = resultSet.getLong("id");
            user = new User(id, firstName, lastName, username, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        logger.traceExit();
        return user;
    }

    @Override
    public Iterable<User> findAll() {
        logger.traceEntry("Finding all users ");
        Set<User> users = new HashSet<>();
        try (Connection connection = dbUtils.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_USERS_SQL);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                users.add(getUserFromResultSet(resultSet));
            }

        } catch (SQLException e) {
            logger.throwing(e);
        }
        logger.traceExit(users);
        return users;
    }

    @Override
    public Collection<User> getAll() {
        logger.traceEntry("Getting all users");
        List<User> users = new ArrayList<>();
        try (Connection connection = dbUtils.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_USERS_SQL);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                users.add(getUserFromResultSet(resultSet));
            }

        } catch (SQLException e) {
            logger.throwing(e);
        }
        logger.traceExit(users);
        return users;
    }

    @Override
    public User findByUsername(String username) {
        logger.traceEntry("Finding user by username {}", username);
        User user = null;
        try (Connection connection = dbUtils.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_USER_BY_USERNAME_SQL);
            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                user = getUserFromResultSet(resultSet);
            }

        } catch (SQLException e) {
            logger.throwing(e);
        }
        logger.traceExit(user);
        return user;
    }
}
