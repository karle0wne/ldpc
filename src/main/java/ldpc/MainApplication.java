package ldpc;

import ldpc.util.service.StandService;
import ldpc.util.template.LDPCEnums;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.AbstractApplicationContext;

@Configuration
@ComponentScan(
        basePackages = {
                "ldpc.service",
                "ldpc.util.service"
        }
)
public class MainApplication {

    public static void main(String[] args) {
        //для создания jar:
        // поставить typeOfCoding
        // указать J K
        // указать COUNT_GENERATION = 10000000
        AbstractApplicationContext context = new AnnotationConfigApplicationContext(MainApplication.class);
        StandService bean = context.getBean(StandService.class);
        bean.stand(null, LDPCEnums.TypeOfChannel.AWGN, LDPCEnums.TypeOfDecoding.PRODUCT_SUM);
        context.registerShutdownHook();
    }
}
