package mikazuki.android.app.feelingmatch.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

/**
 * @author haijimakazuki
 */
open class Match(
        @PrimaryKey open var id: Long = 0,
        open var name: String? = null,
        open var time: Date? = null,
        open var members: RealmList<User>? = null) : RealmObject() {
}
