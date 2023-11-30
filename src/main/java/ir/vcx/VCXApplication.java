package ir.vcx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

/**
 * Created by Sobhan at 8/10/2023 - VCX
 */

//TODO - remove local repo and rewrite the sdk

@SpringBootApplication(exclude = HibernateJpaAutoConfiguration.class)
public class VCXApplication {

    public static void main(String[] args) {
        SpringApplication.run(VCXApplication.class, args);
    }

}
