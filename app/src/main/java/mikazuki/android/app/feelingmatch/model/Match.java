package mikazuki.android.app.feelingmatch.model;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author haijimakazuki
 */
public class Match extends RealmObject {

    @PrimaryKey
    private long id;
    private String name;
    private Date time;
    private RealmList<User> members;

    public Match() {
    }

    public Match(long id, String name, Date time, RealmList<User> members) {
        this.id = id;
        this.name = name;
        this.time = time;
        this.members = members;
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

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public RealmList<User> getMembers() {
        return members;
    }

    public void setMembers(RealmList<User> members) {
        this.members = members;
    }
}
