package codesquad.db;

import javax.sql.DataSource;

public interface ConnectionManager {
    DataSource getDataSource();
}
