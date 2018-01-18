package ldpc.util.template;

public class LDPCEnums {

    public enum TypeOfDecoding {
        MIN_SUM_DUMMY,
        DUMMY
    }

    public enum TypeOfCoding {
        K5J4,
        K6J4,
        K6J5,
        K7J4,
        K8J4,
        K8J3,
        K9J3,
        K9J4
    }

    public enum TypeOfChannel {
        BCS,
        AWGN_DUMMY,
        DUMMY
    }
}
