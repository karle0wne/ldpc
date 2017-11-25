package ldpc.matrix;

import ldpc.matrix.wrapper.generating.GeneratingMatrix;
import ldpc.matrix.wrapper.paritycheck.wrapper.LowDensityParityCheckMatrix;

/**
 * А это у нас будет пара порождающая-проверочная матрицы
 */
public class MatrixPair {

    private GeneratingMatrix generatingMatrix;
    private LowDensityParityCheckMatrix ldpMatrix;

    public MatrixPair(GeneratingMatrix generatingMatrix, LowDensityParityCheckMatrix ldpMatrix) {
        this.generatingMatrix = generatingMatrix;
        this.ldpMatrix = ldpMatrix;
    }

    public GeneratingMatrix getGeneratingMatrix() {
        return generatingMatrix;
    }

    public void setGeneratingMatrix(GeneratingMatrix generatingMatrix) {
        this.generatingMatrix = generatingMatrix;
    }

    public LowDensityParityCheckMatrix getLdpMatrix() {
        return ldpMatrix;
    }

    public void setLdpMatrix(LowDensityParityCheckMatrix ldpMatrix) {
        this.ldpMatrix = ldpMatrix;
    }
}
