package ask.me.directions;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.SystemClock;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.graphics.Matrix;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import ask.me.directions.DirComputation;


public class MainActivity extends AppCompatActivity {

    ImageView compass;
    Button update;

    double lat, lng, inclination;
    int time;//time of the day in hours

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        compass = (ImageView) findViewById(R.id.compass);
        update = (Button) findViewById(R.id.update);

        //default lat, lng if no network
        lat = 19;
        lng = 72;
        time = getCurrentHour();
        inclination = 23.5;

        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        LocationListener mlocListener = new MyLocationListener();


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, mlocListener);
        }

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                time = getCurrentHour();
                double rotation = DirComputation.getDirection(inclination,time,lat);
                compass.setRotation((float)rotation);

                Toast.makeText(getApplicationContext(),
                        "rotation: "+rotation+"\ntime: "+time+"\nlat: "+lat,
                        Toast.LENGTH_SHORT).show();
            }
        });


    }

    private int getCurrentHour(){
        return (Calendar.getInstance()).get(Calendar.HOUR_OF_DAY);
    }

    private Location getLocation(){
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = null;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            location= mlocManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        return location;
    }

    public class MyLocationListener implements LocationListener

    {

        @Override

        public void onLocationChanged(Location loc)

        {
            lat = loc.getLatitude();

            lng = loc.getLongitude();
        }


        @Override

        public void onProviderDisabled(String provider)

        {

            Toast.makeText( getApplicationContext(),"GPS disabled",Toast.LENGTH_SHORT ).show();

        }


        @Override

        public void onProviderEnabled(String provider)

        {

            Toast.makeText( getApplicationContext(),"GPS enabled",Toast.LENGTH_SHORT).show();

        }


        @Override

        public void onStatusChanged(String provider, int status, Bundle extras)

        {


        }

    }/* End of Class MyLocationListener */
}
