package ldpc.util.service.coder;

import ldpc.matrix.basis.BooleanMatrix;
import ldpc.matrix.wrapper.generating.GeneratingMatrix;
import ldpc.service.basis.BooleanMatrixService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EncoderService {

    private final BooleanMatrixService booleanMatrixService;

    @Autowired
    public EncoderService(BooleanMatrixService booleanMatrixService) {
        this.booleanMatrixService = booleanMatrixService;
    }

    public BooleanMatrix encode(BooleanMatrix informationWord, GeneratingMatrix generatingMatrix) {
        return booleanMatrixService.multiplicationMatrix(informationWord, generatingMatrix.getBooleanMatrix());
    }
}
