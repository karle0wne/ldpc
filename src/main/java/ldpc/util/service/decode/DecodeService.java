package ldpc.util.service.decode;

import ldpc.matrix.basis.BooleanMatrix;
import ldpc.matrix.wrapper.paritycheck.wrapper.StrictLowDensityParityCheckMatrix;
import ldpc.service.basis.BooleanMatrixService;
import ldpc.util.template.LDPCEnums;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DecodeService {

    private final BooleanMatrixService booleanMatrixService;

    private final MinSumDecodeService minSumDecodeService;

    @Autowired
    public DecodeService(BooleanMatrixService booleanMatrixService, MinSumDecodeService minSumDecodeService) {
        this.booleanMatrixService = booleanMatrixService;
        this.minSumDecodeService = minSumDecodeService;
    }

    public BooleanMatrix decode(StrictLowDensityParityCheckMatrix matrixLDPC, BooleanMatrix codeWord, LDPCEnums.TypeOfDecoding typeOfDecoding) {
        switch (typeOfDecoding) {
            case MIN_SUM_DUMMY:
                // TODO: 16.12.2017 https://krsk-sibsau-dev.myjetbrains.com/youtrack/issue/LDPC-3
                return minSumDecodeService.decode(matrixLDPC, codeWord);
            case DUMMY:
                return booleanMatrixService.newMatrix(codeWord);
            default:
                return booleanMatrixService.newMatrix(codeWord);
        }
    }
}
