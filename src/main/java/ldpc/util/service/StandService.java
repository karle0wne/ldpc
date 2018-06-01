package ldpc.util.service;

import ldpc.matrix.basis.BooleanMatrix;
import ldpc.matrix.wrapper.generating.GeneratingMatrix;
import ldpc.matrix.wrapper.paritycheck.ParityCheckMatrix;
import ldpc.service.basis.BooleanMatrixService;
import ldpc.service.wrapper.generating.GeneratingMatrixService;
import ldpc.service.wrapper.paritycheck.ParityCheckMatrixService;
import ldpc.util.service.channel.AWGNService;
import ldpc.util.service.channel.ChannelService;
import ldpc.util.service.coder.EncoderService;
import ldpc.util.service.decode.DecoderService;
import ldpc.util.template.CodeWord;
import ldpc.util.template.LDPCEnums;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.IntStream;

@Service
public class StandService {

    private static final String DELIMITER = "\n";
    private static final int COUNT_GENERATION = 10000;

    private final BooleanMatrixService booleanMatrixService;

    private final GeneratingMatrixService generatingMatrixService;

    private final ParityCheckMatrixService parityCheckMatrixService;

    private final ChannelService channelService;

    private final DecoderService decoderService;

    private final EncoderService encoderService;

    @Autowired
    public StandService(BooleanMatrixService booleanMatrixService, GeneratingMatrixService generatingMatrixService, ParityCheckMatrixService parityCheckMatrixService, ChannelService channelService, DecoderService decoderService, EncoderService encoderService) {
        this.booleanMatrixService = booleanMatrixService;
        this.generatingMatrixService = generatingMatrixService;
        this.parityCheckMatrixService = parityCheckMatrixService;
        this.channelService = channelService;
        this.decoderService = decoderService;
        this.encoderService = encoderService;
    }


    public void stand(LDPCEnums.TypeOfCoding typeOfCoding, LDPCEnums.TypeOfChannel typeOfChannel, LDPCEnums.TypeOfDecoding typeOfDecoding) {
        System.out.println(getString(typeOfCoding) + "; " + getString(typeOfChannel) + "; " + getString(typeOfDecoding) + "; " + COUNT_GENERATION + DELIMITER);
        ParityCheckMatrix matrix = parityCheckMatrixService.generateParityCheckMatrix(typeOfCoding);
        System.out.println(matrix.toString() + DELIMITER + DELIMITER);

        GeneratingMatrix generatingMatrix = generatingMatrixService.getGeneratingMatrixFromParityCheckMatrix(matrix);
        System.out.println(generatingMatrix.toString() + DELIMITER + DELIMITER);

        for (Double i : AWGNService.gaussianCoefficient.keySet()) {
            DoubleWrapper doubleWrapper = new DoubleWrapper(0.0D);

            double signalPower = i;
            IntStream.range(0, COUNT_GENERATION)
                    .forEach(
                            dummy -> {
                                BooleanMatrix informationWord = booleanMatrixService.generateInfoWord(generatingMatrix.getBooleanMatrix().getSizeY());

                                BooleanMatrix codeWord = encoderService.encode(informationWord, generatingMatrix);

                                CodeWord brokenCodeWord = channelService.send(codeWord, typeOfChannel, signalPower);

                                BooleanMatrix decode = decoderService.decode(matrix, brokenCodeWord, typeOfDecoding);

                                doubleWrapper.setValue(doubleWrapper.getValue() + decoderService.getProbabilityBitsErrorsInformationWord(informationWord, decode));
                            }
                    );

            doubleWrapper.setValue(doubleWrapper.getValue() / (double) COUNT_GENERATION);
            System.out.println(getReplace(signalPower) + ":\t" + getReplace(doubleWrapper.getValue()));
        }
    }

    private String getString(LDPCEnums.TypeOfDecoding typeOfDecoding) {
        return Optional.ofNullable(typeOfDecoding).map(Enum::name).orElse("Отсутствует");
    }

    private String getString(LDPCEnums.TypeOfChannel typeOfChannel) {
        return Optional.ofNullable(typeOfChannel).map(Enum::name).orElse("Отсутствует");
    }

    private String getString(LDPCEnums.TypeOfCoding typeOfCoding) {
        return Optional.ofNullable(typeOfCoding).map(Enum::name).orElse("Отсутствует");
    }

    private String getReplace(double d) {
        return String.valueOf(d).replace('.', ',');
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
