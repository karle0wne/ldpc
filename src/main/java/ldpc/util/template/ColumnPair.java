package ldpc.util.template;

public class ColumnPair {

    private Integer columnNumberLeft;
    private Integer columnNumberRight;

    public ColumnPair(Integer columnNumberLeft, Integer columnNumberRight) {
        this.columnNumberLeft = columnNumberLeft;
        this.columnNumberRight = columnNumberRight;
    }

    public Integer getColumnNumberLeft() {
        return columnNumberLeft;
    }

    public void setColumnNumberLeft(Integer columnNumberLeft) {
        this.columnNumberLeft = columnNumberLeft;
    }

    public Integer getColumnNumberRight() {
        return columnNumberRight;
    }

    public void setColumnNumberRight(Integer columnNumberRight) {
        this.columnNumberRight = columnNumberRight;
    }

    @Override
    public String toString() {
        return "Столбец № " + columnNumberLeft.toString() + " -> столбец № " + columnNumberRight.toString();
    }
}
