package codesquad.app.config;

import codesquad.container.Component;
import codesquad.db.DDLOption;
import codesquad.db.DataSourceConfigurer;

@Component
public class H2DataSourceConfigurer implements DataSourceConfigurer {
    @Override
    public String getURL() {
        return "jdbc:h2:tcp://localhost:9092/~/java-was/db/was;LOCK_MODE=0;AUTO_SERVER=TRUE";
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
        return DDLOption.CREATE;
    }
}
