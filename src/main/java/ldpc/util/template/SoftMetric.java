package ldpc.util.template;

public class SoftMetric {

    private int column;
    private int row;
    private double sign;
    private double metric;

    public SoftMetric(int column, int row, double metric) {
        this.column = column;
        this.row = row;
        this.sign = metric / Math.abs(metric);
        this.metric = metric;
    }

    public int getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }

    public double getSign() {
        return sign;
    }

    public double getMetric() {
        return metric;
    }

    public void setMetric(double metric) {
        this.sign = metric / Math.abs(metric);
        this.metric = metric;
    }
}
