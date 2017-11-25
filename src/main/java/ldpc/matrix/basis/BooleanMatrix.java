package ldpc.matrix.basis;

import java.util.List;

/**
 * Примитивный класс матрицы
 */
public class BooleanMatrix {

    private List<Row> matrix;

    private int sizeX;

    private int sizeY;

    public BooleanMatrix(List<Row> matrix, int sizeX, int sizeY) {
        this.matrix = matrix;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
    }

    public List<Row> getMatrix() {
        return matrix;
    }

    public int getSizeX() {
        return sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }
}
