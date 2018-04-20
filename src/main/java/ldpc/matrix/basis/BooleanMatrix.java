package ldpc.matrix.basis;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Примитивный класс матрицы
 */
public class BooleanMatrix {

    private List<Row> matrix;
    private int sizeX;
    private int sizeY;
    private double density;

    public BooleanMatrix(List<Row> matrix, int sizeX, int sizeY, double density) {
        this.matrix = matrix;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.density = density;
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

    public double getDensity() {
        return density;
    }

    @Override
    public String toString() {
        String matrixString = matrix.stream()
                .map(Row::toString)
                .collect(Collectors.joining(" \n"));
        String sizeXString = "Size X = " + sizeX;
        String sizeYString = "Size Y = " + sizeY;
        String densityString = "Density = " + density;
        return String.join("\n---\n", sizeXString, sizeYString, densityString);
    }
}
