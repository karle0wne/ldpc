package ldpc.matrix.basis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SoftMetricMatrix {

    private final List<SoftMetricRow> rows = new ArrayList<>();

    public SoftMetricMatrix addRow(List<Double> elements) {
        rows.add(new SoftMetricRow(elements));
        return this;
    }

    public List<List<Double>> getAll() {
        return rows.stream()
                .map(SoftMetricRow::getElements)
                .collect(Collectors.toList());
    }

    public int rowsSize() {
        return rows.size();
    }

    public int columnsSize() {
        return rows.get(0).getElements().size();
    }

    public List<Double> getRow(int index) {
        if (!rows.isEmpty() && index < rows.size()) {
            return rows.get(index).getElements();
        } else {
            return Collections.emptyList();
        }
    }

    public List<Double> getColumn(int index) {
        if (!rows.isEmpty()
                && !rows.get(0).getElements().isEmpty()
                && index < rows.get(0).getElements().size()) {
            return rows.stream()
                    .map(softMetricRow -> softMetricRow.getElements().get(index))
                    .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    public void setValue(int indexColumn, int indexRow, Double value) {
        rows.get(indexRow).getElements().set(indexColumn, value);
    }

    public boolean isEmpty() {
        return rows.isEmpty();
    }


    public void clear() {
        rows.clear();
    }

    private class SoftMetricRow {

        private final List<Double> elements = new ArrayList<>();

        private SoftMetricRow(List<Double> elements) {
            this.elements.addAll(elements);
        }

        private List<Double> getElements() {
            return elements;
        }
    }
}
