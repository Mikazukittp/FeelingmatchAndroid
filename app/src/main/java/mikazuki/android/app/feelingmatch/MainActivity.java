package mikazuki.android.app.feelingmatch;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mikazuki.android.app.feelingmatch.model.User;

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
    private ArrayList<User> mBoys = new ArrayList<>();
    private ArrayList<User> mGirls = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();
        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.getMenu().findItem(R.id.menu_version).setTitle("バージョン " + BuildConfig.VERSION_NAME);

        mBoys.add(new User("たろう", 1));
        mBoys.add(new User("じろう", 1));
        mBoys.add(new User("さぶろう", 1));
        mGirls.add(new User("はるこ", 0));
        mGirls.add(new User("なつこ", 0));
        mGirls.add(new User("あきこ", 0));

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

    @OnClick(R.id.fab)
    public void onClickFab(View view) {
        showMemberEditDialog(null, 0);
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
                    User newUser = new User(name, sexRadioGroup.getCheckedRadioButtonId() == R.id.boy ? 1 : 0);
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

    static class MemberListAdapter extends BaseAdapter {

        Context context;
        LayoutInflater layoutInflater = null;
        List<User> memberList;

        public MemberListAdapter(Context context, List<User> memberList) {
            this.context = context;
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.memberList = memberList;
        }

        @Override
        public int getCount() {
            return memberList.size();
        }

        @Override
        public Object getItem(int position) {
            return memberList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position,
                            View convertView,
                            ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.list_member, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            User user = memberList.get(position);
            holder.name.setText(user.getName());
            final int color = user.isBoy() ? R.color.colorPrimaryDark : R.color.colorAccentDark;
            holder.name.setTextColor(context.getResources().getColor(color));
            holder.remove.setOnClickListener(v ->
                    new AlertDialog.Builder(context)
                            .setTitle("メンバー削除")
                            .setMessage(user.getName() + "さんを削除して本当によろしいですか？")
                            .setPositiveButton("はい", ((dialog, which) -> {
                                memberList.remove(position);
                                notifyDataSetChanged();
                            })).setNegativeButton("いいえ", null)
                            .create()
                            .show());
            return convertView;
        }

        public static class ViewHolder {
            @Bind(R.id.name)
            TextView name;
            @Bind(R.id.remove)
            ImageButton remove;

            public ViewHolder(View view) {
                ButterKnife.bind(view);
            }
        }
    }
}
