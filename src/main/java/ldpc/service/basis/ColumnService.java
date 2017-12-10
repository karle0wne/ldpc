package ldpc.service.basis;

import ldpc.matrix.basis.BooleanMatrix;
import ldpc.matrix.basis.Column;
import ldpc.matrix.basis.Row;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
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

    public Column getColumnByIndex(BooleanMatrix booleanMatrix, int index) {
        List<Boolean> columnElements = booleanMatrix.getMatrix().stream()
                .map(row -> row.get(index))
                .collect(Collectors.toList());
        return newColumn(columnElements);
    }

    /**
     * см описание в RowService
     */
    public Column createColumn(@NotNull Integer... elements) {
        List<Boolean> columnElements = Arrays.stream(elements)
                .map(element -> element == 1)
                .collect(Collectors.toList());
        return newColumn(columnElements);
    }

    public Column createColumn(@NotNull Boolean... elements) {
        return newColumn(Arrays.asList(elements));
    }

    public List<Column> newColumns(List<Column> columns) {
        return columns.stream()
                .map(this::newColumn)
                .collect(Collectors.toList());
    }

    public Column newColumn(Row row) {
        return newColumn(newElements(row.getElements()));
    }

    public Column newColumn(Column column) {
        return newColumn(newElements(column.getElements()));
    }

    public Column newColumn(List<Boolean> elements) {
        return new Column(newElements(elements));
    }

    private ArrayList<Boolean> newElements(List<Boolean> elements) {
        return new ArrayList<>(elements);
    }
}
