package cz.marw.threed_tracker_vive.connectivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import cz.marw.threed_tracker_vive.R;
import cz.marw.threed_tracker_vive.model.PositionTracker;
import cz.marw.threed_tracker_vive.util.PreferenceManager;
import java9.lang.Iterables;
import java9.util.stream.StreamSupport;

public class DevicesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int DEVICE_VIEW = 0;
    private static final int MESSAGE_VIEW = 1;

    private List<PositionTracker> devices;
    private DevicesScanner scanner;
    private DevicesScannerBroadcastReceiver devicesScannerReceiver;
    private Context context;
    private boolean scanning;

    public class DeviceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.connectionStateTextView)
        TextView connected;

        @BindView(R.id.deviceNameTextView)
        TextView name;

        @BindView(R.id.deviceMacTextView)
        TextView mac;

        @BindView(R.id.deviceStateTextView)
        TextView state;

        @BindView(R.id.connectButton)
        MaterialButton connectButton;

        @BindView(R.id.sendGeometryButton)
        MaterialButton sendGeometry;

        @BindView(R.id.itemBackground)
        View background;

        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            connectButton.setOnClickListener(this);
            sendGeometry.setOnClickListener(this);
        }

        public void bind(PositionTracker tracker) {
            connected.setText((tracker.isConnected()) ? R.string.connected : R.string.disconnected);
            connectButton.setText((tracker.isConnected()) ? R.string.disconnect : R.string.connect);
           // connectButton.setEnabled(true);
            name.setText(context.getString(R.string.device_name_formatted, tracker.getDevice().getName()));
            mac.setText(context.getString(R.string.device_mac_addr_formatted, tracker.getDevice().getAddress()));

            String stateStr = "";
            switch(tracker.getState()) {
                case UNKNOWN:
                    stateStr = context.getString(R.string.unknown);
                    break;
                case GEOMETRY_NOT_SET:
                    stateStr = context.getString(R.string.geometry_is_not_set);
                    break;
                case GEOMETRY_IS_SET:
                    stateStr = context.getString(R.string.geometry_is_set);
                    break;
                case RECEIVING_COORDINATES:
                    stateStr = context.getString(R.string.receiving_coordinates);
                    break;
            }
            state.setText(context.getString(R.string.device_state, stateStr.toLowerCase()));
            background.setBackgroundResource(tracker.getColorDrawable());
            //sendGeometry.setEnabled(true);
            if(tracker.isConnected())
                sendGeometry.setVisibility((PreferenceManager.isUserAdmin()) ? View.VISIBLE : View.INVISIBLE);
            else
                sendGeometry.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onClick(View v) {
            PositionTracker tracker = devices.get(getAdapterPosition());
            switch(v.getId()) {
                case R.id.connectButton:
                    if(tracker.isConnected()) {
                        // disconnect
                        connectButton.setText(R.string.disconnecting);
                        //connectButton.setEnabled(false);
                        scanner.disconnectFromDevice(tracker.getDevice());
                    } else {
                        // connect
                        //connectButton.setEnabled(false);
                        boolean result = scanner.connectToDevice(tracker.getDevice(), context);
                        if(result) {
                            connectButton.setText(R.string.connecting);
                        }
                    }
                    break;
                case R.id.sendGeometryButton:
                    scanner.sendGeometryToTracker(tracker.getDevice());
                    //sendGeometry.setEnabled(false);
                    break;
            }
        }
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.messageTextView)
        TextView message;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(@StringRes int messageResId) {
            message.setText(messageResId);
        }
    }

    public DevicesAdapter(Context context) {
        this.context = context;
        scanner = DevicesScanner.getInstance();
        List<PositionTracker> trackers = scanner.getDevices();
        if(!trackers.isEmpty()) {
            devices = trackers;
        }
        devicesScannerReceiver = new DevicesScannerBroadcastReceiver();
        scanner.registerBroadcastReceiver(devicesScannerReceiver, DevicesScanner.ACTION_CONNECTION_STATE,
                DevicesScanner.ACTION_DEVICE_DISCOVERED, DevicesScanner.ACTION_DEVICE_STATE_CHANGED,
                DevicesScanner.ACTION_SCANNING_STATE);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        View view = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch(viewType) {
            case DEVICE_VIEW:
                view = inflater.inflate(R.layout.item_3d_tracker, parent, false);
                viewHolder = new DeviceViewHolder(view);
                break;
            case MESSAGE_VIEW:
                view = inflater.inflate(R.layout.item_3d_tracker_message, parent, false);
                viewHolder = new MessageViewHolder(view);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch(getItemViewType(position)) {
            case DEVICE_VIEW:
                ((DeviceViewHolder) holder).bind(devices.get(position));
                break;
            case MESSAGE_VIEW:
                MessageViewHolder vh = (MessageViewHolder) holder;
                if(devices == null) {
                    vh.bind(R.string.start_scan_nearby_3d_trackers);
                } else if(devices.isEmpty()) {
                    vh.bind(R.string.no_3d_tracker_was_found);
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        if(scanning && devices.isEmpty()) { // during scan hide message
            return 0;
        }

        return (devices == null || devices.isEmpty()) ? 1 : devices.size();  // when devices is empty -> render message view
    }

    @Override
    public int getItemViewType(int position) {
        if(devices == null || devices.isEmpty()) {
            return MESSAGE_VIEW;
        }

        return DEVICE_VIEW;
    }

    public void refresh() {
        if(devices == null) {
            devices = new ArrayList<>();
        }
        // remove not connected trackers from list
        int countBeforeRemove = devices.size();
        Iterables.removeIf(devices, e -> !e.isConnected());
        notifyItemRangeRemoved(0, countBeforeRemove == 0 ? 1 : countBeforeRemove); // if message is there remove it too
        scanner.startScanning();
    }

    private class DevicesScannerBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(DevicesScanner.ACTION_CONNECTION_STATE.equals(action)) {
                handleConnectionState(intent);

            } else if(DevicesScanner.ACTION_DEVICE_DISCOVERED.equals(action)) {
                handleDeviceDiscovered(intent);

            } else if(DevicesScanner.ACTION_DEVICE_STATE_CHANGED.equals(action)) {
                handleDeviceStateChanged(intent);

            } else if(DevicesScanner.ACTION_SCANNING_STATE.equals(action)) {
                handleScanningState(intent);
            }
        }
    }

    private void handleConnectionState(Intent intent) {
        PositionTracker tracker = intent.getParcelableExtra(DevicesScanner.KEY_DEVICE_POSITION_TRACKER);

        StreamSupport.stream(devices)
                .filter(e -> e.getDevice().equals(tracker.getDevice()))
                .forEach((e) -> {
                    e.setConnected(tracker.isConnected());
                    e.setState(tracker.getState());
                    notifyItemChanged(devices.indexOf(e));
                });
    }

    private void handleDeviceDiscovered(Intent intent) {
        PositionTracker tracker = intent.getParcelableExtra(DevicesScanner.KEY_DEVICE_POSITION_TRACKER);

        devices.add(tracker);
        notifyDataSetChanged();
    }

    private void handleDeviceStateChanged(Intent intent) {
        PositionTracker tracker = intent.getParcelableExtra(DevicesScanner.KEY_DEVICE_POSITION_TRACKER);

        StreamSupport.stream(devices)
                .filter(e -> e.getDevice().equals(tracker.getDevice()))
                .forEach((e) -> {
                    e.setState(tracker.getState());
                    notifyItemChanged(devices.indexOf(e));
                });
    }

    private void handleScanningState(Intent intent) {
        scanning = intent.getBooleanExtra(DevicesScanner.KEY_SCANNING_STATE_BOOLEAN, false);
        // at the end of scan notify data set change if none device was found
        if(!scanning && (devices == null || devices.isEmpty())) {
            notifyDataSetChanged();
        }
    }

    public void close() {
        scanner.unregisterBroadcastReceiver(devicesScannerReceiver);
        scanner = null;
        //TODO: next clean up (Context? Devices?)
    }
}
