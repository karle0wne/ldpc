package ldpc.service.basis;

import ldpc.matrix.basis.BooleanMatrix;
import ldpc.matrix.basis.Column;
import ldpc.matrix.basis.Row;
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
    public BooleanMatrix getTransposedBooleanMatrix(BooleanMatrix booleanMatrix) {
        List<Column> columns = columnService.getAllColumnsByBooleanMatrix(booleanMatrix);
        List<Row> rows = rowService.mapColumnsToRows(columns);
        return createMatrix(rows);
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
                    return new Row(resultElements);
                })
                .collect(Collectors.toList());

        return createMatrix(resultMatrix);
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
                .mapToObj(i -> new Row(generateElements(N, false)))
                .collect(Collectors.toList());
        IntStream.range(0, N).forEach(i -> rows.get(i).getElements().set(i, true));
        return createMatrix(rows);
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
        return new Column(generateElements(booleanMatrix.getSizeY(), true));
    }

    public double getDensity(BooleanMatrix booleanMatrix) {
        List<Row> matrix = booleanMatrix.getMatrix();
        long count = matrix.stream()
                .mapToLong(row -> getCountTrueElements(row.getElements()))
                .sum();
        int countElements = booleanMatrix.getSizeY() * booleanMatrix.getSizeX();
        return getPercentage(count, countElements);
    }

    /*
    * блок обслуживающий вывод и создание матриц функций
    * */
    public BooleanMatrix preparedInfoWord() {
        return createCodeWord(1, 1, 1);
    }

    public BooleanMatrix preparedInfoWord2() {
        return createCodeWord(1, 1, 1, 0);
    }

    public BooleanMatrix preparedInfoWord3() {
        return createCodeWord(1, 1, 1, 0, 1, 1, 1);
    }

    public BooleanMatrix createCodeWord(@NotNull Integer... elements) {
        List<Boolean> rowElements = Arrays.stream(elements)
                .map(element -> element == 1)
                .collect(Collectors.toList());
        return createCodeWord(rowElements);
    }

    public BooleanMatrix createCodeWord(@NotNull Boolean... elements) {
        return createCodeWord(Arrays.asList(elements));
    }

    public BooleanMatrix createCodeWord(List<Boolean> elements) {
        return createMatrix(new ArrayList<>(Collections.singletonList(new Row(elements))));
    }

    public BooleanMatrix createMatrix(List<Row> rows) {
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

        return new BooleanMatrix(rows, maxRowSize, rows.size());
    }

    public void print(BooleanMatrix booleanMatrix) {
        System.out.println(
                booleanMatrix.getMatrix().stream()
                        .map(rowService::rowToString)
                        .collect(Collectors.joining(" \n")) + "\n"
        );
    }
}
