package cz.marw.threed_tracker_vive_android.connectivity;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import cz.marw.threed_tracker_vive_android.R;

public class DevicesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // List<Device> devices;

    public class DeviceViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.connectionStateTextView)
        TextView connected;

        @BindView(R.id.deviceNameTextView)
        TextView name;

        @BindView(R.id.deviceMacTextView)
        TextView mac;

        @BindView(R.id.actionButton)
        MaterialButton action;

        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
}
