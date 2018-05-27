package ldpc;

import ldpc.util.service.StandService;
import ldpc.util.template.LDPCEnums;
import ldpc.util.template.MathUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;
import java.util.stream.IntStream;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {MainApplicationTest.class})
@Configuration
@ComponentScan(
        basePackages = {
                "ldpc.service",
                "ldpc.util.service"
        }
)
public class MainApplicationTest {

    @Autowired
    private StandService standService;

    @Test
    public void name() throws Exception {
        standService.stand(null, LDPCEnums.TypeOfChannel.AWGN, LDPCEnums.TypeOfDecoding.APPROXIMATELY);
    }

    @Test
    public void gaussianError() throws Exception {
        Random random = new Random();

        for (int i = 820; i > 810; i--) {
            double ii = (double) i / (double) 1000;
            double x = 0;
            for (int d = 0; d < 100; d++) {
                int error = 0;
                for (int i1 = 0; i1 < 100000; i1++) {
                    double value = random.nextGaussian() * ii - 1;
                    if (value >= 0) {
                        error += 1;
                    }
                }
                x += (double) error / (double) 100000;
            }
            double v = x / (double) 100;
            System.out.println(String.valueOf(ii).replace(".", ","));
            System.out.println(v);
            System.out.println("-----");
        }
    }

    @Test
    public void gaussianDistribution() throws Exception {
        Map<Double, Integer> map = new HashMap<>();
        Random random = new Random();
        for (int i1 = 0; i1 < 100000; i1++) {
            double value = random.nextGaussian() * 2;
            double v = value * (double) 100;
            int v1 = (int) v;
            double v2 = (double) v1 / 10;
            map.merge(v2, 1, (a, b) -> a + b);
        }
        List<Map.Entry<Double, Integer>> collect = new ArrayList<>(map.entrySet());
        collect.sort(Comparator.comparing(Map.Entry::getKey));
        collect.forEach(
                doubleIntegerEntry -> System.out.println(doubleIntegerEntry.getKey())
        );
        System.out.println("-----------------");
        collect.forEach(
                doubleIntegerEntry -> System.out.println(doubleIntegerEntry.getValue())
        );
    }

    @Test
    public void integral() throws Exception {
        System.out.println("Eb/N0 | Peb");
        for (double i = 0.0D; i < 9.0D; i += 0.25D) {
            System.out.println(i + ":\t" + MathUtil.getProbabilityBitErrorBySignal(i));
        }
    }

    @Test
    public void sass() throws Exception {
        Map<Integer, List<Drob>> map = new LinkedHashMap<Integer, List<Drob>>() {{
            put(4080, Arrays.asList(new Drob(1, 2), new Drob(2, 3), new Drob(3, 4), new Drob(4, 5)));
            put(6120, Arrays.asList(new Drob(2, 3), new Drob(3, 4), new Drob(4, 5)));
            put(8160, Arrays.asList(new Drob(3, 4), new Drob(4, 5), new Drob(5, 6), new Drob(9, 10)));
        }};

        asdAsasd(map);
        asdAsasd2(map);
        asdAsd(map);
    }

    private void asdAsasd(Map<Integer, List<Drob>> map) {
        map.forEach((code, value) -> {
            List<Boolean> booleans = Arrays.asList(false);
            System.out.println(code);
            value.forEach(
                    drob -> {
                        Drob increase = drob.reverse().increase(3);
                        int bottom = increase.getBottom();
                        IntStream.range(4, 6)
                                .forEach(
                                        i -> {
                                            if (code == getLenghtCode(code, i, bottom)) {
                                                booleans.set(0, true);
                                                int i1 = ((bottom - 1) * i) * bottom;
                                                StringBuilder append = new StringBuilder()
                                                        .append("J:\t" + 3 + "; ")
                                                        .append("K:\t" + bottom + "; ")
                                                        .append("блок:\t" + i1 + "; ")
                                                        .append("длина кода:\t" + getLenghtCode(code, i, bottom) + "; ");
                                                System.out.println(append);
                                            }
                                        }
                                );
                    }
            );
            if (!booleans.get(0)) {
                System.out.println("-");
            }
        });
        System.out.println("\n");
    }

