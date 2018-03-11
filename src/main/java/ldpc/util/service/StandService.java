package ldpc.util.service;

import ldpc.matrix.basis.BooleanMatrix;
import ldpc.matrix.wrapper.generating.GeneratingMatrix;
import ldpc.matrix.wrapper.paritycheck.wrapper.StrictLowDensityParityCheckMatrix;
import ldpc.service.basis.BooleanMatrixService;
import ldpc.service.wrapper.generating.GeneratingMatrixService;
import ldpc.service.wrapper.paritycheck.wrapper.LDPCMatrixService;
import ldpc.util.service.channel.ChannelService;
import ldpc.util.service.decode.DecodeService;
import ldpc.util.template.CodeWord;
import ldpc.util.template.LDPCEnums;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class StandService {

    private static final String DELIMITER = "\n";
    private static final int COUNT_GENERATION = 1000;
    private static final int BORDER_ITERATOR = 3;
    private final BooleanMatrixService booleanMatrixService;

    private final GeneratingMatrixService generatingMatrixService;

    private final LDPCMatrixService ldpcMatrixService;

    private final ChannelService channelService;

    private final DecodeService decodeService;

    @Autowired
    public StandService(BooleanMatrixService booleanMatrixService, GeneratingMatrixService generatingMatrixService, LDPCMatrixService ldpcMatrixService, ChannelService channelService, DecodeService decodeService) {
        this.booleanMatrixService = booleanMatrixService;
        this.generatingMatrixService = generatingMatrixService;
        this.ldpcMatrixService = ldpcMatrixService;
        this.channelService = channelService;
        this.decodeService = decodeService;
    }

    public void stand(LDPCEnums.TypeOfCoding typeOfCoding,
                      LDPCEnums.TypeOfChannel typeOfChannel,
                      LDPCEnums.TypeOfDecoding typeOfDecoding) {
        StrictLowDensityParityCheckMatrix matrix = ldpcMatrixService.generateLDPCMatrix(typeOfCoding);
        System.out.println(matrix.toString() + DELIMITER);

        GeneratingMatrix generatingMatrix = generatingMatrixService.getGeneratingMatrixFromParityCheckMatrix(matrix.getParityCheckMatrix());
        System.out.println(generatingMatrix.toString() + DELIMITER);

        List<Pair> pairs = new ArrayList<>();
        IntStream.range(1, 72)
                .map(i -> i * BORDER_ITERATOR)
                .forEach(
                        i -> {
                            Pair pair = new Pair(0.0D, 0.0D);
                            IntStream.range(0, COUNT_GENERATION)
                                    .forEach(
                                            dummy -> {
                                                BooleanMatrix informationWord = booleanMatrixService.generateInfoWord(generatingMatrix.getBooleanMatrix().getSizeY());

                                                BooleanMatrix codeWord = booleanMatrixService.multiplicationMatrix(informationWord, generatingMatrix.getBooleanMatrix());

                                                CodeWord brokenCodeWord = channelService.send(codeWord, typeOfChannel, i);

                                                BooleanMatrix decode = decodeService.decode(matrix, brokenCodeWord, typeOfDecoding);

                                                pair.setKey(pair.getKey() + decodeService.getProbabilityBitsErrorsInformationWord(informationWord, decode));
                                                pair.setValue(pair.getValue() + channelService.getProbabilityBitsErrorsCodeWord(codeWord, brokenCodeWord));
                                            }
                                    );

                            pair.setKey(pair.getKey() / (double) COUNT_GENERATION);
                            pair.setValue(pair.getValue() / (double) COUNT_GENERATION);
                            double key = Math.log(1 / (pair.getKey() == 0.0d ? 0.0001 : pair.getKey()));
                            pairs.add(new Pair(key, pair.getValue()));
                        }
                );
        pairs.sort(Comparator.comparing(Pair::getKey));
        pairs.forEach(pair -> System.out.println(String.valueOf(pair.getKey()).replace('.', ',')));
        System.out.println("--------------");
        pairs.forEach(pair -> System.out.println(String.valueOf(pair.getValue()).replace('.', ',')));
    }

    private class Pair {

        private Double key;
        private Double value;

        private Pair(Double key, Double value) {
            this.key = key;
            this.value = value;
        }

        private Double getKey() {
            return key;
        }

        private void setKey(Double key) {
            this.key = key;
        }

        private Double getValue() {
            return value;
        }

        private void setValue(Double value) {
            this.value = value;
        }
    }
}
