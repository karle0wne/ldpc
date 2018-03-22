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

import java.util.Random;

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
        standService.stand(null, LDPCEnums.TypeOfChannel.AWGN, null);
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
