package ldpc.service.basis;

import ldpc.matrix.basis.BooleanMatrix;
import ldpc.matrix.basis.Column;
import ldpc.matrix.basis.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    private static final Logger LOGGER = Logger.getLogger(BooleanMatrixService.class.getName());

    private final RowService rowService;
    private final ColumnService columnService;

    @Autowired
    public BooleanMatrixService(RowService rowService, ColumnService columnService) {
        this.rowService = rowService;
        this.columnService = columnService;
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
                .mapToObj(i -> new Row(new ArrayList<>(Collections.nCopies(N, false))))
                .collect(Collectors.toList());
        IntStream.range(0, N).forEach(i -> rows.get(i).getElements().set(i, true));
        return createMatrix(rows);
    }

    public List<Integer> getTruePositionsWithoutFirstTruePosition(List<Boolean> elements, int firstTruePositionInColumn) {
        return getOnlyTruePositions(elements).stream()
                .filter(truePosition -> truePosition != firstTruePositionInColumn)
                .collect(Collectors.toList());
    }

    public BooleanMatrix getTransposedBooleanMatrix(BooleanMatrix booleanMatrix) {
        List<Column> columns = columnService.getAllColumnsByBooleanMatrix(booleanMatrix);
        List<Row> rows = rowService.mapColumnsToRows(columns);
        return createMatrix(rows);
    }

    public BooleanMatrix multiplicationMatrix(BooleanMatrix booleanMatrixA, BooleanMatrix booleanMatrixB) {
        if (!isValidMatrixForMultiplication(booleanMatrixA, booleanMatrixB)) {
            LOGGER.severe("Проверьте размеры матриц, которые необходимо перемножить!");
            throw new NullPointerException();
        }

        /*
         * onward magic :)
         * */
        List<Row> resultMatrix = booleanMatrixA.getMatrix().stream()
                .map(row -> {
                    List<Integer> onlyTruePositions = getOnlyTruePositions(row.getElements());

                    List<Row> matrixB = booleanMatrixB.getMatrix();

                    List<Row> rowsForXOR = onlyTruePositions.stream()
                            .map(matrixB::get)
                            .collect(Collectors.toList());

                    List<Boolean> resultElements = new ArrayList<>(Collections.nCopies(booleanMatrixB.getSizeX(), false));
                    for (Row filteredRow : rowsForXOR) {
                        resultElements = xor(resultElements, filteredRow.getElements());
                    }
                    return new Row(resultElements);
                })
                .collect(Collectors.toList());

        return createMatrix(resultMatrix);
    }

    private List<Integer> getOnlyTruePositions(List<Boolean> elements) {
        return IntStream.range(0, elements.size())
                .filter(elements::get)
                .boxed()
                .collect(Collectors.toList());
    }

    public List<Boolean> xor(List<Boolean> elementsA, List<Boolean> elementsB) {
        int size = elementsA.size() >= elementsB.size() ? elementsA.size() : elementsB.size();
        return IntStream.range(0, size)
                .mapToObj(i -> elementsA.get(i) ^ elementsB.get(i))
                .collect(Collectors.toList());
    }

    private boolean isValidMatrixForMultiplication(BooleanMatrix leftBooleanMatrix, BooleanMatrix rightBooleanMatrix) {
        return (leftBooleanMatrix.getSizeX() == rightBooleanMatrix.getSizeY());
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
                .orElseThrow(NullPointerException::new);

        Integer minRowSize = rows.stream()
                .mapToInt(row -> row.getElements().size())
                .min()
                .orElseThrow(NullPointerException::new);

        if (!Objects.equals(minRowSize, maxRowSize)) {
            LOGGER.severe("Укажите матрицу с одинаковым количеством элементов в строках!");
            throw new NullPointerException();
        }

        return new BooleanMatrix(rows, maxRowSize, rows.size());
    }

    public void print(BooleanMatrix booleanMatrix) {
        System.out.println(
                booleanMatrix.getMatrix().stream()
                        .map(rowService::rowToString)
                        .collect(Collectors.joining(" \n"))
                        + "\n"
        );
    }

    public BooleanMatrix createPreparedInformationWord() {
        return createCodeWord(1, 1, 1);
    }

    public BooleanMatrix createPrepared2InformationWord() {
        return createCodeWord(1, 1, 1, 0);
    }
}
