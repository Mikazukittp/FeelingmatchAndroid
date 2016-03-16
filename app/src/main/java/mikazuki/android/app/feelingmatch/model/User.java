package mikazuki.android.app.feelingmatch.model;

import junit.framework.Assert;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author haijimakazuki
 */
public class User extends RealmObject {

    @PrimaryKey
    private long id;
    private String name;
    private int sex; // girl=0, boy=1

    private long favoriteUserId;

    public User() {
    }

    public User(long id, String name, int sex) {
        this.id = id;
        this.name = name;
        Assert.assertTrue(sex == 1 || sex == 0);
        this.sex = sex;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public long getFavoriteUserId() {
        return favoriteUserId;
    }

    public void setFavoriteUserId(long favoriteUserId) {
        this.favoriteUserId = favoriteUserId;
    }

    public boolean isBoy() {
        return this.sex == 1;
    }

    public boolean isGirl() {
        return this.sex == 0;
    }
}
