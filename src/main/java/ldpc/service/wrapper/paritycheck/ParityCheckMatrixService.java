package ldpc.service.wrapper.paritycheck;

import ldpc.matrix.basis.BooleanMatrix;
import ldpc.matrix.basis.Row;
import ldpc.matrix.wrapper.paritycheck.ParityCheckMatrix;
import ldpc.service.basis.BooleanMatrixService;
import ldpc.service.basis.RowService;
import ldpc.util.template.LDPCEnums;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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
    public ParityCheckMatrix generateParityCheckMatrix(LDPCEnums.TypeOfCoding typeOfCoding) {
        switch (typeOfCoding) {
            case LDPC_DUMMY_ONE:
                return prepared_PCM_LDPC();
            case LDPC_DUMMY_TWO:
                return prepared_PCM_LDPC1();
            case LDPC_DUMMY_THREE:
                return prepared_PCM_LDPC2();
            case PCM_DUMMY:
                return preparedPCM();
            case LDPC_ONE:
                return generateWithoutGFourAndGSix(3);
            case LDPC_TWO:
                return generateWithoutGFourAndGSixVersionTwo(3);
            default:
                return prepared_PCM_LDPC();
        }
    }

    private ParityCheckMatrix generateWithoutGFourAndGSix(int k) {
        return newParityCheckMatrix(booleanMatrixService.newMatrix(getLDPCBlock(k)));
    }

    private ParityCheckMatrix generateWithoutGFourAndGSixVersionTwo(int k) {
        return newParityCheckMatrix(booleanMatrixService.newMatrix(getLDPCBlockVersionTwo(k)));
    }

    private List<Row> getLDPCBlock(int k) {
        return Stream.concat(getBlock(k, BlockType.FIRST), getBlock(k, BlockType.LAST))
                .collect(Collectors.toList());
    }

    private List<Row> getZeroBlock(int k) {
        return Stream.concat(getBlock(k, BlockType.ZERO), getBlock(k, BlockType.ZERO))
                .collect(Collectors.toList());
    }

    private Stream<Row> getBlock(int k, BlockType blockType) {
        return IntStream.range(0, k)
                .mapToObj(i -> getLine(k, blockType, i))
                .map(rowService::newRow);
    }

    private List<Boolean> getLine(int k, BlockType blockType, int i) {
        return IntStream.range(0, k)
                .mapToObj(j -> getPartOfTheLine(k, blockType, i, j))
                .map(Row::getElements)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private Row getPartOfTheLine(int k, BlockType blockType, int i, int j) {
        switch (blockType) {
            case FIRST:
                return getPartOfTheFirstLine(k, i, j);
            case LAST:
                return getPartOfTheLastLine(k, i);
            case ZERO:
                return getPartOfTheZeroLine(k);
            default:
                return getPartOfTheZeroLine(k);
        }
    }

    private Row getPartOfTheFirstLine(int k, int i, int j) {
        if (i == j) {
            return rowService.newRow(booleanMatrixService.generateElements(k, true));
        } else {
            return rowService.newRow(booleanMatrixService.generateElements(k, false));
        }
    }

    private Row getPartOfTheLastLine(int k, int i) {
        List<Boolean> elements = booleanMatrixService.generateElements(k, false);
        elements.set(i, true);
        return rowService.newRow(elements);
    }

    private Row getPartOfTheZeroLine(int k) {
        return rowService.newRow(booleanMatrixService.generateElements(k, false));
    }

    private List<Row> getLDPCBlockVersionTwo(int k) {
        return Stream.concat(getBlock(k), getLastLine(k))
                .collect(Collectors.toList());
    }

    private Stream<Row> getBlock(int k) {
        return IntStream.range(0, k)
                .mapToObj(i -> getLine(k, i))
                .flatMap(Collection::stream);
    }

    private List<Row> getLine(int k, int i) {
        List<BooleanMatrix> line = IntStream.range(0, k)
                .mapToObj(j -> getPartOfLine(k, i, j))
                .map(booleanMatrixService::newMatrix)
                .collect(Collectors.toList());
        return mergeLine(line);
    }

    private List<Row> getPartOfLine(int k, int i, int j) {
        if (i == j) {
            return getLDPCBlock(k);
        } else {
            return getZeroBlock(k);
        }
    }

    private Stream<Row> getLastLine(int k) {
        List<BooleanMatrix> line = IntStream.range(0, k)
                .mapToObj(i -> booleanMatrixService.createIdentityMatrix(BigInteger.valueOf(k).pow(2).intValue()))
                .collect(Collectors.toList());
        return mergeLine(line).stream();
    }

    private List<Row> mergeLine(List<BooleanMatrix> booleanMatrices) {
        Integer lineSize = getLineSize(booleanMatrices);
        return IntStream.range(0, lineSize)
                .mapToObj(
                        i -> booleanMatrices.stream()
                                .flatMap(booleanMatrix -> booleanMatrix.getMatrix().get(i).getElements().stream())
                                .collect(Collectors.toList())
                )
                .map(rowService::newRow)
                .collect(Collectors.toList());
    }

    private Integer getLineSize(List<BooleanMatrix> booleanMatrices) {
        Integer maxX = booleanMatrices.stream()
                .mapToInt(BooleanMatrix::getSizeX)
                .max()
                .orElseThrow(RuntimeException::new);
        Integer minX = booleanMatrices.stream()
                .mapToInt(BooleanMatrix::getSizeX)
                .min()
                .orElseThrow(RuntimeException::new);

        Integer maxY = booleanMatrices.stream()
                .mapToInt(BooleanMatrix::getSizeY)
                .max()
                .orElseThrow(RuntimeException::new);
        Integer minY = booleanMatrices.stream()
                .mapToInt(BooleanMatrix::getSizeY)
                .min()
                .orElseThrow(RuntimeException::new);

        if (!Objects.equals(minX, maxX)) {
            throw new RuntimeException("Ошибка генерации!");
        }

        if (!Objects.equals(minY, maxY)) {
            throw new RuntimeException("Ошибка генерации!");
        }
        return minY;
    }

    private ParityCheckMatrix preparedPCM() {
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

    private ParityCheckMatrix prepared_PCM_LDPC1() {
        List<Row> matrix = new ArrayList<>();
        matrix.add(rowService.createRow(1, 1, 0, 0, 1, 0));
        matrix.add(rowService.createRow(0, 1, 1, 0, 0, 1));
        matrix.add(rowService.createRow(0, 0, 1, 1, 1, 0));
        matrix.add(rowService.createRow(1, 0, 0, 1, 0, 1));
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

    private enum BlockType {
        FIRST,
        LAST,
        ZERO
    }
}
