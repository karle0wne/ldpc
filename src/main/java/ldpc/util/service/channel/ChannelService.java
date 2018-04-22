package ldpc.util.service.channel;

import ldpc.matrix.basis.BooleanMatrix;
import ldpc.matrix.basis.Row;
import ldpc.util.service.CodeWordService;
import ldpc.util.template.CodeWord;
import ldpc.util.template.LDPCEnums;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChannelService {

    private final CodeWordService codeWordService;
    private final AWGNService AWGNService;

    @Autowired
    public ChannelService(CodeWordService codeWordService, AWGNService AWGNService) {
        this.codeWordService = codeWordService;
        this.AWGNService = AWGNService;
    }

    public CodeWord send(BooleanMatrix codeWord, LDPCEnums.TypeOfChannel typeOfChannel, double signalPower) {
        Row row = codeWord.getMatrix().get(0);
        if (typeOfChannel == null) {
            return dummy(row);
        }
        switch (typeOfChannel) {
            case AWGN:
                return AWGNService.send(row, signalPower);
            default:
                return dummy(row);
        }
    }

    private CodeWord dummy(Row codeWord) {
        List<Double> softMetrics = codeWord.getElements().stream()
                .map(this::getMetric)
                .collect(Collectors.toList());
        return codeWordService.newCodeWord(softMetrics);
    }

    private Double getMetric(Boolean element) {
        return element ? -1. : 1.;
    }
}
