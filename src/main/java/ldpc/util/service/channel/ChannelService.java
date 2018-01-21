package ldpc.util.service.channel;

import ldpc.matrix.basis.BooleanMatrix;
import ldpc.matrix.basis.Row;
import ldpc.util.template.CodeWord;
import ldpc.util.template.LDPCEnums;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChannelService {

    private final AWGNService AWGNService;

    @Autowired
    public ChannelService(AWGNService AWGNService) {
        this.AWGNService = AWGNService;
    }

    public CodeWord send(BooleanMatrix codeWord, LDPCEnums.TypeOfChannel typeOfChannel, int percentage) {
        Row row = codeWord.getMatrix().get(0);
        if (typeOfChannel == null) {
            return AWGNService.dummy(row);
        }
        switch (typeOfChannel) {
            case AWGN:
                return AWGNService.send(row, percentage);
            default:
                return AWGNService.dummy(row);
        }
    }
}
