package ldpc.entity;

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

    public void setBooleanMatrix(BooleanMatrix booleanMatrix) {
        this.booleanMatrix = booleanMatrix;
    }
}
