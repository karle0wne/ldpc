package ldpc.util.service.decode;

import ldpc.matrix.basis.BooleanMatrix;
import ldpc.matrix.wrapper.paritycheck.ParityCheckMatrix;
import ldpc.matrix.wrapper.paritycheck.wrapper.StrictLowDensityParityCheckMatrix;
import ldpc.service.basis.BooleanMatrixService;
import ldpc.service.basis.RowService;
import ldpc.util.template.CodeWord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MinSumDecodeService {

    public static final int BORDER_ITERATION = 100;
    private final BooleanMatrixService booleanMatrixService;

    private final RowService rowService;

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

        BooleanMatrix syndrome = getSyndrome(parityCheckMatrix, codeWord);

        for (int i = 0; !isCorrect(syndrome) && i < BORDER_ITERATION; i++) {
            // TODO: изменяешь значения массива double в CodeWord
            syndrome = getSyndrome(parityCheckMatrix, codeWord);
        }

        return isCorrect(syndrome);
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
        return element > 0.5;
    }

}
