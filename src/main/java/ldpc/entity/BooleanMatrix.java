package ldpc.entity;

import java.util.List;

/**
 * Примитивный класс матрицы
 */
public class BooleanMatrix {

    private List<List<Boolean>> matrix;

    public BooleanMatrix(List<List<Boolean>> matrix) {
        this.matrix = matrix;
    }

    public List<List<Boolean>> getMatrix() {
        return matrix;
    }

    public void setMatrix(List<List<Boolean>> matrix) {
        this.matrix = matrix;
    }
}