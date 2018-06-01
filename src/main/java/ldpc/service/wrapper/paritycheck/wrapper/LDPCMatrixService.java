package ldpc.service.wrapper.paritycheck.wrapper;

import ldpc.matrix.basis.BooleanMatrix;
import ldpc.matrix.basis.Column;
import ldpc.matrix.basis.Row;
import ldpc.matrix.wrapper.paritycheck.ParityCheckMatrix;
import ldpc.matrix.wrapper.paritycheck.wrapper.StrictLowDensityParityCheckMatrix;
import ldpc.service.basis.BooleanMatrixService;
import ldpc.service.basis.ColumnService;
import ldpc.service.basis.RowService;
import ldpc.util.template.TimeLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
public class LDPCMatrixService {

    private final BooleanMatrixService booleanMatrixService;

    private final ColumnService columnService;

    private final RowService rowService;

    private TimeLogger timeLogger;

    @Autowired
    public LDPCMatrixService(BooleanMatrixService booleanMatrixService, ColumnService columnService, RowService rowService) {
        this.booleanMatrixService = booleanMatrixService;
        this.columnService = columnService;
        this.rowService = rowService;
    }

    public StrictLowDensityParityCheckMatrix newStrictLDPCMatrix(BooleanMatrix booleanMatrix) {
        /**
         * strict - строгая матрица, в которой J,K это константы
         * */
        long k = getK(booleanMatrix);
        long j = getJ(booleanMatrix);
//        long g = getG(booleanMatrix);
        long g = -1;
        BooleanMatrix newMatrix = booleanMatrixService.newMatrix(booleanMatrix);
        if (!validate(booleanMatrix, k, j)) {
            throw new RuntimeException("Проверьте значения K, J и m*n матрицы!");
        }
        return new StrictLowDensityParityCheckMatrix(newMatrix, k, j, g);

    }

    public ParityCheckMatrix generateWithGEight(int k, int g) {
        if (g > k) {
            throw new RuntimeException("G должен быть не больше K!");
        }
        timeLogger = new TimeLogger("GIRTH8" + "_" + k + "_" + g, false);
        BooleanMatrix booleanMatrix = booleanMatrixService.newMatrix(getLDPCBlock(k, g, true));
        return newStrictLDPCMatrix(booleanMatrix);
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

    private long getK(BooleanMatrix booleanMatrix) {
        List<Row> matrix = booleanMatrix.getMatrix();
        Long maxK = matrix.stream()
                .mapToLong(row -> booleanMatrixService.getCountTrueElements(row.getElements()))
                .max()
                .orElseThrow(() -> new RuntimeException("Пустая матрица!"));

        Long minK = matrix.stream()
                .mapToLong(row -> booleanMatrixService.getCountTrueElements(row.getElements()))
                .min()
                .orElseThrow(() -> new RuntimeException("Пустая матрица!"));

        if (!Objects.equals(minK, maxK)) {
            throw new RuntimeException("Укажите LDPC матрицу с одинаковым количеством единиц в строках!");
        }
        return maxK;
    }

    private long getJ(BooleanMatrix booleanMatrix) {
        List<Column> matrix = columnService.getAllColumnsByBooleanMatrix(booleanMatrix);
        Long maxJ = matrix.stream()
                .mapToLong(column -> booleanMatrixService.getCountTrueElements(column.getElements()))
                .max()
                .orElseThrow(() -> new RuntimeException("Пустая матрица!"));

        Long minJ = matrix.stream()
                .mapToLong(column -> booleanMatrixService.getCountTrueElements(column.getElements()))
                .min()
                .orElseThrow(() -> new RuntimeException("Пустая матрица!"));

        if (!Objects.equals(minJ, maxJ)) {
            throw new RuntimeException("Укажите LDPC матрицу с одинаковым количеством единиц в столбцах!");
        }
        return maxJ;
    }

    private int getG(BooleanMatrix matrix) {
        TimeLogger timeLogger = new TimeLogger("LDPCMatrixService.getG", true);
        BooleanMatrix booleanMatrixStart = booleanMatrixService.newMatrix(matrix);

        List<Integer> tierLevels = new ArrayList<>();

        IntStream.range(0, booleanMatrixStart.getSizeY())
                .forEach(
                        i -> {
                            List<Integer> columnsForDelete = booleanMatrixService.getPositionsTrueElements(booleanMatrixStart.getMatrix().get(i).getElements());

                            BooleanMatrix transposedBooleanMatrix = booleanMatrixService.getTransposedBooleanMatrix(booleanMatrixStart);

                            BooleanMatrix booleanMatrix = booleanMatrixService.removeColumn(transposedBooleanMatrix, i);
                            int tierLevel;
                            for (tierLevel = 0; !booleanMatrixService.isEmpty(booleanMatrix) && !checkNotUniqueness(columnsForDelete); tierLevel++) {
                                timeLogger.check();

                                List<Integer> columnsForDeleteForNextTierLevel = getColumnsForDelete(columnsForDelete, booleanMatrix);
                                timeLogger.check();

                                transposedBooleanMatrix = booleanMatrixService.getTransposedBooleanMatrix(booleanMatrixService.newMatrix(booleanMatrix));
                                timeLogger.check();

                                booleanMatrix = booleanMatrixService.removeColumns(transposedBooleanMatrix, columnsForDelete);
                                timeLogger.check();

                                columnsForDelete = new ArrayList<>(columnsForDeleteForNextTierLevel);
                            }

                            if (checkNotUniqueness(columnsForDelete)) {
                                int trueTierLevel = (tierLevel + 1) * 2;
                                tierLevels.add(trueTierLevel);
                            }
                            timeLogger.check();
                        }
                );
        timeLogger.check();

        return tierLevels.stream()
                .min(Comparator.comparing(Integer::intValue))
                .orElseThrow(() -> new RuntimeException("Не удалось найти обхват, проверьте матрицу!"));
    }

    private List<Integer> getColumnsForDelete(List<Integer> numbersRowsForSearch, BooleanMatrix booleanMatrix) {
        return numbersRowsForSearch.stream()
                .flatMap(
                        numberRow -> {
                            Row row = booleanMatrix.getMatrix().get(numberRow);
                            return booleanMatrixService.getPositionsTrueElements(row.getElements()).stream();
                        }
                )
                .collect(Collectors.toList());
    }

    private boolean checkNotUniqueness(List<Integer> columns) {
        columns.sort(Comparator.comparing(Integer::intValue));
        Set<Integer> uniques = new HashSet<>();
        for (Integer column : columns) {
            if (!uniques.add(column)) {
                return true;
            }
        }
        return false;
    }

    private boolean validate(BooleanMatrix booleanMatrix, long k, long j) {
        return (j * booleanMatrix.getSizeX()) == (k * booleanMatrix.getSizeY());
    }
}
