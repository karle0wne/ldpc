package ldpc.service.basis;

import ldpc.matrix.basis.BooleanMatrix;
import ldpc.matrix.basis.Column;
import ldpc.matrix.basis.Row;
import ldpc.util.template.ColumnPair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static ldpc.service.wrapper.generating.GeneratingMatrixService.DOES_NOT_EXIST;

/**
 * Сервис для матрицы, где
 * row строка
 * column столбец
 * способ хранения:
 * .........column
 * row  <b>element</b>
 */
@Service
public class BooleanMatrixService {

    private final RowService rowService;
    private final ColumnService columnService;

    @Autowired
    public BooleanMatrixService(RowService rowService, ColumnService columnService) {
        this.rowService = rowService;
        this.columnService = columnService;
    }

    /*
    * блок основных функций!
    * */
    public BooleanMatrix recoveryCodeWord(BooleanMatrix codeWord, List<ColumnPair> swapHistory) {
        List<Column> matrix = columnService.getAllColumnsByBooleanMatrix(codeWord);
        int size = swapHistory.size();
        IntStream.iterate(size - 1, i -> i - 1)
                .limit(size)
                .forEach(i ->
                        Collections.swap(
                                matrix,
                                swapHistory.get(i).getColumnNumberRight(),
                                swapHistory.get(i).getColumnNumberLeft()
                        )
                );
        return getTransposedBooleanMatrix(newMatrix(rowService.mapColumnsToRows(matrix)));
    }

    public BooleanMatrix getTransposedBooleanMatrix(BooleanMatrix booleanMatrix) {
        List<Column> columns = columnService.getAllColumnsByBooleanMatrix(booleanMatrix);
        List<Row> rows = rowService.mapColumnsToRows(columns);
        return newMatrix(rows);
    }

    public BooleanMatrix multiplicationMatrix(BooleanMatrix booleanMatrixA, BooleanMatrix booleanMatrixB) {
        if (!isValidMatrixForMultiplication(booleanMatrixA, booleanMatrixB)) {
            throw new RuntimeException("Проверьте размеры матриц, которые необходимо перемножить!");
        }

        List<Row> resultMatrix = booleanMatrixA.getMatrix().stream()
                .map(row -> {
                    List<Integer> truePositionsInRow = getPositionsTrueElements(row.getElements());

                    List<Row> matrixB = booleanMatrixB.getMatrix();

                    List<Row> rowsForXOR = truePositionsInRow.stream()
                            .map(matrixB::get)
                            .collect(Collectors.toList());

                    List<Boolean> resultElements = generateElements(booleanMatrixB.getSizeX(), false);
                    for (Row filteredRow : rowsForXOR) {
                        resultElements = xor(resultElements, filteredRow.getElements());
                    }
                    return rowService.newRow(resultElements);
                })
                .collect(Collectors.toList());

        return newMatrix(resultMatrix);
    }

    /*
    * блок внутренних служебных функций
    * */
    private boolean isValidMatrixForMultiplication(BooleanMatrix leftBooleanMatrix, BooleanMatrix rightBooleanMatrix) {
        return (leftBooleanMatrix.getSizeX() == rightBooleanMatrix.getSizeY());
    }

    private List<Integer> getPositionsTrueElements(List<Boolean> elements) {
        return IntStream.range(0, elements.size())
                .filter(elements::get)
                .boxed()
                .collect(Collectors.toList());
    }

    private ArrayList<Boolean> generateElements(int size, boolean element) {
        return new ArrayList<>(Collections.nCopies(size, element));
    }

    private double getPercentage(double part, double all) {
        return ((part / all) * 100.0d);
    }

    /*
    * блок внешних служебных функций
    * */
    public List<Boolean> xor(List<Boolean> elementsA, List<Boolean> elementsB) {
        int size = elementsA.size() >= elementsB.size() ? elementsA.size() : elementsB.size();
        return IntStream.range(0, size)
                .mapToObj(i -> elementsA.get(i) ^ elementsB.get(i))
                .collect(Collectors.toList());
    }

    public long getCountEmptyRows(BooleanMatrix booleanMatrix) {
        return booleanMatrix.getMatrix().stream()
                .filter(row -> {
                    List<Boolean> falseElements = row.getElements().stream()
                            .filter(element -> !element)
                            .collect(Collectors.toList());

                    return (falseElements.size() == row.getElements().size());
                })
                .count();
    }

