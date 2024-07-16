package codesquad.db;

import codesquad.container.Component;
import codesquad.file.AppFileReader;
import codesquad.server.HttpServer;
import org.h2.jdbcx.JdbcConnectionPool;
import org.h2.jdbcx.JdbcDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Component
public class JdbcDataSourceManager implements DataSourceManager<DataSource> {
    private static final String CREATE_SQL_FILE_PATH = "db/create.sql";
    private static final String DROP_SQL_FILE_PATH = "db/drop.sql";

    private final JdbcConnectionPool jdbcConnectionPool;

    public JdbcDataSourceManager(DataSourceConfigurer dataSourceConfigurer) {
        JdbcDataSource jdbcDataSource = new JdbcDataSource();
        jdbcDataSource.setURL(dataSourceConfigurer.getURL());
        jdbcDataSource.setUser(dataSourceConfigurer.getUsername());
        jdbcDataSource.setPassword(dataSourceConfigurer.getPassword());
        this.jdbcConnectionPool = JdbcConnectionPool.create(jdbcDataSource);
        init(dataSourceConfigurer);
    }

    @Override
    public DataSource getDataSource() {
        return jdbcConnectionPool;
    }

    private void init(DataSourceConfigurer dataSourceConfigurer) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(getDataSource());
        AppFileReader dropReader = new AppFileReader(DROP_SQL_FILE_PATH);
        AppFileReader createReader = new AppFileReader(CREATE_SQL_FILE_PATH);

        switch (dataSourceConfigurer.ddlOption()) {
            case DROP_CREATE -> {
                jdbcTemplate.execute(dropReader.getContent());
                jdbcTemplate.execute(createReader.getContent());
            }
            case CREATE -> {
                jdbcTemplate.execute(createReader.getContent());
            }
        }
    }
}
