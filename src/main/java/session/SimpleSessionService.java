package session;

import user.User;
import java.util.HashMap;
import java.util.Map;

public class SimpleSessionService {
    private static User activeUser;
    private static final Map<String, Object> sessionData = new HashMap<>();

    public void login(User user) {
        activeUser = user;
    }

    public User getActive() {
        return activeUser;
    }

    public void logout() {
        activeUser = null;
        sessionData.clear();
    }

    public void setObject(String key, Object value) {
        sessionData.put(key, value);
    }

    public Object getObject(String key) {
        return sessionData.get(key);
    }
}