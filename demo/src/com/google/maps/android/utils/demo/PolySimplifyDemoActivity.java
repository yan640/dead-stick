/*
 * Copyright 2015 Sean J. Barbeau
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.maps.android.utils.demo;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import android.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class PolySimplifyDemoActivity extends BaseDemoActivity {
    private TextView tt;
    private TextView at;
    private TextView gt;
    private Button bb;
    private Button bc;
    private final static int ALPHA_ADJUSTMENT = 0x77000000;
    private static double longt = 30.2356;
    private static double lat = 59.93;

     double alt = 0;
    private LocationManager locationManager;
    private LocationListener listener;
    private double movecam=0;
    private int numberdraw=0;
    private static float zoom=12;
    long time1, time2;
    int n = 12;
    int googleApi = 3;
    int work = 0;
    double[] φ2 =new double[n];
    double[] λ2 =new double[n];


    public void drawelipse(){
        final GoogleMap mMap = getMap();

        tt = (TextView) findViewById(R.id.amu_text);
        at = (TextView) findViewById(R.id.alt_text);
        bb = (Button) findViewById(R.id.button2);
        bc = (Button) findViewById(R.id.center);
        // Original line
//        List<LatLng> line = PolyUtil.decode(LINE);
//        mMap.addPolyline(new PolylineOptions()
//                .addAll(line)
//                .color(Color.BLACK));
        tt.append("\n " + longt +    lat );
        at.setText("  " + alt );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 10:
                configure_button();
                break;
            default:
                break;
        }
    }

    void configure_button() {
        final GoogleMap mMap = getMap();
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.INTERNET}
                        , 10);
            }
            return;
        }
        // this code won't execute IF permissions are not allowed, because in the line above there is return statement.
        bb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //noinspection MissingPermission

                locationManager.requestLocationUpdates("gps", 1000, 0, listener);



            }
        });
        bc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //noinspection MissingPermission
                // getMap().getZoom();
                zoom = mMap.getCameraPosition().zoom;
                if (movecam == 0) {
                    getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, longt), 12));
                    movecam = 1;
                } else {
                    getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, longt), zoom));
                }
            }
        });


    }

    public class Elevation implements Serializable {
        private BigDecimal elevation;
        private Location location;
        private BigDecimal resolution;

        public BigDecimal getElevation() {
            return elevation;
        }

        public Location getLocation() {
            return location;
        }

        public BigDecimal getResolution() {
            return resolution;
        }
    }

    public class ElevationResult implements Serializable {
        private PolySimplifyDemoActivity.Elevation results[];
        private String status;

        public PolySimplifyDemoActivity.Elevation[] getResults() {
            return results;
        }

        public String getStatus() {
            return status;
        }
    }
    String mJsonOutput;
    private  String getElevationInfoRequest() {
        String search = "";
        String loc ="";
        try {

            for (int i = 0; i <= 11; i++) {
                if (i<11) {
                        loc =loc +φ2[i] + "," + λ2[i] + "|";
                    }
                else {
                    loc =loc +φ2[i] + "," + λ2[i] ;
                }

            }

//            int k = 15;
//            for (int i = 0; i <= 18; i++) {
//                if (i<18) {
//                    if (i==0 || i==3|| i==6|| i==9|| i==12|| i==15  ) {
//
//                        loc =loc +lat   + "," + longt + "|";
//                        //locc=locc+"\n " +"№"+i+ ": "+lat   + "," + longt  ;
//
//                    }
//                    else {
//                        for (int j = 0; j <= 1; j++) {
//                            loc = loc + φ2[k] + "," + λ2[k] + "|";
//                            //locc=locc+"\n " +"№"+i+ ": "+φ2[k] + "," + λ2[k];
//                            k++;
//                            i++;
//                        }
//                    }
//                }
//                else {
//                    loc =loc +lat   + "," + longt ;
//                    //locc=locc+"\n " +"№"+i+ ": "+lat   + "," + longt  ;
//                }
//
//            }
            search = "https://maps.googleapis.com/maps/api/elevation"
                    + "/json"
                    + "?path="+loc
                    +"&samples=505"
                    + "&key=" + "AIzaSyAe9fHyv7bi9wOxiJeMDs3WuMESEtfAEY0";

            //locc = loc;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return search;
    }
    String locc ="";
    //public void getElevationInfo(final double latz,final double longtz) {
    public void getElevationInfo() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {


                    try {
                        if (googleApi>0) {
                            googleApi--;
                            String search = getElevationInfoRequest();

                        mJsonOutput = doGet(new URL(search));
                          locc = mJsonOutput;
                        }
                    } catch (Exception e) {
                        //Log.e( "Exception: ", e);
                    }
                    return mJsonOutput;

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    ElevationResult elevation = new Gson().fromJson(mJsonOutput, ElevationResult.class);

                    if (elevation.getStatus().equalsIgnoreCase("OK"))
                        onElevationSuccess(elevation);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    // Callbacks to calling Activity/Fragment


    public void onElevationSuccess(ElevationResult elevation){
        gt = (TextView) findViewById(R.id.ground_alt);
        try {
            String loc ="";
            for (int i = 0; i <= 11; i++) {

                loc = loc+ " " + elevation.getResults()[i]
                        .getElevation()
                        .setScale(2, BigDecimal.ROUND_HALF_UP);
            }
            //tt.append(locc);
            gt.append(locc);
            time2 = System.currentTimeMillis()-time1;

            gt.append("ping:"+time2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private String doGet(URL url) {
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();

        try {
            Log.d("Elevation", "Request URL: " + url.toString());

            conn = (HttpURLConnection) url.openConnection();

            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e("Elevation", "Error processing Places API URL", e);
        } catch (IOException e) {
            Log.e("Elevation", "Error connecting to Places API", e);
        } catch (Exception e) {
            Log.e("Elevation", "Exception: ", e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return jsonResults.toString();
    }





    @Override
    protected void startDemo() {
        final GoogleMap mMap = getMap();

        tt = (TextView) findViewById(R.id.amu_text);
        at = (TextView) findViewById(R.id.alt_text);
        bb = (Button) findViewById(R.id.button2);
        bc = (Button) findViewById(R.id.center);
        // Original line
//        List<LatLng> line = PolyUtil.decode(LINE);
//        mMap.addPolyline(new PolylineOptions()
//                .addAll(line)
//                .color(Color.BLACK));
        tt.append("\n " + longt +    lat );
        //at.setText("  " + alt );
       // getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(28.05870, -82.4090), 15));


        //List<LatLng> simplifiedLine;

        //List<LatLng> oval = PolyUtil.decode(OVAL_POLYGON);
//        mMap.addPolygon(new PolygonOptions()
//                .addAll(oval)
//                .fillColor(Color.BLUE - ALPHA_ADJUSTMENT)
//                .strokeColor(Color.BLUE)
//                .strokeWidth(5));



        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


        listener = new LocationListener() {
            private  final String TAG = BaseDemoActivity.class.getName();

            @Override
            public void onLocationChanged(Location location) {


                longt = location.getLongitude();
               lat = location.getLatitude();//lat = location.getLatitude()-0.4;

                alt = 300 + location.getAltitude();
                tt.setText( location.getLongitude() + "  " + location.getLatitude());
               // at.setText(" alt " + alt );


//
                double Rad = 6371e3;
                double d = alt*8;//2806;//
                double ra = Math.PI/180;
                double dec = 180 / Math.PI;
                double brng = 30*ra;
                double φ1 = lat*ra;
                double λ1 = longt*ra;
                long time3;
//                double φ1 = 30.2852*ra;
//                double λ1 = 60.013*ra;

//                int n = 12;
//                double[] φ2 =new double[n];
//                double[] λ2 =new double[n];

                for (int i = 0; i <= 11; i++) {
                    double φ22 =(Math.asin( Math.sin(φ1)*Math.cos(d/Rad) + Math.cos(φ1)*Math.sin(d/Rad)*Math.cos(brng*i) ));
                    φ2[i] = φ22*dec;
                      λ2[i] = (λ1 + Math.atan2(Math.sin(brng*i)*Math.sin(d/Rad)*Math.cos(φ1), Math.cos(d/Rad)-Math.sin(φ1)*Math.sin(φ22)))*dec;
                }

                time3=time1;
                time1 = System.currentTimeMillis();
 //               getElevationInfo(lat, longt);
                getElevationInfo();








                numberdraw++ ;
                tt.append("\n " +"numberdraw=" + numberdraw+ " time: "+(time3-time1) );
                tt.append("\n " +"d=" + d );
                tt.append("\n " +"lat=" + lat);
                tt.append("\n " +"longt=" + longt );


//                for (int i = 0; i <= 11; i++) {
//                    tt.append("\n " +"φ[" + i+ "]=" + φ2[i] );
//                    tt.append("\n " +"λ[" + i+ "]=" + λ2[i] );
//                      }


                PolygonOptions rectOptions = new PolygonOptions()
                        .add(new LatLng(φ2[0], λ2[0]),
                                new LatLng(φ2[1], λ2[1]),
                                new LatLng(φ2[2], λ2[2]),
                                new LatLng(φ2[3], λ2[3]),
                                new LatLng(φ2[4], λ2[4]),
                                new LatLng(φ2[5], λ2[5]),
                                new LatLng(φ2[6], λ2[6]),
                                new LatLng(φ2[7], λ2[7]),
                                new LatLng(φ2[8], λ2[8]),
                                new LatLng(φ2[9], λ2[9]),
                                new LatLng(φ2[10], λ2[10]),
                                new LatLng(φ2[11], λ2[11]),
                                 new LatLng(φ2[0], λ2[0]))
                        .fillColor(Color.BLUE - ALPHA_ADJUSTMENT)
                        .strokeColor(Color.BLUE)
                        .strokeWidth(5);

                mMap.clear() ;
// Get back the mutable Polygon
                mMap.addPolygon(rectOptions);



            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };

        configure_button();

    }





}