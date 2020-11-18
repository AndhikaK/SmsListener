package com.example.explore;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import static com.example.explore.PlaceListActivity.EXTRA_XID;

public class PlaceDetailActivity extends AppCompatActivity {

    //    private String xid = "";
//    private String JSON_URL = "https://api.opentripmap.com/0.1/en/places/xid/N2180835380?apikey=5ae2e3f221c38a28845f05b65ba166329393551235361ab9b66e2889";
//    private String dataName, dataCategory, dataAddress, dataDetail;

    TextView detailCategory, detailName, detailAddress, detailPlace;
    ImageView detailImage;
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

        Intent intent = getIntent();
        String xid = intent.getStringExtra(EXTRA_XID);
        String JSON_URL = "https://api.opentripmap.com/0.1/en/places/xid/" + xid + "?apikey=5ae2e3f221c38a28845f05b65ba166329393551235361ab9b66e2889";

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, JSON_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
//                    JSONObject obj = response.getJSONObject();
                    String dataName = response.getString("name");
//                    String dataCategory = response.getString("kinds");
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

                    Picasso.get().load(dataImage).into(detailImage);
                } catch (JSONException e) {
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
}