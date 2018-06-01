package ldpc.util.template;

import java.util.function.Function;

import static java.lang.Math.*;

public class MathUtil {

    private static final double INCREMENT = 1E-5;
    private static final double LIMIT = 8.0D;
    private static final double ONE = 1.0D;
    private static final double TWO = 2.0D;

    public static double getProbabilityBitErrorBySignal(double signal) {
        return integral(
                (sqrt(TWO * signal)),
                (sqrt(TWO * LIMIT)),
                value -> (ONE / (sqrt(TWO * PI))) * (pow(E, -((pow(value, TWO)) / TWO)))
        );
    }

    public static double integral(double a, double b, Function<Double, Double> function) {
        double area = 0;
        for (double i = a + INCREMENT; i < b; i += INCREMENT) {
            double d = i - a;
            area += (INCREMENT / TWO) * (function.apply(d + a) + function.apply(a + d - INCREMENT));

        }
        return (round(area * (ONE / INCREMENT))) / (ONE / INCREMENT);
    }
}
