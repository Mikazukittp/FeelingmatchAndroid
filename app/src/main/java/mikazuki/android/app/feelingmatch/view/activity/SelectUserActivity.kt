package mikazuki.android.app.feelingmatch.view.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.TextAppearanceSpan
import android.widget.ListView
import butterknife.ButterKnife
import butterknife.OnClick
import com.annimon.stream.Collectors
import com.annimon.stream.Stream
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.activity_select_user.*
import mikazuki.android.app.feelingmatch.R
import mikazuki.android.app.feelingmatch.model.Match
import mikazuki.android.app.feelingmatch.model.User
import mikazuki.android.app.feelingmatch.view.adapter.CandidateListAdapter
import java.util.*

/**
 * @author haijimakazuki
 */
class SelectUserActivity : BaseActivity() {

    private var mRealm: Realm? = null
    private var mMatch: Match? = null
    private var mMembers: List<User>? = null
    private var mTarget: User? = null
    private var mIndex: Int = 0
    private var mAdapter: CandidateListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_user)
        ButterKnife.bind(this)

        // Matchのload
        val matchId = intent.extras.getLong("id")
        val realmConfig = RealmConfiguration.Builder(this).build()
        mRealm = Realm.getInstance(realmConfig)
        mMatch = mRealm!!.where(Match::class.java).equalTo("id", matchId).findFirst()
        mMembers = ArrayList(mMatch!!.members)
        if (mMembers!!.size > 0) {
            mIndex = 0
            renderNext()
        }
    }

    override fun onDestroy() {
        mRealm!!.close()
        super.onDestroy()
    }

    fun renderNext() {
        next.isEnabled = false
        mTarget = mMembers!![mIndex]

        val coloredSpan = TextAppearanceSpan(this, if (mTarget!!.sex == 1) R.style.Boy_Large else R.style.Girl_Large)
        val ssb = SpannableStringBuilder()
        ssb.append(mTarget!!.name)
        ssb.setSpan(coloredSpan, 0, ssb.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        ssb.append("さんの番です")
        title_view.text = ssb

        candidates.choiceMode = ListView.CHOICE_MODE_SINGLE
        val candidateModels = Stream.of(mMembers).filter { m -> m.sex == 1 - mTarget!!.sex }.collect(Collectors.toList<User>())
        mAdapter = CandidateListAdapter(this, candidateModels)
        candidates.adapter = mAdapter
        candidates.setOnItemClickListener { parent, view, position, id ->
            Stream.range(0, mAdapter!!.count).map { mAdapter!!.getItem(it) }.forEach { row -> row.isChecked = false }
            mAdapter!!.getItem(position).isChecked = true
            mAdapter!!.notifyDataSetChanged()
            next.isEnabled = true
        }
    }

    fun renderResult() {
        AlertDialog.Builder(this).setTitle("投票終了").setMessage("全員の投票が終わりました。\n結果画面へ移動します。").setPositiveButton("結果閲覧") { dialog, which ->

            val intent = Intent(this, ResultActivity::class.java)
            intent.putExtra("id", mMatch!!.id)
            startActivity(intent)
            finish()
        }.setCancelable(false).create().show()
    }

    @OnClick(R.id.next)
    fun nextPerson() {
        val selectedUser = mAdapter?.selectedUser ?: return

        val coloredSpan = TextAppearanceSpan(this, if (selectedUser.sex == 1) R.style.Boy else R.style.Girl)
        val ssb = SpannableStringBuilder()
        ssb.append(selectedUser.name)
        ssb.setSpan(coloredSpan, 0, ssb.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        ssb.append("さんでよろしいですか？")

        AlertDialog.Builder(this).setTitle("確認").setMessage(ssb).setPositiveButton("はい") { dialog, which ->
            mRealm!!.beginTransaction()
            mTarget!!.favoriteUserId = selectedUser.id
            mRealm!!.copyToRealmOrUpdate<User>(mTarget)
            mRealm!!.commitTransaction()

            if (mIndex < mMembers!!.size - 1) {
                mIndex++
                renderNext()
            } else {
                renderResult()
            }
        }.setNegativeButton("いいえ", null).create().show()
    }

    override fun onBackPressed() {
        // do nothing
    }
}
