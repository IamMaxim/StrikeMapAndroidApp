package ru.strikemap;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.MapFragment;

/**
 * Created by maxim on 16.10.2016.
 */

public class mMapFragment extends MapFragment {
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View v = super.onCreateView(layoutInflater, viewGroup, bundle);
        v.findViewWithTag("GoogleWatermark").setVisibility(View.INVISIBLE);
        return v;
    }
}
