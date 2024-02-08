package ir.vcx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Created by Sobhan at 8/10/2023 - VCX
 */

//TODO - remove local repo and rewrite the sdk

@EnableAsync
@SpringBootApplication(exclude = HibernateJpaAutoConfiguration.class)
public class VCXApplication {

    public static void main(String[] args) {
        SpringApplication.run(VCXApplication.class, args);
    }

}
