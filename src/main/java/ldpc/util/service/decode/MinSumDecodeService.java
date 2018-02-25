package ldpc.util.service.decode;

import ldpc.matrix.basis.BooleanMatrix;
import ldpc.matrix.basis.Column;
import ldpc.matrix.basis.SoftMetricMatrix;
import ldpc.matrix.wrapper.paritycheck.ParityCheckMatrix;
import ldpc.matrix.wrapper.paritycheck.wrapper.StrictLowDensityParityCheckMatrix;
import ldpc.service.basis.BooleanMatrixService;
import ldpc.service.basis.ColumnService;
import ldpc.service.basis.RowService;
import ldpc.util.service.CodeWordService;
import ldpc.util.template.CodeWord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
                IntStream.range(0, zMatrix.columnsSize())
                        .forEach(
                                j -> {
                                    double sum = zMatrix.getColumn(j).stream()
                                            .mapToDouble(value -> value)
                                            .sum();
                                    IntStream.range(0, zMatrix.rowsSize())
                                            .forEach(
                                                    k -> {
                                                        Double l = lMatrix.getColumn(j).get(k);
                                                        Double z = zMatrix.getColumn(j).get(k);
                                                        lMatrix.setValue(j, k, l + sum - z);
                                                    }
                                            );
                                }
                        );
            }

            List<List<Double>> sign = lMatrix.getAll().stream()
                    .map(values -> values.stream().map(aDouble -> aDouble / Math.abs(aDouble)).collect(Collectors.toList()))
                    .collect(Collectors.toList());

            zMatrix.clear();

            IntStream.range(0, lMatrix.rowsSize())
                    .forEach(
                            j -> {
                                List<Double> values = new ArrayList<>();

                                double sum = lMatrix.getRow(j).stream()
                                        .map(this::getHyperbolicArctg)
                                        .mapToDouble(value -> value)
                                        .sum();

                                Double signSum = sign.get(j).stream().reduce((a, b) -> a * b).orElse(0.0D);

                                IntStream.range(0, lMatrix.columnsSize())
                                        .forEach(
                                                k -> {
                                                    Double value = getHyperbolicArctg(sum - getHyperbolicArctg(lMatrix.getRow(j).get(k)));
                                                    double a = signSum * sign.get(j).get(k);
                                                    values.add(Math.max(Math.min(a * value, 19.07D), -19.07D));
                                                }
                                        );
                                zMatrix.addRow(values);
                            }
                    );

            IntStream.range(0, decode.getSoftMetrics().size())
                    .forEach(
                            j -> {
                                Column columnByIndex = columnService.getColumnByIndex(parityCheckMatrix.getBooleanMatrix(), j);
                                List<Integer> positionsTrueElements = booleanMatrixService.getPositionsTrueElements(columnByIndex.getElements());
                                List<Integer> positionsTrueElements1 = booleanMatrixService.getPositionsTrueElements(parityCheckMatrix.getBooleanMatrix().getMatrix().get(positionsTrueElements.get(0)).getElements());
                                int i1 = IntStream.range(0, positionsTrueElements1.size())
                                        .filter(value -> positionsTrueElements1.get(value) == j)
                                        .findFirst()
                                        .orElse(-1);
                                double sum = positionsTrueElements.stream()
                                        .mapToDouble(integer -> zMatrix.getColumn(i1).get(integer))
                                        .sum();
                                decode.getSoftMetrics().set(j, decode.getSoftMetrics().get(j) + sum);
                            }
                    );

            syndrome = getSyndrome(parityCheckMatrix, decode);
        }

        return isCorrect(syndrome);
    }

    private Double getHyperbolicArctg(Double value) {
        double exp = Math.exp(Math.abs(value));
        return Math.log((exp + 1) / (exp - 1));
    }

    private void fillLMatrix(CodeWord codeWord, ParityCheckMatrix parityCheckMatrix, SoftMetricMatrix lMatrix) {
        parityCheckMatrix.getBooleanMatrix().getMatrix().stream()
                .map(row -> booleanMatrixService.getPositionsTrueElements(row.getElements()))
                .map(ids -> ids.stream().map(id -> codeWord.getSoftMetrics().get(id)).collect(Collectors.toList()))
                .forEach(lMatrix::addRow);
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
