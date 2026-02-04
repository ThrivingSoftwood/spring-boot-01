package thriving.softwood.sample.api;

import java.util.List;

import thriving.softwood.sample.pojo.entity.User;

public interface MockUserApi {
    User getUserById(int id);

    List<User> listAll();

    void addOrModifyUser(User user);

    void removeUserById(int id);
}
