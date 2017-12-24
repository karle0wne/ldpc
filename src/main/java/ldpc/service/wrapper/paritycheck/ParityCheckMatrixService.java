package ldpc.service.wrapper.paritycheck;

import ldpc.matrix.basis.BooleanMatrix;
import ldpc.matrix.basis.Row;
import ldpc.matrix.wrapper.paritycheck.ParityCheckMatrix;
import ldpc.service.basis.BooleanMatrixService;
import ldpc.service.basis.RowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Сервис для проверочной матрицы
 */
@Service
public class ParityCheckMatrixService {

    private final RowService rowService;

    private final BooleanMatrixService booleanMatrixService;

    @Autowired
    public ParityCheckMatrixService(RowService rowService, BooleanMatrixService booleanMatrixService) {
        this.rowService = rowService;
        this.booleanMatrixService = booleanMatrixService;
    }

    /*
    * блок обслуживающий создание матриц функций
    * */
    public ParityCheckMatrix generateParityCheckMatrix() {
        // TODO: 16.12.2017 https://krsk-sibsau-dev.myjetbrains.com/youtrack/issue/LDPC-13 @a.karlov
        return prepared_PCM_LDPC();
    }
    
    public ParityCheckMatrix preparedPCM() {
        List<Row> matrix = new ArrayList<>();
        matrix.add(rowService.createRow(1, 1, 0, 0, 1, 0));
        matrix.add(rowService.createRow(0, 1, 1, 0, 0, 1));
        matrix.add(rowService.createRow(0, 0, 1, 1, 1, 0));
        matrix.add(rowService.createRow(1, 0, 0, 1, 0, 1));
        return newParityCheckMatrix(booleanMatrixService.newMatrix(matrix));
    }

    public ParityCheckMatrix preparedPCM2() {
        List<Row> matrix = new ArrayList<>();
        matrix.add(rowService.createRow(0, 1, 1, 1, 1, 0, 0));
        matrix.add(rowService.createRow(1, 0, 1, 1, 0, 1, 0));
        matrix.add(rowService.createRow(1, 1, 0, 1, 0, 0, 1));
        return newParityCheckMatrix(booleanMatrixService.newMatrix(matrix));
    }

    private ParityCheckMatrix prepared_PCM_LDPC() {
        List<Row> matrix = new ArrayList<>();
        matrix.add(rowService.createRow(1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        matrix.add(rowService.createRow(0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        matrix.add(rowService.createRow(0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0));
        matrix.add(rowService.createRow(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0));
        matrix.add(rowService.createRow(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1));
        matrix.add(rowService.createRow(1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0));
        matrix.add(rowService.createRow(0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0));
        matrix.add(rowService.createRow(0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0));
        matrix.add(rowService.createRow(0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0));
        matrix.add(rowService.createRow(0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1));
        matrix.add(rowService.createRow(1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0));
        matrix.add(rowService.createRow(0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0));
        matrix.add(rowService.createRow(0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0));
        matrix.add(rowService.createRow(0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0));
        matrix.add(rowService.createRow(0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1));
        return newParityCheckMatrix(booleanMatrixService.newMatrix(matrix));
    }

    private ParityCheckMatrix prepared_PCM_LDPC2() {
        List<Row> matrix = new ArrayList<>();
        matrix.add(rowService.createRow(0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1));
        matrix.add(rowService.createRow(1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1, 1, 0, 0, 1));
        matrix.add(rowService.createRow(0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0));
        matrix.add(rowService.createRow(0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 1, 0, 0));
        matrix.add(rowService.createRow(0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0));
        matrix.add(rowService.createRow(0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 1));
        matrix.add(rowService.createRow(1, 0, 0, 1, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0));
        matrix.add(rowService.createRow(0, 0, 1, 0, 0, 1, 1, 0, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0));
        matrix.add(rowService.createRow(1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 1, 1, 0, 0, 0, 1, 0));
        matrix.add(rowService.createRow(0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 1, 0, 0, 0, 1, 0, 1, 0));
        return newParityCheckMatrix(booleanMatrixService.newMatrix(matrix));
    }

    public ParityCheckMatrix newParityCheckMatrix(ParityCheckMatrix parityCheckMatrix) {
        return new ParityCheckMatrix(booleanMatrixService.newMatrix(parityCheckMatrix.getBooleanMatrix()));
    }

    public ParityCheckMatrix newParityCheckMatrix(BooleanMatrix booleanMatrix) {
        return new ParityCheckMatrix(booleanMatrixService.newMatrix(booleanMatrix));
    }
}
