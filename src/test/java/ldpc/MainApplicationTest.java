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
}
