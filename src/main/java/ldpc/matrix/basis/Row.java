package ldpc.matrix.basis;

import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public String toString() {
        return elements.stream()
                .map(element -> element ? "1" : "0")
                .collect(Collectors.joining(", "));
    }
}
