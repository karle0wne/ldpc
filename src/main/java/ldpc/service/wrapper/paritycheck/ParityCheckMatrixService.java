package ldpc.service.wrapper.paritycheck;

import ldpc.matrix.basis.BooleanMatrix;
import ldpc.matrix.basis.Row;
import ldpc.matrix.wrapper.paritycheck.ParityCheckMatrix;
import ldpc.service.basis.BooleanMatrixService;
import ldpc.service.basis.RowService;
import ldpc.util.template.LDPCEnums;
import ldpc.util.template.TimeLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Сервис для проверочной матрицы
 */
@Service
public class ParityCheckMatrixService {

    private final RowService rowService;

    private final BooleanMatrixService booleanMatrixService;
    private TimeLogger timeLogger;

    @Autowired
    public ParityCheckMatrixService(RowService rowService, BooleanMatrixService booleanMatrixService) {
        this.rowService = rowService;
        this.booleanMatrixService = booleanMatrixService;
    }

    /*
    * блок обслуживающий создание матриц функций
    * */
    public ParityCheckMatrix generateParityCheckMatrix(LDPCEnums.TypeOfCoding typeOfCoding) {
        if (typeOfCoding == null) {
            return dummy();
        }
        switch (typeOfCoding) {
            case GIRTH8:
                timeLogger = new TimeLogger("generateWithGEight");
                return generateWithGEight(6, 5);
            default:
                return dummy();
        }
    }

    private ParityCheckMatrix dummy() {
        return createPreparedParityCheckMatrix6();
    }

