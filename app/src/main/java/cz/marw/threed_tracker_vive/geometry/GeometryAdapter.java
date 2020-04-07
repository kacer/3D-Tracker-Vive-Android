package cz.marw.threed_tracker_vive.geometry;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import cz.marw.threed_tracker_vive.R;
import cz.marw.threed_tracker_vive.model.BaseStation;

public class GeometryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_EMPTY = 0;
    private static final int VIEW_ITEM = 1;

    private Context context;
    private List<BaseStation> stations = new ArrayList<>();

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.station1NumberTextView)
        TextView stationNumber;

        @BindView(R.id.origin1TextView)
        TextView origin;

        @BindView(R.id.row11TextView)
        TextView rotationRow1;

        @BindView(R.id.row12TextView)
        TextView rotationRow2;

        @BindView(R.id.row13TextView)
        TextView rotationRow3;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(BaseStation station) {
            stationNumber.setText(context.getString(R.string.base_station_number_formatted, station.getNumber()));
            origin.setText(context.getString(R.string.origin_formatted, station.getOrigin().getX(),
                    station.getOrigin().getY(), station.getOrigin().getZ()));
            BaseStation.Vector3D row1 = station.getRotationMatrix().getRow(0);
            BaseStation.Vector3D row2 = station.getRotationMatrix().getRow(1);
            BaseStation.Vector3D row3 = station.getRotationMatrix().getRow(2);
            rotationRow1.setText(context.getString(R.string.matrix_row_formatted, row1.getX(), row1.getY(), row1.getZ()));
            rotationRow2.setText(context.getString(R.string.matrix_row_formatted, row2.getX(), row2.getY(), row2.getZ()));
            rotationRow3.setText(context.getString(R.string.matrix_row_formatted, row3.getX(), row3.getY(), row3.getZ()));
        }
    }

    public class EmptyViewHolder extends RecyclerView.ViewHolder {

        public EmptyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public GeometryAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<BaseStation> stations) {
        this.stations = stations;
        notifyDataSetChanged();
    }

    public List<BaseStation> getData() {
        return stations;
    }

    @Override
    public int getItemViewType(int position) {
        if(stations.size() == 0) {
            return VIEW_EMPTY;
        }

        return VIEW_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh = null;
        View view = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch(viewType) {
            case VIEW_EMPTY:
                view = inflater.inflate(R.layout.item_base_station_geometry_message, parent, false);
                vh = new EmptyViewHolder(view);
                break;
            case VIEW_ITEM:
                view = inflater.inflate(R.layout.item_base_station_geometry, parent, false);
                vh = new ItemViewHolder(view);
                break;
        }

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch(getItemViewType(position)) {
            case VIEW_EMPTY:
                // nothing to bind
                break;
            case VIEW_ITEM:
                ((ItemViewHolder) holder).bind(stations.get(position));
                break;
        }
    }

    @Override
    public int getItemCount() {
        if(stations.isEmpty())
            return 1;

        return stations.size();
    }
}
