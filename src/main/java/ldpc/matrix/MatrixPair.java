package ldpc.matrix;

import ldpc.matrix.wrapper.generating.GeneratingMatrix;
import ldpc.matrix.wrapper.paritycheck.wrapper.StrictLowDensityParityCheckMatrix;

/**
 * А это у нас будет пара порождающая-проверочная матрицы
 */
public class MatrixPair {

    private GeneratingMatrix generatingMatrix;
    private StrictLowDensityParityCheckMatrix ldpMatrix;

    public MatrixPair(GeneratingMatrix generatingMatrix, StrictLowDensityParityCheckMatrix ldpMatrix) {
        this.generatingMatrix = generatingMatrix;
        this.ldpMatrix = ldpMatrix;
    }

    public GeneratingMatrix getGeneratingMatrix() {
        return generatingMatrix;
    }

    public void setGeneratingMatrix(GeneratingMatrix generatingMatrix) {
        this.generatingMatrix = generatingMatrix;
    }

    public StrictLowDensityParityCheckMatrix getLdpMatrix() {
        return ldpMatrix;
    }

    public void setLdpMatrix(StrictLowDensityParityCheckMatrix ldpMatrix) {
        this.ldpMatrix = ldpMatrix;
    }
}
