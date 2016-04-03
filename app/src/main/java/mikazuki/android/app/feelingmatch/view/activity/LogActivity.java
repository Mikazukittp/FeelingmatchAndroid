package mikazuki.android.app.feelingmatch.view.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.Sort;
import mikazuki.android.app.feelingmatch.R;
import mikazuki.android.app.feelingmatch.model.Match;
import mikazuki.android.app.feelingmatch.view.adapter.LogListAdapter;
import mikazuki.android.app.feelingmatch.view.adapter.SimpleUserListAdapter;

/**
 * @author haijimakazuki
 */
public class LogActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.log_list)
    ListView mLogList;

    List<Match> mResult;
    private Realm mRealm;
    private BaseAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        ButterKnife.bind(this);

        // Toolbarの設定
        mToolbar.setTitle("過去の結果");
        setSupportActionBar(mToolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mToolbar.setNavigationOnClickListener(v -> onBackPressed());


        RealmConfiguration realmConfig = new RealmConfiguration.Builder(this).build();
        mRealm = Realm.getInstance(realmConfig);
        mResult = mRealm.where(Match.class).findAllSorted("time", Sort.DESCENDING);

        mAdapter = new LogListAdapter(this, mResult);
        mLogList.setAdapter(mAdapter);
        mLogList.setOnItemClickListener((parent, view, position, id) -> {
            final Match match = mResult.get(position);
            final View dialogView = getLayoutInflater().inflate(R.layout.dialog_log_detail, null);
            final TextView date = ButterKnife.findById(dialogView, R.id.date);
            final TextView name = ButterKnife.findById(dialogView, R.id.name);
            final ImageButton editName = ButterKnife.findById(dialogView, R.id.edit_name);
            final ListView members = ButterKnife.findById(dialogView, R.id.member);
            date.setText(DateFormat.format("MM/dd hh:mm", match.getTime()));
            name.setText(StringUtils.isEmpty(match.getName()) ? "名前未設定" : match.getName());
            editName.setOnClickListener(v -> {
                final View editDialogView = getLayoutInflater().inflate(R.layout.dialog_log_detail_edit_name, null);
                final EditText nameEditText = ButterKnife.findById(editDialogView, R.id.name);
                nameEditText.setText(match.getName());
                new AlertDialog.Builder(this)
                        .setTitle("名前編集")
                        .setView(editDialogView)
                        .setPositiveButton("決定", (d, w) -> {
                            String newName = nameEditText.getText().toString();
                            if (StringUtils.isEmpty(newName)) {
                                return;
                            }
                            mRealm.beginTransaction();
                            match.setName(newName);
                            mRealm.copyToRealm(match);
                            mRealm.commitTransaction();
                            name.setText(newName);
                        })
                        .setNegativeButton("キャンセル", null)
                        .create().show();
            });
            members.setAdapter(new SimpleUserListAdapter(this, match.getMembers()));

            new AlertDialog.Builder(this)
                    .setTitle("過去結果詳細")
                    .setView(dialogView)
                    .setPositiveButton("閉じる", null)
                    .setOnDismissListener(d -> mAdapter.notifyDataSetChanged())
                    .create().show();
        });
    }


}
