package mikazuki.android.app.feelingmatch;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;

import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // layoutの設定
        setNestViewWithNavigation(R.layout.with_navigation_bar, R.layout.app_bar_fab);
        ButterKnife.bind(this);

        replaceFragment(new MainFragment());

        // ボタンの処理の追加
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
}
