package mikazuki.android.app.feelingmatch.view.activity

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.text.format.DateFormat
import android.widget.BaseAdapter
import android.widget.EditText
import butterknife.ButterKnife
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.Sort
import kotlinx.android.synthetic.main.activity_log.*
import kotlinx.android.synthetic.main.dialog_log_detail.*
import mikazuki.android.app.feelingmatch.R
import mikazuki.android.app.feelingmatch.model.Match
import mikazuki.android.app.feelingmatch.model.User
import mikazuki.android.app.feelingmatch.view.adapter.LogListAdapter
import mikazuki.android.app.feelingmatch.view.adapter.SimpleUserListAdapter

/**
 * @author haijimakazuki
 */
class LogActivity : BaseActivity() {

    internal var mResult: List<Match> = emptyList()
    private var mRealm: Realm? = null
    private var mAdapter: BaseAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)
        ButterKnife.bind(this)

        // Toolbarの設定
        toolbar.title = "過去の結果"
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
        toolbar.setNavigationOnClickListener { v -> onBackPressed() }

        val realmConfig = RealmConfiguration.Builder(this).build()
        mRealm = Realm.getInstance(realmConfig)
        mResult = mRealm!!.where(Match::class.java).findAllSorted("time", Sort.DESCENDING)

        mAdapter = LogListAdapter(this, mResult)
        log_list.adapter = mAdapter
        log_list.setOnItemClickListener { parent, view, position, id ->
            val match = mResult[position]
            val dialogView = layoutInflater.inflate(R.layout.dialog_log_detail, null)
            date.text = DateFormat.format("MM/dd hh:mm", match.time)
            name.text = if (TextUtils.isEmpty(match.name)) "名前未設定" else match.name
            edit_name.setOnClickListener { v ->
                val editDialogView = layoutInflater.inflate(R.layout.dialog_log_detail_edit_name, null)
                val nameEditText = ButterKnife.findById<EditText>(editDialogView, R.id.name)
                nameEditText.setText(match.name)
                AlertDialog.Builder(this)
                        .setTitle("名前編集")
                        .setView(editDialogView)
                        .setPositiveButton("決定") { d, w ->
                            val newName = nameEditText.text.toString()
                            if (!TextUtils.isEmpty(newName)) {
                                mRealm!!.beginTransaction()
                                match.name = newName
                                mRealm!!.copyToRealm(match)
                                mRealm!!.commitTransaction()
                                name.text = newName
                            }
                        }.setNegativeButton("キャンセル", null)
                        .create().show()
            }
            member.adapter = SimpleUserListAdapter(this, match.members ?: emptyList<User>())

            AlertDialog.Builder(this)
                    .setTitle("過去結果詳細")
                    .setView(dialogView)
                    .setPositiveButton("閉じる", null)
                    .setOnDismissListener { d -> mAdapter!!.notifyDataSetChanged() }
                    .create().show()
        }
    }


}
