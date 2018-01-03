package ldpc.util.service.decode;

import ldpc.matrix.basis.BooleanMatrix;
import ldpc.matrix.wrapper.paritycheck.ParityCheckMatrix;
import ldpc.matrix.wrapper.paritycheck.wrapper.StrictLowDensityParityCheckMatrix;
import ldpc.service.basis.BooleanMatrixService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static ldpc.service.wrapper.generating.GeneratingMatrixService.BORDER_FOR_EXCEPTION;

@Service
public class MinSumDecodeService {

    private final BooleanMatrixService booleanMatrixService;

    @Autowired
    public MinSumDecodeService(BooleanMatrixService booleanMatrixService) {
        this.booleanMatrixService = booleanMatrixService;
    }

    public BooleanMatrix decode(StrictLowDensityParityCheckMatrix matrixLDPC, BooleanMatrix codeWord) {
        ParityCheckMatrix parityCheckMatrix = matrixLDPC.getParityCheckMatrix();

        BooleanMatrix localWord = booleanMatrixService.getTransposedBooleanMatrix(booleanMatrixService.newMatrix(codeWord));
        BooleanMatrix syndrome = booleanMatrixService.multiplicationMatrix(parityCheckMatrix.getBooleanMatrix(), localWord);
        int iterator = 0;

        while (booleanMatrixService.getCountTrueElements(booleanMatrixService.getTransposedBooleanMatrix(syndrome).getMatrix().get(0).getElements()) > 0) {
            // TODO: 03.01.2018 here is not logic(
            iterator = checkIterator(iterator);
            syndrome = booleanMatrixService.multiplicationMatrix(parityCheckMatrix.getBooleanMatrix(), localWord);
        }
        return booleanMatrixService.newMatrix(localWord);
    }


    private int checkIterator(int iterator) {
        if (iterator < BORDER_FOR_EXCEPTION) {
            iterator++;
        } else {
            throw new RuntimeException("Прошло " + BORDER_FOR_EXCEPTION + " циклов декодирования!");
        }
        return iterator;
    }

}
