package ldpc.util.service.channel;

import ldpc.matrix.basis.Row;
import ldpc.util.service.CodeWordService;
import ldpc.util.template.CodeWord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class AWGNService {

    public static final Map<Double, Double> gaussianCoefficient = new LinkedHashMap<Double, Double>() {{
        put(0.9691001300805642, 0.7186);
        put(1.1394335230683679, 0.662);
        put(1.3033376849500613, 0.62);
        put(1.46128035678238, 0.5833);
        put(1.6136800223497487, 0.5563);
        put(1.7609125905568124, 0.5334);
        put(1.903316981702915, 0.512);
        put(2.041199826559248, 0.4934);
        put(2.1748394421390627, 0.473);
        put(2.3044892137827393, 0.4643);
        put(2.4303804868629446, 0.45434);
        put(2.5527250510330606, 0.44334);
        put(2.671717284030138, 0.432);
        put(2.787536009528289, 0.4234);
        put(2.9003461136251802, 0.4143);
        put(3.010299956639812, 0.4071);
    }};

    private final CodeWordService codeWordService;

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
        return 2 * (random.nextGaussian() * gaussianCoefficient.get(signalPower) + defaultSoftMetric) / (Math.pow(gaussianCoefficient.get(signalPower), 2.0d));
    }

    private Double getMetric(Boolean element) {
        return element ? -1. : 1.;
    }
}
