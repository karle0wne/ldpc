package ldpc.util.template;

import java.util.ArrayList;
import java.util.List;

public class SoftMetricMatrix {

    private final List<SoftMetricRow> rows = new ArrayList<>();

    public void addRow(SoftMetricRow row) {
        rows.add(row);
    }

    public int ySize() {
        return rows.size();
    }

    public int xSize() {
        return rows.get(0).size();
    }

    public List<SoftMetricRow> getAll() {
        return rows;
    }

    public SoftMetricRow getRow(int index) {
        return rows.get(index);
    }

    public SoftMetricRow getColumn(int index) {
        SoftMetricRow softMetricRowTemp = new SoftMetricRow();
        rows.stream()
                .map(softMetricRow -> softMetricRow.get(index))
                .forEach(softMetricRowTemp::addSoftMetric);
        return softMetricRowTemp;
    }

    public void setValue(int indexColumn, int indexRow, Double metric) {
        rows.get(indexRow).get(indexColumn).setMetric(metric);
    }

    public boolean isEmpty() {
        return rows.isEmpty();
    }


    public void clear() {
        rows.clear();
    }

}
