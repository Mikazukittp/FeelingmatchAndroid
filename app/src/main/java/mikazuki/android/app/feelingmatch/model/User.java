package mikazuki.android.app.feelingmatch.model;

import junit.framework.Assert;

import org.parceler.Parcel;
import org.parceler.Transient;

/**
 * @author haijimakazuki
 */
@Parcel
public class User {

    private String name;
    private int sex; // girl=0, boy=1

    public User() {
    }

    public User(String name, int sex) {
        this.name = name;
        Assert.assertTrue(sex == 1 || sex == 0);
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
        Assert.assertTrue(sex == 1 || sex == 0);
        this.sex = sex;
    }

    @Transient
    public boolean isBoy() {
        return this.sex == 1;
    }

    @Transient
    public boolean isGirl() {
        return this.sex == 0;
    }
}
