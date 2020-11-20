package com.example.explore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PlaceListActivity extends AppCompatActivity implements PlaceAdapter.OnItemClickListener {
    public static final String EXTRA_XID = "xid";

    private RecyclerView mRecyclerView;
    private PlaceAdapter mPlaceAdapter;
    private ArrayList<PlaceItem> mPlaceList;
    private RequestQueue mRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_list);

        mRecyclerView = findViewById(R.id.recycle_view_place_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mPlaceList = new ArrayList<>();

        mRequestQueue = Volley.newRequestQueue(this);
        parseJSON();
    }

    private void parseJSON() {
//        String jsonURL = "https://api.opentripmap.com/0.1/en/places/bbox?lon_min=103.581796&lon_max=106.066163&lat_min=-6.006062&lat_max=-3.932237&src_attr=wikidata&kinds=natural%2Ccultural%2Csport&format=json&limit=100&apikey=5ae2e3f221c38a28845f05b65ba166329393551235361ab9b66e2889";
        String jsonURL = "https://api.opentripmap.com/0.1/en/places/bbox?lon_min=103.581796&lon_max=106.066163&lat_min=-6.006062&lat_max=-3.932237&src_attr=wikidata&kinds=natural%2Ccultural%2Csport%2Chistoric%2Cother%2Cmosques&format=json&limit=200&apikey=5ae2e3f221c38a28845f05b65ba166329393551235361ab9b66e2889";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, jsonURL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject place = response.getJSONObject(i);

//                                parseSecondJSON(place.getString("xid"));
                                String xid = place.getString("xid");
                                String placeName = place.getString("name");
                                int placeRating = place.getInt("rate");
//                                String imageURL = "https://upload.wikimedia.org/wikipedia/commons/f/fe/Stasiun_Waytuba_08-2015.jpg";

                                mPlaceList.add(new PlaceItem(xid, placeName, placeRating));
                            }

                            mPlaceAdapter = new PlaceAdapter(PlaceListActivity.this, mPlaceList);
                            mRecyclerView.setAdapter(mPlaceAdapter);
                            mPlaceAdapter.setOnItemClickListener(PlaceListActivity.this);
                        } catch (JSONException e) {

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mRequestQueue.add(request);
    }

    public void parseSecondJSON(String xid) {
        String url = "https://api.opentripmap.com/0.1/en/places/xid/" + xid + "?apikey=5ae2e3f221c38a28845f05b65ba166329393551235361ab9b66e2889";

        JsonObjectRequest seconRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String xid = response.getString("xid");
                            String placeName = response.getString("name");
                            int placeRating = response.getInt("rate");
                            String imageURL = response.getJSONObject("preview").getString("source");

                            mPlaceList.add(new PlaceItem(xid, imageURL, placeName, placeRating));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mRequestQueue.add(seconRequest);
    }

    @Override
    public void onItemClick(int position) {
        Intent detailIntent = new Intent(this, PlaceDetailActivity.class);
        PlaceItem clickedItem = mPlaceList.get(position);

        detailIntent.putExtra(EXTRA_XID, clickedItem.getmXid());

        startActivity(detailIntent);
    }
}