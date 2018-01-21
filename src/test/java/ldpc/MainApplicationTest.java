package ldpc;

import ldpc.util.service.StandService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static ldpc.util.template.LDPCEnums.TypeOfCoding.GIRTH8;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
public class MainApplicationTest {

    @Autowired
    private StandService standService;

    @Test
    public void ldpc() {
        standService.demoStandLDPC(GIRTH8, null, null);
    }

    @Test
    public void not_ldpc() {
        standService.demoStandWithoutLDPC(GIRTH8);
    }

}
