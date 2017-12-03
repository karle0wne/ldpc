package ldpc;

import ldpc.matrix.basis.BooleanMatrix;
import ldpc.matrix.wrapper.generating.GeneratingMatrix;
import ldpc.matrix.wrapper.paritycheck.ParityCheckMatrix;
import ldpc.service.basis.BooleanMatrixService;
import ldpc.service.wrapper.generating.GeneratingMatrixService;
import ldpc.service.wrapper.paritycheck.ParityCheckMatrixService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
public class MainApplicationTest {

    @Autowired
    private BooleanMatrixService booleanMatrixService;

    @Autowired
    private GeneratingMatrixService generatingMatrixService;

    @Autowired
    private ParityCheckMatrixService parityCheckMatrixService;

    @Test
    public void test() {
        System.out.println("ТЕСТ ПРОЦЕССА КОДИРОВАНИЯ И ДЕКОДИРОВАНИЯ ПРИ ЗАДАННЫХ G, H");

        System.out.println("ПРОВЕРОЧНАЯ МАТРИЦА (H): ");
        ParityCheckMatrix parityCheckMatrix = parityCheckMatrixService.createPreparedParityCheckMatrix();
        booleanMatrixService.print(parityCheckMatrix.getBooleanMatrix());

        System.out.println("ПОРОЖДАЮЩАЯ МАТРИЦА (G): ");
        GeneratingMatrix generatingMatrix = generatingMatrixService.createPreparedGeneratingMatrix();
        booleanMatrixService.print(generatingMatrix.getBooleanMatrix());

        System.out.println("ИНФОРМАЦИОННОЕ СЛОВО (word):");
        BooleanMatrix informationWord = booleanMatrixService.createPreparedInformationWord();
        booleanMatrixService.print(informationWord);

        System.out.println("КОДОВОЕ СЛОВО (word*G): ");
        BooleanMatrix codeWord = booleanMatrixService.multiplicationMatrix(informationWord, generatingMatrix.getBooleanMatrix());
        booleanMatrixService.print(codeWord);

        System.out.println("---------------");
        System.out.println("ТИПА ПЕРЕДАЧА ПО КАНАЛУ!");
        System.out.println("---------------");

        System.out.println("ТРАНСПОНИРОВАНИЕ ПОЛУЧЕННОГО СООБЩЕНИЯ transposed(word*G): ");
        BooleanMatrix transposedCodeWord = booleanMatrixService.getTransposedBooleanMatrix(codeWord);
        booleanMatrixService.print(transposedCodeWord);

        System.out.println("СИНДРОМ ПРОВЕРКИ H*transposed(word*G): ");
        BooleanMatrix syndrome = booleanMatrixService.multiplicationMatrix(parityCheckMatrix.getBooleanMatrix(), transposedCodeWord);
        booleanMatrixService.print(syndrome);
    }

    @Test
    public void test2() {
        System.out.println("1) ТЕСТ ПРОЦЕССА КОДИРОВАНИЯ И ДЕКОДИРОВАНИЯ ПРИ ЗАДАННОЙ H");

        System.out.println("ПРОВЕРОЧНАЯ МАТРИЦА (H): ");
        ParityCheckMatrix preparedParityCheckMatrix = parityCheckMatrixService.createPreparedParityCheckMatrix();
        booleanMatrixService.print(preparedParityCheckMatrix.getBooleanMatrix());

        System.out.println("ПОРОЖДАЮЩАЯ МАТРИЦА (G): ");
        GeneratingMatrix generatingMatrixFromParityCheckMatrix1 = generatingMatrixService.getGeneratingMatrixFromParityCheckMatrix(preparedParityCheckMatrix);
        booleanMatrixService.print(generatingMatrixFromParityCheckMatrix1.getBooleanMatrix());

        System.out.println("ИНФОРМАЦИОННОЕ СЛОВО (word):");
        BooleanMatrix informationWord = booleanMatrixService.createPreparedInformationWord();
        booleanMatrixService.print(informationWord);

        System.out.println("КОДОВОЕ СЛОВО (word*G): ");
        BooleanMatrix codeWord = booleanMatrixService.multiplicationMatrix(informationWord, generatingMatrixFromParityCheckMatrix1.getBooleanMatrix());
        booleanMatrixService.print(codeWord);

        System.out.println("---------------");
        System.out.println("ТИПА ПЕРЕДАЧА ПО КАНАЛУ!");
        System.out.println("---------------");

        System.out.println("ТРАНСПОНИРОВАНИЕ ПОЛУЧЕННОГО СООБЩЕНИЯ transposed(word*G): ");
        BooleanMatrix transposedCodeWord = booleanMatrixService.getTransposedBooleanMatrix(codeWord);
        booleanMatrixService.print(transposedCodeWord);

        System.out.println("СИНДРОМ ПРОВЕРКИ H*transposed(word*G): ");
        BooleanMatrix syndrome = booleanMatrixService.multiplicationMatrix(preparedParityCheckMatrix.getBooleanMatrix(), transposedCodeWord);
        booleanMatrixService.print(syndrome);

        //--------------------------------------------------------------------------------------------------------------
        System.out.println("2) ТЕСТ ПРОЦЕССА КОДИРОВАНИЯ И ДЕКОДИРОВАНИЯ ПРИ ЗАДАННОЙ H");

        System.out.println("ПРОВЕРОЧНАЯ МАТРИЦА (H): ");
        ParityCheckMatrix prepared2ParityCheckMatrix = parityCheckMatrixService.createPrepared2ParityCheckMatrix();
        booleanMatrixService.print(prepared2ParityCheckMatrix.getBooleanMatrix());

        System.out.println("ПОРОЖДАЮЩАЯ МАТРИЦА (G): ");
        GeneratingMatrix generating2MatrixFromParityCheckMatrix1 = generatingMatrixService.getGeneratingMatrixFromParityCheckMatrix(prepared2ParityCheckMatrix);
        booleanMatrixService.print(generating2MatrixFromParityCheckMatrix1.getBooleanMatrix());

        System.out.println("ИНФОРМАЦИОННОЕ СЛОВО (word):");
        BooleanMatrix informationWord2 = booleanMatrixService.createPrepared2InformationWord();
        booleanMatrixService.print(informationWord2);

        System.out.println("КОДОВОЕ СЛОВО (word*G): ");
        BooleanMatrix codeWord2 = booleanMatrixService.multiplicationMatrix(informationWord2, generating2MatrixFromParityCheckMatrix1.getBooleanMatrix());
        booleanMatrixService.print(codeWord2);

        System.out.println("---------------");
        System.out.println("ТИПА ПЕРЕДАЧА ПО КАНАЛУ!");
        System.out.println("---------------");

        System.out.println("ТРАНСПОНИРОВАНИЕ ПОЛУЧЕННОГО СООБЩЕНИЯ transposed(word*G): ");
        BooleanMatrix transposedCodeWord2 = booleanMatrixService.getTransposedBooleanMatrix(codeWord2);
        booleanMatrixService.print(transposedCodeWord2);

        System.out.println("СИНДРОМ ПРОВЕРКИ H*transposed(word*G): ");
        BooleanMatrix syndrome2 = booleanMatrixService.multiplicationMatrix(prepared2ParityCheckMatrix.getBooleanMatrix(), transposedCodeWord2);
        booleanMatrixService.print(syndrome2);
    }
}