    /*
    * блок внутренних служебных функций
    * */
    public ParityCheckMatrix createPreparedParityCheckMatrix6_2() {
        List<Row> matrix = new ArrayList<>();
        matrix.add(rowService.createRow(1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        matrix.add(rowService.createRow(0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        matrix.add(rowService.createRow(0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0));
        matrix.add(rowService.createRow(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0));
        matrix.add(rowService.createRow(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1));
        matrix.add(rowService.createRow(1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0));
        matrix.add(rowService.createRow(0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0));
        matrix.add(rowService.createRow(0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0));
        matrix.add(rowService.createRow(0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0));
        matrix.add(rowService.createRow(0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1));
        matrix.add(rowService.createRow(1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0));
        matrix.add(rowService.createRow(0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0));
        matrix.add(rowService.createRow(0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0));
        matrix.add(rowService.createRow(0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0));
        matrix.add(rowService.createRow(0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1));
        return newParityCheckMatrix(booleanMatrixService.newMatrix(matrix));
    }

    public ParityCheckMatrix createPreparedParityCheckMatrix6() {
        List<Row> matrix = new ArrayList<>();
        matrix.add(rowService.createRow(1, 1, 0, 0, 1, 0));
        matrix.add(rowService.createRow(0, 1, 1, 0, 0, 1));
        matrix.add(rowService.createRow(0, 0, 1, 1, 1, 0));
        matrix.add(rowService.createRow(1, 0, 0, 1, 0, 1));

        return new ParityCheckMatrix(booleanMatrixService.newMatrix(matrix));
    }

    private ParityCheckMatrix createPreparedParityCheckMatrix12() {
        List<Row> matrix = new ArrayList<>();
        matrix.add(rowService.createRow(1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        matrix.add(rowService.createRow(1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        matrix.add(rowService.createRow(0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        matrix.add(rowService.createRow(0, 0, 1, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        matrix.add(rowService.createRow(0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        matrix.add(rowService.createRow(0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0));
        matrix.add(rowService.createRow(0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0));
        matrix.add(rowService.createRow(0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0));
        matrix.add(rowService.createRow(0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0));
        matrix.add(rowService.createRow(0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1));
        matrix.add(rowService.createRow(0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0));
        matrix.add(rowService.createRow(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0));
        matrix.add(rowService.createRow(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 1));
        matrix.add(rowService.createRow(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0));
        return newParityCheckMatrix(booleanMatrixService.newMatrix(matrix));
    }

    private ParityCheckMatrix generateWithGEight(int k, int g) {
        if (g > k) {
            throw new RuntimeException("G должен быть не больше K!");
        }
        return newParityCheckMatrix(booleanMatrixService.newMatrix(getLDPCBlock(k, g, true)));
    }

    private List<Row> getLDPCBlock(int k, int g, boolean isNotZeroBlock) {
        Stream<Row> firstBlock = isNotZeroBlock ? getBlock(k, g, true) : getBlock(k, g, false);
        timeLogger.check();
        Stream<Row> secondBlock = isNotZeroBlock ? getLastLine(k, g - 1, true) : getLastLine(k, g - 1, false);
        timeLogger.check();
        return Stream.concat(firstBlock, secondBlock).collect(Collectors.toList());
    }

    private Stream<Row> getBlock(int k, int g, boolean isNotZeroBlock) {
        return IntStream.range(0, k)
                .mapToObj(i -> getLine(k, g, i, isNotZeroBlock))
                .flatMap(Collection::stream);
    }

    private List<Row> getLine(int k, int g, int i, boolean isNotZeroBlock) {
        List<BooleanMatrix> line = IntStream.range(0, k)
                .mapToObj(j -> getPartOfTheLine(k, g, j, i, isNotZeroBlock))
                .map(booleanMatrixService::newMatrix)
                .collect(Collectors.toList());
        timeLogger.check();
        return mergeLine(line);
    }

    private List<Row> getPartOfTheLine(int k, int g, int j, int i, boolean isNotZeroBlock) {
        if (g == 2) {
            if (isNotZeroBlock && i == j) {
                Row row = rowService.newRow(booleanMatrixService.generateElements(k, true));
                timeLogger.check();
                return Collections.singletonList(row);
            } else {
                Row row = rowService.newRow(booleanMatrixService.generateElements(k, false));
                timeLogger.check();
                return Collections.singletonList(row);
            }
        } else {
            if (isNotZeroBlock && i == j) {
                timeLogger.check();
                return getLDPCBlock(k, g - 1, true);
            } else {
                timeLogger.check();
                return getLDPCBlock(k, g - 1, false);
            }
        }
    }

    private Stream<Row> getLastLine(int k, int pow, boolean isNotZeroBlock) {
        List<BooleanMatrix> line = IntStream.range(0, k)
                .mapToObj(i -> getIdentityMatrix(k, pow, isNotZeroBlock))
                .collect(Collectors.toList());
        timeLogger.check();
        return mergeLine(line).stream();
    }

    private BooleanMatrix getIdentityMatrix(int k, int pow, boolean isNotZeroBlock) {
        if (isNotZeroBlock) {
            timeLogger.check();
            return booleanMatrixService.createIdentityMatrix(BigInteger.valueOf(k).pow(pow).intValue());
        } else {
            List<Row> zeroMatrix = booleanMatrixService.createZeroMatrix(BigInteger.valueOf(k).pow(pow).intValue());
            timeLogger.check();
            return booleanMatrixService.newMatrix(zeroMatrix);
        }
    }

    private List<Row> mergeLine(List<BooleanMatrix> booleanMatrices) {
        Integer lineSize = getLineSize(booleanMatrices);
        return IntStream.range(0, lineSize)
                .mapToObj(
                        i -> booleanMatrices.stream()
                                .flatMap(booleanMatrix -> booleanMatrix.getMatrix().get(i).getElements().stream())
                                .collect(Collectors.toList())
                )
                .map(rowService::newRow)
                .collect(Collectors.toList());
    }

    private Integer getLineSize(List<BooleanMatrix> booleanMatrices) {
        Integer maxX = booleanMatrices.stream()
                .mapToInt(BooleanMatrix::getSizeX)
                .max()
                .orElseThrow(() -> new RuntimeException("Пустая матрица!"));
        Integer minX = booleanMatrices.stream()
                .mapToInt(BooleanMatrix::getSizeX)
                .min()
                .orElseThrow(() -> new RuntimeException("Пустая матрица!"));

        Integer maxY = booleanMatrices.stream()
                .mapToInt(BooleanMatrix::getSizeY)
                .max()
                .orElseThrow(() -> new RuntimeException("Пустая матрица!"));
        Integer minY = booleanMatrices.stream()
                .mapToInt(BooleanMatrix::getSizeY)
                .min()
                .orElseThrow(() -> new RuntimeException("Пустая матрица!"));

        if (!Objects.equals(minX, maxX)) {
            throw new RuntimeException("Ошибка генерации!");
        }

        if (!Objects.equals(minY, maxY)) {
            throw new RuntimeException("Ошибка генерации!");
        }
        return minY;
    }

    /*
    * блок обслуживающий вывод и создание матриц функций
    * */
    public ParityCheckMatrix newParityCheckMatrix(ParityCheckMatrix parityCheckMatrix) {
        return new ParityCheckMatrix(booleanMatrixService.newMatrix(parityCheckMatrix.getBooleanMatrix()));
    }

    public ParityCheckMatrix newParityCheckMatrix(BooleanMatrix booleanMatrix) {
        return new ParityCheckMatrix(booleanMatrixService.newMatrix(booleanMatrix));
    }
}
