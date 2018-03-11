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
        RIGHT_BORDER = DESIRED_MEAN + (double) percentage;
        LEFT_BORDER = DESIRED_MEAN - (double) percentage;

        return defectRow(codeWord);
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
        if (codeWordService.isRange(value, LEFT_BORDER, RIGHT_BORDER)) {
            return codeWordService.getSoftMetric(random, element);
        } else {
            return codeWordService.getSoftMetric(random, !element);
        }
    }
}
