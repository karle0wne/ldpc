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

import java.util.stream.IntStream;

@Service
public class StandService {

    private static final String DELIMITER = "\n";
    private static final int COUNT_GENERATION = 1000;
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

        for (double i = 0.0d; i < 9.0d; i += 0.25D) {
            DoubleWrapper doubleWrapper = new DoubleWrapper(0.0D);

            double signalPower = i;
            IntStream.range(0, COUNT_GENERATION)
                    .forEach(
                            dummy -> {
                                BooleanMatrix informationWord = booleanMatrixService.generateInfoWord(generatingMatrix.getBooleanMatrix().getSizeY());

                                BooleanMatrix codeWord = booleanMatrixService.multiplicationMatrix(informationWord, generatingMatrix.getBooleanMatrix());

                                CodeWord brokenCodeWord = channelService.send(codeWord, typeOfChannel, signalPower);

                                BooleanMatrix decode = decodeService.decode(matrix, brokenCodeWord, typeOfDecoding);

                                doubleWrapper.setValue(doubleWrapper.getValue() + channelService.getProbabilityBitsErrorsCodeWord(codeWord, brokenCodeWord));
                            }
                    );

            doubleWrapper.setValue(doubleWrapper.getValue() / (double) COUNT_GENERATION);
            System.out.println(String.valueOf(doubleWrapper.getValue()).replace('.', ','));
        }
    }

    private class DoubleWrapper {

        private Double value;

        private DoubleWrapper(Double value) {
            this.value = value;
        }

        private Double getValue() {
            return value;
        }

        private void setValue(Double value) {
            this.value = value;
        }
    }
}
