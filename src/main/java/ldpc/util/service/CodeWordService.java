package ldpc.util.service;

import ldpc.matrix.basis.BooleanMatrix;
import ldpc.service.basis.BooleanMatrixService;
import ldpc.service.basis.RowService;
import ldpc.util.template.CodeWord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static java.lang.Math.log;

@Service
public class CodeWordService {

    private final BooleanMatrixService booleanMatrixService;

    private final RowService rowService;

    @Autowired
    public CodeWordService(BooleanMatrixService booleanMatrixService, RowService rowService) {
        this.booleanMatrixService = booleanMatrixService;
        this.rowService = rowService;
    }

    public double getSoftMetric(Random random, Boolean element) {
        if (element) {
            double softMetric = getSoftMetric(random);
            return log((1 - softMetric) / (softMetric));
        } else {
            double softMetric = getSoftMetric(random);
            return log((softMetric) / (1 - softMetric));
        }
    }

    public boolean isRange(double value, double left, double right) {
        return left < value && value < right;
    }

    public BooleanMatrix getBooleanMatrix(CodeWord codeWord) {
        List<Boolean> elements = codeWord.getSoftMetrics().stream()
                .map(this::getBoolean)
                .collect(Collectors.toList());
        return booleanMatrixService.newMatrix(Collections.singletonList(rowService.newRow(elements)));
    }

    public CodeWord newCodeWord(CodeWord codeWord) {
        return new CodeWord(newElements(codeWord.getSoftMetrics()));
    }

    public CodeWord newCodeWord(List<Double> softMetrics) {
        return new CodeWord(newElements(softMetrics));
    }

    private List<Double> newElements(List<Double> elements) {
        return new ArrayList<>(elements);
    }

    private double getSoftMetric(Random random) {
        double softMetric = random.nextDouble();
        while (!isRange(softMetric, 0.5D, 1.0D)) {
            softMetric = random.nextDouble();
        }
        return softMetric;
    }

    private Boolean getBoolean(Double element) {
        return element < 0.0D;
    }

}
