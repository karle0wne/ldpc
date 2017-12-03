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
        BooleanMatrix informationWord = booleanMatrixService.createPreparedInformationWord();
        System.out.println("ИНФОРМАЦИОННОЕ СЛОВО:");
        booleanMatrixService.print(informationWord);
        System.out.println("---------------");

        GeneratingMatrix generatingMatrix = generatingMatrixService.createPreparedGeneratingMatrix();
        System.out.println("ПОРОЖДАЮЩАЯ МАТРИЦА:");
        booleanMatrixService.print(generatingMatrix.getBooleanMatrix());
        System.out.println("---------------");

        ParityCheckMatrix parityCheckMatrix = parityCheckMatrixService.createPreparedParityCheckMatrix();
        System.out.println("ПРОВЕРОЧНАЯ МАТРИЦА:");
        booleanMatrixService.print(parityCheckMatrix.getBooleanMatrix());
        System.out.println("---------------");

        System.out.println("ПОЛУЧЕНИЕ КОДОВОГО СЛОВА");
        BooleanMatrix codeWord = booleanMatrixService.multiplicationMatrix(informationWord, generatingMatrix.getBooleanMatrix());
        System.out.println("---------------");

        System.out.println("КОДОВОЕ СЛОВО: ");
        booleanMatrixService.print(codeWord);
        System.out.println("---------------");

        System.out.println("ТИПА ПЕРЕДАЧА ПО КАНАЛУ!");
        System.out.println("---------------");

        System.out.println("ТРАНСПОНИРОВАНИЕ ПОЛУЧЕННОГО СООБЩЕНИЯ");
        BooleanMatrix transposedCodeWord = booleanMatrixService.getTransposedBooleanMatrix(codeWord);
        System.out.println("---------------");

        System.out.println("ПОЛУЧЕНИЕ СИНДРОМА");
        BooleanMatrix syndrome = booleanMatrixService.multiplicationMatrix(parityCheckMatrix.getBooleanMatrix(), transposedCodeWord);
        System.out.println("---------------");

        System.out.println("СИНДРОМ: ");
        booleanMatrixService.print(syndrome);
        System.out.println("---------------");
    }

    @Test
    public void test2() {
        System.out.println("1) ТЕСТ ПРОЦЕССА КОДИРОВАНИЯ И ДЕКОДИРОВАНИЯ ПРИ ЗАДАННОЙ H");
        System.out.println("проверочная матрица: ");
        ParityCheckMatrix preparedParityCheckMatrix = parityCheckMatrixService.createPreparedParityCheckMatrix();
        booleanMatrixService.print(preparedParityCheckMatrix.getBooleanMatrix());
        GeneratingMatrix generatingMatrixFromParityCheckMatrix1 = generatingMatrixService.getGeneratingMatrixFromParityCheckMatrix(preparedParityCheckMatrix);
        System.out.println("порождающая матрица: ");
        booleanMatrixService.print(generatingMatrixFromParityCheckMatrix1.getBooleanMatrix());

        BooleanMatrix informationWord = booleanMatrixService.createPreparedInformationWord();
        System.out.println("ИНФОРМАЦИОННОЕ СЛОВО:");
        booleanMatrixService.print(informationWord);
        System.out.println("---------------");

        System.out.println("ПОЛУЧЕНИЕ КОДОВОГО СЛОВА");
        BooleanMatrix codeWord = booleanMatrixService.multiplicationMatrix(informationWord, generatingMatrixFromParityCheckMatrix1.getBooleanMatrix());
        System.out.println("---------------");

        System.out.println("КОДОВОЕ СЛОВО: ");
        booleanMatrixService.print(codeWord);
        System.out.println("---------------");

        System.out.println("ТРАНСПОНИРОВАНИЕ ПОЛУЧЕННОГО СООБЩЕНИЯ");
        BooleanMatrix transposedCodeWord = booleanMatrixService.getTransposedBooleanMatrix(codeWord);
        System.out.println("---------------");

        System.out.println("ПОЛУЧЕНИЕ СИНДРОМА");
        BooleanMatrix syndrome = booleanMatrixService.multiplicationMatrix(preparedParityCheckMatrix.getBooleanMatrix(), transposedCodeWord);
        System.out.println("---------------");

        System.out.println("СИНДРОМ: ");
        booleanMatrixService.print(syndrome);
        System.out.println("---------------");


        System.out.println("2) ТЕСТ ПРОЦЕССА КОДИРОВАНИЯ И ДЕКОДИРОВАНИЯ ПРИ ЗАДАННОЙ H");
        System.out.println("проверочная матрица: ");
        ParityCheckMatrix prepared2ParityCheckMatrix = parityCheckMatrixService.createPrepared2ParityCheckMatrix();
        booleanMatrixService.print(prepared2ParityCheckMatrix.getBooleanMatrix());
        GeneratingMatrix generatingMatrixFromParityCheckMatrix2 = generatingMatrixService.getGeneratingMatrixFromParityCheckMatrix(prepared2ParityCheckMatrix);
        System.out.println("порождающая матрица: ");
        booleanMatrixService.print(generatingMatrixFromParityCheckMatrix2.getBooleanMatrix());

        BooleanMatrix informationWord1 = booleanMatrixService.createPrepared2InformationWord();
        System.out.println("ИНФОРМАЦИОННОЕ СЛОВО:");
        booleanMatrixService.print(informationWord1);
        System.out.println("---------------");

        System.out.println("ПОЛУЧЕНИЕ КОДОВОГО СЛОВА");
        BooleanMatrix codeWord1 = booleanMatrixService.multiplicationMatrix(informationWord1, generatingMatrixFromParityCheckMatrix2.getBooleanMatrix());
        System.out.println("---------------");

        System.out.println("КОДОВОЕ СЛОВО: ");
        booleanMatrixService.print(codeWord1);
        System.out.println("---------------");

        System.out.println("ТРАНСПОНИРОВАНИЕ ПОЛУЧЕННОГО СООБЩЕНИЯ");
        BooleanMatrix transposedCodeWord1 = booleanMatrixService.getTransposedBooleanMatrix(codeWord1);
        System.out.println("---------------");

        System.out.println("ПОЛУЧЕНИЕ СИНДРОМА");
        BooleanMatrix syndrome1 = booleanMatrixService.multiplicationMatrix(prepared2ParityCheckMatrix.getBooleanMatrix(), transposedCodeWord1);
        System.out.println("---------------");

        System.out.println("СИНДРОМ: ");
        booleanMatrixService.print(syndrome1);
        System.out.println("---------------");
    }
}
