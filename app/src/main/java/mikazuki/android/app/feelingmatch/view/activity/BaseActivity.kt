package mikazuki.android.app.feelingmatch.view.activity

import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import mikazuki.android.app.feelingmatch.R

abstract class BaseActivity : AppCompatActivity() {

    /**
     * @param fragment
     * @param layoutId
     * @param shouldPushToStack
     */
    @JvmOverloads protected fun replaceFragment(fragment: Fragment,
                                                @IdRes layoutId: Int,
                                                shouldPushToStack: Boolean = false) {
        val transaction = supportFragmentManager.beginTransaction().replace(layoutId, fragment, null)
        if (shouldPushToStack) {
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }

    override fun onBackPressed() {
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout?
        if (drawer?.isDrawerOpen(GravityCompat.START) ?: false) {
            drawer?.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

}
