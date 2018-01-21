package ldpc;

import ldpc.util.service.StandService;
import ldpc.util.template.LDPCEnums;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static ldpc.util.template.LDPCEnums.TypeOfCoding.GIRTH8;

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
    public void ldpc() {
        standService.demoStandLDPC(GIRTH8, LDPCEnums.TypeOfChannel.AWGN, LDPCEnums.TypeOfDecoding.MIN_SUM);
    }

/*
// ниже исследование распределения генератора
    @Test
    public void name() throws Exception {
        Random random = new Random();
        List<Double> values = IntStream.range(0, 10000000)
                .mapToObj(value -> random.nextGaussian() * DESIRED_STANDARD_DEVIATION + DESIRED_MEAN)
                .collect(Collectors.toList());

        List<Long> longList = IntStream.range(0, 500)
                .mapToObj(operand -> values.stream()
                        .filter(aDouble -> (500 - operand) < aDouble && aDouble < (500 + operand))
                        .count() / 100000)
                .collect(Collectors.toList());
        longList.forEach(System.out::println);
    }
*/
}
