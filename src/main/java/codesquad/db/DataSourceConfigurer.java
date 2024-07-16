package codesquad.db;

public interface DataSourceConfigurer {
    String getURL();
    String getUsername();
    String getPassword();
    DDLOption ddlOption();
}
