package ldpc.util.template;

import java.util.ArrayList;
import java.util.List;

public class SoftMetricRow {

    private final List<SoftMetric> softMetrics = new ArrayList<>();

    public void addSoftMetric(SoftMetric softMetric) {
        this.softMetrics.add(softMetric);
    }

    public SoftMetric get(int index) {
        return softMetrics.get(index);
    }

    public List<SoftMetric> getAll() {
        return softMetrics;
    }

    public int size() {
        return softMetrics.size();
    }
}
