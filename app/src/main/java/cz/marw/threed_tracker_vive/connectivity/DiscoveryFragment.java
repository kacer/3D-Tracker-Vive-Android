package cz.marw.threed_tracker_vive_android.connectivity;

import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import cz.marw.threed_tracker_vive_android.R;

public class DiscoveryFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout refreshLayout;

    @BindView(R.id.devicesRecyclerView)
    RecyclerView devicesRecyclerView;

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
        refreshLayout.setOnRefreshListener(this);
        devicesRecyclerView.setHasFixedSize(true);
        devicesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //devicesRecyclerView.setAdapter();
        new DevicesScanner((BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE), getContext());
    }

    @Override
    public void onRefresh() {
        System.out.println("onRefresh");
    }
}
