package ldpc.matrix.basis;

import java.util.List;

public class Column {

    private List<Boolean> elements;

    public Column(List<Boolean> elements) {
        this.elements = elements;
    }

    public List<Boolean> getElements() {
        return elements;
    }

    public Boolean get(int index) {
        return elements.get(index);
    }
}
