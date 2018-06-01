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

@Service
public class CodeWordService {

    private final BooleanMatrixService booleanMatrixService;

    private final RowService rowService;

    @Autowired
    public CodeWordService(BooleanMatrixService booleanMatrixService, RowService rowService) {
        this.booleanMatrixService = booleanMatrixService;
        this.rowService = rowService;
    }

    public boolean isRange(double value, double left, double right) {
        return left < value && value < right;
    }

    public BooleanMatrix getBooleanMatrix(CodeWord codeWord) {
        return booleanMatrixService.newMatrix(Collections.singletonList(rowService.newRow(codeWord.getHardMetrics())));
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

}
