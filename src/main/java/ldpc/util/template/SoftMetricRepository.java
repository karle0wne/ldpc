package ldpc.util.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SoftMetricRepository {

    private final List<SoftMetric> softMetrics = new ArrayList<>();
    private final Map<Integer, Map<Integer, SoftMetric>> byColumnByRow = new HashMap<>();
    private final Map<Integer, Map<Integer, SoftMetric>> byRowByColumn = new HashMap<>();

    public void addMetric(SoftMetric softMetric) {
        softMetrics.add(softMetric);
    }

    public void createMaps() {
        byColumnByRow.putAll(
                softMetrics.stream()
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
                        )
        );
        byRowByColumn.putAll(
                softMetrics.stream()
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
                        )
        );
    }

    public void setMetric(int indexColumn, int indexRow, Double metric) {
        byColumnByRow.get(indexColumn).get(indexRow).setMetric(metric);
    }

    public Double getMetric(int indexColumn, int indexRow) {
        return byColumnByRow.get(indexColumn).get(indexRow).getMetric();
    }

    public Map<Integer, Map<Integer, SoftMetric>> getMetricsByColumnByRow() {
        return byColumnByRow;
    }

    public Map<Integer, Map<Integer, SoftMetric>> getMetricsByRowByColumn() {
        return byRowByColumn;
    }

    public boolean isEmpty() {
        return softMetrics.isEmpty();
    }

    public void clear() {
        softMetrics.clear();
        byRowByColumn.clear();
        byColumnByRow.clear();
    }

}
