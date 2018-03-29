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

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {MainApplicationTest.class})
@Configuration
@ComponentScan(
        basePackages = {
                "ldpc.service",
                "ldpc.util.service"
        })
public class MainApplicationTest {

    @Autowired
    private StandService standService;


    @Test
    public void name() throws Exception {
        standService.stand(null, LDPCEnums.TypeOfChannel.AWGN, LDPCEnums.TypeOfDecoding.PRODUCT_SUM);
    }

    @Test
    public void sad() throws Exception {
        Random random = new Random();
        for (int i = 0; i < 500; i++) {
            int error = 0;
            for (int i1 = 0; i1 < 1000; i1++) {
                double value = random.nextGaussian() * 100.0D + 500.0D;
                if (isRange(value, 500.0D - (double) i, 500.0D + (double) i)) {
                    error += 1;
                }
            }
            double x = (double) error / 1000.0D;
            System.out.println(x);
        }
    }

    @Test
    public void ddddd() throws Exception {
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
    public void aaaaaa() throws Exception {

        for (double i = 2.0D; i > 0.0D; i -= 0.1D) {
            System.out.println(i);
            System.out.println("-----");
        }

    }

    @Test
    public void sxsxs() throws Exception {
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

    public boolean isRange(double value, double left, double right) {
        return left < value && value < right;
    }

    @Test
    public void aaa() throws Exception {
        for (double i = 0.0D; i < 9.0D; i += 0.25D) {
            System.out.println(i);
            System.out.println(MathUtil.getProbabilityBitErrorBySignal(i));
            System.out.println("---");
        }
    }
}
