package desmedt.frederik.cachebenchmarking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Create a {@link String}-based table based on columns and multiple rows
 */
public class TableFormatter {

    private List<String> columnNames = new ArrayList<>();
    private List<List<?>> rows = new ArrayList<>();

    public TableFormatter() {
    }

    public TableFormatter(String... columns) {
        this(Arrays.asList(columns));
    }

    public TableFormatter(Collection<String> columns) {
        for (String column : columns) {
            addColumn(column);
        }
    }

    public TableFormatter addColumn(String column) {
        columnNames.add(column);
        return this;
    }

    public TableFormatter addRow(Object... columnValues) {
        return addRow(Arrays.asList(columnValues));
    }

    public TableFormatter addRow(Collection<?> columnValues) {
        rows.add(new LinkedList<>(columnValues));
        return this;
    }

    public TableFormatter addRow(RowBuilder rowBuilder) {
        rows.add(rowBuilder.values);
        return this;
    }

    /**
     * Sort all rows currently received by the values in the column at index {@code columnIndex}.
     *
     * @param columnIndex the column index on which should be sorted
     */
    public TableFormatter sort(int columnIndex) {
        Collections.sort(rows, new SingleColumnRowComparator(columnIndex));
        return this;
    }

    @Override
    public String toString() {
        final List<Integer> columnWidths = new ArrayList<>();
        final StringBuilder builder = new StringBuilder();

        for (List<?> row : rows) {
            for (int i = 0; i < row.size(); i++) {
                if (i < columnWidths.size()) {
                    final int columnWidth = row.get(i).toString().length();
                    if (columnWidths.get(i) < columnWidth) {
                        columnWidths.set(i, columnWidth);
                    }
                } else {
                    columnWidths.add(row.get(i).toString().length());
                }
            }
        }

        for (int i = 0; i < columnNames.size(); i++) {
            if (i < columnWidths.size()) {
                final String column = columnNames.get(i);
                final int columnWidth = column.toString().length();
                if (columnWidths.get(i) < columnWidth) {
                    columnWidths.set(i, columnWidth);
                }

                builder.append(String.format(" %" + columnWidths.get(i) + "s ", column));
            }
        }

        builder.append(String.format("%n"));
        for (Collection<?> row : rows) {
            int i = 0;
            for (Object value : row) {
                builder.append(String.format(" %" + columnWidths.get(i++) + "s ", value.toString()));
            }

            builder.append(String.format("%n"));
        }

        return builder.toString();
    }

    public static String formatRow(String... values) {
        StringBuilder builder = new StringBuilder();
        for (String value : values) {
            builder.append(String.format("%" + value.length() + "s ", value));
        }

        return builder.toString();
    }

    public static String generateHitRatioTable(String policyTag, Collection<CacheBenchmarkConfiguration.CacheStats> stats) {
        TableFormatter table = new TableFormatter(policyTag);
        Map<Double, RowBuilder> cacheRatioRowMap = new HashMap<>();
        List<String> tagIndexer = new ArrayList<>(stats.size() + 1);
        tagIndexer.add("");
        for (CacheBenchmarkConfiguration.CacheStats stat : stats) {
            if (stat.areReadStats()) {
                if (!tagIndexer.contains(stat.getTraceTag())) {
                    tagIndexer.add(stat.getTraceTag());
                    table.addColumn(stat.getTraceTag());
                }

                if (!cacheRatioRowMap.containsKey(stat.getCacheRatio())) {
                    cacheRatioRowMap.put(stat.getCacheRatio(), new RowBuilder(String.format("%.2f%%", stat.getCacheRatio() * 100)));
                }

                cacheRatioRowMap.get(stat.getCacheRatio()).insertValue(tagIndexer.indexOf(stat.getTraceTag()), String.format("%.3f%%", stat.getHitrate()));
            }
        }

        for (RowBuilder builder : cacheRatioRowMap.values()) {
            table.addRow(builder);
        }

        return table.sort(0).toString();
    }

    public static class RowBuilder {

        private List<String> values;

        public RowBuilder() {
            values = new LinkedList<>();
        }

        public RowBuilder(String... values) {
            this(new LinkedList<>(Arrays.asList(values)));
        }

        public RowBuilder(Collection<String> values) {
            this.values = new LinkedList<>(values);
        }

        public RowBuilder addValue(String value) {
            values.add(value);
            return this;
        }

        public RowBuilder insertValue(int index, String value) {
            if (index >= values.size()) {
                for (int i = 0; values.size() < index; i++) {
                    values.add("");
                }

                values.add(value);
            } else {
                values.add(index, value);
            }

            return this;
        }

        public List<String> build() {
            return values;
        }

    }

    private static class SingleColumnRowComparator implements Comparator<List<?>> {

        private int columnIndex;

        public SingleColumnRowComparator(int columnIndex) {
            this.columnIndex = columnIndex;
        }

        @Override
        public int compare(List<?> lhs, List<?> rhs) {
            if (lhs.size() <= columnIndex) {
                return 1;
            }

            if (rhs.size() <= columnIndex) {
                return -1;
            }

            Object left = lhs.get(columnIndex);
            Object right = rhs.get(columnIndex);

            try {
                double leftValue = 0;
                if (left.toString().endsWith("%")) {
                    leftValue = Double.parseDouble(left.toString().substring(0, left.toString().length() - 1));
                }

                double rightValue = 0;
                if (right.toString().endsWith("%")) {
                    rightValue = Double.parseDouble(right.toString().substring(0, right.toString().length() - 1));
                }

                return leftValue < rightValue ? -1 : 1;
            } catch (NumberFormatException nfex) {
                return left.toString().compareTo(right.toString());
            }
        }
    }
}
