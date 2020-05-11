package com.example.hellowworld;

import android.service.autofill.FillEventHistory;
import android.telephony.TelephonyManager;
import android.widget.TextView;

public class Global {
    private static Global global;
    public static GPSparams gpsParams;
    public static GSMparams gsmParams;
    private Global()
    {

        gpsParams= new GPSparams();
        gsmParams= new GSMparams();

    }
    public static Global getInstance()
    {
        if(global == null)
        {
            global = new Global();
        }
        return global;
    }

    public static TextView txtSignalStr;
}
