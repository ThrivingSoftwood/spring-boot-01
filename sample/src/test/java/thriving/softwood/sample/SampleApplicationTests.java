package thriving.softwood.sample;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.annotation.Resource;
import thriving.softwood.sample.api.AncestorAsyncApi;

@SpringBootTest
class SampleApplicationTests {

    private static final Logger logger = LoggerFactory.getLogger(SampleApplicationTests.class);

    @Resource
    AncestorAsyncApi ancestorAsyncApi;

    @Test
    void contextLoads() {
        logger.info(ancestorAsyncApi.getClass().getName());
    }

}
