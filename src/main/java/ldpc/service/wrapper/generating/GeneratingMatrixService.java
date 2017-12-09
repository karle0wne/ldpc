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

    /*
    * блок основных функций!
    * */
    public GeneratingMatrix getGeneratingMatrixFromParityCheckMatrix(ParityCheckMatrix parityCheckMatrix) {
        BooleanMatrix booleanMatrix = parityCheckMatrix.getBooleanMatrix();
        int iterator = 0;

        /*
        * Получение маски проверки
        * */
        Column mask = booleanMatrixService.getMask(booleanMatrix);

        /*
        * Пока маска проверки не станет полность состоять из false элементов (то есть вся матрица будет преобразована)
        * */
        while (booleanMatrixService.getCountTrueElements(mask.getElements()) > 0) {

            iterator = checkIterator(iterator);

            /*
            * Обновляем маску
            * */
            mask = booleanMatrixService.getMask(booleanMatrix);

            for (int i = 0; i < booleanMatrix.getSizeY(); i++) {
                Column columnMatrix = columnService.getColumnByIndex(booleanMatrix, i);

                /*
                * Поиск не преобразованного элемента маски проверки в матрице
                * */
                int firstTruePosition = getFirstTruePosition(mask, columnMatrix);

                if (firstTruePosition != DOES_NOT_EXIST) {
                    /*
                    * XOR строк матрицы для столбца
                    * */
                    getIdentityColumnByFirstTruePositionColumn(booleanMatrix, mask, columnMatrix, firstTruePosition);
                }
            }

            /*
            * Удаляем пустые строки и удаляем на позиции пустой строки элемент из маски проверки
            * */
            removeEmptyRows(booleanMatrix, mask);

            /*
            * создаем матрицу заново, тк матрица изменилась после удаления пустых строк
            * */
            booleanMatrix = booleanMatrixService.createMatrix(booleanMatrix.getMatrix());

            sortedRows(booleanMatrix);

            sortedColumns(booleanMatrix);
        }

        /*
        * Извлекаем столбцы с зависимыми элементами матрицы (то есть матрица без единичной матричной части)
        * */
        List<Column> columnsForGeneratingMatrix = getMatrixWithoutIdentityMatrix(booleanMatrix);

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

    /*
    * блок внутренних служебных функций
    * */
    private int checkIterator(int iterator) {
        if (iterator < BORDER_FOR_EXCEPTION) {
            iterator++;
        } else {
            throw new RuntimeException("Проверьте валидность проверочной матрицы!");
        }
        return iterator;
    }

    private int getFirstTruePosition(Column mask, Column columnMatrix) {
        return IntStream.range(0, mask.getElements().size())
                .filter(j -> mask.get(j) && columnMatrix.get(j))
                .findFirst()
                .orElse(DOES_NOT_EXIST);
    }

    private void getIdentityColumnByFirstTruePositionColumn(BooleanMatrix booleanMatrix, Column mask, Column columnMatrix, int firstTruePosition) {
        mask.getElements().set(firstTruePosition, false);

        List<Integer> changingTruePositions = booleanMatrixService.getPositionsTrueElementsWithoutFirst(columnMatrix.getElements(), firstTruePosition);

        for (Integer changingTruePosition : changingTruePositions) {
            Row currentRow = booleanMatrix.getMatrix().get(firstTruePosition);
            Row changingRow = booleanMatrix.getMatrix().get(changingTruePosition);
            List<Boolean> resultElements = booleanMatrixService.xor(currentRow.getElements(), changingRow.getElements());
            Row resultRow = new Row(resultElements);
            booleanMatrix.getMatrix().set(changingTruePosition, resultRow);
        }
    }

    private void removeEmptyRows(BooleanMatrix booleanMatrix, Column mask) {
        long countRowsForDelete = booleanMatrixService.getCountEmptyRows(booleanMatrix);

        List<Row> matrix = booleanMatrix.getMatrix();
        for (int i = 0; i < countRowsForDelete; i++) {
            Integer numberOfRowForDelete = IntStream.range(0, booleanMatrix.getSizeY())
                    .filter(j -> rowService.isFullFalseElementsRow(matrix.get(j)))
                    .findFirst()
                    .orElse(DOES_NOT_EXIST);
            if (numberOfRowForDelete != DOES_NOT_EXIST) {
                matrix.remove((int) numberOfRowForDelete);
                mask.getElements().remove((int) numberOfRowForDelete);
            }
        }
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

    private void sortedColumns(BooleanMatrix booleanMatrix) {
        List<Integer> unsortedRows = getUnsortedRows(booleanMatrix);

        List<Row> matrix = booleanMatrix.getMatrix();
        for (Integer rowNumber : unsortedRows) {
            Row row = matrix.get(rowNumber);
            Integer truePosition = booleanMatrixService.getPositionFirstTrueElement(row.getElements(), rowNumber, booleanMatrix.getSizeX());
            if (truePosition != DOES_NOT_EXIST) {
                IntStream.range(0, booleanMatrix.getSizeY()).forEach(i -> {
                    List<Boolean> elements = matrix.get(i).getElements();
                    Collections.swap(elements, rowNumber, truePosition);
                });
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

    private List<Column> getMatrixWithoutIdentityMatrix(BooleanMatrix booleanMatrix) {
        List<Column> allColumnsByBooleanMatrix = columnService.getAllColumnsByBooleanMatrix(booleanMatrix);
        return IntStream.range(booleanMatrix.getSizeY(), booleanMatrix.getSizeX())
                .mapToObj(allColumnsByBooleanMatrix::get)
                .collect(Collectors.toList());
    }

    private BooleanMatrix addIdentityMatrix(BooleanMatrix booleanGeneratingMatrix) {
        BooleanMatrix booleanIdentityMatrix = booleanMatrixService.createIdentityMatrix(booleanGeneratingMatrix.getSizeY());

        List<Row> matrix = booleanGeneratingMatrix.getMatrix();
        List<Row> identityMatrix = booleanIdentityMatrix.getMatrix();
        for (int i = 0; i < booleanGeneratingMatrix.getSizeY(); i++) {
            matrix.get(i).getElements().addAll(identityMatrix.get(i).getElements());
        }
        return booleanMatrixService.createMatrix(matrix);
    }

    /*
    * блок обслуживающий создание матриц функций
    * */
    public GeneratingMatrix preparedPGM() {
        List<Row> matrix = new ArrayList<>();
        matrix.add(rowService.createRow(1, 1, 1, 1, 0, 0));
        matrix.add(rowService.createRow(0, 1, 1, 0, 1, 0));
        matrix.add(rowService.createRow(1, 1, 0, 0, 0, 1));
        return new GeneratingMatrix(booleanMatrixService.createMatrix(matrix));
    }

    public GeneratingMatrix preparedPGM2() {
        List<Row> matrix = new ArrayList<>();
        matrix.add(rowService.createRow(1, 0, 0, 0, 0, 1, 1));
        matrix.add(rowService.createRow(0, 1, 0, 0, 1, 0, 1));
        matrix.add(rowService.createRow(0, 0, 1, 0, 1, 1, 0));
        matrix.add(rowService.createRow(0, 0, 0, 1, 1, 1, 1));
        return new GeneratingMatrix(booleanMatrixService.createMatrix(matrix));
    }
}
