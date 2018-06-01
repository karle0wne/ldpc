package ldpc.util.template;

import java.util.List;
import java.util.stream.Collectors;

public class CodeWord {

    private List<Double> softMetrics;

    public CodeWord(List<Double> softMetrics) {
        this.softMetrics = softMetrics;
    }

    public List<Double> getSoftMetrics() {
        return softMetrics;
    }

    public List<Boolean> getHardMetrics() {
        return softMetrics.stream()
                .map(element -> element < 0.0D)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return softMetrics.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
    }
}
