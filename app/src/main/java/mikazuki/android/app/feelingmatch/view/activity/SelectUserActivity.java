package mikazuki.android.app.feelingmatch.view.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ListView;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import org.parceler.Parcels;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import mikazuki.android.app.feelingmatch.R;
import mikazuki.android.app.feelingmatch.model.User;
import mikazuki.android.app.feelingmatch.view.adapter.MemberListAdapter;

/**
 * @author haijimakazuki
 */
public class SelectUserActivity extends BaseActivity {

    @Bind(R.id.candidates)
    ListView mCandidateList;

    private List<User> mBoys;
    private List<User> mGirls;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_user);
        ButterKnife.bind(this);

        mBoys = Stream.of(getIntent().getParcelableArrayExtra("boys")).map(Parcels::<User>unwrap).collect(Collectors.toList());
        mGirls = Stream.of(getIntent().getParcelableArrayExtra("girls")).map(Parcels::<User>unwrap).collect(Collectors.toList());

        mCandidateList.setAdapter(new MemberListAdapter(getApplicationContext(), mGirls));
    }
}
