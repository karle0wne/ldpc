package ldpc.service.wrapper.generating;

import ldpc.matrix.basis.BooleanMatrix;
import ldpc.matrix.basis.Column;
import ldpc.matrix.basis.Row;
import ldpc.matrix.wrapper.generating.GeneratingMatrix;
import ldpc.matrix.wrapper.paritycheck.ParityCheckMatrix;
import ldpc.service.basis.BooleanMatrixService;
import ldpc.service.basis.ColumnService;
import ldpc.service.basis.RowService;
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
    public static final int BORDER_FOR_EXCEPTION = 100;

    private final RowService rowService;

    private final BooleanMatrixService booleanMatrixService;

    private final ColumnService columnService;

    @Autowired
    public GeneratingMatrixService(RowService rowService, BooleanMatrixService booleanMatrixService, ColumnService columnService) {
        this.rowService = rowService;
        this.booleanMatrixService = booleanMatrixService;
        this.columnService = columnService;
    }

    public GeneratingMatrix getGeneratingMatrixFromParityCheckMatrix(ParityCheckMatrix parityCheckMatrix) {
        BooleanMatrix booleanMatrix = parityCheckMatrix.getBooleanMatrix();
        int iterator = 0;

        /*
        * Получение синдрома проверки
        * */
        Column columnCheckSyndrome = columnService.getColumnCheckSyndrome(booleanMatrix);

        /*
        * Пока синдром проверки не станет полность состоять из false элементов (то есть вся матрица будет преобразована)
        * */
        while (!getNumbersOfErrorBySyndrome(columnCheckSyndrome).isEmpty()) {
            for (int i = 0; i < booleanMatrix.getSizeY(); i++) {
                Column columnMatrix = columnService.getColumnByIndex(booleanMatrix, i);

                /*
                * Поиск не преобразованного элемента синдрома проверки в матрице
                * */
                int firstTruePosition = getFirstTruePosition(columnCheckSyndrome, columnMatrix);

                if (firstTruePosition != DOES_NOT_EXIST) {
                    /*
                    * XOR строк матрицы и приведение столбца матрицы к виду, когда только (i,j)=true
                    * */
                    getIdentityColumnByFirstTruePositionColumn(booleanMatrix, columnCheckSyndrome, columnMatrix, firstTruePosition);
                }
            }

            /*
            * Удаляем пустые строк и удаляем на позиции пустой строки элемент из синдрома проверки
            * */
            removeEmptyRows(booleanMatrix, columnCheckSyndrome);

            /*
            * создаем матрицу заново, тк матрица изменилась после удаления пустых строк
            * */
            booleanMatrix = booleanMatrixService.createMatrix(booleanMatrix.getMatrix());

            /*
            * переставляем столбец местами со столбцом в которой есть true элемент,
            * если в соотвествующей позиции не удалось привести столбец матрицы
            * */
            swapPositionsForAdjustmentsIdentityMatrixInBooleanMatrix(booleanMatrix, columnCheckSyndrome);

            if (iterator < BORDER_FOR_EXCEPTION) {
                iterator++;
            } else {
                throw new NullPointerException("Проверьте валидность проверочной матрицы!");
            }
        }

        /*
        * Сортировка строк матрицы, чтоб получилась упорядоченная единичная матрица,
        * в которой (i,j) = true
        * */
        sortedRowsByIdentityMatrix(booleanMatrix);

        /*
        * Извлекаем столбцы с зависимыми элементами матрицы (то есть матрица без единичной матричной части)
        * */
        List<Column> columnsForGeneratingMatrix = getColumnsWithoutIdentityMatrixBySortedBooleanMatrix(booleanMatrix);

        /*
        * создаем порождающую матрицу
        * */
        BooleanMatrix booleanGeneratingMatrix = booleanMatrixService.createMatrix(rowService.mapColumnsToRows(columnsForGeneratingMatrix));

        /*
        * добавляем единичную матрицу к порождающей
        * */
        booleanGeneratingMatrix = addIdentityMatrix(booleanGeneratingMatrix);
        return new GeneratingMatrix(booleanGeneratingMatrix);
    }

    private void getIdentityColumnByFirstTruePositionColumn(BooleanMatrix booleanMatrix, Column columnCheckSyndrome, Column columnMatrix, int firstTruePosition) {
        columnCheckSyndrome.getElements().set(firstTruePosition, false);

        List<Integer> changingTruePositions = booleanMatrixService.getTruePositionsWithoutFirstTruePosition(columnMatrix.getElements(), firstTruePosition);

        for (Integer changingTruePosition : changingTruePositions) {
            Row currentRow = booleanMatrix.getMatrix().get(firstTruePosition);
            Row changingRow = booleanMatrix.getMatrix().get(changingTruePosition);
            List<Boolean> resultElements = booleanMatrixService.xor(currentRow.getElements(), changingRow.getElements());
            Row resultRow = new Row(resultElements);
            booleanMatrix.getMatrix().set(changingTruePosition, resultRow);
        }
    }

    private void removeEmptyRows(BooleanMatrix booleanMatrix, Column columnCheckSyndrome) {
        long countRowsForDelete = booleanMatrixService.getCountEmptyRows(booleanMatrix);

        for (int i = 0; i < countRowsForDelete; i++) {
            Integer numberOfRowForDelete = DOES_NOT_EXIST;
            for (Row row : booleanMatrix.getMatrix()) {
                if (rowService.isFullFalseElementsRow(row)) {
                    numberOfRowForDelete = booleanMatrix.getMatrix().indexOf(row);
                    break;
                }
            }
            if (numberOfRowForDelete != DOES_NOT_EXIST) {
                booleanMatrix.getMatrix().remove((int) numberOfRowForDelete);
                columnCheckSyndrome.getElements().remove((int) numberOfRowForDelete);
            }
        }
    }

    private void sortedRowsByIdentityMatrix(BooleanMatrix booleanMatrix) {
        for (int i = 0; i < booleanMatrix.getSizeY(); i++) {
            if (!booleanMatrix.getMatrix().get(i).get(i)) {
                Column columnByIndex = columnService.getColumnByIndex(booleanMatrix, i);
                Integer indexForSwap = columnByIndex.getElements().stream()
                        .filter(element -> element)
                        .map(element -> columnByIndex.getElements().indexOf(element))
                        .findFirst()
                        .orElse(DOES_NOT_EXIST);
                if (indexForSwap != DOES_NOT_EXIST) {
                    Collections.swap(booleanMatrix.getMatrix(), i, indexForSwap);
                }
            }
        }
    }

    private List<Column> getColumnsWithoutIdentityMatrixBySortedBooleanMatrix(BooleanMatrix booleanMatrix) {
        List<Column> allColumnsByBooleanMatrix = columnService.getAllColumnsByBooleanMatrix(booleanMatrix);
        return IntStream.range(booleanMatrix.getSizeY(), booleanMatrix.getSizeX())
                .mapToObj(allColumnsByBooleanMatrix::get)
                .collect(Collectors.toList());
    }

    private BooleanMatrix addIdentityMatrix(BooleanMatrix booleanGeneratingMatrix) {
        BooleanMatrix booleanIdentityMatrix = booleanMatrixService.createIdentityMatrix(booleanGeneratingMatrix.getSizeY());
        for (int i = 0; i < booleanGeneratingMatrix.getSizeY(); i++) {
            booleanGeneratingMatrix.getMatrix().get(i).getElements().addAll(booleanIdentityMatrix.getMatrix().get(i).getElements());
        }

        /*
        * создаем матрицу заново, тк матрица изменилась после добавления элементов
        * */
        booleanGeneratingMatrix = booleanMatrixService.createMatrix(booleanGeneratingMatrix.getMatrix());
        return booleanGeneratingMatrix;
    }

    private void swapPositionsForAdjustmentsIdentityMatrixInBooleanMatrix(BooleanMatrix booleanMatrix, Column columnCheckSyndrome) {
        List<Integer> numbersRowAndColumnOfError = getNumbersOfErrorBySyndrome(columnCheckSyndrome);
        for (Integer number : numbersRowAndColumnOfError) {
            for (int i = booleanMatrix.getSizeY(); i < booleanMatrix.getSizeX(); i++) {
                if (booleanMatrix.getMatrix().get(number).get(i)) {
                    for (int j = 0; j < booleanMatrix.getSizeY(); j++) {
                        Collections.swap(booleanMatrix.getMatrix().get(j).getElements(), number, i);
                    }
                }
            }
        }
    }

    private List<Integer> getNumbersOfErrorBySyndrome(Column columnCheckSyndrome) {
        return columnCheckSyndrome.getElements().stream()
                .filter(element -> element)
                .map(element -> columnCheckSyndrome.getElements().indexOf(element))
                .collect(Collectors.toList());
    }

    private int getFirstTruePosition(Column columnCheckSyndrome, Column columnMatrix) {
        return IntStream.range(0, columnCheckSyndrome.getElements().size())
                .filter(j -> columnCheckSyndrome.get(j) && columnMatrix.get(j))
                .findFirst()
                .orElse(DOES_NOT_EXIST);
    }

    public GeneratingMatrix createPreparedGeneratingMatrix() {
        List<Row> matrix = new ArrayList<>();
        matrix.add(rowService.createRow(1, 1, 1, 1, 0, 0));
        matrix.add(rowService.createRow(0, 1, 1, 0, 1, 0));
        matrix.add(rowService.createRow(1, 1, 0, 0, 0, 1));

        return new GeneratingMatrix(booleanMatrixService.createMatrix(matrix));
    }

    public GeneratingMatrix createPrepared2GeneratingMatrix() {
        List<Row> matrix = new ArrayList<>();
        matrix.add(rowService.createRow(1, 0, 0, 0, 0, 1, 1));
        matrix.add(rowService.createRow(0, 1, 0, 0, 1, 0, 1));
        matrix.add(rowService.createRow(0, 0, 1, 0, 1, 1, 0));
        matrix.add(rowService.createRow(0, 0, 0, 1, 1, 1, 1));

        return new GeneratingMatrix(booleanMatrixService.createMatrix(matrix));
    }
}
