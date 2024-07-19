package codesquad.db.csv.command;

import codesquad.db.csv.CsvProcessor;

import java.util.Map;

public class CsvInsert extends CsvCommand {
    private final Map<String, String> values;

    public CsvInsert(String table, Map<String, String> values) {
        super(table);
        this.values = values;
    }

    public boolean process() {
        CsvProcessor.appendRow(table, values);
        return true;
    }
}
