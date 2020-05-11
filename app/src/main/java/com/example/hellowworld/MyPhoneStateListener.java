package com.example.hellowworld;

import android.os.Build;
import android.telephony.CellSignalStrength;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;

import androidx.annotation.RequiresApi;

import java.util.List;

public class MyPhoneStateListener extends PhoneStateListener {
    public int signalStrengthValue;


    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
        super.onSignalStrengthsChanged(signalStrength);
        if (signalStrength.isGsm()) {
            if (signalStrength.getGsmSignalStrength() != 99)
                signalStrengthValue = signalStrength.getGsmSignalStrength() * 2 - 113;
            else
                signalStrengthValue = signalStrength.getGsmSignalStrength();
        } else {
            signalStrengthValue = signalStrength.getCdmaDbm();
            //
            //List<CellSignalStrength> cellSignalStrengths = signalStrength.getCellSignalStrengths();


        }

        //Global.txtSignalStr.setText("Signal Strength : " + signalStrengthValue);
        Global.gsmParams.cntGSM+=1;
        Global.gsmParams.signalStrength = "Signal Strength : " + signalStrengthValue+", cnt: "+Global.gsmParams.cntGSM;

    }
}