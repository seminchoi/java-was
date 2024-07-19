package codesquad.db.csv.command;

import codesquad.db.csv.CsvProcessor;

import java.util.List;

public class CsvCreate extends CsvCommand {
    private final List<String> values;

    public CsvCreate(String table, List<String> values) {
        super(table);
        this.values = values;
    }

    public void process() {
        CsvProcessor.createCsvFile(table, values);
    }
}
