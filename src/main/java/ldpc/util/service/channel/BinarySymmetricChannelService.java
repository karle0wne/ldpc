package ldpc.util.service.channel;

import ldpc.matrix.basis.BooleanMatrix;
import ldpc.matrix.basis.Row;
import ldpc.service.basis.BooleanMatrixService;
import ldpc.service.basis.RowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class BinarySymmetricChannelService {

    private static final double DESIRED_STANDARD_DEVIATION = 100.0D;
    private static final double DESIRED_MEAN = 500.0D;
    private static double TOP_BORDER;
    private static double BOTTOM_BORDER;

    private final RowService rowService;
    private final BooleanMatrixService booleanMatrixService;

    @Autowired
    public BinarySymmetricChannelService(RowService rowService, BooleanMatrixService booleanMatrixService) {
        this.rowService = rowService;
        this.booleanMatrixService = booleanMatrixService;
    }

    BooleanMatrix send(BooleanMatrix booleanMatrix, double errorChance) {
        List<Row> matrix = booleanMatrix.getMatrix();
        double border = getBorder(errorChance);
        TOP_BORDER = DESIRED_MEAN + border;
        BOTTOM_BORDER = DESIRED_MEAN - border;

        List<Row> rows = matrix.stream()
                .map(this::defectRow)
                .map(rowService::newRow)
                .collect(Collectors.toList());

        return booleanMatrixService.newMatrix(rows);
    }

    private List<Boolean> defectRow(Row row) {
        Random random = new Random();
        return row.getElements().stream()
                .map(
                        element -> {
                            double value = random.nextGaussian() * DESIRED_STANDARD_DEVIATION + DESIRED_MEAN;
                            if (value < TOP_BORDER
                                    && value > BOTTOM_BORDER) {
                                return element;
                            } else {
                                return !element;
                            }
                        }
                )
                .collect(Collectors.toList());
    }

    private static double getBorder(double errorChance) {
        double errorChanceChecked = getErrorChance(errorChance);
        double result = Math.pow(1 - errorChanceChecked, 2.0D);
        return ((double) 2 * (DESIRED_STANDARD_DEVIATION + 1)) * result;
    }

    private static double getErrorChance(double errorChance) {
        return errorChance >= 1.0D
                ? 0.99D
                : (errorChance <= 0.0D ? 0.0001D : errorChance);
    }
}
