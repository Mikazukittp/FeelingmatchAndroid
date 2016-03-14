package mikazuki.android.app.feelingmatch;

import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    /**
     * @param fragment
     */
    protected void replaceFragment(Fragment fragment,
                                   @IdRes int layoutId) {
        replaceFragment(fragment, layoutId, false);
    }

    /**
     * @param fragment
     * @param shouldPushToStack
     */
    protected void replaceFragment(Fragment fragment,
                                   @IdRes int layoutId,
                                   boolean shouldPushToStack) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(layoutId, fragment, null);
        if (shouldPushToStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}
