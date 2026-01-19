package thriving.softwood.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sample")
public class SampleController {
    @RequestMapping("/sample01")
    public String sample01() {
        return "Hello World!";
    }
}
