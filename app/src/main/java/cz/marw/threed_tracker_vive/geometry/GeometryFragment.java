package cz.marw.threed_tracker_vive.geometry;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import cz.marw.threed_tracker_vive.R;
import cz.marw.threed_tracker_vive.geometry.io.GeometryDAO;
import cz.marw.threed_tracker_vive.geometry.io.GeometryParser;
import cz.marw.threed_tracker_vive.geometry.io.TextFileGeometryParser;
import cz.marw.threed_tracker_vive.model.BaseStation;
import java9.util.Comparators;
import java9.util.stream.Collectors;
import java9.util.stream.StreamSupport;

public class GeometryFragment extends Fragment {

    private static final int OPEN_STORAGE_REQ_CODE = 1;

    @BindView(R.id.baseStationsActualGeometryRV)
    RecyclerView actualGeometryRecyclerView;
    private GeometryAdapter actualGeometryAdapter;

    @BindView(R.id.baseStationsStoredGeometryRV)
    RecyclerView storedGeometryRecyclerView;
    private GeometryAdapter storedGeometryAdapter;

    private GeometryParser parser;
    private GeometryDAO geometryDAO;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_geometry, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle(R.string.geometry);
        setHasOptionsMenu(true);
        parser = new TextFileGeometryParser();
        geometryDAO = new GeometryDAO(getContext());

        actualGeometryAdapter = new GeometryAdapter(getContext());
        actualGeometryRecyclerView.setHasFixedSize(true);
        actualGeometryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        actualGeometryRecyclerView.setAdapter(actualGeometryAdapter);
        actualGeometryAdapter.setData(Geometry.getGeometry());

        storedGeometryAdapter = new GeometryAdapter(getContext());
        storedGeometryRecyclerView.setHasFixedSize(true);
        storedGeometryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        storedGeometryRecyclerView.setAdapter(storedGeometryAdapter);
        storedGeometryAdapter.setData(geometryDAO.load());
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_geometry, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.itemAddGeometry:
                openStorage();
                return true;
            case R.id.itemSwapGeometry:
                swapGeometry();
                return true;
        }

        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode != Activity.RESULT_OK) {
            return;
        }

        switch(requestCode) {
            case OPEN_STORAGE_REQ_CODE:
                if(data == null || data.getData() == null)
                    return;

                try {
                    List<BaseStation> stations = parser.parse(getContext().getContentResolver().openInputStream(data.getData()));
                    stations = StreamSupport.stream(stations)
                            .sorted(Comparators.comparingInt(BaseStation::getNumber))
                            .collect(Collectors.toList());
                    Geometry.setGeometry(stations);
                    actualGeometryAdapter.setData(stations);
                    storedGeometryAdapter.setData(stations);
                    boolean res = geometryDAO.save(stations);
                    if(!res) {
                        Toast.makeText(getContext(), R.string.file_with_station_geom_could_not_be_stored, Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), R.string.chosen_file_could_not_be_opened, Toast.LENGTH_LONG).show();
                }

                break;
        }
    }

    private void swapGeometry() {
        List<BaseStation> stations = storedGeometryAdapter.getData();
        Geometry.setGeometry(stations);
        actualGeometryAdapter.setData(stations);
    }

    private void openStorage() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/*");

        startActivityForResult(intent, OPEN_STORAGE_REQ_CODE);
    }

}
