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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static ldpc.util.service.decode.SumProductDecodeService.BORDER_ITERATION;

/**
 * Сервис для порождающей матрицы
 */
@Service
public class GeneratingMatrixService {

    public static final int DOES_NOT_EXIST = -1;

    private final RowService rowService;

    private final BooleanMatrixService booleanMatrixService;

    private final ColumnService columnService;

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
        int iterator = 0;
        List<ColumnPair> swapHistory = new ArrayList<>();

        /*
        * Получение маски проверки
        * */
        Column mask = booleanMatrixService.getMask(booleanMatrix);

        /*
        * Пока маска проверки не станет полность состоять из false элементов (то есть вся матрица будет преобразована)
        * */
        for (int j = 0; !isCorrect(mask) && j < BORDER_ITERATION; j++) {
            /*
            * Обновляем маску
            * */
            mask = booleanMatrixService.getMask(booleanMatrix);

            for (int i = 0; i < booleanMatrix.getSizeY(); i++) {
                Column columnMatrix = columnService.getColumnByIndex(booleanMatrix, i);

                /*
                * Поиск не преобразованного элемента маски проверки в матрице
                * */
                int position = getFirstTruePosition(mask, columnMatrix);

                if (position != DOES_NOT_EXIST) {
                    mask.getElements().set(position, false);
                    /*
                    * XOR строк матрицы для столбца
                    * */
                    additionalRowsByPosition(booleanMatrix, columnMatrix, position);
                }
            }

            /*
            * Удаляем пустые строки и удаляем на позиции пустой строки элемент из маски проверки
            * */
            removeEmptyRows(booleanMatrix, mask);

            /*
            * Обновляем параметры, которые рассчитываются при создании, тк матрица изменилась после удаления пустых строк
            * */
            booleanMatrix = booleanMatrixService.copyMatrix(booleanMatrix.getMatrix());

            sortedRows(booleanMatrix);

            sortedColumns(booleanMatrix, swapHistory);
        }

        checkIterator(iterator);

        /*
        * Извлекаем столбцы с зависимыми элементами матрицы (то есть матрица без единичной матричной части)
        * */
        BooleanMatrix booleanGeneratingMatrix = getMatrixWithoutIdentityMatrix(booleanMatrix);

        /*
        * Добавляем единичную матрицу к порождающей
        * */
        booleanGeneratingMatrix = addIdentityMatrix(booleanGeneratingMatrix);

        GeneratingMatrix generatingMatrix = newGeneratingMatrix(booleanGeneratingMatrix);

        /*
        * Восстанавливаем порядок столбцов в порождающей матрице
        * */
        return newGeneratingMatrix(booleanMatrixService.recoveryBySwapHistory(generatingMatrix.getBooleanMatrix(), swapHistory));
    }

    private boolean isCorrect(Column mask) {
        return booleanMatrixService.getCountTrueElements(mask.getElements()) == 0;
    }

    /*
    * блок внутренних служебных функций
    * */
    private void checkIterator(int iterator) {
        if (iterator == BORDER_ITERATION) {
            throw new RuntimeException("Проверьте валидность проверочной матрицы!");
        }
    }

    private int getFirstTruePosition(Column mask, Column columnMatrix) {
        return IntStream.range(0, mask.getElements().size())
                .filter(j -> mask.get(j) && columnMatrix.get(j))
                .findFirst()
                .orElse(DOES_NOT_EXIST);
    }

    private void additionalRowsByPosition(BooleanMatrix booleanMatrix, Column columnMatrix, int firstTruePosition) {
        List<Integer> changingTruePositions = booleanMatrixService.getPositionsTrueElementsWithoutFirst(columnMatrix.getElements(), firstTruePosition);
        Row currentRow = booleanMatrix.getMatrix().get(firstTruePosition);

        for (Integer changingTruePosition : changingTruePositions) {
            Row changingRow = booleanMatrix.getMatrix().get(changingTruePosition);
            Row resultRow = rowService.newRow(booleanMatrixService.xor(currentRow.getElements(), changingRow.getElements()));
            booleanMatrix.getMatrix().set(changingTruePosition, resultRow);
        }
    }

    private void removeEmptyRows(BooleanMatrix booleanMatrix, Column mask) {
        List<Integer> numberEmptyRows = booleanMatrixService.getNumbersPositionsEmptyRows(booleanMatrix);
        numberEmptyRows.sort(Comparator.comparing(Integer::intValue).reversed());
        numberEmptyRows.forEach(
                numberEmptyRow -> {
                    booleanMatrix.getMatrix().remove((int) numberEmptyRow);
                    mask.getElements().remove((int) numberEmptyRow);
                }
        );
    }

    private void sortedRows(BooleanMatrix booleanMatrix) {
        List<Row> matrix = booleanMatrix.getMatrix();
        for (int i = 0; i < booleanMatrix.getSizeY(); i++) {
            Column columnByIndex = columnService.getColumnByIndex(booleanMatrix, i);
            if (booleanMatrixService.getCountTrueElements(columnByIndex.getElements()) == 1) {
                if (!matrix.get(i).get(i)) {
                    Integer indexForSwap = booleanMatrixService.getPositionFirstTrueElement(columnByIndex.getElements(), 0, booleanMatrix.getSizeY());
                    if (indexForSwap != DOES_NOT_EXIST) {
                        Collections.swap(matrix, i, indexForSwap);
                    }
                }
            }
        }
    }

    private void sortedColumns(BooleanMatrix booleanMatrix, List<ColumnPair> swapHistory) {
        List<Integer> unsortedRows = getUnsortedRows(booleanMatrix);

        List<Row> matrix = booleanMatrix.getMatrix();
        for (Integer rowNumber : unsortedRows) {
            Row row = matrix.get(rowNumber);
            Integer truePosition = booleanMatrixService.getPositionFirstTrueElement(row.getElements(), rowNumber, booleanMatrix.getSizeX());
            if (truePosition != DOES_NOT_EXIST) {
                IntStream.range(0, booleanMatrix.getSizeY())
                        .forEach(
                                i -> {
                                    List<Boolean> elements = matrix.get(i).getElements();
                                    Collections.swap(elements, rowNumber, truePosition);
                                }
                        );
                swapHistory.add(new ColumnPair(rowNumber, truePosition));
            }
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
