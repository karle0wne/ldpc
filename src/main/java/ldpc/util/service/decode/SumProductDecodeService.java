package ldpc.util.service.decode;

import ldpc.matrix.basis.BooleanMatrix;
import ldpc.matrix.wrapper.paritycheck.ParityCheckMatrix;
import ldpc.matrix.wrapper.paritycheck.wrapper.StrictLowDensityParityCheckMatrix;
import ldpc.service.basis.BooleanMatrixService;
import ldpc.service.wrapper.generating.GeneratingMatrixService;
import ldpc.util.service.CodeWordService;
import ldpc.util.template.CodeWord;
import ldpc.util.template.SoftMetric;
import ldpc.util.template.SoftMetricRepository;
import ldpc.util.template.TimeLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

@Service
public class SumProductDecodeService {

    public static final int BORDER_ITERATION = 100;

    private final CodeWordService codeWordService;
    private final BooleanMatrixService booleanMatrixService;
    private final GeneratingMatrixService generatingMatrixService;

    @Autowired
    public SumProductDecodeService(CodeWordService codeWordService, BooleanMatrixService booleanMatrixService, GeneratingMatrixService generatingMatrixService) {
        this.codeWordService = codeWordService;
        this.booleanMatrixService = booleanMatrixService;
        this.generatingMatrixService = generatingMatrixService;
    }

    BooleanMatrix decode(StrictLowDensityParityCheckMatrix matrixLDPC, CodeWord codeWord) {
        TimeLogger timeLogger = new TimeLogger("decode");
        ParityCheckMatrix parityCheckMatrix = matrixLDPC.getParityCheckMatrix();

        SoftMetricRepository lMatrix = new SoftMetricRepository();
        SoftMetricRepository zMatrix = new SoftMetricRepository();

        BooleanMatrix syndrome = getSyndrome(parityCheckMatrix, codeWord);

        CodeWord result = codeWordService.newCodeWord(codeWord);

        for (int i = 0; !isCorrect(syndrome) && i < BORDER_ITERATION; i++) {
            lMatrix.clear();

            fillLMatrix(codeWord, parityCheckMatrix, lMatrix);
            timeLogger.check();

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
            timeLogger.check();

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
            timeLogger.check();

            result.getSoftMetrics().clear();

            zMatrix.getMetricsByColumnByRow().forEach(
                    (column, map) -> {
                        double sum = map.values().stream()
                                .mapToDouble(SoftMetric::getMetric)
                                .sum();
                        result.getSoftMetrics().add(codeWord.getSoftMetrics().get(column) + sum);
                    }
            );
            timeLogger.check();

            syndrome = getSyndrome(parityCheckMatrix, codeWordService.newCodeWord(result));
        }

        return generatingMatrixService.recoveryBySwapHistory(codeWordService.getBooleanMatrix(codeWordService.newCodeWord(result)), false);
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
        BooleanMatrix code = codeWordService.getBooleanMatrix(codeWord);
        return booleanMatrixService.getTransposedBooleanMatrix(code);
    }
}
