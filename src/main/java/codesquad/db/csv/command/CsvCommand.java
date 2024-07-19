package codesquad.db.csv.command;

public abstract class CsvCommand {
    protected final String table;

    public CsvCommand(String table) {
        this.table = table;
    }
}
