package codesquad.db;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QueryTemplate {
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
            return resultSetMapper.map(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
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
            return results;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void update(String sql, Object... values) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            for (int i = 1; i <= values.length; i++) {
                preparedStatement.setObject(i, values[i - 1]);
            }

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void execute(String sql) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}