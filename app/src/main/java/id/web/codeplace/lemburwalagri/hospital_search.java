package id.web.codeplace.lemburwalagri;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

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
        if (isServicesOK()) {
            init();
            finish();
        }

    }



    @Override
    public void onBackPressed() {
        Intent intent= new Intent(hospital_search.this,MainActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }

    private void init(){
        Intent intent = new Intent(hospital_search.this, MapsActivity.class);
        startActivity(intent);
    }

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(hospital_search.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(hospital_search.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }


}
