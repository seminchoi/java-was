package codesquad.db.csv;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CsvRecord {
    private Map<String, Integer> headerMap;
    private final List<CsvRow> values = new ArrayList<>();

    public void setHeaderMap(Map<String, Integer> headerMap) {
        this.headerMap = headerMap;
    }

    public void addRow(CsvRow row) {
        values.add(row);
    }

    public Map<String, Integer> getHeaderMap() {
        return headerMap;
    }

    public List<CsvRow> getValues() {
        return values;
    }
}