    private void asdAsasd2(Map<Integer, List<Drob>> map) {
        map.forEach((code, value) -> {
            List<Boolean> booleans = Arrays.asList(false);
            System.out.println(code);
            value.forEach(
                    drob -> {
                        Drob increase = drob.reverse().increase(4);
                        int bottom = increase.getBottom();
                        IntStream.range(2, 8)
                                .forEach(
                                        i -> {
                                            if (code == getLenghtCode2(code, i, bottom)) {
                                                booleans.set(0, true);
                                                int i1 = bottom * i - i + 1;
                                                StringBuilder append = new StringBuilder()
                                                        .append("J:\t" + 4 + "; ")
                                                        .append("K:\t" + bottom + "; ")
                                                        .append("блок:\t" + i1 + "; ")
                                                        .append("длина кода:\t" + getLenghtCode2(code, i, bottom) + "; ");
                                                System.out.println(append);
                                            }
                                        }
                                );
                    }
            );
            if (!booleans.get(0)) {
                System.out.println("-");
            }
        });
        System.out.println("\n");
    }

    private void asdAsd(Map<Integer, List<Drob>> map) {
        map.forEach((code, value) -> {
            List<Boolean> booleans = Arrays.asList(false);
            HashSet<MatrixParameters> parameters = new HashSet<>();
            System.out.println(code);
            value.forEach(
                    drob -> IntStream.range(3, 7)
                            .forEach(
                                    i -> IntStream.range(3, 30)
                                            .forEach(
                                                    j -> {
                                                        if (j > i) {
                                                            int k = (int) Math.pow(j, i);
                                                            if ((code - 100) < k && k < (code + 100)) {
                                                                if (parameters.add(new MatrixParameters(i, j, k))) {
                                                                    booleans.set(0, true);
                                                                    StringBuilder append = new StringBuilder()
                                                                            .append("J: " + i + "; ")
                                                                            .append("K: " + j + "; ")
                                                                            .append("длина кода: " + k + "; ");
                                                                    System.out.println(append);
                                                                }
                                                            }
                                                        }
                                                    }
                                            )
                            )
            );
            if (!booleans.get(0)) {
                System.out.println("-");
            }
        });
    }

    private int getLenghtCode(Integer codeLenght, int i, int bottom) {
        int lenghtH1 = ((bottom - 1) * i) * bottom;
        int coef;
        if (codeLenght % lenghtH1 != 0) {
            coef = codeLenght / lenghtH1 + 1;
        } else {
            coef = codeLenght / lenghtH1;
        }
        return lenghtH1 * coef;
    }

    private int getLenghtCode2(Integer codeLenght, int i, int bottom) {
        int lenghtH1 = bottom * i - i + 1;
        int coef;
        if (codeLenght % lenghtH1 != 0) {
            coef = codeLenght / lenghtH1 + 1;
        } else {
            coef = codeLenght / lenghtH1;
        }
        return lenghtH1 * coef;
    }

    private class Drob {
        private int top;
        private int bottom;

        public Drob(int top, int bottom) {
            this.top = top;
            this.bottom = bottom;
        }

        public int getTop() {
            return top;
        }

        public int getBottom() {
            return bottom;
        }

        public Drob increase(int k) {
            top *= k;
            bottom *= k;
            return this;
        }

        public Drob reverse() {
            return new Drob(bottom - top, bottom);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Drob drob = (Drob) o;

            if (top != drob.top) return false;
            return bottom == drob.bottom;
        }

        @Override
        public int hashCode() {
            int result = top;
            result = 31 * result + bottom;
            return result;
        }

        @Override
        public String toString() {
            return top + "\\" + bottom;
        }
    }

    private class MatrixParameters {
        private final int i;
        private final int j;
        private final int k;

        public MatrixParameters(int i, int j, int k) {
            this.i = i;
            this.j = j;
            this.k = k;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            MatrixParameters that = (MatrixParameters) o;

            if (i != that.i) return false;
            if (j != that.j) return false;
            return k == that.k;
        }

        @Override
        public int hashCode() {
            int result = i;
            result = 31 * result + j;
            result = 31 * result + k;
            return result;
        }
    }
}
