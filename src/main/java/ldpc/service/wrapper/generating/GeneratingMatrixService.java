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

        Column columnCheckSyndrome = columnService.getColumnCheckSyndrome(booleanMatrix);

        while (!getNumbersOfErrorBySyndrome(columnCheckSyndrome).isEmpty()) {
            for (int i = 0; i < booleanMatrix.getSizeY(); i++) {
                Column columnMatrix = columnService.getColumnByIndex(booleanMatrix, i);

                int firstTruePosition = getFirstTruePosition(columnCheckSyndrome, columnMatrix);

                if (firstTruePosition != DOES_NOT_EXIST) {
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
            }

            long countRowsForDelete = booleanMatrix.getMatrix().stream()
                    .filter(row -> {
                        List<Boolean> falseElements = row.getElements().stream()
                                .filter(element -> !element)
                                .collect(Collectors.toList());

                        return (falseElements.size() == row.getElements().size());
                    })
                    .count();

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

            booleanMatrix = booleanMatrixService.createMatrix(booleanMatrix.getMatrix());

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

        System.out.println("проверочная матрица приведенная: ");
        booleanMatrixService.print(booleanMatrix);


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

        System.out.println("проверочная матрица приведенная сортированная: ");
        booleanMatrixService.print(booleanMatrix);

        List<Column> allColumnsByBooleanMatrix = columnService.getAllColumnsByBooleanMatrix(booleanMatrix);
        List<Column> columnsForGeneratingMatrix = IntStream.range(booleanMatrix.getSizeY(), booleanMatrix.getSizeX())
                .mapToObj(allColumnsByBooleanMatrix::get)
                .collect(Collectors.toList());


        BooleanMatrix booleanGeneratingMatrix = booleanMatrixService.createMatrix(rowService.mapColumnsToRows(columnsForGeneratingMatrix));
        BooleanMatrix booleanIdentityMatrix = booleanMatrixService.createIdentityMatrix(booleanGeneratingMatrix.getSizeY());
        for (int i = 0; i < booleanGeneratingMatrix.getSizeY(); i++) {
            booleanGeneratingMatrix.getMatrix().get(i).getElements().addAll(booleanIdentityMatrix.getMatrix().get(i).getElements());
        }
        booleanGeneratingMatrix = booleanMatrixService.createMatrix(booleanGeneratingMatrix.getMatrix());
        return new GeneratingMatrix(booleanGeneratingMatrix);
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
