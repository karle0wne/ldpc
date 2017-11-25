package ldpc.service.basis;

import ldpc.matrix.basis.Row;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RowService {

    public String rowToString(Row row) {
        return row.getElements().stream()
                .map(element -> element ? "1" : "0")
                .collect(Collectors.joining(", "));
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
        return new Row(rowElements);
    }

    public Row createRow(@NotNull Boolean... elements) {
        return new Row(Arrays.asList(elements));
    }
}
