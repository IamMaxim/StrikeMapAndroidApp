package ru.strikemap;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Iterator;

import ru.strikemap.strikemap.MainActivity;
import ru.strikemap.strikemap.R;

public class MapActivity extends Activity {
    //private FrameLayout container;
    private static final int DELAY = 300;
    private static final float SCROLL_MODIFIER = 300;

    private MapFragment mapFragment;
    private Client client;
    private GoogleMap map;
    PosUpdater posUpdater;
    private int lashHash = 0;
    private HashMap<Integer, Marker> markers = new HashMap<>();
    Handler handler = new Handler();
    final Interpolator interpolator = new LinearInterpolator();
    private HashMap<Integer, Float> lastUpdates = new HashMap<>();
    private HashMap<Integer, Player.State> lastStates = new HashMap<>();

    private OnMapReadyCallback onMapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap map) {
            MapActivity.this.map = map;
            map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

            posUpdater = new PosUpdater();
            posUpdater.start();
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
                                ((TextView)findViewById(R.id.debugTv)).setText(MainActivity.lastInput.toString());

                                map.animateCamera(CameraUpdateFactory.scrollBy(
                                        (-MainActivity.lastInput.left + MainActivity.lastInput.right) * SCROLL_MODIFIER,
                                        (-MainActivity.lastInput.up + MainActivity.lastInput.down) * SCROLL_MODIFIER), DELAY, null);

                                if (client == null) {
                                    System.out.println("client is null");
                                    client = MainActivity.client;
                                }
                                int hash = client.players.hashCode();
                                if (lashHash != hash) {
                                    lashHash = hash;
                                    //remove disconnected client from map
                                    Iterator<Integer> it = markers.keySet().iterator();
                                    while (it.hasNext()) {
                                        int id = it.next();
                                        if (!client.players.containsKey(id))
                                            System.out.println("removing player " + id);
                                        markers.get(id).remove();
                                        it.remove();
                                    }
                                    //add new client to map
                                    for (Integer id : client.players.keySet()) {
                                        if (!markers.containsKey(id)) {
                                            Player p = client.players.get(id);
                                            System.out.println("adding player " + id);
                                            System.out.println(markers.put(id, map.addMarker(new MarkerOptions().position(new LatLng(p.x, p.y)).title(p.name).flat(true))));
                                        }
                                    }
                                }
                                for (final Integer id : markers.keySet()) {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                Marker marker = markers.get(id);
                                                Player player = client.players.get(id);
                                                if (lastStates.get(id) != player.state) {
                                                    lastStates.put(id, player.state);
                                                    if (player.state == Player.State.DEAD) {
                                                        marker.remove();
                                                        marker = map.addMarker(new MarkerOptions().position(marker.getPosition()).title(marker.getTitle()).flat(true).anchor(0.5f, 0.5f)
                                                                .icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.dead), 64, 64, true))));
                                                        markers.put(id, marker);
                                                    } else if (player.state == Player.State.COVER) {
                                                        marker.remove();
                                                        marker = map.addMarker(new MarkerOptions().position(marker.getPosition()).title(marker.getTitle()).flat(true).anchor(0.5f, 0.5f)
                                                                .icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.cover), 64, 64, true))));
                                                        markers.put(id, marker);
                                                    } else {
                                                        marker.remove();
                                                        marker = map.addMarker(new MarkerOptions().position(marker.getPosition()).title(marker.getTitle()).flat(true).anchor(0.5f, 0.5f)
                                                                .icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.normal), 64, 64, true))));
                                                        markers.put(id, marker);
                                                    }
                                                }

                                                float t = (float) (System.currentTimeMillis() - client.prevPosUpdates.get(id)) / (client.lastPosUpdates.get(id) - client.prevPosUpdates.get(id));
                                                System.out.println(t);
                                                if (t < 0) t = 0;
                                                if (t > 1) t = 1;
                                                System.out.println("trying to set pos to " + (player.prevX + (player.x - player.prevX) * t) + " " + (player.prevY + (player.y - player.prevY) * t));
                                                marker.setPosition(new LatLng(
                                                        player.prevX + (player.x - player.prevX) * t,
                                                        player.prevY + (player.y - player.prevY) * t
                                                ));
                                            } catch (NullPointerException e) {
                                                e.printStackTrace();
                                            }
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
