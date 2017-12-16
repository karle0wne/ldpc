package ldpc.matrix.wrapper.generating;

import ldpc.matrix.basis.BooleanMatrix;

/**
 * Порождающая матрица
 */
public class GeneratingMatrix {

    private BooleanMatrix booleanMatrix;

    public GeneratingMatrix(BooleanMatrix booleanMatrix) {
        this.booleanMatrix = booleanMatrix;
    }

    public BooleanMatrix getBooleanMatrix() {
        return booleanMatrix;
    }

    @Override
    public String toString() {
        return String.join("\n", "ПОРОЖДАЮЩАЯ МАТРИЦА: ", booleanMatrix.toString());
    }
}
