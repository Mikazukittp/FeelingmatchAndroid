package mikazuki.android.app.feelingmatch.view.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import butterknife.ButterKnife
import butterknife.OnClick
import com.annimon.stream.Stream
import com.google.common.collect.Maps
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.activity_result.*
import mikazuki.android.app.feelingmatch.R
import mikazuki.android.app.feelingmatch.model.Match
import mikazuki.android.app.feelingmatch.model.User
import java.util.*

/**
 * @author haijimakazuki
 */
class ResultActivity : BaseActivity() {


    private var mRealm: Realm? = null
    private var mMatch: Match? = null
    private var mMembers: List<User>? = null
    private var mMatchNum: Number? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        ButterKnife.bind(this)


        if (intent.extras == null) {
            result.text = "0組"
            return
        }
        // Matchのload
        val matchId = intent.extras.getLong("id")
        val realmConfig = RealmConfiguration.Builder(this).build()
        mRealm = Realm.getInstance(realmConfig)
        mMatch = mRealm!!.where(Match::class.java).equalTo("id", matchId).findFirst()
        mMembers = ArrayList(mMatch!!.members)

        val boys = Maps.newHashMap<Long, User>()
        val girls = Maps.newHashMap<Long, User>()
        Stream.of(mMembers).forEach { u -> (if (u.sex == 1) boys else girls).put(u.id, u) }
        mMatchNum = Stream.of(boys).filter { b -> girls[b.value.favoriteUserId]?.favoriteUserId == b.key }.count()
        result.text = "${mMatchNum ?: 0}組"
    }

    override fun onResume() {
        super.onResume()
        heart.animate(false)
        Handler().postDelayed({ heart.animate(true) }, 200)
    }

    @OnClick(R.id.go_top)
    fun goTopScreen(v: View) {
        startActivity(Intent(this, MainActivity::class.java))
    }
}
