package ldpc.util.service.channel;

import ldpc.matrix.basis.Row;
import ldpc.util.service.CodeWordService;
import ldpc.util.template.CodeWord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class AWGNService {

    private static final double DESIRED_STANDARD_DEVIATION = 100.0D;
    private static final double DESIRED_MEAN = 500.0D;
    private static double RIGHT_BORDER;
    private static double LEFT_BORDER;

    private final CodeWordService codeWordService;

    private final Map<Double, Integer> borderBySignal = new HashMap<Double, Integer>() {{
        put(0D, 68);
        put(0.25D, 118);
        put(0.5D, 142);
        put(0.75D, 160);
        put(1D, 176);
        put(1.25D, 191);
        put(1.5D, 204);
        put(1.75D, 217);
        put(2D, 228);
        put(2.25D, 239);
        put(2.5D, 250);
        put(2.75D, 260);
        put(3D, 269);
        put(3.25D, 280);
        put(3.5D, 288);
        put(3.75D, 297);
        put(4D, 304);
        put(4.25D, 313);
        put(4.5D, 319);
        put(4.75D, 329);
        put(5D, 336);
        put(5.25D, 344);
        put(5.5D, 349);
        put(5.75D, 355);
        put(6D, 362);
        put(6.25D, 373);
        put(6.5D, 386);
        put(6.75D, 394);
        put(7D, 409);
        put(7.25D, 416);
        put(7.5D, 427);
        put(7.75D, 444);
        put(8D, 492);
        put(8.25D, 496);
        put(8.5D, 497);
        put(8.75, 499);
    }};

    @Autowired
    public AWGNService(CodeWordService codeWordService) {
        this.codeWordService = codeWordService;
    }

    CodeWord send(Row codeWord, double signalPower) {
        RIGHT_BORDER = DESIRED_MEAN + borderBySignal.get(signalPower);
        LEFT_BORDER = DESIRED_MEAN - borderBySignal.get(signalPower);

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
