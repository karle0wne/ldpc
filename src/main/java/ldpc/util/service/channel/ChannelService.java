package ldpc.util.service.channel;

import ldpc.matrix.basis.BooleanMatrix;
import ldpc.matrix.basis.Row;
import ldpc.service.basis.BooleanMatrixService;
import ldpc.util.service.CodeWordService;
import ldpc.util.template.CodeWord;
import ldpc.util.template.LDPCEnums;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class ChannelService {

    private final CodeWordService codeWordService;
    private final BooleanMatrixService booleanMatrixService;
    private final AWGNService AWGNService;

    @Autowired
    public ChannelService(CodeWordService codeWordService, BooleanMatrixService booleanMatrixService, AWGNService AWGNService) {
        this.codeWordService = codeWordService;
        this.booleanMatrixService = booleanMatrixService;
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

    public double getProbabilityBitsErrorsCodeWord(BooleanMatrix codeWord, CodeWord codeWordByChannel) {
        return booleanMatrixService.getProbabilityBitsErrors(booleanMatrixService.getFirstRowValues(codeWord), booleanMatrixService.getFirstRowValues(codeWordService.getBooleanMatrix(codeWordByChannel)));
    }

    private CodeWord dummy(Row codeWord) {
        Random random = new Random();
        List<Double> softMetrics = codeWord.getElements().stream()
                .map(element -> codeWordService.getSoftMetric(random, element))
                .collect(Collectors.toList());
        return codeWordService.newCodeWord(softMetrics);
    }
}
