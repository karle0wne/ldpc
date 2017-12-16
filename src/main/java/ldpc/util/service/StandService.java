package ldpc.util.service;

import ldpc.matrix.basis.BooleanMatrix;
import ldpc.matrix.wrapper.generating.GeneratingMatrix;
import ldpc.matrix.wrapper.paritycheck.ParityCheckMatrix;
import ldpc.matrix.wrapper.paritycheck.wrapper.StrictLowDensityParityCheckMatrix;
import ldpc.service.basis.BooleanMatrixService;
import ldpc.service.wrapper.generating.GeneratingMatrixService;
import ldpc.service.wrapper.paritycheck.ParityCheckMatrixService;
import ldpc.service.wrapper.paritycheck.wrapper.LDPCMatrixService;
import ldpc.util.template.ColumnPair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StandService {

    private static final String DELIMITER = "\n";

    private final BooleanMatrixService booleanMatrixService;

    private final GeneratingMatrixService generatingMatrixService;

    private final ParityCheckMatrixService parityCheckMatrixService;

    private final ColumnPairService columnPairService;

    private final LDPCMatrixService ldpcMatrixService;

    @Autowired
    public StandService(BooleanMatrixService booleanMatrixService, GeneratingMatrixService generatingMatrixService, ParityCheckMatrixService parityCheckMatrixService, ColumnPairService columnPairService, LDPCMatrixService ldpcMatrixService) {
        this.booleanMatrixService = booleanMatrixService;
        this.generatingMatrixService = generatingMatrixService;
        this.parityCheckMatrixService = parityCheckMatrixService;
        this.columnPairService = columnPairService;
        this.ldpcMatrixService = ldpcMatrixService;
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

        BooleanMatrix brokenCodeWord = booleanMatrixService.breakDownCodeWordWithGaussianNoise(recoveryCodeWord);

        BooleanMatrix decodedCodeWord = ldpcMatrixService.decode(matrix, recoveryCodeWord);

        BooleanMatrix syndrome = booleanMatrixService.multiplicationMatrix(parityCheckMatrix.getBooleanMatrix(), decodedCodeWord);

        showToConsole(matrix, generatingMatrix, swapHistory, informationWord, recoveryCodeWord, brokenCodeWord, decodedCodeWord, syndrome);
    }

    private void showToConsole(StrictLowDensityParityCheckMatrix matrix,
                               GeneratingMatrix generatingMatrix,
                               List<ColumnPair> swapHistory,
                               BooleanMatrix informationWord,
                               BooleanMatrix recoveryCodeWord,
                               BooleanMatrix brokenCodeWord,
                               BooleanMatrix decodedCodeWord,
                               BooleanMatrix syndrome) {
        System.out.println(generatingMatrix.toString() + DELIMITER);

        System.out.println("ИНФОРМАЦИОННОЕ СЛОВО:");
        System.out.println(informationWord.getMatrix().get(0).toString() + DELIMITER);

        if (!swapHistory.isEmpty()) {
            System.out.println(columnPairService.arrayToString(swapHistory) + DELIMITER);
        }

        System.out.println("ВОССТАНОВЛЕННОЕ С ПОМОЩЬЮ ИСТОРИИ ПЕРЕСТАНОВОК КОДОВОЕ СЛОВО: ");
        System.out.println(recoveryCodeWord.getMatrix().get(0).toString() + DELIMITER);

        System.out.println("---------------");
        System.out.println("ТИПА ПЕРЕДАЧА ПО КАНАЛУ...");
        System.out.println("ВНЕСЕНИЕ ОШИБОК В КОДОВОЕ СЛОВО");
        System.out.println("---------------" + DELIMITER);

        System.out.println(matrix.toString() + DELIMITER);

        System.out.println("КОДОВОЕ СООБЩЕНИЕ, КОТОРОЕ ПРИШЛО ИЗ КАНАЛА: ");
        System.out.println(brokenCodeWord.getMatrix().get(0).toString() + DELIMITER);

        System.out.println("---------------");
        System.out.println("ДЕКОДИРОВАНИЕ ПОЛУЧЕННОГО СООБЩЕНИЯ...");
        System.out.println("---------------" + DELIMITER);

        System.out.println("ДЕКОДИРОВАННОЕ КОДОВОЕ СЛОВО:");
        System.out.println(booleanMatrixService.getTransposedBooleanMatrix(decodedCodeWord).getMatrix().get(0).toString() + DELIMITER);

        System.out.println("ПЕРЕМНОЖЕНИЕ {ТРАНСПОНИРОВАННОГО ДЕКОДИРОВАННОГО СООБЩЕНИЯ} НА {ПРОВЕРОЧНУЮ МАТРИЦУ} (и транспонирование для удобства)");
        System.out.println(booleanMatrixService.getTransposedBooleanMatrix(syndrome).getMatrix().get(0).toString() + DELIMITER);
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

    private void showToConsole(ParityCheckMatrix parityCheckMatrix,
                               GeneratingMatrix generatingMatrix,
                               List<ColumnPair> swapHistory,
                               BooleanMatrix informationWord,
                               BooleanMatrix recoveryCodeWord,
                               BooleanMatrix syndrome) {
        System.out.println(generatingMatrix.toString() + DELIMITER);

        System.out.println("ИНФОРМАЦИОННОЕ СЛОВО:");
        System.out.println(informationWord.getMatrix().get(0).toString() + DELIMITER);

        if (!swapHistory.isEmpty()) {
            System.out.println(columnPairService.arrayToString(swapHistory) + DELIMITER);
        }

        System.out.println("ВОССТАНОВЛЕННОЕ С ПОМОЩЬЮ ИСТОРИИ ПЕРЕСТАНОВОК КОДОВОЕ СЛОВО: ");
        System.out.println(recoveryCodeWord.getMatrix().get(0).toString() + DELIMITER);

        System.out.println("---------------");
        System.out.println("ТИПА ПЕРЕДАЧА ПО КАНАЛУ!");
        System.out.println("---------------" + DELIMITER);

        System.out.println(parityCheckMatrix.toString() + DELIMITER);

        System.out.println("ПЕРЕМНОЖЕНИЕ ТРАНСПОНИРОВАННОГО СООБЩЕНИЯ НА ПРОВЕРОЧНУЮ МАТРИЦУ (и транспонирование для удобства)");
        System.out.println(booleanMatrixService.getTransposedBooleanMatrix(syndrome).getMatrix().get(0).toString() + DELIMITER);
    }
}
