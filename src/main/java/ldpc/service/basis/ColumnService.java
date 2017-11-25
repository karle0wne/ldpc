package ldpc.service.basis;

import ldpc.matrix.basis.BooleanMatrix;
import ldpc.matrix.basis.Column;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class ColumnService {

    public List<Column> getAllColumnsByBooleanMatrix(BooleanMatrix booleanMatrix) {
        return IntStream.range(0, booleanMatrix.getSizeX())
                .mapToObj(i -> getColumnByIndex(booleanMatrix, i))
                .collect(Collectors.toList());
    }

    private Column getColumnByIndex(BooleanMatrix booleanMatrix, int index) {
        List<Boolean> columnElements = booleanMatrix.getMatrix().stream()
                .map(row -> row.get(index))
                .collect(Collectors.toList());
        return new Column(columnElements);
    }

    /**
     * см описание в RowService
     */
    public Column createColumn(@NotNull Integer... elements) {
        List<Boolean> columnElements = Arrays.stream(elements)
                .map(element -> element == 1)
                .collect(Collectors.toList());
        return new Column(columnElements);
    }

    public Column createColumn(@NotNull Boolean... elements) {
        return new Column(Arrays.asList(elements));
    }
}
