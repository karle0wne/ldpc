package ldpc.util.service.decode;

import ldpc.matrix.wrapper.paritycheck.wrapper.StrictLowDensityParityCheckMatrix;
import ldpc.util.template.CodeWord;
import ldpc.util.template.LDPCEnums;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DecodeService {

    private final SumProductDecodeService sumProductDecodeService;

    @Autowired
    public DecodeService(SumProductDecodeService sumProductDecodeService) {
        this.sumProductDecodeService = sumProductDecodeService;
    }

    public boolean decode(StrictLowDensityParityCheckMatrix matrixLDPC, CodeWord codeWord, LDPCEnums.TypeOfDecoding typeOfDecoding) {
        if (typeOfDecoding == null) {
            return sumProductDecodeService.dummy(matrixLDPC, codeWord);
        }
        switch (typeOfDecoding) {
            case MIN_SUM:
                return sumProductDecodeService.decode(matrixLDPC, codeWord);
            case DEFAULT:
                return sumProductDecodeService.dummy(matrixLDPC, codeWord);
            default:
                return sumProductDecodeService.dummy(matrixLDPC, codeWord);
        }
    }
}
