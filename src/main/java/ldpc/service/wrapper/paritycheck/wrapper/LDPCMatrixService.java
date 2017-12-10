package ldpc.service.wrapper.paritycheck.wrapper;

import ldpc.matrix.basis.BooleanMatrix;
import ldpc.matrix.basis.Column;
import ldpc.matrix.basis.Row;
import ldpc.matrix.wrapper.paritycheck.ParityCheckMatrix;
import ldpc.matrix.wrapper.paritycheck.wrapper.StrictLowDensityParityCheckMatrix;
import ldpc.service.basis.BooleanMatrixService;
import ldpc.service.basis.ColumnService;
import ldpc.service.wrapper.paritycheck.ParityCheckMatrixService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class LDPCMatrixService {

    private final ParityCheckMatrixService parityCheckMatrixService;

    private final BooleanMatrixService booleanMatrixService;

    private final ColumnService columnService;

    @Autowired
    public LDPCMatrixService(ParityCheckMatrixService parityCheckMatrixService, BooleanMatrixService booleanMatrixService, ColumnService columnService) {
        this.parityCheckMatrixService = parityCheckMatrixService;
        this.booleanMatrixService = booleanMatrixService;
        this.columnService = columnService;
    }

    public StrictLowDensityParityCheckMatrix newStrictLDPCMatrix(ParityCheckMatrix parityCheckMatrix) {
        /**
         * strict - строгая матрица, в которой J,K это константы
         * */
        long k = getK(parityCheckMatrix);
        long j = getJ(parityCheckMatrix);
        long g = getG(parityCheckMatrix);
        return new StrictLowDensityParityCheckMatrix(parityCheckMatrixService.newParityCheckMatrix(parityCheckMatrix), k, j, g);

    }

    private long getK(ParityCheckMatrix parityCheckMatrix) {
        List<Row> matrix = parityCheckMatrix.getBooleanMatrix().getMatrix();
        Long maxK = matrix.stream()
                .mapToLong(row -> booleanMatrixService.getCountTrueElements(row.getElements()))
                .max()
                .orElseThrow(RuntimeException::new);

        Long minK = matrix.stream()
                .mapToLong(row -> booleanMatrixService.getCountTrueElements(row.getElements()))
                .min()
                .orElseThrow(RuntimeException::new);

        if (!Objects.equals(minK, maxK)) {
            throw new RuntimeException("Укажите LDPC матрицу с одинаковым количеством единиц в строках!");
        }
        return maxK;
    }

    private long getJ(ParityCheckMatrix parityCheckMatrix) {
        List<Column> matrix = columnService.getAllColumnsByBooleanMatrix(parityCheckMatrix.getBooleanMatrix());
        Long maxJ = matrix.stream()
                .mapToLong(column -> booleanMatrixService.getCountTrueElements(column.getElements()))
                .max()
                .orElseThrow(RuntimeException::new);

        Long minJ = matrix.stream()
                .mapToLong(column -> booleanMatrixService.getCountTrueElements(column.getElements()))
                .min()
                .orElseThrow(RuntimeException::new);

        if (!Objects.equals(minJ, maxJ)) {
            throw new RuntimeException("Укажите LDPC матрицу с одинаковым количеством единиц в столбцах!");
        }
        return maxJ;
    }

    private int getG(ParityCheckMatrix parityCheckMatrix) {
        BooleanMatrix booleanMatrix = parityCheckMatrix.getBooleanMatrix();
        // TODO: 10.12.2017 https://krsk-sibsau-dev.myjetbrains.com/youtrack/issue/LDPC-20
        return 0;
    }

    public void print(StrictLowDensityParityCheckMatrix ldpcMatrix) {
        booleanMatrixService.print(ldpcMatrix.getParityCheckMatrix().getBooleanMatrix());
        System.out.println("J = " + ldpcMatrix.getJ() + "\n" + "K = " + ldpcMatrix.getK() + "\n" + "G = " + ldpcMatrix.getG() + "\n");
    }
}
