package cz.marw.threed_tracker_vive;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.os.Handler;
import android.view.View;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import cz.marw.threed_tracker_vive.geometry.GeometryFragment;
import cz.marw.threed_tracker_vive.model.PositionTracker;
import cz.marw.threed_tracker_vive.rendering.RenderingFragment;
import cz.marw.threed_tracker_vive.util.PreferenceManager;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, DrawerLayout.DrawerListener,
        View.OnClickListener {

    private static final String FRAGMENT_KEY = "fragment";

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    TextView connectedTrackers;

    private int itemSelected;
    private Fragment discoveryFragment;
    private Fragment renderingFragment;

    private DevicesBroadcastReceiver broadcastReceiver = new DevicesBroadcastReceiver();

    private int clickCounter;
    private Handler clickHandler = new Handler();
    private static final int MAX_CLICK_DELAY = 2000; // 2s

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
        connectedTrackers = navigationView.getHeaderView(0).findViewById(R.id.connectedTrackersTextView);
        connectedTrackers.setText(getConnectedTrackersString(0));
        if(PreferenceManager.isUserAdmin())
            navigationView.getMenu().findItem(R.id.nav_geometry).setVisible(true);

        /*if(savedInstanceState != null) {
            // restore the fragment's instance
            discoveryFragment = getSupportFragmentManager().getFragment(savedInstanceState, FRAGMENT_KEY);

        } else {
            discoveryFragment = new DiscoveryFragment();
        }*/
        discoveryFragment = new DiscoveryFragment();
        renderingFragment = new RenderingFragment();

        DevicesScanner.getInstance().registerBroadcastReceiver(broadcastReceiver, DevicesScanner.ACTION_CONNECTION_STATE);

        openFragment(discoveryFragment);

        // check supported version of OpenGL
        if(isOpenGLVersionSupported()) {
            navigationView.getMenu().findItem(R.id.nav_renderer).setEnabled(true);
        } else {
            navigationView.getMenu().findItem(R.id.nav_renderer).setEnabled(false);
            Toast.makeText(this, R.string.device_does_not_support_opengl, Toast.LENGTH_LONG).show();
        }
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
        switch(itemSelected) {
            case R.id.nav_trackers:
                openFragment(discoveryFragment);
                break;
            case R.id.nav_geometry:
                openFragment(new GeometryFragment());
                break;
            case R.id.nav_renderer:
                openFragment(renderingFragment);
                break;
            case R.id.nav_about_app:
                itemSelected = 0;
                aboutAppDialog();
                break;
        }
    }

    private boolean isOpenGLVersionSupported() {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo info = am.getDeviceConfigurationInfo();

        return info.reqGlEsVersion >= 0x30000;
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

    private void aboutAppDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_about_app, null);
        View appIcon = view.findViewById(R.id.appIcon);
        appIcon.setOnClickListener(this);
        builder.setView(view);

        TextView version = view.findViewById(R.id.appVersionTextView);
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            version.setText(info.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        builder.show();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.appIcon) {
            if(!PreferenceManager.isUserAdmin()) {
                clickHandler.removeCallbacksAndMessages(null);
                clickHandler.postDelayed(() -> clickCounter = 0, MAX_CLICK_DELAY);
                if(++clickCounter == 10) {
                    PreferenceManager.setUserAdmin(true);
                    recreate();
                    Toast.makeText(this, R.string.now_you_are_admin, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private String getConnectedTrackersString(int count) {
        return getString(R.string.connected_colon_formatted,
                getResources().getQuantityString(R.plurals.number_of_trackers, count, count));
    }

    private class DevicesBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(DevicesScanner.ACTION_CONNECTION_STATE.equals(action)) {
                // recalculate a connected trackers
                List<PositionTracker> devices = DevicesScanner.getInstance().getDevices();
                int count = 0;
                for(PositionTracker tracker : devices) {
                    if(tracker.isConnected()) {
                        count++;
                    }
                }
                connectedTrackers.setText(getConnectedTrackersString(count));
            }
        }
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
