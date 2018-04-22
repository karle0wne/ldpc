package ldpc.service.wrapper.generating;

import ldpc.matrix.basis.BooleanMatrix;
import ldpc.matrix.basis.Column;
import ldpc.matrix.basis.Row;
import ldpc.matrix.wrapper.generating.GeneratingMatrix;
import ldpc.matrix.wrapper.paritycheck.ParityCheckMatrix;
import ldpc.service.basis.BooleanMatrixService;
import ldpc.service.basis.ColumnService;
import ldpc.service.basis.RowService;
import ldpc.util.template.ColumnPair;
import ldpc.util.template.TimeLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Сервис для порождающей матрицы
 */
@Service
public class GeneratingMatrixService {

    public static final int DOES_NOT_EXIST = -1;

    private final RowService rowService;

    private final BooleanMatrixService booleanMatrixService;

    private final ColumnService columnService;

    private List<ColumnPair> columnPairs;

    @Autowired
    public GeneratingMatrixService(RowService rowService, BooleanMatrixService booleanMatrixService, ColumnService columnService) {
        this.rowService = rowService;
        this.booleanMatrixService = booleanMatrixService;
        this.columnService = columnService;
    }

    /*
    * блок основных функций!
    * */
    public GeneratingMatrix getGeneratingMatrixFromParityCheckMatrix(ParityCheckMatrix parityCheckMatrix) {
        BooleanMatrix booleanMatrix = booleanMatrixService.newMatrix(parityCheckMatrix.getBooleanMatrix());
        List<ColumnPair> swapHistory = new ArrayList<>();

        int i = 0;
        TimeLogger timeLogger = new TimeLogger("getGeneratingMatrixFromParityCheckMatrix()", true);
        while (i < booleanMatrix.getSizeY()) {
            Column column = columnService.getColumnByIndex(booleanMatrix, i);

            int position = IntStream.range(i, column.getElements().size())
                    .filter(column::get)
                    .findFirst()
                    .orElse(DOES_NOT_EXIST);

            if (position != DOES_NOT_EXIST) {
                additionalRowsByPosition(booleanMatrix, column, position);
                Collections.swap(booleanMatrix.getMatrix(), i, position);
                i++;
            } else {
                final List<Row> matrix = booleanMatrix.getMatrix();
                final int iterator = i;
                position = IntStream.range(iterator, booleanMatrix.getSizeX())
                        .filter(value -> matrix.get(iterator).get(value))
                        .findFirst()
                        .orElse(DOES_NOT_EXIST);

                if (position == DOES_NOT_EXIST) {
                    booleanMatrix.getMatrix().remove(iterator);
                    booleanMatrix = booleanMatrixService.newMatrix(booleanMatrix.getMatrix());
                } else {
                    final int swapFrom = position;

                    IntStream.range(0, booleanMatrix.getSizeY())
                            .forEach(
                                    j -> {
                                        List<Boolean> elements = matrix.get(j).getElements();
                                        Collections.swap(elements, iterator, swapFrom);
                                    }
                            );

                    swapHistory.add(new ColumnPair(iterator, swapFrom));
                }
            }
            timeLogger.check();
        }

        /*
        * Извлекаем столбцы с зависимыми элементами матрицы (то есть матрица без единичной матричной части)
        * */
        BooleanMatrix booleanGeneratingMatrix = getMatrixWithoutIdentityMatrix(booleanMatrix);

        /*
        * Добавляем единичную матрицу к порождающей
        * */
        booleanGeneratingMatrix = addIdentityMatrix(booleanGeneratingMatrix);

        GeneratingMatrix generatingMatrix = newGeneratingMatrix(booleanGeneratingMatrix);

        setColumnPairs(swapHistory);
        /*
        * Восстанавливаем порядок столбцов в порождающей матрице
        * */
        return newGeneratingMatrix(recoveryBySwapHistory(generatingMatrix.getBooleanMatrix(), true));
    }

