package cz.marw.threed_tracker_vive.rendering;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import cz.marw.threed_tracker_vive.R;
import cz.marw.threed_tracker_vive.util.PreferenceManager;

public class RenderingFragment extends Fragment {

    @BindView(R.id.sceneGLSurfaceView)
    SceneGLSurfaceView glSurfaceView;

    private AlertDialog dialogInfo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rendering, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle(R.string.position_rendering);
        setHasOptionsMenu(true);
        dialogInfo = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.info)
                .setView(R.layout.dialog_rendering_info)
                .create();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_rendering, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.itemChangeBackground:
                boolean dark = PreferenceManager.isDarkBackground3DScene();
                PreferenceManager.setDarkBackground3DScene(!dark);
                glSurfaceView.changeBackground();
                return true;
            case R.id.itemShowInfoDialog:
                dialogInfo.show();
                return true;
        }

        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        glSurfaceView.onResume();
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onPause() {
        super.onPause();
        glSurfaceView.onPause();
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
}
