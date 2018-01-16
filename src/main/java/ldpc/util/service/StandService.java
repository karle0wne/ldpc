package ldpc.util.service;

import ldpc.matrix.basis.BooleanMatrix;
import ldpc.matrix.wrapper.generating.GeneratingMatrix;
import ldpc.matrix.wrapper.paritycheck.ParityCheckMatrix;
import ldpc.matrix.wrapper.paritycheck.wrapper.StrictLowDensityParityCheckMatrix;
import ldpc.service.basis.BooleanMatrixService;
import ldpc.service.wrapper.generating.GeneratingMatrixService;
import ldpc.service.wrapper.paritycheck.ParityCheckMatrixService;
import ldpc.service.wrapper.paritycheck.wrapper.LDPCMatrixService;
import ldpc.util.service.channel.ChannelService;
import ldpc.util.service.decode.DecodeService;
import ldpc.util.template.LDPCEnums;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StandService {

    private static final String DELIMITER = "\n";

    private final BooleanMatrixService booleanMatrixService;

    private final GeneratingMatrixService generatingMatrixService;

    private final LDPCMatrixService ldpcMatrixService;

    private final ChannelService channelService;

    private final DecodeService decodeService;

    private final ParityCheckMatrixService parityCheckMatrixService;

    @Autowired
    public StandService(BooleanMatrixService booleanMatrixService, GeneratingMatrixService generatingMatrixService, LDPCMatrixService ldpcMatrixService, ChannelService channelService, DecodeService decodeService, ParityCheckMatrixService parityCheckMatrixService) {
        this.booleanMatrixService = booleanMatrixService;
        this.generatingMatrixService = generatingMatrixService;
        this.ldpcMatrixService = ldpcMatrixService;
        this.channelService = channelService;
        this.decodeService = decodeService;
        this.parityCheckMatrixService = parityCheckMatrixService;
    }

    public void demoStandLDPC(LDPCEnums.TypeOfCoding typeOfCoding,
                              LDPCEnums.TypeOfChannel typeOfChannel,
                              LDPCEnums.TypeOfDecoding typeOfDecoding) {
        StrictLowDensityParityCheckMatrix matrix = ldpcMatrixService.generateLDPCMatrix(typeOfCoding);

        GeneratingMatrix generatingMatrix = generatingMatrixService.getGeneratingMatrixFromParityCheckMatrix(matrix.getParityCheckMatrix());

        BooleanMatrix informationWord = booleanMatrixService.generateInfoWord(generatingMatrix.getBooleanMatrix().getSizeY());

        BooleanMatrix codeWord = booleanMatrixService.multiplicationMatrix(informationWord, generatingMatrix.getBooleanMatrix());

        BooleanMatrix brokenCodeWord = channelService.send(codeWord, typeOfChannel);

        BooleanMatrix decodedCodeWord = decodeService.decode(matrix, brokenCodeWord, typeOfDecoding);

        BooleanMatrix syndrome = booleanMatrixService.multiplicationMatrix(matrix.getParityCheckMatrix().getBooleanMatrix(), decodedCodeWord);

        showToConsole(matrix, generatingMatrix, informationWord, codeWord, brokenCodeWord, decodedCodeWord, syndrome);
    }

    private void showToConsole(StrictLowDensityParityCheckMatrix matrix,
                               GeneratingMatrix generatingMatrix,
                               BooleanMatrix informationWord,
                               BooleanMatrix recoveryCodeWord,
                               BooleanMatrix brokenCodeWord,
                               BooleanMatrix decodedCodeWord,
                               BooleanMatrix syndrome) {
        System.out.println("----------ТЕСТ----------" + DELIMITER);

        System.out.println(generatingMatrix.toString() + DELIMITER);

//        System.out.println("ИНФОРМАЦИОННОЕ СЛОВО:");
//        System.out.println(informationWord.getMatrix().get(0).toString() + DELIMITER);

//        System.out.println("КОДОВОЕ СЛОВО: ");
//        System.out.println(recoveryCodeWord.getMatrix().get(0).toString() + DELIMITER);

//        System.out.println("---------------");
//        System.out.println("ТИПА ПЕРЕДАЧА ПО КАНАЛУ...");
//        System.out.println("ВНЕСЕНИЕ ОШИБОК В КОДОВОЕ СЛОВО");
//        System.out.println("---------------" + DELIMITER);

        System.out.println(matrix.toString() + DELIMITER);

//        System.out.println("КОДОВОЕ СООБЩЕНИЕ, КОТОРОЕ ПРИШЛО ИЗ КАНАЛА: ");
//        System.out.println(brokenCodeWord.getMatrix().get(0).toString() + DELIMITER);

//        System.out.println("---------------");
//        System.out.println("ДЕКОДИРОВАНИЕ ПОЛУЧЕННОГО СООБЩЕНИЯ...");
//        System.out.println("---------------" + DELIMITER);

//        System.out.println("ДЕКОДИРОВАННОЕ КОДОВОЕ СЛОВО:");
//        System.out.println(booleanMatrixService.getTransposedBooleanMatrix(decodedCodeWord).getMatrix().get(0).toString() + DELIMITER);

        System.out.println("СИНДРОМ ПРОВЕРКИ");
        System.out.println(booleanMatrixService.getTransposedBooleanMatrix(syndrome).getMatrix().get(0).toString() + DELIMITER);
    }

    public void demoStandWithoutLDPC(LDPCEnums.TypeOfCoding typeOfCoding) {
        ParityCheckMatrix parityCheckMatrix = parityCheckMatrixService.generateParityCheckMatrix(typeOfCoding);

        GeneratingMatrix generatingMatrix = generatingMatrixService.getGeneratingMatrixFromParityCheckMatrix(parityCheckMatrix);

        BooleanMatrix informationWord = booleanMatrixService.generateInfoWord(generatingMatrix.getBooleanMatrix().getSizeY());

        BooleanMatrix recoveryCodeWord = booleanMatrixService.multiplicationMatrix(informationWord, generatingMatrix.getBooleanMatrix());

        BooleanMatrix syndrome = booleanMatrixService.multiplicationMatrix(parityCheckMatrix.getBooleanMatrix(), booleanMatrixService.getTransposedBooleanMatrix(recoveryCodeWord));

        showToConsole(parityCheckMatrix, generatingMatrix, informationWord, recoveryCodeWord, syndrome);
    }

    private void showToConsole(ParityCheckMatrix parityCheckMatrix,
                               GeneratingMatrix generatingMatrix,
                               BooleanMatrix informationWord,
                               BooleanMatrix recoveryCodeWord,
                               BooleanMatrix syndrome) {
        System.out.println("----------ТЕСТ БЕЗ ЛДПС----------" + DELIMITER
        );
        System.out.println(generatingMatrix.toString() + DELIMITER);

        System.out.println("ИНФОРМАЦИОННОЕ СЛОВО:");
        System.out.println(informationWord.getMatrix().get(0).toString() + DELIMITER);

        System.out.println("КОДОВОЕ СЛОВО: ");
        System.out.println(recoveryCodeWord.getMatrix().get(0).toString() + DELIMITER);

        System.out.println("---------------");
        System.out.println("ТИПА ПЕРЕДАЧА ПО КАНАЛУ!");
        System.out.println("---------------" + DELIMITER);

        System.out.println(parityCheckMatrix.toString() + DELIMITER);

        System.out.println("СИНДРОМ ПРОВЕРКИ");
        System.out.println(booleanMatrixService.getTransposedBooleanMatrix(syndrome).getMatrix().get(0).toString() + DELIMITER);
    }
}
