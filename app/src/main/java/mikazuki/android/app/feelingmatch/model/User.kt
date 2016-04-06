package mikazuki.android.app.feelingmatch.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * @author haijimakazuki
 */
open class User(
        @PrimaryKey open var id: Long = 0,
        open var name: String? = null,
        open var sex: Int = 0,
        open var favoriteUserId: Long = 0,
        open var isPublicUser: Boolean = false) : RealmObject() {

    val isBoy: Boolean
        get() = this.sex == 1

    val isGirl: Boolean
        get() = this.sex == 0
}
