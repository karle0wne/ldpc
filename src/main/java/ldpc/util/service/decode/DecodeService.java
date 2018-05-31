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
    private final MinSumDecodeService minSumDecodeService;

    @Autowired
    public DecodeService(CodeWordService codeWordService, BooleanMatrixService booleanMatrixService, GeneratingMatrixService generatingMatrixService, SumProductDecodeService sumProductDecodeService, MinSumDecodeService minSumDecodeService) {
        this.codeWordService = codeWordService;
        this.booleanMatrixService = booleanMatrixService;
        this.generatingMatrixService = generatingMatrixService;
        this.sumProductDecodeService = sumProductDecodeService;
        this.minSumDecodeService = minSumDecodeService;
    }

    public BooleanMatrix decode(StrictLowDensityParityCheckMatrix matrixLDPC, CodeWord codeWord, LDPCEnums.TypeOfDecoding typeOfDecoding) {
        if (typeOfDecoding == null) {
            return dummy(codeWord);
        }
        switch (typeOfDecoding) {
            case PRODUCT_SUM:
                return sumProductDecodeService.decode(matrixLDPC, codeWord, this::getHyperbolicArcTan);
            case PRODUCT_SUM_APPROXIMATELY:
                return sumProductDecodeService.decode(matrixLDPC, codeWord, this::getApproximately);
            case PRODUCT_SUM_APPROXIMATELY2:
                return sumProductDecodeService.decode(matrixLDPC, codeWord, this::getApproximately2);
            case MIN_SUM:
                return minSumDecodeService.decode(matrixLDPC, codeWord);
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
        return generatingMatrixService.recoveryBySwapHistory(codeWordService.getBooleanMatrix(codeWord), false);
    }

    private Double getHyperbolicArcTan(Double value) {
        double exp = Math.exp(Math.abs(value));
        return Math.log((exp + 1) / (exp - 1));
    }

    private Double getApproximately(Double value) {
        double t = Math.abs(value);
        double v = 0.7;
        double v1 = 8.0;
        return v / t + 0.006038325080 - 0.006709250 * t
                + 0.003354628501 * (Math.pow((t - v1), 2.))
                - 0.001118211422 * (Math.pow((t - v1), 3.))
                + 0.0002795538798 * (Math.pow((t - v1), 4.));
    }

    private Double getApproximately2(Double value) {
        double t = Math.abs(value);
        double y = t - 1;
        double z = t + 1;

        double x = 2 * y / z;
        double y1 = Math.pow(y, 2.);
        double z1 = Math.pow(z, 2.);

        double x1 = (1.) + (y1 / z1) * ((1. / 3.) + (y1 / z1) * ((1. / 5.) + (y1 / z1) * ((1. / 7.) + (y1 / z1) * ((1. / 9.) + (y1 / (11. * z1))))));
        double appr = 0.69305 - t * (0.00699 + t * (0.10513 - t * (0.02123 + t * (0.00205 - t * (0.000106 + t * (0.000002789 - t * (0.00000002935)))))));
        return appr - (x * x1);
    }
}
