package codesquad.db.csv;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class CsvProcessor {
    private static final String path = System.getProperty("user.home") + "/java-was/csv/";

    public static CsvRecord parseCsv(String table) {
        CsvRecord record = new CsvRecord();

        try (BufferedReader br = new BufferedReader(new FileReader(getFileName(table)))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    record.setHeaderMap(parseHeader(line));
                    isFirstLine = false;
                } else {
                    CsvRow row = parseRow(line);
                    record.addRow(row);
                }
            }
            return record;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Map<String, Integer> parseHeader(String headerLine) {
        String[] headers = parseLine(headerLine);
        Map<String, Integer> headerMap = new HashMap<>();
        for (int i = 0; i < headers.length; i++) {
            headerMap.put(headers[i].trim(), i);
        }
        return headerMap;
    }

    private static CsvRow parseRow(String rowLine) {
        String[] values = parseLine(rowLine);
        CsvRow row = new CsvRow();
        for (String value : values) {
            row.add(value.trim());
        }
        return row;
    }

    private static String[] parseLine(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        for (char c : line.toCharArray()) {
            if (c == '"') {
                if (inQuotes && !sb.isEmpty() && sb.charAt(sb.length() - 1) == '"') {
                    sb.append(c);
                    continue;
                }
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                tokens.add(sb.toString());
                sb = new StringBuilder();
            } else {
                sb.append(c);
            }
        }
        tokens.add(sb.toString());

        return tokens.stream()
                .map(CsvProcessor::unescapeField)
                .toArray(String[]::new);
    }

    private static String unescapeField(String field) {
        if (field.startsWith("\"") && field.endsWith("\"")) {
            return field.substring(1, field.length() - 1).replace("\"\"", "\"");
        }
        return field;
    }

    public static void appendRow(String table, Map<String, String> rowData) {
        try {
            List<String> headers = readHeaders(table);

            if (!isValidRowData(headers, rowData)) {
                throw new IllegalArgumentException("Invalid row data");
            }

            List<String> newRow = new ArrayList<>();
            boolean needsId = false;

            if (rowData.size() == headers.size() - 1 && headers.contains("id")) {
                needsId = true;
            }

            for (String header : headers) {
                if (header.equals("id") && needsId) {
                    int newId = getLastId(getFileName(table)) + 1;
                    newRow.add(String.valueOf(newId));
                } else {
                    newRow.add(rowData.getOrDefault(header, ""));
                }
            }

            writeRowToFile(getFileName(table), newRow);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<String> readHeaders(String table) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(getFileName(table)))) {
            String headerLine = reader.readLine();
            return Arrays.asList(headerLine.split(","));
        }
    }

    private static boolean isValidRowData(List<String> headers, Map<String, String> rowData) {
        return rowData.size() == headers.size() ||
                (rowData.size() == headers.size() - 1 && headers.contains("id"));
    }

    private static int getLastId(String path) throws IOException {
        String lastLine = "";
        int lineCount = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            lineCount++;
            String line;
            while ((line = reader.readLine()) != null) {
                lastLine = line;
            }
        }

        if(lineCount == 1) {
            return 0;
        }
        String[] values = lastLine.split(",");
        return Integer.parseInt(values[0]);
    }

    private static void writeRowToFile(String path, List<String> values) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path, true))) {
            String rowLine = values.stream()
                    .map(CsvProcessor::escapeSpecialCharacters)
                    .collect(Collectors.joining(","));
            writer.write(rowLine);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException("Failed to append row to CSV file: " + e.getMessage(), e);
        }
    }

    private static String escapeSpecialCharacters(String data) {
        String escapedData = data.replaceAll("\"", "\"\"");
        if (escapedData.contains(",") || escapedData.contains("\"") || escapedData.contains("\n")) {
            escapedData = "\"" + escapedData + "\"";
        }
        return escapedData;
    }

    public static void createCsvFile(String table, List<String> headers) {
        if (headers == null || headers.isEmpty()) {
            return;
        }

        File file = new File(getFileName(table));
        if (file.exists() && file.length() > 0) {
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            String headerLine = String.join(",", headers);
            writer.write(headerLine);
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getFileName(String table) {
        return path + table + ".csv";
    }
}
