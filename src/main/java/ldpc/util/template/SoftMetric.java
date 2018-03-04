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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SoftMetric that = (SoftMetric) o;

        if (column != that.column) {
            return false;
        }
        if (row != that.row) {
            return false;
        }
        return Double.compare(that.metric, metric) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = column;
        result = 31 * result + row;
        temp = Double.doubleToLongBits(metric);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
