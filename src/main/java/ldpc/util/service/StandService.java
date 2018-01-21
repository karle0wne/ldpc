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
    private static final int COUNT_GENERATION = 10;
    private static final int START_BORDER = 1;
    private static final int TO_PERCENTAGE = 100;
    private static final int BORDER_ITERATOR = 2;
    private static final int END_BORDER = 28; //больше 53% уже 99% значений , проверять нет смысла
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

    public void demoStandLDPC(LDPCEnums.TypeOfCoding typeOfCoding,
                              LDPCEnums.TypeOfChannel typeOfChannel,
                              LDPCEnums.TypeOfDecoding typeOfDecoding) {
        StrictLowDensityParityCheckMatrix matrix = ldpcMatrixService.generateLDPCMatrix(typeOfCoding);
        System.out.println(matrix.toString() + DELIMITER);

        GeneratingMatrix generatingMatrix = generatingMatrixService.getGeneratingMatrixFromParityCheckMatrix(matrix.getParityCheckMatrix());
        System.out.println(generatingMatrix.toString() + DELIMITER);


        IntStream.range(START_BORDER, END_BORDER)
                .map(i -> i * BORDER_ITERATOR)
                .forEach(
                        i -> {
                            double probabilityFalseDecode = (double) IntStream.range(0, COUNT_GENERATION)
                                    .mapToObj(
                                            value -> {
                                                BooleanMatrix informationWord = booleanMatrixService.generateInfoWord(generatingMatrix.getBooleanMatrix().getSizeY());

                                                BooleanMatrix codeWord = booleanMatrixService.multiplicationMatrix(informationWord, generatingMatrix.getBooleanMatrix());

                                                CodeWord brokenCodeWord = channelService.send(codeWord, typeOfChannel, i);

                                                return decodeService.decode(matrix, brokenCodeWord, typeOfDecoding);
                                            }
                                    )
                                    .filter(elementTrue -> !elementTrue)
                                    .count() / (double) COUNT_GENERATION;
                            double percentage = probabilityFalseDecode * (double) TO_PERCENTAGE;
                            System.out.println("Сигнал: " + i + "%, вероятность ошибки декодирования: " + (int) percentage + "%");
                        }
                );
    }

/*
    public void demoStandWithoutLDPC(LDPCEnums.TypeOfCoding typeOfCoding) {
        ParityCheckMatrix parityCheckMatrix = parityCheckMatrixService.generateParityCheckMatrix(typeOfCoding);

        GeneratingMatrix generatingMatrix = generatingMatrixService.getGeneratingMatrixFromParityCheckMatrix(parityCheckMatrix);

        BooleanMatrix informationWord = booleanMatrixService.generateInfoWord(generatingMatrix.getBooleanMatrix().getSizeY());

        BooleanMatrix recoveryCodeWord = booleanMatrixService.multiplicationMatrix(informationWord, generatingMatrix.getBooleanMatrix());

        BooleanMatrix syndrome = booleanMatrixService.multiplicationMatrix(parityCheckMatrix.getBooleanMatrix(), booleanMatrixService.getTransposedBooleanMatrix(recoveryCodeWord));
    }
*/
}
