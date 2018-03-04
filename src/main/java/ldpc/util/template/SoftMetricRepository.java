package ldpc.util.template;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SoftMetricRepository {

    private final List<SoftMetric> softMetrics = new ArrayList<>();

    public void addMetric(SoftMetric softMetric) {
        softMetrics.add(softMetric);
    }

    public void setMetric(int indexColumn, int indexRow, Double metric) {
        getMetricsByColumnByRow().get(indexColumn).get(indexRow).setMetric(metric);
    }

    public Double getMetric(int indexColumn, int indexRow) {
        return getMetricsByColumnByRow().get(indexColumn).get(indexRow).getMetric();
    }

    public Map<Integer, Map<Integer, SoftMetric>> getMetricsByColumnByRow() {
        return softMetrics.stream()
                .collect(
                        Collectors.groupingBy(
                                SoftMetric::getColumn,
                                Collectors.mapping(
                                        softMetric -> softMetric,
                                        Collectors.toMap(
                                                SoftMetric::getRow,
                                                softMetric -> softMetric
                                        )
                                )
                        )
                );
    }

    public Map<Integer, Map<Integer, SoftMetric>> getMetricsByRowByColumn() {
        return softMetrics.stream()
                .collect(
                        Collectors.groupingBy(
                                SoftMetric::getRow,
                                Collectors.mapping(
                                        softMetric -> softMetric,
                                        Collectors.toMap(
                                                SoftMetric::getColumn,
                                                softMetric -> softMetric
                                        )
                                )
                        )
                );
    }

    public boolean isEmpty() {
        return softMetrics.isEmpty();
    }

    public void clear() {
        softMetrics.clear();
    }

}
