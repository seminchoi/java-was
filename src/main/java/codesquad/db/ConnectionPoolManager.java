package codesquad.db;

import javax.sql.DataSource;

public interface ConnectionPoolManager {
    DataSource getDataSource();
}
