package reoger.hut.com.retrofit2.bean;

/**
 * Created by 24540 on 2017/4/29.
 */

public class User {
    private String user;
    private String passwd;

    public User(String user, String passwd) {
        this.user = user;
        this.passwd = passwd;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }
}
