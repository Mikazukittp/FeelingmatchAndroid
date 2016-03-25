package mikazuki.android.app.feelingmatch.view.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ListView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import mikazuki.android.app.feelingmatch.R;
import mikazuki.android.app.feelingmatch.model.Match;
import mikazuki.android.app.feelingmatch.view.adapter.LogListAdapter;

/**
 * @author haijimakazuki
 */
public class LogActivity extends BaseActivity {

    @Bind(R.id.log_list)
    ListView mLogList;


    List<Match> mResult;
    private Realm mRealm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        ButterKnife.bind(this);

        RealmConfiguration realmConfig = new RealmConfiguration.Builder(this).build();
        mRealm = Realm.getInstance(realmConfig);
        mResult = mRealm.where(Match.class).findAll();

        mLogList.setAdapter(new LogListAdapter(this, mResult));
        mLogList.setOnItemClickListener((parent, view, position, id) -> {
            final View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_member, null);
            ButterKnife.findById(dialogView, R.id.)

            new AlertDialog.Builder(this)
                    .setTitle("過去結果詳細")
                    .setView(dialogView)
                    .setPositiveButton("閉じる", null);
        });
    }


}
