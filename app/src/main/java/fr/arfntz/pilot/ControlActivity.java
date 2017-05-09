package fr.arfntz.pilot;

import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import static java.lang.Math.abs;

public class ControlActivity extends AppCompatActivity {

    private ControlView mcontrolView;
    private String url_base = "http://192.168.1.67/arduino/digital/";
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        queue = Volley.newRequestQueue(this);
        getSupportActionBar().hide();

        Point p = new Point();
        getWindowManager().getDefaultDisplay().getSize(p);
        ControlView controlView = new ControlView(this,p);
        setContentView(controlView);
    }

    public void request(String command) {
        String _url = url_base+command;
        System.out.println(_url);
        final StringRequest stringRequest = new StringRequest(Request.Method.GET, _url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("Response:"+response);
                        // do nothing
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.print("Volley Error : "+error);
                        // do nothing
                    }
                }
        );
        queue.add(stringRequest);
    }

}