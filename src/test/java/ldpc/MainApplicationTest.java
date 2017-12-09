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

        System.out.println("ПОРОЖДАЮЩАЯ МАТРИЦА (G): ");
        GeneratingMatrix generatingMatrix = generatingMatrixService.preparedPGM();
        booleanMatrixService.print(generatingMatrix.getBooleanMatrix());

        System.out.println("ИНФОРМАЦИОННОЕ СЛОВО (word):");
        BooleanMatrix informationWord = booleanMatrixService.preparedInfoWord();
        booleanMatrixService.print(informationWord);

        System.out.println("КОДОВОЕ СЛОВО (word*G): ");
        BooleanMatrix codeWord = booleanMatrixService.multiplicationMatrix(informationWord, generatingMatrix.getBooleanMatrix());
        booleanMatrixService.print(codeWord);

        System.out.println("---------------");
        System.out.println("ТИПА ПЕРЕДАЧА ПО КАНАЛУ!");
        System.out.println("---------------");

        System.out.println("ПРОВЕРОЧНАЯ МАТРИЦА (H): ");
        ParityCheckMatrix parityCheckMatrix = parityCheckMatrixService.preparedPCM();
        booleanMatrixService.print(parityCheckMatrix.getBooleanMatrix());

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

        System.out.println("ПРОВЕРОЧНАЯ МАТРИЦА (H) ДО ПРИВЕДЕНИЯ: ");
        ParityCheckMatrix parityCheckMatrix = parityCheckMatrixService.preparedPCM();
        booleanMatrixService.print(parityCheckMatrix.getBooleanMatrix());

        System.out.println("ПОРОЖДАЮЩАЯ МАТРИЦА (G): ");
        GeneratingMatrix generatingMatrix = generatingMatrixService.getGeneratingMatrixFromParityCheckMatrix(parityCheckMatrix);
        booleanMatrixService.print(generatingMatrix.getBooleanMatrix());

        System.out.println("ИНФОРМАЦИОННОЕ СЛОВО (word):");
        BooleanMatrix informationWord = booleanMatrixService.preparedInfoWord();
        booleanMatrixService.print(informationWord);

        System.out.println("КОДОВОЕ СЛОВО (word*G): ");
        BooleanMatrix codeWord = booleanMatrixService.multiplicationMatrix(informationWord, generatingMatrix.getBooleanMatrix());
        booleanMatrixService.print(codeWord);

        System.out.println("---------------");
        System.out.println("ТИПА ПЕРЕДАЧА ПО КАНАЛУ!");
        System.out.println("---------------");

        System.out.println("ПРОВЕРОЧНАЯ МАТРИЦА (H): ");
        booleanMatrixService.print(parityCheckMatrix.getBooleanMatrix());

        System.out.println("ТРАНСПОНИРОВАНИЕ ПОЛУЧЕННОГО СООБЩЕНИЯ transposed(word*G): ");
        BooleanMatrix transposedCodeWord = booleanMatrixService.getTransposedBooleanMatrix(codeWord);
        booleanMatrixService.print(transposedCodeWord);

        System.out.println("СИНДРОМ ПРОВЕРКИ H*transposed(word*G): ");
        BooleanMatrix syndrome = booleanMatrixService.multiplicationMatrix(parityCheckMatrix.getBooleanMatrix(), transposedCodeWord);
        booleanMatrixService.print(syndrome);

        //--------------------------------------------------------------------------------------------------------------
        System.out.println("2) ТЕСТ ПРОЦЕССА КОДИРОВАНИЯ И ДЕКОДИРОВАНИЯ ПРИ ЗАДАННОЙ H");

        System.out.println("ПРОВЕРОЧНАЯ МАТРИЦА (H) ДО ПРИВЕДЕНИЯ: ");
        ParityCheckMatrix parityCheckMatrix1 = parityCheckMatrixService.preparedPCM2();
        booleanMatrixService.print(parityCheckMatrix1.getBooleanMatrix());

        System.out.println("ПОРОЖДАЮЩАЯ МАТРИЦА (G): ");
        GeneratingMatrix generatingMatrix1 = generatingMatrixService.getGeneratingMatrixFromParityCheckMatrix(parityCheckMatrix1);
        booleanMatrixService.print(generatingMatrix1.getBooleanMatrix());

        System.out.println("ИНФОРМАЦИОННОЕ СЛОВО (word):");
        BooleanMatrix informationWord2 = booleanMatrixService.preparedInfoWord2();
        booleanMatrixService.print(informationWord2);

        System.out.println("КОДОВОЕ СЛОВО (word*G): ");
        BooleanMatrix codeWord2 = booleanMatrixService.multiplicationMatrix(informationWord2, generatingMatrix1.getBooleanMatrix());
        booleanMatrixService.print(codeWord2);

        System.out.println("---------------");
        System.out.println("ТИПА ПЕРЕДАЧА ПО КАНАЛУ!");
        System.out.println("---------------");

        System.out.println("ПРОВЕРОЧНАЯ МАТРИЦА (H): ");
        booleanMatrixService.print(parityCheckMatrix1.getBooleanMatrix());

        System.out.println("ТРАНСПОНИРОВАНИЕ ПОЛУЧЕННОГО СООБЩЕНИЯ transposed(word*G): ");
        BooleanMatrix transposedCodeWord2 = booleanMatrixService.getTransposedBooleanMatrix(codeWord2);
        booleanMatrixService.print(transposedCodeWord2);

        System.out.println("СИНДРОМ ПРОВЕРКИ H*transposed(word*G): ");
        BooleanMatrix syndrome2 = booleanMatrixService.multiplicationMatrix(parityCheckMatrix1.getBooleanMatrix(), transposedCodeWord2);
        booleanMatrixService.print(syndrome2);
    }

    @Test
    public void test3() {
        System.out.println("ПРОВЕРОЧНАЯ МАТРИЦА (H) ДО ПРИВЕДЕНИЯ: ");
        ParityCheckMatrix parityCheckMatrix = parityCheckMatrixService.preparedPCM4();
        booleanMatrixService.print(parityCheckMatrix.getBooleanMatrix());

        System.out.println("ПОРОЖДАЮЩАЯ МАТРИЦА (G): ");
        GeneratingMatrix generatingMatrix = generatingMatrixService.getGeneratingMatrixFromParityCheckMatrix(parityCheckMatrix);
        booleanMatrixService.print(generatingMatrix.getBooleanMatrix());

        System.out.println("ПЛОТНОСТЬ ЕДИНИЦ ПОРОЖДАЮЩАЯ МАТРИЦА (G): ");
        System.out.println(booleanMatrixService.getDensity(generatingMatrix.getBooleanMatrix()) + "\n");

        System.out.println("ИНФОРМАЦИОННОЕ СЛОВО (word):");
        BooleanMatrix informationWord = booleanMatrixService.preparedInfoWord3();
        booleanMatrixService.print(informationWord);

        System.out.println("КОДОВОЕ СЛОВО (word*G): ");
        BooleanMatrix codeWord = booleanMatrixService.multiplicationMatrix(informationWord, generatingMatrix.getBooleanMatrix());
        booleanMatrixService.print(codeWord);

        System.out.println("---------------");
        System.out.println("ТИПА ПЕРЕДАЧА ПО КАНАЛУ!");
        System.out.println("---------------");

        System.out.println("ПРОВЕРОЧНАЯ МАТРИЦА (H): ");
        booleanMatrixService.print(parityCheckMatrix.getBooleanMatrix());

        System.out.println("ПЛОТНОСТЬ ЕДИНИЦ ПРОВЕРОЧНАЯ МАТРИЦА (H): ");
        System.out.println(booleanMatrixService.getDensity(parityCheckMatrix.getBooleanMatrix()) + "\n");

        System.out.println("ТРАНСПОНИРОВАНИЕ ПОЛУЧЕННОГО СООБЩЕНИЯ transposed(word*G): ");
        BooleanMatrix transposedCodeWord = booleanMatrixService.getTransposedBooleanMatrix(codeWord);
        booleanMatrixService.print(transposedCodeWord);

        System.out.println("СИНДРОМ ПРОВЕРКИ H*transposed(word*G): ");
        BooleanMatrix syndrome = booleanMatrixService.multiplicationMatrix(parityCheckMatrix.getBooleanMatrix(), transposedCodeWord);
        booleanMatrixService.print(syndrome);
    }
}
