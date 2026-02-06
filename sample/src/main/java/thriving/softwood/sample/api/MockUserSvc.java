package thriving.softwood.sample.api;

import java.io.Serial;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import thriving.softwood.sample.pojo.entity.User;

@Service
public class MockUserSvc implements MockUserApi {

    private static final Logger logger = LoggerFactory.getLogger(MockUserSvc.class);

    @Resource
    MessageSource messageSource;

    Map<Integer, User> users = new HashMap<>() {
        @Serial
        private static final long serialVersionUID = 1L;

        {
            put(1, new User(1, "王1", "村1"));
            put(2, new User(2, "王2", "村2"));
            put(3, new User(3, "王3", "村3"));
            put(4, new User(4, "王4", "村4"));
            put(5, new User(5, "王5", "村5"));
        }
    };

    @Override
    public User getUserById(int id) {
        // 如果想要在微服务架构下跨 session 设置 Locale,可以在自定义 WebMvcConfigurer 中注册、使用自定义的 CookieLocaleResolver
        // 对象覆盖默认 LocaleResolver,然后在 addInterceptors 中注册 LocaleChangeInterceptor 对象

        // 此处只演示国际化信息获取
        logger.info(messageSource.getMessage("query.success", null, LocaleContextHolder.getLocale()));
        return users.get(id);
    }

    @Override
    public List<User> listAll() {
        return users.values().stream().toList();
    }

    @Override
    public void addOrModifyUser(User user) {
        users.put(user.getId(), user);
    }

    @Override
    public void removeUserById(int id) {
        users.remove(id);
    }
}
