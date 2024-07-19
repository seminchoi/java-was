package codesquad.db.csv;

import java.util.ArrayList;
import java.util.List;

public class CsvRow {
    private final List<String> values = new ArrayList<>();

    public void add(String value) {
        values.add(value);
    }

    public Object get(int index) {
        return values.get(index);
    }

    public List<String> getValues() {
        return values;
    }
}
