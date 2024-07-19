package codesquad.db;

import codesquad.container.Component;
import codesquad.db.csv.CsvDatasource;
import codesquad.file.AppFileReader;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@Component
public class CsvDataSourceManager implements ConnectionPoolManager {
    private static final String homePath = System.getProperty("user.home");
    private static final String CREATE_SQL_FILE_PATH = "db/create.sql";
    private static final String DROP_SQL_FILE_PATH = "db/drop.sql";
    private File DB_DIR;


    public CsvDataSourceManager(DataSourceConfigurer dataSourceConfigurer) {
        try {
            initFile();
            initTable(dataSourceConfigurer);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void initFile() throws IOException {
        File wasDir = new File(homePath, "java-was");
        if (!wasDir.exists()) {
            wasDir.mkdirs();
        }

        final String UPLOAD_DIR_PATH = "csv";
        DB_DIR = new File(wasDir, UPLOAD_DIR_PATH);

        if (!DB_DIR.exists()) {
            DB_DIR.mkdirs();
        }


        File users = new File(DB_DIR, "users.csv");
        if (!users.exists()) {
            users.createNewFile();
        }

        File posts = new File(DB_DIR, "posts.csv");
        if (!posts.exists()) {
            posts.createNewFile();
        }

        File comments = new File(DB_DIR, "comments.csv");
        if (!comments.exists()) {
            comments.createNewFile();
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
                executeCreate(statement, createSql.getContent());
            }
            case CREATE -> executeCreate(statement, createSql.getContent());
        }
    }

    private void executeCreate(Statement statement, String sqls) throws SQLException {
        String[] splitSqls = sqls.split(";");
        for (String sql : splitSqls) {
            statement.execute(sql.trim());
        }
    }

    @Override
    public DataSource getDataSource() {
        return new CsvDatasource();
    }
}
