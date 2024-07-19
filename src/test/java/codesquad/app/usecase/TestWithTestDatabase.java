package codesquad.app.usecase;

import codesquad.container.Container;
import codesquad.db.TestDataSourceConfigurer;
import codesquad.db.TestH2ConnectionPoolManager;
import org.junit.jupiter.api.AfterEach;

import java.sql.SQLException;

public class TestWithTestDatabase {
    protected static Container container;

    @AfterEach
    public void destroy() throws SQLException {
        TestH2ConnectionPoolManager dataSourceConfigurer = (TestH2ConnectionPoolManager) container.getComponent("testH2ConnectionPoolManager");
        TestDataSourceConfigurer testDataSourceConfigurer = (TestDataSourceConfigurer) container.getComponent("testDataSourceConfigurer");

        dataSourceConfigurer.initTable(testDataSourceConfigurer);
    }
}
