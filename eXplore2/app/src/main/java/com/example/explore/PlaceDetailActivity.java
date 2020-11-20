package com.example.explore;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import static com.example.explore.PlaceListActivity.EXTRA_XID;

public class PlaceDetailActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {
    //    private String xid = "";
//    private String JSON_URL = "https://api.opentripmap.com/0.1/en/places/xid/N2180835380?apikey=5ae2e3f221c38a28845f05b65ba166329393551235361ab9b66e2889";
//    private String dataName, dataCategory, dataAddress, dataDetail;
    private MapView mMapView;

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    double placeLong;
    double placeLatt;
    String placeName;
    TextView detailCategory, detailName, detailAddress, detailPlace;
    ImageView detailImage;
    Button btnIntentMaps;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);

        requestQueue = Volley.newRequestQueue(this);
        detailName = findViewById(R.id.detailName);
//        detailCategory = findViewById(R.id.detailCategory);
        detailAddress = findViewById(R.id.detailAddress);
        detailPlace = findViewById(R.id.detailPlace);
        detailImage = findViewById(R.id.detailImage);

        btnIntentMaps = findViewById(R.id.btn_navigate_map);
        btnIntentMaps.setOnClickListener(this);

        Intent intent = getIntent();
        String xid = intent.getStringExtra(EXTRA_XID);

        parseJSON(xid);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mMapView = (MapView) findViewById(R.id.map_view_detail);
        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        double placeLatt = this.placeLatt;
        double placeLong = this.placeLong;
        LatLng position = new LatLng(placeLatt, placeLong);
        map.addMarker(new MarkerOptions().position(position).title("Marker"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 18));
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    private void parseJSON(String xid) {
        String JSON_URL = "https://api.opentripmap.com/0.1/en/places/xid/" + xid + "?apikey=5ae2e3f221c38a28845f05b65ba166329393551235361ab9b66e2889";

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, JSON_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
//                    JSONObject obj = response.getJSONObject();
//                    String dataCategory = response.getString("kinds");


                    double dataLon = response.getJSONObject("point").getDouble("lon");
                    double dataLatt = response.getJSONObject("point").getDouble("lat");
                    String dataName = response.getString("name");
                    String dataAddress = response.getJSONObject("address").getString("village") + ", " +
                            response.getJSONObject("address").getString("state");
                    String dataPlace = response.getJSONObject("wikipedia_extracts").getString("text");
                    String dataImage = response.getJSONObject("preview").getString("source");

                    // Formatting url karena gk sesuai
                    dataImage = dataImage.replaceFirst("thumb/", "");
                    int lastIndex = dataImage.indexOf(".jpg/") + 4;
                    dataImage = dataImage.substring(0, lastIndex);

//                    detailCategory.setText(dataCategory);
                    detailName.setText(dataName);
                    detailAddress.setText(dataAddress);
                    detailPlace.setText(dataPlace);
                    placeLatt = dataLatt;
                    placeLong = dataLon;
                    placeName = dataName;

                    Picasso.get().load(dataImage).into(detailImage);
                } catch (JSONException e) {
                    Toast.makeText(PlaceDetailActivity.this, "Something when wrong!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.getMessage());
            }
        });
        requestQueue.add(objectRequest);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_navigate_map) {
            Uri gmmURI = Uri.parse("geo:" + placeLatt + "," + placeLong + "?z=18&q=" + placeName);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmURI);
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "Can't run maps", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }
}