package codesquad.db;

import codesquad.container.Component;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Component
public class JdbcTemplateManager {
    private final JdbcTemplate jdbcTemplate;

    public JdbcTemplateManager(DataSourceManager<DataSource> jdbcConnectionManager) {
        jdbcTemplate = new JdbcTemplate(jdbcConnectionManager.getDataSource());
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
}
