package id.web.codeplace.lemburwalagri;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mapbox.mapboxsdk.Mapbox;


public class hospital_search extends AppCompatActivity {


    /*
    ---------------var global implementation-----------------------
     */
    private static final String TAG = "MainActivity";

    private static final int ERROR_DIALOG_REQUEST = 9001;
/*
----------------------------------------------------------------
 */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_search);
        Mapbox.getInstance(this,getString(R.string.acces_token));
        Intent intent = new Intent(hospital_search.this, MapsActivity.class);
        startActivity(intent);
    }




    @Override
    public void onBackPressed() {
        Intent intent= new Intent(hospital_search.this,MainActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }




}
