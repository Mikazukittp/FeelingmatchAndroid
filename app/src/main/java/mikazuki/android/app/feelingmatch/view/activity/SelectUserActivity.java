package mikazuki.android.app.feelingmatch.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.TextAppearanceSpan;
import android.widget.Button;
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
import mikazuki.android.app.feelingmatch.view.adapter.CandidateListAdapter;

/**
 * @author haijimakazuki
 */
public class SelectUserActivity extends BaseActivity {

    @Bind(R.id.title)
    TextView mTitle;
    @Bind(R.id.candidates)
    ListView mCandidateList;
    @Bind(R.id.next)
    Button mNext;

    private Realm mRealm;
    private Match mMatch;
    private List<User> mMembers;
    private User mTarget;
    private int mIndex;
    private CandidateListAdapter mAdapter;

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
        mNext.setEnabled(false);
        mTarget = mMembers.get(mIndex);

        TextAppearanceSpan coloredSpan = new TextAppearanceSpan(this, mTarget.isBoy() ? R.style.Boy_Large : R.style.Girl_Large);
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        ssb.append(mTarget.getName());
        ssb.setSpan(coloredSpan, 0, ssb.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssb.append("さんの番です");
        mTitle.setText(ssb);

        mCandidateList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        final List<User> candidates = Stream.of(mMembers).filter(m -> m.getSex() == 1 - mTarget.getSex()).collect(Collectors.toList());
        mAdapter = new CandidateListAdapter(this, candidates);
        mCandidateList.setAdapter(mAdapter);
        mCandidateList.setOnItemClickListener((parent, view, position, id) -> {
            Stream.range(0, mAdapter.getCount()).map(mAdapter::getItem).forEach(row -> row.setChecked(false));
            mAdapter.getItem(position).setChecked(true);
            mAdapter.notifyDataSetChanged();
            mNext.setEnabled(true);
        });
    }

    public void renderResult() {
        new AlertDialog.Builder(this)
                .setTitle("投票終了")
                .setMessage("全員の投票が終わりました。\n結果画面へ移動します。")
                .setPositiveButton("結果閲覧", (dialog, which) -> {

                    Intent intent = new Intent(this, ResultActivity.class);
                    intent.putExtra("id", mMatch.getId());
                    startActivity(intent);
                    finish();
                }).setCancelable(false)
                .create().show();
    }

    @OnClick(R.id.next)
    public void nextPerson() {
        final User selectedUser = mAdapter.getSelectedUser();
        if (selectedUser == null) {
            return;
        }

        TextAppearanceSpan coloredSpan = new TextAppearanceSpan(this, selectedUser.isBoy() ? R.style.Boy : R.style.Girl);
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        ssb.append(selectedUser.getName());
        ssb.setSpan(coloredSpan, 0, ssb.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssb.append("さんでよろしいですか？");

        new AlertDialog.Builder(this)
                .setTitle("確認")
                .setMessage(ssb)
                .setPositiveButton("はい", (dialog, which) -> {
                    mRealm.beginTransaction();
                    mTarget.setFavoriteUserId(selectedUser.getId());
                    mRealm.copyToRealmOrUpdate(mTarget);
                    mRealm.commitTransaction();

                    if (mIndex < mMembers.size() - 1) {
                        mIndex++;
                        renderNext();
                    } else {
                        renderResult();
                    }
                }).setNegativeButton("いいえ", null)
                .create().show();
    }

    @Override
    public void onBackPressed() {
        // do nothing
    }
}
