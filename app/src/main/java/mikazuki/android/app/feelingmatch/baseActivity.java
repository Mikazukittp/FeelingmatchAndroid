package mikazuki.android.app.feelingmatch;

import android.support.annotation.LayoutRes;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewStub;

public abstract class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    /**
     * @param containerLayoutId
     * @param stubViewId
     */
    protected void setNestView(@LayoutRes int containerLayoutId,
                               @LayoutRes int stubViewId) {
        setContentView(containerLayoutId);
        setStubView(stubViewId);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    /**
     * @param containerLayoutId
     * @param stubViewId
     */
    protected void setNestViewWithNavigation(@LayoutRes int containerLayoutId,
                                             @LayoutRes int stubViewId) {
        setNestView(containerLayoutId, stubViewId);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setNavigationDrawer(toolbar);
    }


    private void setStubView(@LayoutRes int layoutId) {
        ViewStub stub = (ViewStub) findViewById(R.id.stub);
        stub.setLayoutResource(layoutId);
        stub.inflate();
    }

    private void setNavigationDrawer(final Toolbar toolbar) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * @param fragment
     */
    protected void replaceFragment(Fragment fragment) {
        replaceFragment(fragment, false);
    }

    /**
     * @param fragment
     * @param shouldPushToStack
     */
    protected void replaceFragment(Fragment fragment, boolean shouldPushToStack) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment, null);
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


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
