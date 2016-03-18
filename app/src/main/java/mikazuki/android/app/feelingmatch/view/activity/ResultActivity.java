package mikazuki.android.app.feelingmatch.view.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.annimon.stream.Stream;
import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import mikazuki.android.app.feelingmatch.R;
import mikazuki.android.app.feelingmatch.model.Match;
import mikazuki.android.app.feelingmatch.model.User;

/**
 * @author haijimakazuki
 */
public class ResultActivity extends BaseActivity {

    @Bind(R.id.result)
    TextView mResult;

    private Realm mRealm;
    private Match mMatch;
    private List<User> mMembers;
    private Number mMatchNum;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        ButterKnife.bind(this);

        // Matchのload
        final long matchId = getIntent().getExtras().getLong("id");
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(this).build();
        mRealm = Realm.getInstance(realmConfig);
        mMatch = mRealm.where(Match.class).equalTo("id", matchId).findFirst();
        mMembers = new ArrayList<>(mMatch.getMembers());

        Map<Long, User> boys = Maps.newHashMap();
        Map<Long, User> girls = Maps.newHashMap();
        Stream.of(mMembers).forEach(u -> (u.isBoy() ? boys : girls).put(u.getId(), u));
        mMatchNum = Stream.of(boys).filter(b -> girls.get(b.getValue().getFavoriteUserId()).getFavoriteUserId() == b.getKey()).count();
        mResult.setText(mMatchNum + "組マッチ！");
    }


}
