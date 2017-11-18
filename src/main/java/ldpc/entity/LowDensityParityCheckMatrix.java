package ldpc.entity;

/**
 * Проверочная матрица с малой плотностью проверок на четность
 */
public class LowDensityParityCheckMatrix {

    private BooleanMatrix booleanMatrix;
    private int K;
    private int J;
    private int G;

    /**
     * @param booleanMatrix сама матрица
     * @param K             количество единиц в каждой строке
     * @param J             количество единиц в каждом столбце
     * @param G             обхват графа
     */
    public LowDensityParityCheckMatrix(BooleanMatrix booleanMatrix, int K, int J, int G) {
        this.booleanMatrix = booleanMatrix;
        this.K = K;
        this.J = J;
        this.G = G;
    }

    public BooleanMatrix getBooleanMatrix() {
        return booleanMatrix;
    }

    public void setBooleanMatrix(BooleanMatrix booleanMatrix) {
        this.booleanMatrix = booleanMatrix;
    }

    public int getK() {
        return K;
    }

    public void setK(int k) {
        K = k;
    }

    public int getJ() {
        return J;
    }

    public void setJ(int j) {
        J = j;
    }

    public int getG() {
        return G;
    }

    public void setG(int g) {
        G = g;
    }
}
