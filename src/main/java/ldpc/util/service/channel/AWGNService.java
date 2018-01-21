package ldpc.util.service.channel;

import ldpc.matrix.basis.Row;
import ldpc.util.service.CodeWordService;
import ldpc.util.template.CodeWord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class AWGNService {

    public static final double DESIRED_STANDARD_DEVIATION = 100.0D;
    public static final double DESIRED_MEAN = 500.0D;
    private static double TOP_BORDER;
    private static double BOTTOM_BORDER;

    private final CodeWordService codeWordService;

    @Autowired
    public AWGNService(CodeWordService codeWordService) {
        this.codeWordService = codeWordService;
    }

    CodeWord send(Row codeWord, int percentage) {
        int border = getBorder(percentage);
        TOP_BORDER = DESIRED_MEAN + (double) border;
        BOTTOM_BORDER = DESIRED_MEAN - (double) border;

        return defectRow(codeWord);
    }

    CodeWord dummy(Row codeWord) {
        Random random = new Random();
        List<Double> softMetrics = codeWord.getElements().stream()
                .map(element -> getSoftMetric(random, element))
                .collect(Collectors.toList());
        return codeWordService.newCodeWord(softMetrics);
    }

    private CodeWord defectRow(Row row) {
        Random random = new Random();
        List<Double> softMetrics = row.getElements().stream()
                .map(element -> checkByGaussian(random, element))
                .collect(Collectors.toList());
        return codeWordService.newCodeWord(softMetrics);
    }

    private Double checkByGaussian(Random random, Boolean element) {
        double value = random.nextGaussian() * DESIRED_STANDARD_DEVIATION + DESIRED_MEAN;
        if (isRange(value, BOTTOM_BORDER, TOP_BORDER)) {
            return getSoftMetric(random, element);
        } else {
            return getSoftMetric(random, !element);
        }
    }

    private double getSoftMetric(Random random, Boolean element) {
        if (element) {
            return getSoftMetric(random, 0.5D, 1.0D);
        } else {
            return getSoftMetric(random, 0.0D, 0.5D);
        }
    }

    private double getSoftMetric(Random random, double left, double right) {
        double softMetric = random.nextDouble();
        while (!isRange(softMetric, left, right)) {
            softMetric = random.nextDouble();
        }
        return softMetric;
    }

    private boolean isRange(double value, double left, double right) {
        return left < value && value < right;
    }

    private int getBorder(int percentage) {
        int checkedPercentage = checkedPercentage(percentage);
        return checkedPercentage * 5;
    }

    private int checkedPercentage(int percentage) {
        return percentage >= 100
                ? 99
                : (percentage <= 0 ? 1 : percentage);
    }
}
