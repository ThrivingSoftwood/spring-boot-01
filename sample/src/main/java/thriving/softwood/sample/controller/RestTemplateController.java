package thriving.softwood.sample.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import thriving.softwood.common.core.result.Result;
import thriving.softwood.sample.pojo.entity.User;

/**
 * RestTemplate 使用案例
 */
@RestController
@RequestMapping("/rest/template")
public class RestTemplateController {
    private static final Logger logger = LoggerFactory.getLogger(RestTemplateController.class);
    private final RestTemplate restTemplate;
    private final RestClient restClient;

    // 此处演示构建过程,实际上当前项目已经在 common-web 模块中定义了 RestClient 和 RestTemplate 对应的 bean,直接注入即可
    @Autowired
    /*public RestTemplateController(RestTemplateBuilder restTemplateBuilder, RestClientBuilder restClientBuilder) {
        restTemplate = restTemplateBuilder.build();
        restClient = restClientBuilder.build();
    }*/
    public RestTemplateController(RestTemplate restTemplate, RestClient restClient) {
        this.restTemplate = restTemplate;
        this.restClient = restClient;
    }

    @RequestMapping("/sample01")
    public String sample01() {
        Result<User> result = restTemplate.getForObject("http://localhost:6160/mock/user/{id}", Result.class, 1);
        logger.info(result.getData().toString());

        ResponseEntity<Result> responseEntity =
            restTemplate.getForEntity("http://localhost:6160/mock/user/{id}", Result.class, 1);
        logger.info(responseEntity.getBody().toString());

        // 因为 restTemplate 某些方法无返回值,若想获得返回值,请用 exchange
        HttpEntity<User> reqEntity = new HttpEntity<>(new User(1, "测试", "村落"));
        responseEntity =
            restTemplate.exchange("http://localhost:6160/mock/user/{id}", HttpMethod.PUT, reqEntity, Result.class, 1);
        logger.info(responseEntity.getBody().toString());

        return result.getData().toString();
    }
}
