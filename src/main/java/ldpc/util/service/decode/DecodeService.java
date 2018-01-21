package ldpc.util.service.decode;

import ldpc.matrix.wrapper.paritycheck.wrapper.StrictLowDensityParityCheckMatrix;
import ldpc.util.template.CodeWord;
import ldpc.util.template.LDPCEnums;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DecodeService {

    private final MinSumDecodeService minSumDecodeService;

    @Autowired
    public DecodeService(MinSumDecodeService minSumDecodeService) {
        this.minSumDecodeService = minSumDecodeService;
    }

    public boolean decode(StrictLowDensityParityCheckMatrix matrixLDPC, CodeWord codeWord, LDPCEnums.TypeOfDecoding typeOfDecoding) {
        if (typeOfDecoding == null) {
            return minSumDecodeService.dummy(matrixLDPC, codeWord);
        }
        switch (typeOfDecoding) {
            case MIN_SUM:
                return minSumDecodeService.decode(matrixLDPC, codeWord);
            default:
                return minSumDecodeService.dummy(matrixLDPC, codeWord);
        }
    }
}
