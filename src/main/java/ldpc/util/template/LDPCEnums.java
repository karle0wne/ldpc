package ldpc.util.template;

public class LDPCEnums {

    public enum TypeOfDecoding {
        MIN_SUM_DUMMY,
        DUMMY
    }

    public enum TypeOfCoding {
        LDPC_DUMMY_ONE,
        LDPC_DUMMY_TWO,
        LDPC_DUMMY_THREE,
        LDPC_ONE,
        PCM_DUMMY
    }

    public enum TypeOfChannel {
        BCS,
        AWGN_DUMMY,
        DUMMY
    }
}
