package com.example.hellowworld;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.CellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static android.Manifest.*;

public class MainActivity extends AppCompatActivity {

    private LocationManager locationManager;
    private LocationListener locationListener;
    private static final String FILENAME ="SJU_SIGNAL";
    private static final DateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    public Button btnShowLocation;
    public int cntGSMmain;
    public int cntGPSmain;
    private static final int WRITE_EXTERNAL_STORAGE_CODE =1;

    // GPSTracker class
    GPSTracker gps;

    @RequiresApi(api = VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cntGSMmain=0;
        cntGPSmain=0;
        setContentView(R.layout.activity_main);
        Global.getInstance();
        Global.txtSignalStr = (TextView) findViewById(R.id.tbKasia);
        Global.gsmParams.psListener = new MyPhoneStateListener();
        Global.gsmParams.telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        Global.gsmParams.telephonyManager.listen(Global.getInstance().gsmParams.psListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        btnShowLocation = (Button) findViewById(R.id.btnKasia3);


        // Show location button click event
        btnShowLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Create class object
                gps = new GPSTracker(MainActivity.this);

                // Check if GPS enabled
                if(gps.canGetLocation()) {

                    if (gps.getLongitude()!=0)
                    {
                        Global.gpsParams.longitude=gps.getLongitude();
                        Global.gpsParams.latitude=gps.getLatitude();
                        Global.gpsParams.cntGPS+=1;
                        // \n is for new line
                        //Toast.makeText(getApplicationContext(), "GPS Data Received", Toast.LENGTH_LONG).show();
                        TextView textView = (TextView)findViewById(R.id.tbKasia);
                        //textView.setText(resultText);
                        textView.append("longitude: "+Global.gpsParams.longitude+", latitude:"+Global.gpsParams.latitude+", "+Global.gpsParams.cntGPS+"\n");
                        textView.setMovementMethod(new ScrollingMovementMethod());
                    }


                } else {
                    // Can't get location.
                    // GPS or network is not enabled.
                    // Ask user to enable GPS/network in settings.
                    gps.showSettingsAlert();
                }
            }
        });
    }

    private void saveFile() {
    }


    public void scanWifi(View view)
    {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission.ACCESS_FINE_LOCATION }, 1);
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        List<ScanResult> wifiList = wifiManager.getScanResults();
        StringBuilder resultText = new StringBuilder();
        resultText.append("wifi list size: "+wifiList.size()+".\n");
        for (ScanResult scanResult : wifiList) {
            resultText.append("SSID: " + scanResult.SSID + ", " +
                    "BSSID: " + scanResult.BSSID + ", " +
                    "dBm: " + scanResult.level + "\n " );
        }

        TextView textView = (TextView)findViewById(R.id.tbKasia);
        //textView.setText(resultText);
        textView.append(resultText+"\n");
        textView.setMovementMethod(new ScrollingMovementMethod());
    }


    public void scanGPRS(View view)
    {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission.ACCESS_FINE_LOCATION }, 1);

        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
        List<Network> networkList = null;
        StringBuilder resultText = new StringBuilder();
        int sdk_int = android.os.Build.VERSION.SDK_INT;
        resultText.append("sdk version: "+sdk_int+"\n");
        if (sdk_int >= VERSION_CODES.LOLLIPOP) {
            networkList = Arrays.asList(connectivityManager.getAllNetworks());
        }else
        {
            resultText.append("sdk version not enought.\n");
            networkList = new ArrayList<>();
        }

        resultText.append("Mobile list size: "+networkList.size()+".\n");

        TextView textView = (TextView)findViewById(R.id.tbKasia);
        textView.setText(resultText);
        textView.setMovementMethod(new ScrollingMovementMethod());
    }


    public void scanGPS(View view)
    {
        if (cntGPSmain<Global.gpsParams.cntGPS)
        {
            TextView textView = (TextView)findViewById(R.id.tbKasia);
            textView.append(Global.gpsParams.longitude+", "+Global.gpsParams.latitude+", "+Global.gpsParams.cntGPS+"\n");
            textView.setMovementMethod(new ScrollingMovementMethod());
            cntGPSmain=Global.gpsParams.cntGPS;
        }

    }

    public void saveText(View view)
    {
        TextView textView = (TextView)findViewById(R.id.tbKasia);

        if (Build.VERSION.SDK_INT >= VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_DENIED)
            {
                String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permission, WRITE_EXTERNAL_STORAGE_CODE);
            }
            else
            {
                if (textView.getText()!=null)
                {

                    String dataToSave =textView.getText().toString();
                    //   try {
                    Date date = new Date();
                    String fullFilename=FILENAME+"_"+sdf.format(date)+".txt";



                    try {
                        File myFile = new File("/sdcard/"+fullFilename);
                        myFile.createNewFile();
                        FileOutputStream fOut = new FileOutputStream(myFile);
                        OutputStreamWriter myOutWriter =
                                new OutputStreamWriter(fOut);
                        myOutWriter.append(dataToSave);
                        myOutWriter.close();
                        fOut.close();
                        Toast.makeText(getBaseContext(),
                                "Done writing SD '"+fullFilename+",",
                                Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(getBaseContext(), e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }

                    /*
                    File path = Environment.getDataDirectory();
                    File dir = new File(path, "KASIA");
                    dir.mkdirs();
                    if (!dir.exists())
                        Toast.makeText(this, "nie ma folderu", Toast.LENGTH_LONG).show();
                    File file = new File(dir, fullFilename);
                    OutputStream out;
                    try
                    {
                        out = new FileOutputStream(file);
                        out.write(dataToSave.getBytes());
                        out.flush();
                        out.close();
                    }catch (Exception e)
                    {
                        Toast.makeText(this, "failed to save. "+e.getMessage(), Toast.LENGTH_LONG).show();
                        return;
                    }
                    Toast.makeText(this, "file saved: "+dir+fullFilename, Toast.LENGTH_LONG).show(); */
                }
            }
        }
    }

    public void clearText(View view)
    {
        TextView textView = (TextView)findViewById(R.id.tbKasia);
        textView.setText("");
    }

    public void scanGPRSSecond(View view)
    {
        if (cntGSMmain<Global.gsmParams.cntGSM)
        {
            TextView textView = (TextView)findViewById(R.id.tbKasia);
            textView.append(Global.gsmParams.signalStrength+"\n");
            textView.setMovementMethod(new ScrollingMovementMethod());
            cntGSMmain=Global.gsmParams.cntGSM;
        }
    }


    public void getWifiParameters(View view) {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();
        String macAdr = wifiInfo.getMacAddress();
        String bssid = wifiInfo.getBSSID();
        int rssi = wifiInfo.getRssi();
        int linkspeed = wifiInfo.getLinkSpeed();
        String ssid = wifiInfo.getSSID();
        int networkId = wifiInfo.getNetworkId();
        String ipAdres = Formatter.formatIpAddress(ip);
        String info = "IPAdrress: " + ipAdres + ", MacAddress: "+macAdr+", BSSID: "+bssid+", SSID: "+ssid+", NetworkID:"+networkId +", RSSI: "+rssi+", linekSpeed: "+linkspeed;
        TextView textView = (TextView)findViewById(R.id.tbKasia);
        textView.setText(info);
//        textView.setText(longString);
        textView.setMovementMethod(new ScrollingMovementMethod());
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case WRITE_EXTERNAL_STORAGE_CODE:{
                if (grantResults.length>0 && grantResults[0] ==
                PackageManager.PERMISSION_GRANTED)
                {

                }else
                {
                    Toast.makeText(this, "Permission enable", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}
