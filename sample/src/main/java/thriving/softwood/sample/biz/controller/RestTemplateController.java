package thriving.softwood.sample.biz.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

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
        return null;
    }
}
