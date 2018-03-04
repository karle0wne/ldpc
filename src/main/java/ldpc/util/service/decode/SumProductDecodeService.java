package ldpc.util.service.decode;

import ldpc.matrix.basis.BooleanMatrix;
import ldpc.matrix.wrapper.paritycheck.ParityCheckMatrix;
import ldpc.matrix.wrapper.paritycheck.wrapper.StrictLowDensityParityCheckMatrix;
import ldpc.service.basis.BooleanMatrixService;
import ldpc.service.basis.RowService;
import ldpc.util.service.CodeWordService;
import ldpc.util.template.CodeWord;
import ldpc.util.template.SoftMetric;
import ldpc.util.template.SoftMetricRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class SumProductDecodeService {

    public static final int BORDER_ITERATION = 100;
    
    private final BooleanMatrixService booleanMatrixService;

    private final RowService rowService;

    private final CodeWordService codeWordService;

    @Autowired
    public SumProductDecodeService(BooleanMatrixService booleanMatrixService, RowService rowService, CodeWordService codeWordService) {
        this.booleanMatrixService = booleanMatrixService;
        this.rowService = rowService;
        this.codeWordService = codeWordService;
    }

    boolean dummy(StrictLowDensityParityCheckMatrix matrixLDPC, CodeWord codeWord) {
        ParityCheckMatrix parityCheckMatrix = matrixLDPC.getParityCheckMatrix();
        return isCorrect(getSyndrome(parityCheckMatrix, codeWord));
    }

    boolean decode(StrictLowDensityParityCheckMatrix matrixLDPC, CodeWord codeWord) {
        ParityCheckMatrix parityCheckMatrix = matrixLDPC.getParityCheckMatrix();

        SoftMetricRepository lMatrix = new SoftMetricRepository();
        SoftMetricRepository zMatrix = new SoftMetricRepository();

        BooleanMatrix syndrome = getSyndrome(parityCheckMatrix, codeWord);

        for (int i = 0; !isCorrect(syndrome) && i < BORDER_ITERATION; i++) {
            lMatrix.clear();

            fillLMatrix(codeWord, parityCheckMatrix, lMatrix);

            if (!zMatrix.isEmpty()) {
                zMatrix.getMetricsByColumnByRow().forEach(
                        (column, map) -> {
                            double sum = map.values().stream()
                                    .mapToDouble(SoftMetric::getMetric)
                                    .sum();

                            map.forEach(
                                    (row, softMetric) -> lMatrix.setMetric(column, row, lMatrix.getMetric(column, row) + sum - softMetric.getMetric())
                            );
                        }
                );
            }

            zMatrix.clear();

            lMatrix.getMetricsByRowByColumn().forEach(
                    (row, map) -> {
                        double sum = map.values().stream()
                                .map(softMetric -> getHyperbolicArcTan(softMetric.getMetric()))
                                .mapToDouble(value -> value)
                                .sum();

                        double signSum = map.values().stream()
                                .map(SoftMetric::getSign)
                                .reduce((a, b) -> a * b)
                                .orElse(0.0D);

                        map.forEach(
                                (column, softMetric) -> {
                                    double value = getHyperbolicArcTan(sum - getHyperbolicArcTan(softMetric.getMetric()));
                                    double a = signSum * softMetric.getSign();
                                    zMatrix.addMetric(new SoftMetric(column, row, Math.max(Math.min(a * value, 19.07D), -19.07D)));
                                }
                        );
                    }
            );

            List<Double> values = new ArrayList<>();

            zMatrix.getMetricsByColumnByRow().forEach(
                    (column, map) -> {
                        double sum = map.values().stream()
                                .mapToDouble(SoftMetric::getMetric)
                                .sum();
                        values.add(codeWord.getSoftMetrics().get(column) + sum);
                    }
            );

            syndrome = getSyndrome(parityCheckMatrix, codeWordService.newCodeWord(values));
        }

        return isCorrect(syndrome);
    }

    private Double getHyperbolicArcTan(Double value) {
        double exp = Math.exp(Math.abs(value));
        return Math.log((exp + 1) / (exp - 1));
    }

    private void fillLMatrix(CodeWord codeWord, ParityCheckMatrix parityCheckMatrix, SoftMetricRepository lMatrix) {
        IntStream.range(0, parityCheckMatrix.getBooleanMatrix().getSizeY())
                .forEach(
                        rowId -> {
                            List<Integer> columnIds = booleanMatrixService.getPositionsTrueElements(parityCheckMatrix.getBooleanMatrix().getMatrix().get(rowId).getElements());
                            columnIds.forEach(columnId -> lMatrix.addMetric(new SoftMetric(columnId, rowId, codeWord.getSoftMetrics().get(columnId))));
                        }
                );
    }

    private boolean isCorrect(BooleanMatrix syndrome) {
        return booleanMatrixService.getCountTrueElements(booleanMatrixService.getTransposedBooleanMatrix(syndrome).getMatrix().get(0).getElements()) == 0;
    }

    private BooleanMatrix getSyndrome(ParityCheckMatrix parityCheckMatrix, CodeWord codeWord) {
        BooleanMatrix localWord = getTransposed(codeWord);
        return booleanMatrixService.multiplicationMatrix(parityCheckMatrix.getBooleanMatrix(), localWord);
    }

    private BooleanMatrix getTransposed(CodeWord codeWord) {
        BooleanMatrix code = getBooleanMatrix(codeWord);
        return booleanMatrixService.getTransposedBooleanMatrix(code);
    }

    private BooleanMatrix getBooleanMatrix(CodeWord codeWord) {
        List<Boolean> elements = codeWord.getSoftMetrics().stream()
                .map(this::getBoolean)
                .collect(Collectors.toList());
        return booleanMatrixService.newMatrix(Collections.singletonList(rowService.newRow(elements)));
    }

    private Boolean getBoolean(Double element) {
        return element < 0.0D;
    }

}
