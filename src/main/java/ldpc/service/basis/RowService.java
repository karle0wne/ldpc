package ldpc.service.basis;

import ldpc.matrix.basis.Column;
import ldpc.matrix.basis.Row;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RowService {

    public List<Row> mapColumnsToRows(List<Column> columns) {
        return columns.stream()
                .map(this::newRow)
                .collect(Collectors.toList());
    }

    public boolean isFullFalseElementsRow(Row row) {
        List<Boolean> falseElements = row.getElements().stream()
                .filter(element -> !element)
                .collect(Collectors.toList());

        return falseElements.size() == row.getElements().size();
    }

    /**
     * {@code createRow} предназначена для создания строки матрицы, можно сразу передавать boolean, но
     * если неудобно можно передавать в качестве параметра int элементы, причем если ошибочно будет передан элемент
     * element > 1, то функция с конвертирует в false, например,
     * createRow(1,1,0,2) результат будет 1,1,0,0
     */
    public Row createRow(@NotNull Integer... elements) {
        List<Boolean> rowElements = Arrays.stream(elements)
                .map(element -> element == 1)
                .collect(Collectors.toList());
        return newRow(rowElements);
    }

    public Row createRow(@NotNull Boolean... elements) {
        return newRow(Arrays.asList(elements));
    }

    public List<Row> newRows(List<Row> rows) {
        return rows.stream()
                .map(this::newRow)
                .collect(Collectors.toList());
    }

    public Row newRow(Row row) {
        return newRow(newElements(row.getElements()));
    }

    public Row newRow(Column column) {
        return newRow(newElements(column.getElements()));
    }

    public Row newRow(List<Boolean> elements) {
        return new Row(newElements(elements));
    }

    private ArrayList<Boolean> newElements(List<Boolean> elements) {
        return new ArrayList<>(elements);
    }
}
