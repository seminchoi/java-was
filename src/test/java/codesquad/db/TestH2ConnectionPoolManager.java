package codesquad.db;

import codesquad.container.Component;
import codesquad.file.AppFileReader;
import org.h2.jdbcx.JdbcConnectionPool;
import org.h2.jdbcx.JdbcDataSource;
import org.h2.tools.Server;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@Component
public class TestH2ConnectionPoolManager implements ConnectionPoolManager {
    private static final String homePath = System.getProperty("user.home");
    private static final String CREATE_SQL_FILE_PATH = "db/create.sql";
    private static final String DROP_SQL_FILE_PATH = "db/drop.sql";
    private File DB_DIR;

    private final JdbcConnectionPool jdbcConnectionPool;


    public TestH2ConnectionPoolManager(DataSourceConfigurer dataSourceConfigurer) {
        try {
            initServer();
            initTable(dataSourceConfigurer);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        JdbcDataSource jdbcDataSource = new JdbcDataSource();
        jdbcDataSource.setURL(dataSourceConfigurer.getURL());
        jdbcDataSource.setUser(dataSourceConfigurer.getUsername());
        jdbcDataSource.setPassword(dataSourceConfigurer.getPassword());
        this.jdbcConnectionPool = JdbcConnectionPool.create(jdbcDataSource);
    }

    private void initServer() throws SQLException {
        if (!isPortInUse(9092)) {
            Server.createTcpServer("-tcp", "-tcpPort", "9092").start();
        }
    }

    private boolean isPortInUse(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setReuseAddress(true);
            return false; // 포트가 사용 중이 아님
        } catch (IOException e) {
            return true; // 포트가 이미 사용 중임
        }
    }

    public void initTable(DataSourceConfigurer dataSourceConfigurer) throws SQLException {
        Connection connection = DriverManager.getConnection(
                dataSourceConfigurer.getURL(),
                dataSourceConfigurer.getUsername(),
                dataSourceConfigurer.getPassword()
        );


        Statement statement = connection.createStatement();

        AppFileReader dropSql = new AppFileReader(DROP_SQL_FILE_PATH);
        AppFileReader createSql = new AppFileReader(CREATE_SQL_FILE_PATH);

        switch (dataSourceConfigurer.ddlOption()) {
            case DROP_CREATE -> {
                statement.execute(dropSql.getContent());
                statement.execute(createSql.getContent());
            }
            case CREATE -> statement.execute(createSql.getContent());
        }
    }

    @Override
    public DataSource getDataSource() {
        return jdbcConnectionPool;
    }
}
