package mikazuki.android.app.feelingmatch.view.activity

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.support.annotation.ColorRes
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.AppCompatEditText
import android.support.v7.widget.AppCompatRadioButton
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.widget.RadioGroup
import android.widget.TextView
import butterknife.ButterKnife
import com.annimon.stream.Collectors
import com.annimon.stream.Stream
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmList
import kotlinx.android.synthetic.main.activity_main.*
import mikazuki.android.app.feelingmatch.BuildConfig
import mikazuki.android.app.feelingmatch.R
import mikazuki.android.app.feelingmatch.model.AutoIncrement
import mikazuki.android.app.feelingmatch.model.Match
import mikazuki.android.app.feelingmatch.model.User
import mikazuki.android.app.feelingmatch.view.adapter.MemberListAdapter
import java.util.*

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var mBoysAdapter: MemberListAdapter? = null
    private var mGirlsAdapter: MemberListAdapter? = null
    private val mBoys = ArrayList<User>()
    private val mGirls = ArrayList<User>()
    private var mRealm: Realm? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //        ButterKnife.bind(this)

        val realmConfig = RealmConfiguration.Builder(this).build()
        mRealm = Realm.getInstance(realmConfig)

        toolbar.title = getString(R.string.app_name)
        //        setSupportActionBar(mToolbar);
        toolbar.inflateMenu(R.menu.main_toolbar)
        toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.menu_start) {
                if (mBoys.size < 2 || mGirls.size < 2) {
                    AlertDialog.Builder(this).setMessage("男性と女性のメンバーを最低2人ずつ追加してください。").setPositiveButton("閉じる", null).create().show()
                    // TODO
                    false
                } else {
                    val members = RealmList<User>()
                    var nextId = AutoIncrement.newId(mRealm!!, User::class.java)
                    mRealm!!.beginTransaction()
                    for (user in Stream.concat(Stream.of(mBoys), Stream.of(mGirls)).collect(Collectors.toList<User>())) {
                        if (user.id == -1L) user.id = nextId++
                        members.add(user)
                        mRealm!!.copyToRealmOrUpdate(user)
                    }
                    val match = Match(AutoIncrement.newId(mRealm!!, Match::class.java), null, Date(), members)
                    mRealm!!.copyToRealmOrUpdate(match)
                    mRealm!!.commitTransaction()

                    val intent = Intent(this, SelectUserActivity::class.java)
                    intent.putExtra("id", match.id)
                    startActivity(intent)
                    // TODO
                    true
                }
            }
            true
        }

        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.setDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)
        nav_view.menu.findItem(R.id.menu_version).title = "バージョン " + BuildConfig.VERSION_NAME

        nav_view.itemTextColor = Pressed_Cyan
        nav_view.itemIconTintList = Pressed_Cyan
        mBoysAdapter = MemberListAdapter(this, mBoys)
        mGirlsAdapter = MemberListAdapter(this, mGirls)
        boys.adapter = mBoysAdapter
        girls.adapter = mGirlsAdapter
        boys.setOnItemLongClickListener { parent, view, position, id ->
            showMemberEditDialog(mBoys[position], position)
            true
        }
        girls.setOnItemLongClickListener { parent, view, position, id ->
            showMemberEditDialog(mGirls[position], position)
            true
        }

        fab.setOnClickListener { onClickFab(it) }

        // TODO: リリース時は外す
        if (BuildConfig.DEBUG) {
            val match = mRealm!!.where(Match::class.java).equalTo("id", 0).findFirst()
            if (match != null) {
                Stream.of(match.members).forEach { member ->
                    if (member.sex == 1)
                        mBoys.add(member)
                    else
                        mGirls.add(member)
                }
            }
        }
    }


    override fun onDestroy() {
        mRealm!!.close()
        super.onDestroy()
    }

    @SuppressWarnings("StatementWithEmptyBody")
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_result -> startActivity(Intent(this, LogActivity::class.java))
            R.id.menu_about -> {
            }
            R.id.menu_license -> {
            }
            R.id.menu_privacy -> {
            }
            R.id.menu_version -> // TODO: リリース時は外す
                if (BuildConfig.DEBUG) {
                    startActivity(Intent(this, ResultActivity::class.java))
                }
            else -> return false
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    fun onClickFab(view: View) {
        showMemberEditDialog(null, 0)
    }

    fun showMemberEditDialog(user: User?,
                             position: Int) {
        val alertDialog: AlertDialog
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_member, null)
        val nameEditText = ButterKnife.findById<AppCompatEditText>(dialogView, R.id.name)
        val sexRadioGroup = ButterKnife.findById<RadioGroup>(dialogView, R.id.sex)
        val boyRadioButton = ButterKnife.findById<AppCompatRadioButton>(dialogView, R.id.boy)
        val girlRadioButton = ButterKnife.findById<AppCompatRadioButton>(dialogView, R.id.girl)
        val isEdit = user != null
        if (isEdit) {
            nameEditText.setText(user!!.name)
            sexRadioGroup.check(if (user.sex == 1) R.id.boy else R.id.girl)
        }
        alertDialog = AlertDialog.Builder(this).setTitle(if (user != null) "メンバー編集" else "メンバー追加").setView(dialogView).setPositiveButton(if (user != null) "完了" else "追加") { dialog, which ->
            val name = nameEditText.text.toString()
            if (!TextUtils.isEmpty(name)) {
                if (isEdit && user!!.sex == 1) {
                    mBoys.removeAt(position)
                } else if (isEdit && user!!.sex == 0) {
                    mGirls.removeAt(position)
                }

                // Create new User and insert to realm
                val newUser = User(
                        if (isEdit) user!!.id else -1,
                        name,
                        if (sexRadioGroup.checkedRadioButtonId == R.id.boy) 1 else 0)


                if (newUser.sex == 1) {
                    if (isEdit && user!!.sex == 1) {
                        mBoys.add(position, newUser)
                    } else {
                        mBoys.add(newUser)
                    }
                    mBoysAdapter!!.notifyDataSetChanged()
                } else {
                    if (isEdit && user!!.sex == 0) {
                        mGirls.add(position, newUser)
                    } else {
                        mGirls.add(newUser)
                    }
                    mGirlsAdapter!!.notifyDataSetChanged()
                }
            }
        }.create()
        alertDialog.setOnShowListener { dialog ->
            if (isEdit && user!!.sex == 0) {
                changeDialogColor(alertDialog, nameEditText, R.color.girlDark)
            } else {
                changeDialogColor(alertDialog, nameEditText, R.color.boyDark)
            }
            boyRadioButton.setOnCheckedChangeListener { buttonView, isChecked -> if (isChecked) changeDialogColor(alertDialog, nameEditText, R.color.boyDark) }
            girlRadioButton.setOnCheckedChangeListener { buttonView, isChecked -> if (isChecked) changeDialogColor(alertDialog, nameEditText, R.color.girlDark) }
        }
        alertDialog.show()
    }

    fun changeDialogColor(dialog: AlertDialog,
                          editText: AppCompatEditText,
                          @ColorRes colorRes: Int) {
        val color = resources.getColor(colorRes)

        val titleView = dialog.window.findViewById(R.id.alertTitle) as TextView
        titleView.setTextColor(color)
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(color)
        editText.setTextColor(color)
    }

    companion object {

        private val Pressed_Cyan = ColorStateList(
                arrayOf(intArrayOf(android.R.attr.state_pressed), intArrayOf()),
                intArrayOf(Color.parseColor("#00BCD4"), Color.DKGRAY))
    }
}
