package thriving.softwood.sample.biz.api;

import java.util.List;

import thriving.softwood.sample.infrastructure.db.entity.User;

public interface MockUserApi {
    User getUserById(int id);

    List<User> listAll();

    void addOrModifyUser(User user);

    void removeUserById(int id);
}
