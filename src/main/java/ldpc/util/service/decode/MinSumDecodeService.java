package ldpc.util.service.decode;

import ldpc.matrix.basis.BooleanMatrix;
import ldpc.matrix.wrapper.paritycheck.ParityCheckMatrix;
import ldpc.matrix.wrapper.paritycheck.wrapper.StrictLowDensityParityCheckMatrix;
import ldpc.service.basis.BooleanMatrixService;
import ldpc.service.basis.ColumnService;
import ldpc.service.basis.RowService;
import ldpc.util.service.CodeWordService;
import ldpc.util.template.CodeWord;
import ldpc.util.template.SoftMetric;
import ldpc.util.template.SoftMetricMatrix;
import ldpc.util.template.SoftMetricRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class MinSumDecodeService {

    public static final int BORDER_ITERATION = 100;
    private final BooleanMatrixService booleanMatrixService;

    private final RowService rowService;

    @Autowired
    private CodeWordService codeWordService;

    @Autowired
    private ColumnService columnService;

    @Autowired
    public MinSumDecodeService(BooleanMatrixService booleanMatrixService, RowService rowService) {
        this.booleanMatrixService = booleanMatrixService;
        this.rowService = rowService;
    }

    boolean dummy(StrictLowDensityParityCheckMatrix matrixLDPC, CodeWord codeWord) {
        ParityCheckMatrix parityCheckMatrix = matrixLDPC.getParityCheckMatrix();
        return isCorrect(getSyndrome(parityCheckMatrix, codeWord));
    }

    boolean decode(StrictLowDensityParityCheckMatrix matrixLDPC, CodeWord codeWord) {
        ParityCheckMatrix parityCheckMatrix = matrixLDPC.getParityCheckMatrix();

        SoftMetricMatrix lMatrix = new SoftMetricMatrix();
        SoftMetricMatrix zMatrix = new SoftMetricMatrix();
        CodeWord decode = codeWordService.newCodeWord(codeWord.getSoftMetrics());

        BooleanMatrix syndrome = getSyndrome(parityCheckMatrix, decode);

        for (int i = 0; !isCorrect(syndrome) && i < BORDER_ITERATION; i++) {
            lMatrix.clear();

            fillLMatrix(codeWord, parityCheckMatrix, lMatrix);

            if (!zMatrix.isEmpty()) {
                IntStream.range(0, zMatrix.xSize())
                        .forEach(
                                j -> {
                                    double sum = zMatrix.getColumn(j).getAll().stream()
                                            .mapToDouble(SoftMetric::getMetric)
                                            .sum();
                                    IntStream.range(0, zMatrix.ySize())
                                            .forEach(
                                                    k -> {
                                                        Double l = lMatrix.getColumn(j).get(k).getMetric();
                                                        Double z = zMatrix.getColumn(j).get(k).getMetric();
                                                        lMatrix.setValue(j, k, l + sum - z);
                                                    }
                                            );
                                }
                        );
            }

            zMatrix.clear();

            IntStream.range(0, lMatrix.ySize())
                    .forEach(
                            j -> {
                                SoftMetricRow softMetricRow = new SoftMetricRow();

                                double sum = lMatrix.getRow(j).getAll().stream()
                                        .map(softMetric -> getHyperbolicArcTan(softMetric.getMetric()))
                                        .mapToDouble(value -> value)
                                        .sum();

                                Double signSum = lMatrix.getRow(j).getAll().stream()
                                        .map(SoftMetric::getSign)
                                        .reduce((a, b) -> a * b)
                                        .orElse(0.0D);

                                IntStream.range(0, lMatrix.xSize())
                                        .forEach(
                                                k -> {
                                                    SoftMetric softMetric = lMatrix.getRow(j).get(k);
                                                    Double value = getHyperbolicArcTan(sum - getHyperbolicArcTan(softMetric.getMetric()));
                                                    double a = signSum * softMetric.getSign();
                                                    softMetricRow.addSoftMetric(new SoftMetric(softMetric.getColumn(), softMetric.getRow(), Math.max(Math.min(a * value, 19.07D), -19.07D)));
                                                }
                                        );
                                zMatrix.addRow(softMetricRow);
                            }
                    );

            Map<Integer, List<SoftMetric>> softMetricsByColumn = zMatrix.getAll().stream()
                    .flatMap(row -> row.getAll().stream())
                    .collect(
                            Collectors.groupingBy(
                                    SoftMetric::getColumn,
                                    Collectors.mapping(softMetric -> softMetric, Collectors.toList())
                            )
                    );

            IntStream.range(0, decode.getSoftMetrics().size())
                    .forEach(
                            j -> {
                                double sum = softMetricsByColumn.get(j).stream()
                                        .mapToDouble(SoftMetric::getMetric)
                                        .sum();
                                decode.getSoftMetrics().set(j, decode.getSoftMetrics().get(j) + sum);
                            }
                    );

            syndrome = getSyndrome(parityCheckMatrix, decode);
        }

        return isCorrect(syndrome);
    }

    private Double getHyperbolicArcTan(Double value) {
        double exp = Math.exp(Math.abs(value));
        return Math.log((exp + 1) / (exp - 1));
    }

    private void fillLMatrix(CodeWord codeWord, ParityCheckMatrix parityCheckMatrix, SoftMetricMatrix lMatrix) {
        IntStream.range(0, parityCheckMatrix.getBooleanMatrix().getSizeY())
                .forEach(
                        rowId -> {
                            SoftMetricRow softMetricRow = new SoftMetricRow();
                            List<Integer> columnIds = booleanMatrixService.getPositionsTrueElements(parityCheckMatrix.getBooleanMatrix().getMatrix().get(rowId).getElements());
                            columnIds.forEach(columnId -> softMetricRow.addSoftMetric(new SoftMetric(columnId, rowId, codeWord.getSoftMetrics().get(columnId))));
                            lMatrix.addRow(softMetricRow);
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