    public BooleanMatrix recoveryBySwapHistory(BooleanMatrix booleanMatrix, boolean reverse) {
        List<ColumnPair> swapHistory = new ArrayList<>(getColumnPairs());
        List<Column> matrix = columnService.getAllColumnsByBooleanMatrix(booleanMatrix);
        if (reverse) {
            Collections.reverse(swapHistory);
        }
        swapHistory.forEach(
                columnPair ->
                        Collections.swap(
                                matrix,
                                columnPair.getColumnNumberRight(),
                                columnPair.getColumnNumberLeft()
                        )
        );
        return booleanMatrixService.newMatrix(booleanMatrixService.getTransposedBooleanMatrix(booleanMatrixService.newMatrix(rowService.mapColumnsToRows(matrix))));
    }

    private List<ColumnPair> getColumnPairs() {
        if (columnPairs == null) {
            throw new NullPointerException("Нет истории перестановок проверочной матрицы!");
        } else {
            return new ArrayList<>(columnPairs);
        }
    }

    private void setColumnPairs(List<ColumnPair> columnPairs) {
        this.columnPairs = new ArrayList<>(columnPairs);
    }

    /*
    * блок внутренних служебных функций
    * */

    private void additionalRowsByPosition(BooleanMatrix booleanMatrix, Column columnMatrix, int firstTruePosition) {
        List<Integer> changingTruePositions = booleanMatrixService.getPositionsTrueElementsWithoutFirst(columnMatrix.getElements(), firstTruePosition);
        Row currentRow = booleanMatrix.getMatrix().get(firstTruePosition);

        for (Integer changingTruePosition : changingTruePositions) {
            Row changingRow = booleanMatrix.getMatrix().get(changingTruePosition);
            Row resultRow = rowService.newRow(booleanMatrixService.xor(currentRow.getElements(), changingRow.getElements()));
            booleanMatrix.getMatrix().set(changingTruePosition, resultRow);
        }
    }

    private List<Integer> getUnsortedRows(BooleanMatrix booleanMatrix) {
        List<Row> matrix = booleanMatrix.getMatrix();
        return IntStream.range(0, booleanMatrix.getSizeY())
                .filter(i -> !matrix.get(i).get(i))
                .boxed()
                .collect(Collectors.toList());
    }

    private BooleanMatrix getMatrixWithoutIdentityMatrix(BooleanMatrix booleanMatrix) {
        if (!getUnsortedRows(booleanMatrix).isEmpty()) {
            throw new RuntimeException("Приведите матрицу к виду [I]|[T] !");
        }
        List<Column> allColumnsByBooleanMatrix = columnService.getAllColumnsByBooleanMatrix(booleanMatrix);
        List<Column> columns = IntStream.range(booleanMatrix.getSizeY(), booleanMatrix.getSizeX())
                .mapToObj(allColumnsByBooleanMatrix::get)
                .collect(Collectors.toList());
        return booleanMatrixService.newMatrix(rowService.mapColumnsToRows(columns));
    }

    private BooleanMatrix addIdentityMatrix(BooleanMatrix booleanGeneratingMatrix) {
        BooleanMatrix booleanIdentityMatrix = booleanMatrixService.createIdentityMatrix(booleanGeneratingMatrix.getSizeY());

        List<Row> matrix = booleanGeneratingMatrix.getMatrix();
        List<Row> identityMatrix = booleanIdentityMatrix.getMatrix();
        IntStream.range(0, booleanGeneratingMatrix.getSizeY())
                .forEach(i -> matrix.get(i).getElements().addAll(identityMatrix.get(i).getElements()));

        return booleanMatrixService.newMatrix(matrix);
    }

    /*
    * блок обслуживающий создание матриц функций
    * */
    public GeneratingMatrix newGeneratingMatrix(BooleanMatrix booleanMatrix) {
        return new GeneratingMatrix(booleanMatrixService.newMatrix(booleanMatrix));
    }
}
