package ldpc;

import ldpc.util.service.StandService;
import ldpc.util.template.LDPCEnums;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static ldpc.util.template.LDPCEnums.TypeOfChannel.AWGN_DUMMY;
import static ldpc.util.template.LDPCEnums.TypeOfCoding.K5J4;
import static ldpc.util.template.LDPCEnums.TypeOfDecoding.MIN_SUM_DUMMY;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
public class MainApplicationTest {

    @Autowired
    private StandService standService;

    @Test
    public void ldpc() {
        for (LDPCEnums.TypeOfCoding typeOfCoding : LDPCEnums.TypeOfCoding.values()) {
            standService.demoStandLDPC(typeOfCoding, AWGN_DUMMY, MIN_SUM_DUMMY);
        }
    }

    @Test
    public void not_ldpc() {
        standService.demoStandWithoutLDPC(K5J4);
    }
}
