package mikazuki.android.app.feelingmatch;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
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
            final View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_member, null);
            ((EditText) dialogView.findViewById(R.id.name)).setText(mBoys.get(position).getName());
//            ((RadioGroup) dialogView.findViewById(R.id.sex)).check(R.id.boy);
            new AlertDialog.Builder(this)
                    .setTitle("メンバー編集")
                    .setView(dialogView)
                    .setPositiveButton("完了", (dialog, which) -> {
                        final String name = ((EditText) dialogView.findViewById(R.id.name)).getText().toString();
                        if (name.length() == 0) {
                            return;
                        }
                        mBoys.remove(position);
                        final int sex = ((RadioGroup) dialogView.findViewById(R.id.sex)).getCheckedRadioButtonId();
                        if (sex == R.id.boy) {
                            mBoys.add(position, new User(name, 1));
                            mBoysAdapter.notifyDataSetChanged();
                        } else {
                            mGirls.add(new User(name, 0));
                            mGirlsAdapter.notifyDataSetChanged();
                        }
                    }).create().show();
            return true;
        });
        mGirlsList.setOnItemLongClickListener((parent, view, position, id) -> {
            final View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_member, null);
            ((EditText) dialogView.findViewById(R.id.name)).setText(mGirls.get(position).getName());
            ((RadioGroup) dialogView.findViewById(R.id.sex)).check(R.id.girl);
            new AlertDialog.Builder(this)
                    .setTitle("メンバー編集")
                    .setView(dialogView)
                    .setPositiveButton("完了", (dialog, which) -> {
                        final String name = ((EditText) dialogView.findViewById(R.id.name)).getText().toString();
                        if (name.length() == 0) {
                            return;
                        }
                        mGirls.remove(position);
                        final int sex = ((RadioGroup) dialogView.findViewById(R.id.sex)).getCheckedRadioButtonId();
                        if (sex == R.id.boy) {
                            mBoys.add(new User(name, 1));
                            mBoysAdapter.notifyDataSetChanged();
                        } else {
                            mGirls.add(position, new User(name, 0));
                            mGirlsAdapter.notifyDataSetChanged();
                        }
                    }).create().show();
            return true;
        });
        mBoysList.setItemsCanFocus(true);
    }

    @OnClick(R.id.fab)
    public void onClickFab(View view) {
        final View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_member, null);
        new AlertDialog.Builder(this)
                .setTitle("メンバー追加")
                .setView(dialogView)
                .setPositiveButton("追加", (dialog, id) -> {
                    final String name = ((EditText) dialogView.findViewById(R.id.name)).getText().toString();
                    if (name.length() == 0) {
                        return;
                    }
                    final int sex = ((RadioGroup) dialogView.findViewById(R.id.sex)).getCheckedRadioButtonId();
                    if (sex == R.id.boy) {
                        mBoys.add(new User(name, 1));
                        mBoysAdapter.notifyDataSetChanged();
                    } else {
                        mGirls.add(new User(name, 0));
                        mGirlsAdapter.notifyDataSetChanged();
                    }
                }).create().show();
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
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.list_member, parent, false);
            }
            User user = memberList.get(position);
            ((TextView) convertView.findViewById(R.id.name)).setText(user.getName());
            final int color = user.getSex() == 1 ? R.color.colorPrimaryDark : R.color.colorAccentDark;
            ((TextView) convertView.findViewById(R.id.name)).setTextColor(context.getResources().getColor(color));
            ((ImageButton) convertView.findViewById(R.id.remove)).setOnClickListener(v -> {
                new AlertDialog.Builder(context)
                        .setTitle("メンバー削除")
                        .setMessage(user.getName() + "さんを削除して本当によろしいですか？")
                        .setPositiveButton("はい", ((dialog, which) -> {
                            memberList.remove(position);
                            notifyDataSetChanged();
                        })).setNegativeButton("いいえ", null)
                        .create().show();
            });
            return convertView;
        }
    }
}
