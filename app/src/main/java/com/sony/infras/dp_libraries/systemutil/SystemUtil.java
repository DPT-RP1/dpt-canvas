package com.sony.infras.dp_libraries.systemutil;

import android.graphics.Rect;

/**
 * Native map of the internal /system/lib/libSystemUtil.so library
 * It sadly has to be in the same package/namespace as Sony's own.
 */
public class SystemUtil {

    private static SystemUtil.EpdUtil epdUtil;
    private static final SystemUtil systemUtil;

    private static boolean emulatorMode = false;

    static {
        try {
            System.loadLibrary("SystemUtil");
        } catch (Throwable ignored) {
            // We will run in emulator mode
            emulatorMode = true;
            System.err.println("An attempt to load the Sony /system/lib/libSystemUtil.so failed on this device, we will load the emulator mode");
        }
        systemUtil = new SystemUtil();
    }

    private SystemUtil() {
    }

    public native int getScreenShot(byte[] content);

    public native int nativeAddDhwArea(int left, int top, int right, int bottom, int penWidth, boolean portraitOrientation);

    public native int nativeChangeDhwStrokeWidth(int widthParam1, int widthParam2);

    public native boolean nativeGetDhwState();

    public native int nativeRemoveDhwArea(int index);

    public native void nativeSetDhwState(boolean enabled);

    public native int nativeWriteWaveform(byte[] waveform);

    public native int setShutdownScreenFlag(boolean enabled);

    public native int setShutdownScreenImage(byte[] image);

    public native int setStandbyScreenImage(byte[] image);

    // Quirky Sony plumbing to get through the policy

    private SystemUtil.EpdUtil getEpdUtil() {
        if (epdUtil != null) return epdUtil;

        if (emulatorMode) {
            epdUtil = new SystemUtil.EmulatedEpdUtil();
        } else {
            epdUtil = new SystemUtil.EpdUtil();
        }
        return epdUtil;
    }

    public static SystemUtil.EpdUtil getEpdUtilInstance() {
        return systemUtil.getEpdUtil();
    }

    public class EpdUtil {
        public EpdUtil() {
        }

        public int addDhwArea(Rect area, int penWidth, int orientation) {
            SystemUtil systemUtil = SystemUtil.this;
            return systemUtil.nativeAddDhwArea(area.left, area.top, area.right, area.bottom, penWidth, orientation == 0);
        }

        public void setDhwState(boolean enabled) {
            SystemUtil.this.nativeSetDhwState(enabled);
        }

        public int changeDhwStrokeWidth(int width, int unknown) {
            if (width < 0) {
                width = -1;
            } else {
                width = SystemUtil.this.nativeChangeDhwStrokeWidth(width, unknown);
            }

            return width;
        }

        public void removeAllDhwArea() {
            SystemUtil.this.nativeRemoveDhwArea(-1);
        }

        public int removeDhwArea(int areaCode) {
            if (areaCode < 0) {
                areaCode = -1;
            }
            return SystemUtil.this.nativeRemoveDhwArea(areaCode);
        }
    }

    public class EmulatedEpdUtil extends EpdUtil {
        public EmulatedEpdUtil() {
        }

        public int addDhwArea(Rect area, int penWidth, int orientation) {
            System.err.println("A Direct Handwritting Area was created, but not native call available in this device. We will do nothing.");
            return 0;
        }

        public void setDhwState(boolean enabled) {
            System.err.println("A Direct Handwritting Area was activated/disabled, but not native call available in this device. We will do nothing.");
        }

        public int changeDhwStrokeWidth(int width, int unknown) {
            System.err.println("A Direct Handwritting Stroke width was changed, but not native call available in this device. We will do nothing");
            return 0;
        }

        public void removeAllDhwArea() {
            System.err.println("All direct handwriting areas are being removed, but not native call available on this device. We will do nothing.");
        }

        public int removeDhwArea(int nativeRemoveDhwArea) {
            System.err.println("A direct handwriting areas is being removed, but not native call available on this device. We will do nothing.");

            return 0;
        }
    }

}
