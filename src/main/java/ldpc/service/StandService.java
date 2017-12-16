package ldpc.service;

import ldpc.matrix.basis.BooleanMatrix;
import ldpc.matrix.wrapper.generating.GeneratingMatrix;
import ldpc.matrix.wrapper.paritycheck.ParityCheckMatrix;
import ldpc.matrix.wrapper.paritycheck.wrapper.StrictLowDensityParityCheckMatrix;
import ldpc.service.basis.BooleanMatrixService;
import ldpc.service.wrapper.generating.GeneratingMatrixService;
import ldpc.service.wrapper.paritycheck.ParityCheckMatrixService;
import ldpc.util.service.ColumnPairService;
import ldpc.util.template.ColumnPair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StandService {

    public static final String DELIMETER = "\n";
    private final BooleanMatrixService booleanMatrixService;

    private final GeneratingMatrixService generatingMatrixService;

    private final ParityCheckMatrixService parityCheckMatrixService;
    @Autowired
    private ColumnPairService columnPairService;

    @Autowired
    public StandService(BooleanMatrixService booleanMatrixService, GeneratingMatrixService generatingMatrixService, ParityCheckMatrixService parityCheckMatrixService) {
        this.booleanMatrixService = booleanMatrixService;
        this.generatingMatrixService = generatingMatrixService;
        this.parityCheckMatrixService = parityCheckMatrixService;
    }

    public void demoStandLDPC(StrictLowDensityParityCheckMatrix matrix) {
        List<ColumnPair> swapHistory = new ArrayList<>();
        ParityCheckMatrix parityCheckMatrix = matrix.getParityCheckMatrix();

        GeneratingMatrix generatingMatrix = generatingMatrixService.getGeneratingMatrixFromParityCheckMatrix(
                parityCheckMatrixService.newParityCheckMatrix(parityCheckMatrix),
                swapHistory
        );

        BooleanMatrix informationWord = booleanMatrixService.generateInfoWord(generatingMatrix.getBooleanMatrix().getSizeY());

        BooleanMatrix recoveryCodeWord = booleanMatrixService.recoveryCodeWord(
                booleanMatrixService.multiplicationMatrix(informationWord, generatingMatrix.getBooleanMatrix()),
                swapHistory
        );

        BooleanMatrix syndrome = booleanMatrixService.multiplicationMatrix(
                parityCheckMatrix.getBooleanMatrix(),
                booleanMatrixService.getTransposedBooleanMatrix(recoveryCodeWord)
        );

        showToConsole(matrix, generatingMatrix, swapHistory, informationWord, recoveryCodeWord, syndrome);
    }

    private void showToConsole(StrictLowDensityParityCheckMatrix matrix, GeneratingMatrix generatingMatrix, List<ColumnPair> swapHistory, BooleanMatrix informationWord, BooleanMatrix recoveryCodeWord, BooleanMatrix syndrome) {
        showFirstPart(generatingMatrix, swapHistory, informationWord, recoveryCodeWord);
        System.out.println(matrix.toString() + DELIMETER);
        showSecondPart(syndrome);
    }

    public void demoStandWithoutLDPC(ParityCheckMatrix parityCheckMatrix) {
        List<ColumnPair> swapHistory = new ArrayList<>();

        GeneratingMatrix generatingMatrix = generatingMatrixService.getGeneratingMatrixFromParityCheckMatrix(
                parityCheckMatrixService.newParityCheckMatrix(parityCheckMatrix),
                swapHistory
        );

        BooleanMatrix informationWord = booleanMatrixService.generateInfoWord(generatingMatrix.getBooleanMatrix().getSizeY());

        BooleanMatrix recoveryCodeWord = booleanMatrixService.recoveryCodeWord(
                booleanMatrixService.multiplicationMatrix(informationWord, generatingMatrix.getBooleanMatrix()),
                swapHistory
        );

        BooleanMatrix syndrome = booleanMatrixService.multiplicationMatrix(
                parityCheckMatrix.getBooleanMatrix(),
                booleanMatrixService.getTransposedBooleanMatrix(recoveryCodeWord)
        );

        showToConsole(parityCheckMatrix, generatingMatrix, swapHistory, informationWord, recoveryCodeWord, syndrome);
    }

    private void showToConsole(ParityCheckMatrix parityCheckMatrix, GeneratingMatrix generatingMatrix, List<ColumnPair> swapHistory, BooleanMatrix informationWord, BooleanMatrix recoveryCodeWord, BooleanMatrix syndrome) {
        showFirstPart(generatingMatrix, swapHistory, informationWord, recoveryCodeWord);
        System.out.println(parityCheckMatrix.toString() + DELIMETER);
        showSecondPart(syndrome);
    }

    private void showFirstPart(GeneratingMatrix generatingMatrix, List<ColumnPair> swapHistory, BooleanMatrix informationWord, BooleanMatrix recoveryCodeWord) {
        System.out.println(generatingMatrix.toString() + DELIMETER);

        System.out.println("ИНФОРМАЦИОННОЕ СЛОВО:");
        System.out.println(informationWord.getMatrix().get(0).toString() + DELIMETER);

        if (!swapHistory.isEmpty()) {
            System.out.println(columnPairService.arrayToString(swapHistory) + DELIMETER);
        }

        System.out.println("ВОССТАНОВЛЕННОЕ С ПОМОЩЬЮ ИСТОРИИ ПЕРЕСТАНОВОК КОДОВОЕ СЛОВО: ");
        System.out.println(recoveryCodeWord.getMatrix().get(0).toString() + DELIMETER);

        System.out.println("---------------");
        System.out.println("ТИПА ПЕРЕДАЧА ПО КАНАЛУ!");
        System.out.println("---------------");
    }

    private void showSecondPart(BooleanMatrix syndrome) {
        System.out.println("---------------");
        System.out.println("ТРАНСПОНИРОВАНИЕ ПОЛУЧЕННОГО СООБЩЕНИЯ");
        System.out.println("---------------");

        System.out.println("ПЕРЕМНОЖЕНИЕ ТРАНСПОНИРОВАННОГО СООБЩЕНИЯ НА ПРОВЕРОЧНУЮ МАТРИЦУ (и транспонирование для удобства)");
        System.out.println(booleanMatrixService.getTransposedBooleanMatrix(syndrome).getMatrix().get(0).toString() + DELIMETER);
    }
}
