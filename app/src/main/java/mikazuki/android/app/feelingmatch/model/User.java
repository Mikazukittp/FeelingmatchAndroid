package mikazuki.android.app.feelingmatch.model;

/**
 * @author haijimakazuki
 */
public class User {

    private String name;
    private int sex; // girl=0, boy=1

    public User(String name, int sex) {
        this.name = name;
        this.sex = sex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }
}
