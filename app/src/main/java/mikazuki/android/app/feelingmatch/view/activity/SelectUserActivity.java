package mikazuki.android.app.feelingmatch.view.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ListView;
import android.widget.TextView;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import mikazuki.android.app.feelingmatch.R;
import mikazuki.android.app.feelingmatch.model.Match;
import mikazuki.android.app.feelingmatch.model.User;
import mikazuki.android.app.feelingmatch.view.adapter.MemberListAdapter;

/**
 * @author haijimakazuki
 */
public class SelectUserActivity extends BaseActivity {

    @Bind(R.id.title)
    TextView mTitle;
    @Bind(R.id.candidates)
    ListView mCandidateList;

    private Realm mRealm;
    private Match mMatch;
    private List<User> mMembers;
    private User mTarget;
    private int mIndex;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_user);
        ButterKnife.bind(this);

        // Matchのload
        final long matchId = getIntent().getExtras().getLong("id");
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(this).build();
        mRealm = Realm.getInstance(realmConfig);
        mMatch = mRealm.where(Match.class).equalTo("id", matchId).findFirst();
        mMembers = new ArrayList<>(mMatch.getMembers());
        if (mMembers.size() > 0) {
            mIndex = 0;
            renderNext();
        }
    }

    @Override
    protected void onDestroy() {
        mRealm.close();
        super.onDestroy();
    }

    public void renderNext() {
        mTarget = mMembers.get(mIndex);
        mTitle.setText(mTarget.getName() + "さんの番です");

        // TODO: 正しいAdapterをセット
        mCandidateList.setAdapter(new MemberListAdapter(this, Stream.of(mMembers).filter(m -> m.getSex() == 1 - mTarget.getSex()).collect(Collectors.toList())));
    }

    public void renderResult() {
        // TODO: 結果画面へ
        // startActivity();
    }

    @OnClick(R.id.next)
    public void nextPerson() {
        // TODO: 確認ダイアログ
        // TODO: 好みを保存
        // mTarget.favoriteUserId = selectedUser.getId();
        mRealm.beginTransaction();
        mRealm.copyToRealmOrUpdate(mTarget);
        mRealm.commitTransaction();


        if (mIndex < mMembers.size() - 1) {
            mIndex++;
            renderNext();
        } else {
            renderResult();
        }
    }

    // TODO: 戻るボタン禁止

}
