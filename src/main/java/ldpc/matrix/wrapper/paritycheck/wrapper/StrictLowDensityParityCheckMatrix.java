package ldpc.matrix.wrapper.paritycheck.wrapper;

import ldpc.matrix.basis.BooleanMatrix;
import ldpc.matrix.wrapper.paritycheck.ParityCheckMatrix;

/**
 * Проверочная матрица с малой плотностью проверок на четность
 */
public class StrictLowDensityParityCheckMatrix extends ParityCheckMatrix {

    private final long K;
    private final long J;
    private final long G;

    public StrictLowDensityParityCheckMatrix(BooleanMatrix booleanMatrix, long k, long j, long g) {
        super(booleanMatrix);
        this.K = k;
        this.J = j;
        this.G = g;
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

    @Override
    public String toString() {
        String matrix = super.toString();
        String k = "K = " + K;
        String j = "J = " + J;
        String g = "G = " + G;
        return String.join("\n---\n", matrix, k, j, g);
    }
}
