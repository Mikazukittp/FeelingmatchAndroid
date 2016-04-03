package mikazuki.android.app.feelingmatch.model

import io.realm.Realm
import io.realm.RealmObject

/**
 * @author haijimakazuki
 */
object AutoIncrement {
    fun newId(realm: Realm, clazz: Class<out RealmObject>): Long {
        return newIdWithIdName(realm, clazz, "id")
    }

    fun newIdWithIdName(realm: Realm, clazz: Class<out RealmObject>, idName: String): Long {
        var max: Number? = realm.where(clazz).max(idName)
        max = if (max != null) max else -1
        return max.toLong() + 1
    }
}
