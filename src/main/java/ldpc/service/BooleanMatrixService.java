package ldpc.service;

import ldpc.entity.BooleanMatrix;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Сервис для матрицы, где
 * row строка
 * column столбец
 * способ хранения:
 * .........column
 * row  <b>element</b>
 */
public class BooleanMatrixService {

    //TODO дополнить сервис
    private static final Logger LOGGER = Logger.getLogger(BooleanMatrixService.class.getName());
    private static final int MODULE = 2;

    public BooleanMatrix multiplicationMatrix(BooleanMatrix booleanMatrixA, BooleanMatrix booleanMatrixB) {
        if (!isValidMatrixForMultiplication(booleanMatrixA, booleanMatrixB)) {
            LOGGER.warning("bad matrix!");
            return null;
        }

        List<List<Boolean>> matrixA = booleanMatrixA.getMatrix();

        List<List<Boolean>> transposedMatrixB = getTransposedMatrix(booleanMatrixB);

        List<List<Boolean>> resultTransposedMatrix = new ArrayList<>();
        for (List<Boolean> rowMatrixA : matrixA) {
            List<Boolean> resultRow = new ArrayList<>();
            for (List<Boolean> rowTransposedMatrixB : transposedMatrixB) {

                if (!isValidRowsForMultiplication(rowMatrixA, rowTransposedMatrixB)) {
                    return null;
                }

                resultRow.add(multiplicationRows(rowMatrixA, rowTransposedMatrixB));

            }
            resultTransposedMatrix.add(resultRow);
        }

        List<List<Boolean>> resultMatrix = getTransposedMatrix(new BooleanMatrix(resultTransposedMatrix));
        return new BooleanMatrix(resultMatrix);
    }

    private boolean multiplicationRows(List<Boolean> rowA, List<Boolean> rowB) {
        List<Boolean> rowTemp = new ArrayList<>();
        int sizeRow = rowA.size();
        for (int i = 0; i < sizeRow; i++) {
            rowTemp.add(rowA.get(i) && rowB.get(i));
        }

        List<Boolean> onlyTrueRowTemp = rowTemp.stream().filter(element -> element).collect(Collectors.toList());

        return (onlyTrueRowTemp.size() % MODULE != 0);
    }

    private boolean isValidRowsForMultiplication(List<Boolean> rowA, List<Boolean> rowB) {
        if (rowA.size() != rowB.size()) {
            LOGGER.warning("bad matrix rows!");
            return false;
        }
        return true;
    }

    private List<List<Boolean>> getTransposedMatrix(BooleanMatrix booleanMatrix) {
        List<List<Boolean>> matrix = booleanMatrix.getMatrix();
        List<List<Boolean>> transposedMatrix = new ArrayList<>();
        for (int i = 0; i < matrix.size(); i++) {
            transposedMatrix.add(getColumnByIndex(booleanMatrix, i));
        }
        return transposedMatrix;
    }

    private boolean isValidMatrixForMultiplication(BooleanMatrix leftBooleanMatrix, BooleanMatrix rightBooleanMatrix) {
        List<List<Boolean>> leftMatrix = leftBooleanMatrix.getMatrix();
        List<List<Boolean>> rightMatrix = rightBooleanMatrix.getMatrix();

        List<Integer> lengthLeftMatrixRows = leftMatrix.stream()
                .map(List::size)
                .collect(Collectors.toList());

        List<Integer> lengthRightMatrixColumns = new ArrayList<>();
        for (int i = 0; i < rightMatrix.size(); i++) {
            int size = getColumnByIndex(rightBooleanMatrix, i).size();
            lengthRightMatrixColumns.add(size);
        }

        for (Integer lengthLeftRows : lengthLeftMatrixRows) {
            for (Integer lengthRightColumn : lengthRightMatrixColumns) {
                if ((int) lengthRightColumn != lengthLeftRows) {
                    return false;
                }
            }
        }
        return true;
    }

    public List<Boolean> getRowByIndex(BooleanMatrix booleanMatrix, int index) {
        List<List<Boolean>> matrix = booleanMatrix.getMatrix();
        return matrix.get(index);
    }

    private List<Boolean> getColumnByIndex(BooleanMatrix booleanMatrix, int index) {
        List<List<Boolean>> matrix = booleanMatrix.getMatrix();
        List<Boolean> column = new ArrayList<>();
        for (List<Boolean> row : matrix) {
            column.add(row.get(index));
        }
        return column;
    }

    public void printlnMatrix(BooleanMatrix booleanMatrix) {
        List<List<Boolean>> matrix = booleanMatrix.getMatrix();
        for (List<Boolean> row : matrix) {
            for (Boolean element : row) {
                if (element) {
                    System.out.println("1 ");
                } else {
                    System.out.println("0 ");
                }
            }
            System.out.println("\n");
        }
    }
}
