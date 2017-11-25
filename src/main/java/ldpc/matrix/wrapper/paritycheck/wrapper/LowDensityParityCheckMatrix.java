package ldpc.matrix.wrapper.paritycheck.wrapper;

import ldpc.matrix.wrapper.paritycheck.ParityCheckMatrix;

/**
 * Проверочная матрица с малой плотностью проверок на четность
 */
public class LowDensityParityCheckMatrix {

    private ParityCheckMatrix booleanMatrix;
    private int K;
    private int J;
    private int G;

    /**
     * @param booleanMatrix сама матрица
     * @param K             количество единиц в каждой строке
     * @param J             количество единиц в каждом столбце
     * @param G             обхват графа
     */
    public LowDensityParityCheckMatrix(ParityCheckMatrix booleanMatrix, int K, int J, int G) {
        this.booleanMatrix = booleanMatrix;
        this.K = K;
        this.J = J;
        this.G = G;
    }

    public ParityCheckMatrix getBooleanMatrix() {
        return booleanMatrix;
    }

    public int getK() {
        return K;
    }

    public int getJ() {
        return J;
    }

    public int getG() {
        return G;
    }
}
