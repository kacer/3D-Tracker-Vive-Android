package cz.marw.threed_tracker_vive;

import android.os.Bundle;

import android.view.View;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import cz.marw.threed_tracker_vive.connectivity.DevicesScanner;
import cz.marw.threed_tracker_vive.connectivity.DiscoveryFragment;
import cz.marw.threed_tracker_vive.rendering.RenderingFragment;

import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, DrawerLayout.DrawerListener {

    private static final String FRAGMENT_KEY = "fragment";

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    private int itemSelected;
    private Fragment discoveryFragment;
    private Fragment renderingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DevicesScanner.init(this);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        drawer.addDrawerListener(this);

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_trackers);

        /*if(savedInstanceState != null) {
            // restore the fragment's instance
            discoveryFragment = getSupportFragmentManager().getFragment(savedInstanceState, FRAGMENT_KEY);

        } else {
            discoveryFragment = new DiscoveryFragment();
        }*/
        discoveryFragment = new DiscoveryFragment();
        renderingFragment = new RenderingFragment();

        openFragment(discoveryFragment);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DevicesScanner.destroy();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save the fragment's instance
       // getSupportFragmentManager().putFragment(outState, FRAGMENT_KEY, discoveryFragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        itemSelected = item.getItemId();

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onDrawerClosed(@NonNull View drawerView) {
        if (itemSelected == R.id.nav_trackers) {
            openFragment(discoveryFragment);
        } else if (itemSelected == R.id.nav_renderer) {
            openFragment(renderingFragment);
        }
    }

    private void openFragment(Fragment fragment) {
        Fragment actualFragment = getActualFragment();
        if(fragment == null || (actualFragment != null && actualFragment.getClass() == fragment.getClass()))
            return;

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.containerFrameLayout, fragment)
                .commit();
    }

    private Fragment getActualFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.containerFrameLayout);
    }


    @Override
    public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(@NonNull View drawerView) {

    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }
}
