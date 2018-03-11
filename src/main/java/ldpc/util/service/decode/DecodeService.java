package ldpc.util.service.decode;

import ldpc.matrix.basis.BooleanMatrix;
import ldpc.matrix.wrapper.paritycheck.wrapper.StrictLowDensityParityCheckMatrix;
import ldpc.service.basis.BooleanMatrixService;
import ldpc.service.wrapper.generating.GeneratingMatrixService;
import ldpc.util.service.CodeWordService;
import ldpc.util.template.CodeWord;
import ldpc.util.template.LDPCEnums;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class DecodeService {

    private final CodeWordService codeWordService;
    private final BooleanMatrixService booleanMatrixService;
    private final GeneratingMatrixService generatingMatrixService;
    private final SumProductDecodeService sumProductDecodeService;

    @Autowired
    public DecodeService(CodeWordService codeWordService, BooleanMatrixService booleanMatrixService, GeneratingMatrixService generatingMatrixService, SumProductDecodeService sumProductDecodeService) {
        this.codeWordService = codeWordService;
        this.booleanMatrixService = booleanMatrixService;
        this.generatingMatrixService = generatingMatrixService;
        this.sumProductDecodeService = sumProductDecodeService;
    }

    public BooleanMatrix decode(StrictLowDensityParityCheckMatrix matrixLDPC, CodeWord codeWord, LDPCEnums.TypeOfDecoding typeOfDecoding) {
        if (typeOfDecoding == null) {
            return dummy(codeWord);
        }
        switch (typeOfDecoding) {
            case PRODUCT_SUM:
                return sumProductDecodeService.decode(matrixLDPC, codeWord);
            default:
                return dummy(codeWord);
        }
    }

    public double getProbabilityBitsErrorsInformationWord(BooleanMatrix informationWord, BooleanMatrix decode) {
        List<Boolean> decodeValues = IntStream.range(decode.getSizeX() - informationWord.getSizeX(), decode.getSizeX())
                .mapToObj(i -> booleanMatrixService.getFirstRowValues(decode).get(i))
                .collect(Collectors.toList());

        List<Boolean> infoWordValues = booleanMatrixService.getFirstRowValues(informationWord);

        return booleanMatrixService.getProbabilityBitsErrors(decodeValues, infoWordValues);
    }

    private BooleanMatrix dummy(CodeWord codeWord) {
        return generatingMatrixService.recoveryBySwapHistory(codeWordService.getBooleanMatrix(codeWord));
    }
}
