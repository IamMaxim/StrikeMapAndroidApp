package ru.strikemap;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

import ru.strikemap.strikemap.MainActivity;
import ru.strikemap.strikemap.R;

public class MapActivity extends Activity {
    //private FrameLayout container;
    private static final int DELAY = 100;

    private MapFragment mapFragment;
    private Client client;
    private GoogleMap map;
    PosUpdater posUpdater;
    private int lashHash = 0;
    private HashMap<Integer, Marker> markers = new HashMap<>();
    Handler handler = new Handler();
    final Interpolator interpolator = new LinearInterpolator();

    private OnMapReadyCallback onMapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap map) {
            MapActivity.this.map = map;
            map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);
        mapFragment = new mMapFragment();
        getFragmentManager().beginTransaction().add(R.id.container, mapFragment).commit();

        client = MainActivity.client;

        mapFragment.getMapAsync(onMapReadyCallback);
        posUpdater = new PosUpdater();
        posUpdater.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        posUpdater.interrupt();
    }

    public class PosUpdater extends Thread {
        @Override
        public void run() {
            while (!isInterrupted()) {
                if (map != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (client == null) {
                                    System.out.println("client is null");
                                    client = MainActivity.client;
                                }
                                int hash = client.players.hashCode();
                                if (lashHash != hash) {
                                    //remove disconnected client from map
                                    for (Integer id : markers.keySet()) {
                                        if (!client.players.containsKey(id))
                                            System.out.println("removing player " + id);
                                        markers.remove(id).remove();
                                    }
                                    //add new client to map
                                    for (Integer id : client.players.keySet()) {
                                        if (!markers.containsKey(id)) {
                                            Player p = client.players.get(id);
                                            System.out.println("adding player " + id);
                                            markers.put(id, map.addMarker(new MarkerOptions().position(new LatLng(p.x, p.y)).title(p.name)));
                                        }
                                    }
                                }
                                for (final Integer id : markers.keySet()) {
                                    final Marker marker = markers.get(id);
                                    final Player player = client.players.get(id);
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            float t = (float) (System.currentTimeMillis() - client.lastPosUpdates.get(id)) / DELAY;
                                            marker.setPosition(new LatLng(
                                                    player.prevX + (player.x - player.prevX) * t,
                                                    player.prevY + (player.y - player.prevY) * t
                                            ));
                                        }
                                    });
                                }
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
                try {
                    Thread.sleep(DELAY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
