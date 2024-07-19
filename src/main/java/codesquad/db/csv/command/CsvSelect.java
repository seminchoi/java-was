package codesquad.db.csv.command;

import codesquad.db.csv.*;
import codesquad.db.csv.record.CsvResultSet;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;

public class CsvSelect extends CsvCommand {

    private final QueryOperator operator;
    private final List<String> selectedColumns;
    private final String column;
    private final List<String> values;
    private final boolean hasWhere;

    public CsvSelect(String table, QueryOperator operator, List<String> selectedColumns, String column, List<String> values, boolean hasWhere) {
        super(table);
        this.operator = operator;
        this.selectedColumns = selectedColumns;
        this.column = column;
        this.values = values;
        this.hasWhere = hasWhere;
    }

    public CsvResultSet process() {
        CsvRecord csvRecord = CsvProcessor.parseCsv(table);

        Map<String, Integer> originalHeaderMap = csvRecord.getHeaderMap();
        List<CsvRow> rows = csvRecord.getValues();

        Map<String, Integer> selectedHeaderMap = createSelectedHeaderMap(originalHeaderMap);
        List<List<String>> filteredRecords = new ArrayList<>();

        if (hasWhere) {
            if (!originalHeaderMap.containsKey(column)) {
                throw new IllegalArgumentException("Column not found: " + column);
            }

            int columnIndex = originalHeaderMap.get(column);
            for (CsvRow row : rows) {
                String cellValue = row.getValues().get(columnIndex);
                if (matchesCondition(cellValue)) {
                    filteredRecords.add(selectColumns(row, originalHeaderMap, selectedHeaderMap));
                }
            }
        } else {
            for (CsvRow row : rows) {
                filteredRecords.add(selectColumns(row, originalHeaderMap, selectedHeaderMap));
            }
        }

        return new CsvResultSet(selectedHeaderMap, filteredRecords);
    }

    private Map<String, Integer> createSelectedHeaderMap(Map<String, Integer> originalHeaderMap) {
        Map<String, Integer> selectedHeaderMap = new LinkedHashMap<>();
        int newIndex = 0;
        for (String col : selectedColumns) {
            if (originalHeaderMap.containsKey(col)) {
                selectedHeaderMap.put(col, newIndex++);
            } else {
                throw new IllegalArgumentException("Selected column not found: " + col);
            }
        }
        return selectedHeaderMap;
    }

    private List<String> selectColumns(CsvRow row, Map<String, Integer> originalHeaderMap, Map<String, Integer> selectedHeaderMap) {
        List<String> selectedValues = new ArrayList<>();
        for (String col : selectedHeaderMap.keySet()) {
            int originalIndex = originalHeaderMap.get(col);
            selectedValues.add(row.getValues().get(originalIndex));
        }
        return selectedValues;
    }

    private boolean matchesCondition(String cellValue) {
        if (operator == QueryOperator.EQUAL && values.size() > 1) {
            return values.contains(cellValue);
        } else {
            BiPredicate<String, String> predicate = operator.getOperator();
            return predicate.test(cellValue, values.get(0));
        }
    }
}
