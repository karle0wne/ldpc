package ldpc.service;

import ldpc.entity.BooleanMatrix;

import java.util.ArrayList;
import java.util.List;

/**
 * Сервис для матрицы, где
 * row строка
 * column столбец
 * способ хранения:
 * column
 * row  <b>element</b>
 */
public class BooleanMatrixService {

    //TODO дополнить сервис

    public List<Boolean> getRowByIndex(BooleanMatrix booleanMatrix, int index) {
        List<List<Boolean>> matrix = booleanMatrix.getMatrix();
        return matrix.get(index);
    }

    public List<Boolean> getColumnByIndex(BooleanMatrix booleanMatrix, int index) {
        List<List<Boolean>> matrix = booleanMatrix.getMatrix();
        List<Boolean> column = new ArrayList<Boolean>();
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
