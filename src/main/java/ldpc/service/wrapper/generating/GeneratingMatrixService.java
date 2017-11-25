package ldpc.service.wrapper.generating;

import ldpc.matrix.basis.Row;
import ldpc.matrix.wrapper.generating.GeneratingMatrix;
import ldpc.service.basis.BooleanMatrixService;
import ldpc.service.basis.RowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Сервис для порождающей матрицы
 */
@Service
public class GeneratingMatrixService {

    private final RowService rowService;

    private final BooleanMatrixService booleanMatrixService;

    @Autowired
    public GeneratingMatrixService(RowService rowService, BooleanMatrixService booleanMatrixService) {
        this.rowService = rowService;
        this.booleanMatrixService = booleanMatrixService;
    }

    public GeneratingMatrix createPreparedGeneratingMatrix() {
        List<Row> matrix = new ArrayList<>();
        matrix.add(rowService.createRow(1, 1, 1, 1, 0, 0));
        matrix.add(rowService.createRow(0, 1, 1, 0, 1, 0));
        matrix.add(rowService.createRow(1, 1, 0, 0, 0, 1));

        return new GeneratingMatrix(booleanMatrixService.createMatrix(matrix));
    }

    public GeneratingMatrix createPrepared2GeneratingMatrix() {
        List<Row> matrix = new ArrayList<>();
        matrix.add(rowService.createRow(1, 0, 0, 0, 0, 1, 1));
        matrix.add(rowService.createRow(0, 1, 0, 0, 1, 0, 1));
        matrix.add(rowService.createRow(0, 0, 1, 0, 1, 1, 0));
        matrix.add(rowService.createRow(0, 0, 0, 1, 1, 1, 1));

        return new GeneratingMatrix(booleanMatrixService.createMatrix(matrix));
    }
}
