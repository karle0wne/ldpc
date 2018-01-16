package ldpc;

import ldpc.util.service.StandService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static ldpc.util.template.LDPCEnums.TypeOfChannel.AWGN_DUMMY;
import static ldpc.util.template.LDPCEnums.TypeOfCoding.LDPC_ONE;
import static ldpc.util.template.LDPCEnums.TypeOfCoding.PCM_DUMMY;
import static ldpc.util.template.LDPCEnums.TypeOfDecoding.MIN_SUM_DUMMY;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
public class MainApplicationTest {

    @Autowired
    private StandService standService;

    @Test
    public void ldpc() {
        standService.demoStandLDPC(LDPC_ONE, AWGN_DUMMY, MIN_SUM_DUMMY);
    }

    @Test
    public void not_ldpc() {
        standService.demoStandWithoutLDPC(PCM_DUMMY);
    }
}
