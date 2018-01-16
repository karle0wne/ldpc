package ldpc.service.wrapper.paritycheck.wrapper;

import ldpc.matrix.basis.BooleanMatrix;
import ldpc.matrix.basis.Column;
import ldpc.matrix.basis.Row;
import ldpc.matrix.wrapper.paritycheck.ParityCheckMatrix;
import ldpc.matrix.wrapper.paritycheck.wrapper.StrictLowDensityParityCheckMatrix;
import ldpc.service.basis.BooleanMatrixService;
import ldpc.service.basis.ColumnService;
import ldpc.service.wrapper.paritycheck.ParityCheckMatrixService;
import ldpc.util.template.LDPCEnums;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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

    public StrictLowDensityParityCheckMatrix generateLDPCMatrix(LDPCEnums.TypeOfCoding typeOfCoding) {
        ParityCheckMatrix parityCheckMatrix = parityCheckMatrixService.generateParityCheckMatrix(typeOfCoding);
        return newStrictLDPCMatrix(parityCheckMatrix);
    }

    public StrictLowDensityParityCheckMatrix newStrictLDPCMatrix(ParityCheckMatrix parityCheckMatrix) {
        /**
         * strict - строгая матрица, в которой J,K это константы
         * */
        long k = getK(parityCheckMatrix);
        long j = getJ(parityCheckMatrix);
        long g = getG(parityCheckMatrix);
        ParityCheckMatrix matrix = parityCheckMatrixService.newParityCheckMatrix(parityCheckMatrix);
        if (!validate(matrix, k, j)) {
            throw new RuntimeException("Проверьте значения K, J и m*n матрицы!");
        }
        return new StrictLowDensityParityCheckMatrix(matrix, k, j, g);

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
        BooleanMatrix booleanMatrixStart = booleanMatrixService.newMatrix(parityCheckMatrix.getBooleanMatrix());
        int tier_level;

        for (int i = 0; i < booleanMatrixStart.getSizeY(); i++) {

            List<Integer> columnsForDelete = booleanMatrixService.getPositionsTrueElements(booleanMatrixStart.getMatrix().get(i).getElements());

            BooleanMatrix transposedBooleanMatrix = booleanMatrixService.getTransposedBooleanMatrix(booleanMatrixStart);

            BooleanMatrix booleanMatrix = booleanMatrixService.removeColumn(transposedBooleanMatrix, i);

            for (tier_level = 0; !booleanMatrixService.isEmpty(booleanMatrix) && !checkNotUniqueness(columnsForDelete); tier_level++) {

                List<Integer> columnsForDeleteForNextTierLevel = getColumnsForDelete(columnsForDelete, booleanMatrix);

                transposedBooleanMatrix = booleanMatrixService.getTransposedBooleanMatrix(booleanMatrixService.newMatrix(booleanMatrix));

                booleanMatrix = booleanMatrixService.removeColumns(transposedBooleanMatrix, columnsForDelete);

                columnsForDelete = new ArrayList<>(columnsForDeleteForNextTierLevel);
            }

            if (checkNotUniqueness(columnsForDelete)) {
                return (tier_level + 1) * 2;
            }
        }
        throw new RuntimeException("Не могу посчитать обхват матрицы G, проверьте алгоритм и матрицу!");
    }

    private List<Integer> getColumnsForDelete(List<Integer> numbersRowsForSearch, BooleanMatrix booleanMatrix) {
        return numbersRowsForSearch.stream()
                .flatMap(
                        numberRow -> {
                            Row row = booleanMatrix.getMatrix().get(numberRow);
                            return booleanMatrixService.getPositionsTrueElements(row.getElements()).stream();
                        }
                )
                .collect(Collectors.toList());
    }

    private boolean checkNotUniqueness(List<Integer> columns) {
        columns.sort(Comparator.comparing(Integer::intValue));
        Set<Integer> uniques = new HashSet<>();
        for (Integer column : columns) {
            if (!uniques.add(column)) {
                return true;
            }
        }
        return false;
    }

    private boolean validate(ParityCheckMatrix parityCheckMatrix, long k, long j) {
        return (j * parityCheckMatrix.getBooleanMatrix().getSizeX()) == (k * parityCheckMatrix.getBooleanMatrix().getSizeY());
    }
}
