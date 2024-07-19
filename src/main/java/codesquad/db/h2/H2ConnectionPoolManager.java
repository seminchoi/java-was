package codesquad.db.h2;

import codesquad.db.ConnectionPoolManager;
import codesquad.db.DataSourceConfigurer;
import codesquad.file.AppFileReader;
import org.h2.jdbcx.JdbcConnectionPool;
import org.h2.jdbcx.JdbcDataSource;
import org.h2.tools.Server;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class H2ConnectionPoolManager implements ConnectionPoolManager {
    private static final String homePath = System.getProperty("user.home");
    private static final String CREATE_SQL_FILE_PATH = "db/create.sql";
    private static final String DROP_SQL_FILE_PATH = "db/drop.sql";
    private File DB_DIR;

    private final JdbcConnectionPool jdbcConnectionPool;


    public H2ConnectionPoolManager(DataSourceConfigurer dataSourceConfigurer) {
        try {
            initFile();
            initServer();
            initTable(dataSourceConfigurer);
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }

        JdbcDataSource jdbcDataSource = new JdbcDataSource();
        jdbcDataSource.setURL(dataSourceConfigurer.getURL());
        jdbcDataSource.setUser(dataSourceConfigurer.getUsername());
        jdbcDataSource.setPassword(dataSourceConfigurer.getPassword());
        this.jdbcConnectionPool = JdbcConnectionPool.create(jdbcDataSource);
    }

    private void initServer() throws SQLException {
        Server.createTcpServer("-tcp", "-tcpPort", "9092").start();
    }

    private void initFile() throws IOException {
        File wasDir = new File(homePath, "java-was");
        if (!wasDir.exists()) {
            wasDir.mkdirs();
        }

        final String UPLOAD_DIR_PATH = "db";
        DB_DIR = new File(wasDir, UPLOAD_DIR_PATH);

        if (!DB_DIR.exists()) {
            DB_DIR.mkdirs();
        }

        File wasDb = new File(DB_DIR, "was.mv.db");
        if (!wasDb.exists()) {
            wasDb.createNewFile();
        }
    }

    private void initTable(DataSourceConfigurer dataSourceConfigurer) throws SQLException {
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
