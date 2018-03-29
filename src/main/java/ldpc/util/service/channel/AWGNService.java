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
    private final CodeWordService codeWordService;

    private final Map<Double, Double> gaussianCoefficient = new HashMap<Double, Double>() {{
        put(0.25D, 1.414);
        put(0.5D, 1.);
        put(0.75D, 0.816);
        put(1D, 0.706);
        put(1.25D, 0.632);
        put(1.5D, 0.577);
        put(1.75D, 0.534);
        put(2D, 0.499);
        put(2.25D, 0.471);
        put(2.5D, 0.447);
        put(2.75D, 0.426);
        put(3D, 0.408);
        put(3.25D, 0.392);
        put(3.5D, 0.377);
        put(3.75D, 0.364);
        put(4D, 0.353);
        put(4.25D, 0.342);
        put(4.5D, 0.333);
        put(4.75D, 0.323);
        put(5D, 0.315);
        put(5.25D, 0.306);
        put(5.5D, 0.299);
        put(5.75D, 0.292);
        put(6D, 0.286);
        put(6.25D, 0.28);
        put(6.5D, 0.273);
        put(6.75D, 0.266);
        put(7D, 0.26);
        put(7.25D, 0.252);
        put(7.5D, 0.244);
        put(7.75D, 0.234);
    }};

    @Autowired
    public AWGNService(CodeWordService codeWordService) {
        this.codeWordService = codeWordService;
    }

    CodeWord send(Row codeWord, double signalPower) {
        Random random = new Random();
        List<Double> defaultSoftMetrics = codeWord.getElements().stream()
                .map(this::getMetric)
                .collect(Collectors.toList());

        List<Double> softMetrics = defaultSoftMetrics.stream()
                .map(defaultMetric -> getGaussian(random, signalPower, defaultMetric))
                .collect(Collectors.toList());

        return codeWordService.newCodeWord(softMetrics);
    }

    private double getGaussian(Random random, double signalPower, Double defaultSoftMetric) {
        return random.nextGaussian() * gaussianCoefficient.get(signalPower) + defaultSoftMetric;
    }

    private Double getMetric(Boolean element) {
        return element ? -1. : 1.;
    }


}
