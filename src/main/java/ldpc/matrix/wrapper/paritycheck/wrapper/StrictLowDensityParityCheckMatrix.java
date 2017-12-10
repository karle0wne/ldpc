package ldpc.matrix.wrapper.paritycheck.wrapper;

import ldpc.matrix.wrapper.paritycheck.ParityCheckMatrix;

/**
 * Проверочная матрица с малой плотностью проверок на четность
 */
public class StrictLowDensityParityCheckMatrix {

    private ParityCheckMatrix parityCheckMatrix;
    private long K;
    private long J;
    private long G;

    /**
     * @param parityCheckMatrix сама матрица
     * @param K             количество единиц в каждой строке
     * @param J             количество единиц в каждом столбце
     * @param G             обхват графа
     */
    public StrictLowDensityParityCheckMatrix(ParityCheckMatrix parityCheckMatrix, long K, long J, long G) {
        this.parityCheckMatrix = parityCheckMatrix;
        this.K = K;
        this.J = J;
        this.G = G;
    }

    public ParityCheckMatrix getParityCheckMatrix() {
        return parityCheckMatrix;
    }

    public long getK() {
        return K;
    }

    public long getJ() {
        return J;
    }

    public long getG() {
        return G;
    }
}
