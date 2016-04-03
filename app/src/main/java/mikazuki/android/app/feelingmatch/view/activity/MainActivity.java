package mikazuki.android.app.feelingmatch.view.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Nonnull;

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

    private static final ColorStateList Pressed_Cyan = new ColorStateList(
            new int[][]{new int[]{android.R.attr.state_pressed}, new int[]{}},
            new int[]{Color.parseColor("#00BCD4"), Color.DKGRAY});

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
                if (mBoys.size() < 2 || mGirls.size() < 2) {
                    new AlertDialog.Builder(this)
                            .setMessage("男性と女性のメンバーを最低2人ずつ追加してください。")
                            .setPositiveButton("閉じる", null)
                            .create().show();
                    return false;
                }

                RealmList<User> members = new RealmList<>();

                long nextId = AutoIncrement.INSTANCE.newId(mRealm, User.class);
                mRealm.beginTransaction();
                for (User user : Stream.concat(Stream.of(mBoys), Stream.of(mGirls)).collect(Collectors.toList())) {
                    if (user.getId() == -1) user.setId(nextId++);
                    members.add(user);
                    mRealm.copyToRealmOrUpdate(user);
                }
                Match match = new Match(AutoIncrement.INSTANCE.newId(mRealm, Match.class), null, new Date(), members);
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

        mNavigationView.setItemTextColor(Pressed_Cyan);
        mNavigationView.setItemIconTintList(Pressed_Cyan);
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

        // TODO: リリース時は外す
        if (BuildConfig.DEBUG) {
            Match match = mRealm.where(Match.class).equalTo("id", 0).findFirst();
            if (match != null) {
                Stream.of(match.getMembers()).forEach(member -> {
                    if (member.isBoy()) mBoys.add(member);
                    else mGirls.add(member);
                });
            }
        }
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
                startActivity(new Intent(this, LogActivity.class));
                break;
            case R.id.menu_about:
                break;
            case R.id.menu_license:
                break;
            case R.id.menu_privacy:
                break;
            case R.id.menu_version:
                // TODO: リリース時は外す
                if (BuildConfig.DEBUG) {
                    startActivity(new Intent(this, ResultActivity.class));
                }
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
        AlertDialog alertDialog;
        final View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_member, null);
        final AppCompatEditText nameEditText = ButterKnife.findById(dialogView, R.id.name);
        final RadioGroup sexRadioGroup = ButterKnife.findById(dialogView, R.id.sex);
        final AppCompatRadioButton boyRadioButton = ButterKnife.findById(dialogView, R.id.boy);
        final AppCompatRadioButton girlRadioButton = ButterKnife.findById(dialogView, R.id.girl);
        final boolean isEdit = user != null;
        if (isEdit) {
            nameEditText.setText(user.getName());
            sexRadioGroup.check(user.isBoy() ? R.id.boy : R.id.girl);
        }
        alertDialog = new AlertDialog.Builder(this)
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
                            isEdit ? user.getId() : -1,
                            name,
                            sexRadioGroup.getCheckedRadioButtonId() == R.id.boy ? 1 : 0);


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
                }).create();
        alertDialog.setOnShowListener(dialog -> {
            if (isEdit && user.isGirl()) {
                changeDialogColor(alertDialog, nameEditText, R.color.girlDark);
            } else {
                changeDialogColor(alertDialog, nameEditText, R.color.boyDark);
            }
            boyRadioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) changeDialogColor(alertDialog, nameEditText, R.color.boyDark);
            });
            girlRadioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) changeDialogColor(alertDialog, nameEditText, R.color.girlDark);
            });
        });
        alertDialog.show();
    }

    public void changeDialogColor(@Nonnull final AlertDialog dialog,
                                  @Nonnull final AppCompatEditText editText,
                                  @ColorRes final int colorRes) {
        int color = getResources().getColor(colorRes);

        TextView titleView = (TextView) dialog.getWindow().findViewById(R.id.alertTitle);
        titleView.setTextColor(color);
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(color);
        editText.setTextColor(color);
    }
}
