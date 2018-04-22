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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.LinkedHashMap;
import java.util.Map;
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
        String fileName = getName(typeOfCoding) + getName(typeOfChannel) + getName(typeOfDecoding) + ".txt";

        StrictLowDensityParityCheckMatrix matrix = ldpcMatrixService.generateLDPCMatrix(typeOfCoding);
        String ldpcMatrixString = matrix.toString() + DELIMITER + DELIMITER;
        System.out.println(ldpcMatrixString);

        GeneratingMatrix generatingMatrix = generatingMatrixService.getGeneratingMatrixFromParityCheckMatrix(matrix.getParityCheckMatrix());
        String generatingMatrixString = generatingMatrix.toString() + DELIMITER + DELIMITER;
        System.out.println(generatingMatrixString);
        String matrixParameters = ldpcMatrixString + generatingMatrixString;

        deleteFile(fileName);
        writeToFile(fileName, matrixParameters);

        Map<Double, String> map = new LinkedHashMap<>();

        for (double i = 1.0D; i < 4.25D; i += 0.25D) {
            DoubleWrapper doubleWrapper = new DoubleWrapper(0.0D);

            double signalPower = i;
            IntStream.range(0, COUNT_GENERATION)
                    .forEach(
                            dummy -> {
                                BooleanMatrix informationWord = booleanMatrixService.generateInfoWord(generatingMatrix.getBooleanMatrix().getSizeY());

                                BooleanMatrix codeWord = booleanMatrixService.multiplicationMatrix(informationWord, generatingMatrix.getBooleanMatrix());

                                CodeWord brokenCodeWord = channelService.send(codeWord, typeOfChannel, signalPower);

                                BooleanMatrix decode = decodeService.decode(matrix, brokenCodeWord, typeOfDecoding);

                                doubleWrapper.setValue(doubleWrapper.getValue() + decodeService.getProbabilityBitsErrorsInformationWord(informationWord, decode));

                                if (dummy % 100 == 0) {
                                    map.put(signalPower, getReplace(signalPower) + ": " + "(итерация: " + dummy + ") " + getReplace(doubleWrapper.getValue() / (double) dummy) + DELIMITER);
                                    writeToFile(fileName, matrixParameters + getReduce(map));
                                }
                            }
                    );

            doubleWrapper.setValue(doubleWrapper.getValue() / (double) COUNT_GENERATION);
            System.out.println(getReplace(signalPower) + ": " + getReplace(doubleWrapper.getValue()));

            map.put(signalPower, getReplace(signalPower) + ": " + getReplace(doubleWrapper.getValue()) + DELIMITER);
            writeToFile(fileName, matrixParameters + getReduce(map));
        }

        System.out.println("END!");
    }

    private void deleteFile(String fileName) {
        try {
            Files.deleteIfExists(Paths.get(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getReplace(double d) {
        return String.valueOf(d).replace('.', ',');
    }

    private String getName(LDPCEnums.TypeOfDecoding typeOfDecoding) {
        return typeOfDecoding == null ? "" : typeOfDecoding.name();
    }

    private String getName(LDPCEnums.TypeOfChannel typeOfChannel) {
        return typeOfChannel == null ? "" : typeOfChannel.name() + "_";
    }

    private String getName(LDPCEnums.TypeOfCoding typeOfCoding) {
        return typeOfCoding == null ? "" : typeOfCoding.name() + "_";
    }

    private String getReduce(Map<Double, String> map) {
        return map.values().stream().reduce("", (s, s2) -> s + s2);
    }

    private void writeToFile(String fileName, String data) {
        try {
            Files.write(Paths.get(fileName), data.getBytes(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
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
