package com.example.hellowworld;

import android.telephony.TelephonyManager;

public class GSMparams {
    public static String signalStrength;;
    public static int cntGSM;
    public static TelephonyManager telephonyManager;
    public static MyPhoneStateListener psListener;
    public GSMparams()
    {
        signalStrength="";
        cntGSM=0;
    }
}
