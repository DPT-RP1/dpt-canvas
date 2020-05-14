package com.sony.dpt.utils;

import android.content.Context;
import android.os.PowerManager;

import static android.content.Context.POWER_SERVICE;

public class WakelockUtils {

    private final PowerManager powerManager;
    private final PowerManager.WakeLock wakeLock;

    public WakelockUtils(Context context) {
        powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(
                PowerManager.FULL_WAKE_LOCK,
                "DTP::WakelockTag"
        );
    }

    public void acquire() {
        wakeLock.acquire(10 * 60 * 1000L /*10 minutes*/);
    }

    public void release() {
        wakeLock.release();
    }

}
