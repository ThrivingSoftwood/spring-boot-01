package thriving.softwood.sample.api;

import java.io.Serial;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import thriving.softwood.sample.pojo.entity.User;

@Service
public class MockUserSvc implements MockUserApi {
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
