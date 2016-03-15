package mikazuki.android.app.feelingmatch.model;

import io.realm.Realm;
import io.realm.RealmObject;

/**
 * @author haijimakazuki
 */
public class AutoIncrement {
    public static long newId(Realm realm, Class<? extends RealmObject> clazz) {
        return newIdWithIdName(realm, clazz, "id");
    }

    public static long newIdWithIdName(Realm realm, Class<? extends RealmObject> clazz, String idName) {
        Number max = realm.where(clazz).max(idName);
        max = max != null ? max : 0;
        return max.longValue() + 1;
    }
}
