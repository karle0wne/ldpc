package ldpc.matrix.basis;

import java.util.List;

public class Row {

    private List<Boolean> elements;

    public Row(List<Boolean> elements) {
        this.elements = elements;
    }

    public List<Boolean> getElements() {
        return elements;
    }

    public Boolean get(int index) {
        return elements.get(index);
    }
}
