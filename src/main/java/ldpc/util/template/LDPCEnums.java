package ldpc.util.template;

public class LDPCEnums {

    public enum TypeOfDecoding {
        PRODUCT_SUM,
        PRODUCT_SUM_APPROXIMATELY,
        PRODUCT_SUM_APPROXIMATELY2,
        MIN_SUM
    }

    public enum TypeOfCoding {
        LDPC_GIRTH8_8_4,
        LDPC_GIRTH8_6_5
    }

    public enum TypeOfChannel {
        AWGN
    }
}
