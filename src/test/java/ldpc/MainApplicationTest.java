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
        BooleanMatrix informationWord = booleanMatrixService.createPrepared2InformationWord();
        System.out.println("ИНФОРМАЦИОННОЕ СЛОВО:");
        booleanMatrixService.print(informationWord);
        System.out.println("---------------");

        GeneratingMatrix generatingMatrix = generatingMatrixService.createPrepared2GeneratingMatrix();
        System.out.println("ПОРОЖДАЮЩАЯ МАТРИЦА:");
        booleanMatrixService.print(generatingMatrix.getBooleanMatrix());
        System.out.println("---------------");

        ParityCheckMatrix parityCheckMatrix = parityCheckMatrixService.createPrepared2ParityCheckMatrix();
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
}