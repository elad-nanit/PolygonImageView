package com.wwdablu.soumya.extimageview.nanit;

import java.io.Serializable;

public class MotionRoiCoords implements Serializable {

    public static final MotionRoiCoords DEFAULT = new MotionRoiCoords();

    public double x0 = 0;
    public double y0 = 0;
    public double x1 = 1;
    public double y1 = 1;

    public MotionRoiCoords() {}

    public MotionRoiCoords(double x0, double y0, double x1, double y1)
    {
        this.x0 = x0;
        this.x1 = x1;
        this.y0 = y0;
        this.y1 = y1;
    }

    public void copy(MotionRoiCoords other) {
        if (other != null) {
            x0 = other.x0;
            y0 = other.y0;
            x1 = other.x1;
            y1 = other.y1;
        }
    }

    boolean isValidMotionRoi() {
        return 0 <= x0 && x0 <= 1 && x0 < x1 && x1 <= 1 &&
                0 <= y0 && y0 <= 1 && y0 < y1 && y1 <= 1;
    }

    public boolean isDefault()
    {
        if (x0 == 0 && x1 == 1 && y0 == 0 && y1 == 1)
        {
            return true;
        }

        return false;
    }
}
