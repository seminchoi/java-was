package codesquad.db.csv;

import codesquad.db.csv.command.*;

import java.sql.SQLException;
import java.util.*;
import java.util.regex.*;

public class SqlParser {

    public static CsvCommand parse(String sql) throws SQLException {
        sql = sql.trim().toLowerCase();
        if (sql.startsWith("insert")) {
            return parseInsert(sql);
        }
        if (sql.startsWith("select")) {
            return parseSelect(sql);
        }
        if (sql.startsWith("create")) {
            return parseCreate(sql);
        }
        if (sql.startsWith("drop")) {
            return new CsvDrop();
        }

        throw new SQLException("Unsupported SQL command");
    }

    private static CsvInsert parseInsert(String sql) throws SQLException {
        Pattern pattern = Pattern.compile("insert into (\\w+) \\(([\\w,\\s]+)\\) values \\(([\\w\\W,]+)\\)");
        Matcher matcher = pattern.matcher(sql);
        if (!matcher.find()) {
            throw new SQLException("Invalid INSERT statement");
        }

        String table = matcher.group(1);
        String[] columns = matcher.group(2).split(",\\s*");
        String[] placeholders = matcher.group(3).split(",\\s*");

        if (columns.length != placeholders.length) {
            throw new SQLException("Mismatch between columns and values");
        }

        Map<String, String> values = new HashMap<>();
        for (int i = 0; i < columns.length; i++) {
            values.put(columns[i].trim(), placeholders[i].trim());
        }

        return new CsvInsert(table, values);
    }

    private static CsvSelect parseSelect(String sql) throws SQLException {
        boolean hasWhere = false;
        String[] split = sql.split("where");
        Pattern pattern = Pattern.compile("select (.+) from (\\w+)");
        Matcher matcher = pattern.matcher(split[0]);
        if (!matcher.find()) {
            throw new SQLException("Invalid SELECT statement");
        }

        List<String> selectedColumns = Arrays.asList(matcher.group(1).split(",\\s*"));
        String table = matcher.group(2);

        String column = null;
        String operatorStr = null;
        String value = null;
        List<String> values = new ArrayList<>();
        QueryOperator operator = null;


        if(split.length > 1) {
            hasWhere = true;
            Pattern pattern2 = Pattern.compile("([\\w]+) ([><=]) (.+)");
            Matcher matcher2 = pattern2.matcher(split[1]);

            if(matcher2.find()) {
                column = matcher2.group(1);
                operatorStr = matcher2.group(2);
                value = matcher2.group(3);

                if (operatorStr.equals("=") && value.startsWith("(") && value.endsWith(")")) {
                    operator = QueryOperator.EQUAL;
                    values = Arrays.asList(value.substring(1, value.length() - 1).split(",\\s*"));
                } else {
                    operator = QueryOperator.fromSign(operatorStr.charAt(0));
                    values.add(value);
                }
            }
        }



        return new CsvSelect(table, operator, selectedColumns, column, values, hasWhere);
    }

    private static CsvCreate parseCreate(String sql) {
        Pattern pattern = Pattern.compile("create table if not exists (\\w+)\\s+\\(\\s+([\\w\\d\\s_\\(\\),]+)\\s+\\)");
        Matcher matcher = pattern.matcher(sql);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Invalid CREATE statement");
        }

        String table = matcher.group(1);
        String lines = matcher.group(2);
        List<String> columns = Arrays.asList(lines.split(","))
                .stream()
                .map(line -> {
                    line = line.trim();
                    String[] split = line.split(" ");
                    return split[0].trim().toLowerCase();
                })
                .filter(name -> !name.toLowerCase().startsWith("foreign"))
                .toList();


        return new CsvCreate(table, columns);
    }
}