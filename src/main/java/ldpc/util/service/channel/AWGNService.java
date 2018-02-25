package ldpc.util.service.channel;

import ldpc.matrix.basis.Row;
import ldpc.util.service.CodeWordService;
import ldpc.util.template.CodeWord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static java.lang.Math.log;

@Service
public class AWGNService {

    private static final double DESIRED_STANDARD_DEVIATION = 100.0D;
    private static final double DESIRED_MEAN = 500.0D;
    private static double RIGHT_BORDER;
    private static double LEFT_BORDER;

    private final CodeWordService codeWordService;

    @Autowired
    public AWGNService(CodeWordService codeWordService) {
        this.codeWordService = codeWordService;
    }

    CodeWord send(Row codeWord, int percentage) {
        int border = getBorder(percentage);
        RIGHT_BORDER = DESIRED_MEAN + (double) border;
        LEFT_BORDER = DESIRED_MEAN - (double) border;

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
        if (isRange(value, LEFT_BORDER, RIGHT_BORDER)) {
            return getSoftMetric(random, element);
        } else {
            return getSoftMetric(random, !element);
        }
    }

    private double getSoftMetric(Random random, Boolean element) {
        if (element) {
            double softMetric = getSoftMetric(random);
            return log((1 - softMetric) / (softMetric));
        } else {
            double softMetric = getSoftMetric(random);
            return log((softMetric) / (1 - softMetric));
        }
    }

    private double getSoftMetric(Random random) {
        double softMetric = random.nextDouble();
        while (!isRange(softMetric, 0.5D, 1.0D)) {
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
