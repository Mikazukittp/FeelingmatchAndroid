package mikazuki.android.app.feelingmatch.view.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;

import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import mikazuki.android.app.feelingmatch.BuildConfig;
import mikazuki.android.app.feelingmatch.R;
import mikazuki.android.app.feelingmatch.model.AutoIncrement;
import mikazuki.android.app.feelingmatch.model.Match;
import mikazuki.android.app.feelingmatch.model.User;
import mikazuki.android.app.feelingmatch.view.adapter.MemberListAdapter;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @Bind(R.id.nav_view)
    NavigationView mNavigationView;
    @Bind(R.id.boys)
    ListView mBoysList;
    @Bind(R.id.girls)
    ListView mGirlsList;
    private MemberListAdapter mBoysAdapter;
    private MemberListAdapter mGirlsAdapter;
    private List<User> mBoys = new ArrayList<>();
    private List<User> mGirls = new ArrayList<>();
    private Realm mRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        RealmConfiguration realmConfig = new RealmConfiguration.Builder(this).build();
        mRealm = Realm.getInstance(realmConfig);

        mToolbar.setTitle(getString(R.string.app_name));
//        setSupportActionBar(mToolbar);
        mToolbar.inflateMenu(R.menu.main_toolbar);
        mToolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_start) {
                RealmList<User> members = new RealmList<>();
                Stream.of(mBoys).forEach(members::add);
                Stream.of(mGirls).forEach(members::add);
                Match match = new Match(AutoIncrement.newId(mRealm, Match.class), new Date(), members);
                mRealm.beginTransaction();
                mRealm.copyToRealmOrUpdate(match);
                mRealm.commitTransaction();

                Intent intent = new Intent(this, SelectUserActivity.class);
                intent.putExtra("id", match.getId());
                startActivity(intent);
                return true;
            }
            return true;
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();
        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.getMenu().findItem(R.id.menu_version).setTitle("バージョン " + BuildConfig.VERSION_NAME);

        mBoysAdapter = new MemberListAdapter(this, mBoys);
        mGirlsAdapter = new MemberListAdapter(this, mGirls);
        mBoysList.setAdapter(mBoysAdapter);
        mGirlsList.setAdapter(mGirlsAdapter);
        mBoysList.setOnItemLongClickListener((parent, view, position, id) -> {
            showMemberEditDialog(mBoys.get(position), position);
            return true;
        });
        mGirlsList.setOnItemLongClickListener((parent, view, position, id) -> {
            showMemberEditDialog(mGirls.get(position), position);
            return true;
        });
    }


    @Override
    protected void onDestroy() {
        mRealm.close();
        super.onDestroy();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_result:
                break;
            case R.id.menu_about:
                break;
            case R.id.menu_license:
                break;
            case R.id.menu_privacy:
                break;
            case R.id.menu_version:
                break;
            default:
                return false;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @OnClick(R.id.fab)
    public void onClickFab(View view) {
        showMemberEditDialog(null, 0);
    }

    public void showMemberEditDialog(@Nullable User user,
                                     int position) {
        final View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_member, null);
        final EditText nameEditText = ButterKnife.findById(dialogView, R.id.name);
        final RadioGroup sexRadioGroup = ButterKnife.findById(dialogView, R.id.sex);
        final boolean isEdit = user != null;
        if (isEdit) {
            nameEditText.setText(user.getName());
            sexRadioGroup.check(user.isBoy() ? R.id.boy : R.id.girl);
        }
        new AlertDialog.Builder(this)
                .setTitle(user != null ? "メンバー編集" : "メンバー追加")
                .setView(dialogView)
                .setPositiveButton(user != null ? "完了" : "追加", (dialog, which) -> {
                    final String name = nameEditText.getText().toString();
                    if (TextUtils.isEmpty(name)) return;
                    if (isEdit && user.isBoy()) {
                        mBoys.remove(position);
                    } else if (isEdit && user.isGirl()) {
                        mGirls.remove(position);
                    }

                    // Create new User and insert to realm
                    User newUser = new User(
                            isEdit ? user.getId() : AutoIncrement.newId(mRealm, User.class),
                            name,
                            sexRadioGroup.getCheckedRadioButtonId() == R.id.boy ? 1 : 0);
                    mRealm.beginTransaction();
                    mRealm.copyToRealmOrUpdate(newUser);
                    mRealm.commitTransaction();

                    if (newUser.isBoy()) {
                        if (isEdit && user.isBoy()) {
                            mBoys.add(position, newUser);
                        } else {
                            mBoys.add(newUser);
                        }
                        mBoysAdapter.notifyDataSetChanged();
                    } else {
                        if (isEdit && user.isGirl()) {
                            mGirls.add(position, newUser);
                        } else {
                            mGirls.add(newUser);
                        }
                        mGirlsAdapter.notifyDataSetChanged();
                    }
                }).create().show();
    }

    public void changeDialogColor(Dialog dialog) {

    }
}
