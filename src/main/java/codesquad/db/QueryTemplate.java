package codesquad.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QueryTemplate {
    private static final Logger logger = LoggerFactory.getLogger(QueryTemplate.class);
    private final DataSource dataSource;

    public QueryTemplate(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public <T> T queryForObject(String sql, ResultSetMapper<T> resultSetMapper, Object... values) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            for (int i = 1; i <= values.length; i++) {
                preparedStatement.setObject(i, values[i - 1]);
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSetMapper.map(resultSet);
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public Object queryForObject(String sql, Object... values) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            for (int i = 1; i <= values.length; i++) {
                preparedStatement.setObject(i, values[i - 1]);
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getObject(1);
            } else {
                return null;
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T mapResultSet(ResultSet resultSet, Class<T> requiredType) throws SQLException {
        if (requiredType == Long.class) {
            return (T) Long.valueOf(resultSet.getLong(1));
        } else if (requiredType == Integer.class) {
            return (T) Integer.valueOf(resultSet.getInt(1));
        } else if (requiredType == String.class) {
            return (T) resultSet.getString(1);
        } else if (requiredType == Double.class) {
            return (T) Double.valueOf(resultSet.getDouble(1));
        } else if (requiredType == Float.class) {
            return (T) Float.valueOf(resultSet.getFloat(1));
        } else if (requiredType == Boolean.class) {
            return (T) Boolean.valueOf(resultSet.getBoolean(1));
        } else if (requiredType == Date.class) {
            return (T) resultSet.getDate(1);
        } else {
            throw new IllegalArgumentException("Unsupported type: " + requiredType.getName());
        }
    }

    public <T> List<T> query(String sql, ResultSetMapper<T> resultSetMapper, Object... values) {
        List<T> results = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            for (int i = 1; i <= values.length; i++) {
                preparedStatement.setObject(i, values[i - 1]);
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                results.add(resultSetMapper.map(resultSet));
            }
            logger.debug("executeQuery: {}", sql);
            return results;
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public void update(String sql, Object... values) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            logger.debug("executeQuery: {}", sql);

            for (int i = 1; i <= values.length; i++) {
                preparedStatement.setObject(i, values[i - 1]);
            }

            logger.debug("executeQuery: {}", sql);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public void execute(String sql) {
        try (Connection connection = dataSource.getConnection()) {
            Statement statement = connection.createStatement();
            logger.debug("executeQuery: {}", sql);
            statement.execute(sql);
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}