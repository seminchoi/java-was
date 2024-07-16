package codesquad.db;

import codesquad.container.Component;
import codesquad.file.AppFileReader;
import org.h2.jdbcx.JdbcConnectionPool;
import org.h2.jdbcx.JdbcDataSource;

import javax.sql.DataSource;

@Component
public class H2ConnectionPoolManager implements ConnectionPoolManager {
    private static final String CREATE_SQL_FILE_PATH = "db/create.sql";
    private static final String DROP_SQL_FILE_PATH = "db/drop.sql";

    private final JdbcConnectionPool jdbcConnectionPool;

    public H2ConnectionPoolManager(DataSourceConfigurer dataSourceConfigurer) {
        JdbcDataSource jdbcDataSource = new JdbcDataSource();
        jdbcDataSource.setURL(dataSourceConfigurer.getURL());
        jdbcDataSource.setUser(dataSourceConfigurer.getUsername());
        jdbcDataSource.setPassword(dataSourceConfigurer.getPassword());
        this.jdbcConnectionPool = JdbcConnectionPool.create(jdbcDataSource);
        init(dataSourceConfigurer);
    }

    private void init(DataSourceConfigurer dataSourceConfigurer) {
        QueryTemplate queryTemplate = new QueryTemplate(jdbcConnectionPool);
        AppFileReader dropSql = new AppFileReader(DROP_SQL_FILE_PATH);
        AppFileReader createSql = new AppFileReader(CREATE_SQL_FILE_PATH);

        switch (dataSourceConfigurer.ddlOption()) {
            case DROP_CREATE -> {
                queryTemplate.execute(dropSql.getContent());
                queryTemplate.execute(createSql.getContent());
            }
            case CREATE -> queryTemplate.execute(createSql.getContent());
        }
    }

    @Override
    public DataSource getDataSource() {
        return jdbcConnectionPool;
    }
}
