package ldpc;

import ldpc.service.StandService;
import ldpc.service.wrapper.paritycheck.ParityCheckMatrixService;
import ldpc.service.wrapper.paritycheck.wrapper.LDPCMatrixService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
public class MainApplicationTest {

    @Autowired
    private ParityCheckMatrixService parityCheckMatrixService;

    @Autowired
    private LDPCMatrixService ldpcMatrixService;

    @Autowired
    private StandService standService;

    @Test
    public void ldpc() {
        standService.demoStandLDPC(ldpcMatrixService.newStrictLDPCMatrix(parityCheckMatrixService.prepared_PCM_LDPC()));
    }

    @Test
    public void not_ldpc() {
        //--------------------------------------------------------------------------------------------------------------
        System.out.println("----------ТЕСТ 1----------" + "\n");
        standService.demoStandWithoutLDPC(parityCheckMatrixService.preparedPCM());

        //--------------------------------------------------------------------------------------------------------------
        System.out.println("----------ТЕСТ 2----------" + "\n");
        standService.demoStandWithoutLDPC(parityCheckMatrixService.preparedPCM2());
    }
}
