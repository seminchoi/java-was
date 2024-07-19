package codesquad.app.config;

import codesquad.db.DDLOption;
import codesquad.db.DataSourceConfigurer;

public class CsvDataSourceConfigurer implements DataSourceConfigurer {

    @Override
    public String getURL() {
        return System.getProperty("user.home") + "/java-was/csv";
    }

    @Override
    public String getUsername() {
        return "";
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public DDLOption ddlOption() {
        return DDLOption.CREATE;
    }
}