    public BooleanMatrix createIdentityMatrix(int N) {
        List<Row> rows = IntStream.range(0, N)
                .mapToObj(i -> rowService.newRow(generateElements(N, false)))
                .collect(Collectors.toList());
        IntStream.range(0, N).forEach(i -> rows.get(i).getElements().set(i, true));
        return newMatrix(rows);
    }

    public List<Integer> getPositionsTrueElementsWithoutFirst(List<Boolean> elements, int firstTruePosition) {
        return getPositionsTrueElements(elements).stream()
                .filter(truePosition -> truePosition != firstTruePosition)
                .collect(Collectors.toList());
    }

    public long getCountTrueElements(List<Boolean> elements) {
        return elements.stream()
                .filter(element -> element)
                .count();
    }

    public Integer getPositionFirstTrueElement(List<Boolean> elements, int rangeStart, int rangeStop) {
        return IntStream.range(rangeStart, rangeStop)
                .filter(elements::get)
                .findFirst()
                .orElse(DOES_NOT_EXIST);
    }

    public Column getMask(BooleanMatrix booleanMatrix) {
        return columnService.newColumn(generateElements(booleanMatrix.getSizeY(), true));
    }

    private double getDensity(List<Row> matrix) {
        long count = matrix.stream()
                .mapToLong(row -> getCountTrueElements(row.getElements()))
                .sum();
        int countElements = matrix.size() * matrix.get(0).getElements().size();
        return getPercentage(count, countElements);
    }

    /*
    * блок обслуживающий вывод и создание матриц функций
    * */
    public BooleanMatrix generateInfoWord(int size) {
        List<Boolean> elements = generateElements(size, false);
        Random random = new Random();
        while (getCountTrueElements(elements) == 0) {
            elements = IntStream.range(0, size)
                    .mapToObj(value -> random.nextBoolean())
                    .collect(Collectors.toList());
        }
        return newWord(elements);
    }

    public BooleanMatrix newWord(@NotNull Integer... elements) {
        List<Boolean> rowElements = Arrays.stream(elements)
                .map(element -> element == 1)
                .collect(Collectors.toList());
        return newWord(rowElements);
    }

    public BooleanMatrix newWord(@NotNull Boolean... elements) {
        return newWord(Arrays.asList(elements));
    }

    public BooleanMatrix newWord(List<Boolean> elements) {
        return newMatrix(rowService.newRows(Collections.singletonList(rowService.newRow(elements))));
    }

    public BooleanMatrix newMatrix(BooleanMatrix booleanMatrix) {
        return newMatrix(booleanMatrix.getMatrix());
    }

    public BooleanMatrix newMatrix(List<Row> rows) {
        Integer maxRowSize = rows.stream()
                .mapToInt(row -> row.getElements().size())
                .max()
                .orElseThrow(RuntimeException::new);

        Integer minRowSize = rows.stream()
                .mapToInt(row -> row.getElements().size())
                .min()
                .orElseThrow(RuntimeException::new);

        if (!Objects.equals(minRowSize, maxRowSize)) {
            throw new RuntimeException("Укажите матрицу с одинаковым количеством элементов в строках!");
        }

        return new BooleanMatrix(rowService.newRows(rows), maxRowSize, rows.size(), getDensity(rows));
    }

    public BooleanMatrix copyMatrix(BooleanMatrix booleanMatrix) {
        return copyMatrix(booleanMatrix.getMatrix());
    }

    public BooleanMatrix copyMatrix(List<Row> rows) {
        Integer maxRowSize = rows.stream()
                .mapToInt(row -> row.getElements().size())
                .max()
                .orElseThrow(RuntimeException::new);

        Integer minRowSize = rows.stream()
                .mapToInt(row -> row.getElements().size())
                .min()
                .orElseThrow(RuntimeException::new);

        if (!Objects.equals(minRowSize, maxRowSize)) {
            throw new RuntimeException("Укажите матрицу с одинаковым количеством элементов в строках!");
        }

        return new BooleanMatrix(rows, maxRowSize, rows.size(), getDensity(rows));
    }
}
