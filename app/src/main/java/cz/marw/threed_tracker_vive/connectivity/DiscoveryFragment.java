package cz.marw.threed_tracker_vive.connectivity;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import cz.marw.threed_tracker_vive.R;

public class DiscoveryFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String MILLIS_KEY = "millis";
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_LOCATION_PERMISSIONS = 2;

    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout refreshLayout;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.devicesRecyclerView)
    RecyclerView devicesRecyclerView;
    private DevicesAdapter devicesAdapter;

    private DeviceScannerBroadcastReceiver deviceScannerReceiver;
    private boolean scanning;
    private Snackbar shownSnackbar;

    private long millis;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(savedInstanceState != null) {
            // restore fragment's state
            millis = savedInstanceState.getLong(MILLIS_KEY);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discovery, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.threed_trackers);
        refreshLayout.setOnRefreshListener(this);
        devicesAdapter = new DevicesAdapter(getContext());
        devicesRecyclerView.setHasFixedSize(true);
        devicesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        devicesRecyclerView.setAdapter(devicesAdapter);

        setHasOptionsMenu(true);

        deviceScannerReceiver = new DeviceScannerBroadcastReceiver();
        DevicesScanner.getInstance().registerBroadcastReceiver(deviceScannerReceiver, DevicesScanner.ACTION_SCANNING_STATE, DevicesScanner.ACTION_DEBUG_BLE_CONNECTION);

        if(savedInstanceState == null) {
            millis = System.currentTimeMillis();
        }
    }

    private class DeviceScannerBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(DevicesScanner.ACTION_SCANNING_STATE.equals(action)) {
                scanning = intent.getBooleanExtra(DevicesScanner.KEY_SCANNING_STATE_BOOLEAN, false);
                if(scanning) {
                    progressBar.setVisibility(View.VISIBLE);
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                }

                // show/hide stop scanning option in options menu
                if(getActivity() != null) {
                    getActivity().invalidateOptionsMenu();
                }
            } else if(DevicesScanner.ACTION_DEBUG_BLE_CONNECTION.equals(action)) {
                handleDebugBleConnection(intent);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_discovery, menu);
        menu.findItem(R.id.stopScanning).setVisible(scanning);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.stopScanning:
                DevicesScanner.getInstance().stopScanning();
                return true;
        }

        return false;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // save fragment's state
        outState.putLong(MILLIS_KEY, millis);
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("Millis: " + millis);
    }

    private void handleDebugBleConnection(Intent intent) {
        int status = intent.getIntExtra(DevicesScanner.KEY_DEBUG_BLE_CONN_STATUS, 611996);
        int newState = intent.getIntExtra(DevicesScanner.KEY_DEBUG_BLE_CONN_NEW_STATE, 611996);
        String str = "BLE Debug -> status: " + status + " new state: " + newState;
        Toast.makeText(getContext(), str, Toast.LENGTH_LONG).show();
    }

    private boolean checkBlePermissions() {
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

                requestPermissions();
            } else {
                requestPermissions();
            }

            return false;
        }

        return true;
    }

    private void requestPermissions() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSIONS);
    }

    private boolean checkBleStatus() {
        boolean permissionsGranted = checkBlePermissions();
        if(!permissionsGranted) {
            return false;
        }

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        final BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();


        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (bluetoothAdapter == null || bluetoothAdapter.getBluetoothLeScanner() == null
                || !bluetoothAdapter.isEnabled()) {

            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

            return false;
        }

        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case REQUEST_ENABLE_BT:
                if(resultCode == Activity.RESULT_OK) {
                    refreshDevices();
                } else {
                    shownSnackbar = Snackbar.make(devicesRecyclerView, R.string.bluetooth_is_not_enabled, Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.ok, (v) -> {});
                    shownSnackbar.show();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case REQUEST_LOCATION_PERMISSIONS:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    refreshDevices();
                } else {
                    shownSnackbar = Snackbar.make(devicesRecyclerView, R.string.location_permissions_are_not_enabled, Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.ok, (v) -> {});
                    shownSnackbar.show();
                }
                break;
        }
    }

    private void refreshDevices() {
        hideSnackbar();
        if(checkBleStatus()) {
            devicesAdapter.refresh();
        }
    }

    private void hideSnackbar() {
        if(shownSnackbar != null)
            shownSnackbar.dismiss();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        devicesAdapter.close();
        DevicesScanner.getInstance().unregisterBroadcastReceiver(deviceScannerReceiver);
    }

    @Override
    public void onRefresh() {
        refreshLayout.setRefreshing(false);
        refreshDevices();
    }
}
