package me.beartransit.apollojain.myapplication;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

public class LocationDetailsActivity extends AppCompatActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private TextView description;
    private String url;
    private String location;
    private LocationDetailsActivity b = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getActionBar();
        setContentView(R.layout.activity_location_details);
        Intent intent = getIntent();
        url = intent.getExtras().getString("url");
        location = intent.getExtras().getString("name");
        setTitle(location);

        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();

        CameraUpdate center= CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude));
        CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);
        mMap.moveCamera(center);
        mMap.animateCamera(zoom);

        mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("Your Current Location"));
        Log.e("THE URL IS!!!", url);
        new GetLocationTask().execute(url);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_map:
                Intent intent1 = new Intent(this, MapsActivity.class);
                this.startActivity(intent1);
                break;
            case R.id.menu_list:
                Intent intent2 = new Intent(this, BusListActivity.class);
                this.startActivity(intent2);
                break;
            case R.id.menu_current:
                Intent intent3 = new Intent(this, CurrentMapsActivity.class);
                this.startActivity(intent3);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private class GetLocationTask extends AsyncTask {
        protected Object doInBackground(Object[] urls) {
            try{
//                JSONObject page = new Communicator().getJsonFromUrl((String) urls[0]);
                JSONObject page = new Communicator().getJsonFromUrl((String) urls[0]);
                return page;
            }catch(Exception e){
                return null;
            }

        }

        protected void onPostExecute(Object result) {
            try {
                String name = ((JSONObject) result).getString("name");
                double lat = ((JSONObject) result).getDouble("lat");
                double lon = ((JSONObject) result).getDouble("lon");
                JSONArray ja = ((JSONObject) result).getJSONArray("next");
                String[] useless = new String[ja.length()];
                mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title(name).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_stop)));
                LocationDetailsAdapter adapter = new LocationDetailsAdapter(b, R.layout.location_details_list, useless,  ja);
                final ListView listView = (ListView) findViewById(R.id.details_list);
                listView.setAdapter(adapter);
            }catch(Exception e){
                Log.e("Error", e.toString());
            }
        }
    }
}
