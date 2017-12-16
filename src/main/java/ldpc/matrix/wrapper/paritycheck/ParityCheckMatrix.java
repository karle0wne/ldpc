package ldpc.matrix.wrapper.paritycheck;

import ldpc.matrix.basis.BooleanMatrix;

/**
 * Проверочная матрица
 */
public class ParityCheckMatrix {

    private BooleanMatrix booleanMatrix;

    public ParityCheckMatrix(BooleanMatrix booleanMatrix) {
        this.booleanMatrix = booleanMatrix;
    }

    public BooleanMatrix getBooleanMatrix() {
        return booleanMatrix;
    }

    @Override
    public String toString() {
        return String.join("\n", "ПРОВЕРОЧНАЯ МАТРИЦА: ", booleanMatrix.toString());
    }
}
