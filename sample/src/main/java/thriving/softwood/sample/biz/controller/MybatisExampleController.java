package thriving.softwood.sample.biz.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mybatis")
public class MybatisExampleController {
    private static final Logger logger = LoggerFactory.getLogger(MybatisExampleController.class);
    // AppInfoRepo appInfoRepo;
    // NovelRepo novelRepo;
    //
    // @Autowired
    // public MybatisExampleController(AppInfoRepo appInfoRepo, NovelRepo novelRepo) {
    // this.appInfoRepo = appInfoRepo;
    // this.novelRepo = novelRepo;
    // }
    //
    // @RequestMapping("/query")
    // public Result query() {
    // QueryWrapper<AppInfo> queryWrapper = Wrappers.query();
    // queryWrapper.eq("id", 1);
    // logger.info(appInfoRepo.getOne(queryWrapper).toString());
    // QueryWrapper<Novel> queryWrapper1 = Wrappers.query();
    // queryWrapper1.eq("id", 1);
    // logger.info(novelRepo.getOne(queryWrapper1).toString());
    // return Result.success();
    // }
}
