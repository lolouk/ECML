package com.ecml;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.util.Log;

import com.ecml.ECMLActivity;

/** Countdown of the current music activity
 *
 * Created by Jerome on 24/06/2015.
 */
public class Countdown extends CountDownTimer {

    private int number;
    private Context context;

    public Countdown(long millisInFuture, long countDownInterval, int number, Context context) {
        super(millisInFuture, countDownInterval);
        this.number = number;
        this.context = context;
    }

    @Override
    public void onTick(long l) {
        ActivityParameters a = ReadWriteXMLFile.readActivityByNumber(number, context);
        if (a != null) {
            a.setCountdown((int) l);
            ReadWriteXMLFile.edit(a, context);
            Long l1 = Long.valueOf(l);
            l1 = l1 / 1000;
            Log.d("Countdown", l1.toString());
        }
    }

    @Override
    public void onFinish() {
        Intent intent = new Intent(this.context, ECMLActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Log.d("Countdown:onFinish", "number");
        ActivityParameters a1 = ReadWriteXMLFile.readActivityByNumber(number,context);
        a1.finish();
        ReadWriteXMLFile.edit(a1, context);
        if (ReadWriteXMLFile.readActivityByNumber(number+1,context) != null) {
            Log.d("Countdown:onFinish","number+1");
            ActivityParameters a2 = ReadWriteXMLFile.readActivityByNumber(number+1,context);
            a2.setActive(true);
            ReadWriteXMLFile.edit(a2, context);
        }
        this.context.startActivity(intent);
    }
}
