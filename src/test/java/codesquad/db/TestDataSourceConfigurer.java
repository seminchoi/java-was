package codesquad.db;

import codesquad.container.Component;

@Component
public class TestDataSourceConfigurer implements DataSourceConfigurer {
    @Override
    public String getURL() {
        return "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
    }

    @Override
    public String getUsername() {
        return "sa";
    }

    @Override
    public String getPassword() {
        return "1234";
    }

    @Override
    public DDLOption ddlOption() {
        return DDLOption.DROP_CREATE;
    }
}
