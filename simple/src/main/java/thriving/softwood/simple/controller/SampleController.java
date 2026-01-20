package thriving.softwood.simple.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ThrivingSoftwood
 */
@RestController
@RequestMapping("/sample")
public class SampleController {
    private final Logger logger = LoggerFactory.getLogger(SampleController.class);

    @RequestMapping("/sample01")
    public String sample01() {
        logger.info("实际日志记录对象类型:{}", logger.getClass().getName());
        return "Hello World!";
    }
}
