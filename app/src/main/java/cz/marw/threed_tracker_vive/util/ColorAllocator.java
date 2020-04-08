package cz.marw.threed_tracker_vive.util;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.util.Collection;

import androidx.annotation.DrawableRes;
import cz.marw.threed_tracker_vive.R;
import cz.marw.threed_tracker_vive.model.PositionTracker;

public class ColorAllocator {

    private static final @DrawableRes int[] colors = {R.drawable.item_3d_tracker_blue,
                    R.drawable.item_3d_tracker_orange, R.drawable.item_3d_tracker_pink,
                    R.drawable.item_3d_tracker_cherry, R.drawable.item_3d_tracker_purple,
                    R.drawable.item_3d_tracker_red, R.drawable.item_3d_tracker_green};

    private Multimap<Integer, PositionTracker> colorAllocation = HashMultimap.create();

    public ColorAllocator() {
        for(int i = 0; i < colors.length; i++) {
            colorAllocation.put(colors[i], null);
        }
    }

    public void allocate(PositionTracker tracker) {
        // find the key (color) with minimum values
        Integer key = colors[0];
        int valuesCount = colorAllocation.get(key).size();
        for(int i = 1; i < colors.length; i++) {
            Integer tempKey = colors[i];
            Collection<PositionTracker> values = colorAllocation.get(tempKey);

            if(values.size() < valuesCount) {
                key = tempKey;
                valuesCount = values.size();
            }
        }

        // allocate the color to the tracker
        tracker.setColorDrawable(key);
        colorAllocation.put(key, tracker);
    }

    public void free(PositionTracker tracker) {
        colorAllocation.remove(tracker.getColorDrawable(), tracker);
    }

}
