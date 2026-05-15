package thriving.softwood.customer.first;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"thriving.softwood", "cn.hutool.v7.extra.spring"})
@MapperScan("thriving.softwood.**.infrastructure.db.**.mapper")
@EnableScheduling
public class FirstApplication {

    public static void main(String[] args) {
        SpringApplication.run(FirstApplication.class, args);
    }

}
