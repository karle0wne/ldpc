package ldpc.util.service.channel;

import ldpc.matrix.basis.BooleanMatrix;
import ldpc.service.basis.BooleanMatrixService;
import ldpc.util.template.LDPCEnums;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChannelService {

    private final BinarySymmetricChannelService binarySymmetricChannelService;

    private final BooleanMatrixService booleanMatrixService;

    @Autowired
    public ChannelService(BinarySymmetricChannelService binarySymmetricChannelService, BooleanMatrixService booleanMatrixService) {
        this.binarySymmetricChannelService = binarySymmetricChannelService;
        this.booleanMatrixService = booleanMatrixService;
    }

    public BooleanMatrix send(BooleanMatrix codeWord, LDPCEnums.TypeOfChannel typeOfChannel) {
        if (typeOfChannel == null) {
            return booleanMatrixService.newMatrix(codeWord);
        }
        switch (typeOfChannel) {
            case AWGN:
                // TODO: 16.12.2017 https://krsk-sibsau-dev.myjetbrains.com/youtrack/issue/LDPC-23
                return binarySymmetricChannelService.send(codeWord, 0.03D);
            default:
                return booleanMatrixService.newMatrix(codeWord);
        }
    }
}
