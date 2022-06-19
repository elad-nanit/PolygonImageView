package com.wwdablu.soumya.extimageview.nanit

import android.graphics.PointF
import android.view.View


internal fun validateInArea(
    motionRoi: View,
    topLeft: PointF,
    topRight: PointF,
    bottomRight: PointF,
    bottomLeft: PointF,
    dx: Float,
    dy: Float
): Boolean {


    var result = false


    topLeft.x.plus(dx)
    topLeft.y.plus(dy)

    topRight.x.plus(dx)
    topRight.y.plus(dy)

    bottomRight.x.plus(dx)
    bottomRight.y.plus(dy)

    bottomLeft.x.plus(dx)
    bottomLeft.y.plus(dy)


    // stay within crib limits
    val maxWidth: Double = (motionRoi.width - MotionRoiWidget.ROI_MARGINS).toDouble()
    val maxHeight: Double = (motionRoi.height - MotionRoiWidget.ROI_MARGINS).toDouble()

//    if (startX >= MotionRoiWidget.ROI_MARGINS
//        && startY >= MotionRoiWidget.ROI_MARGINS
//        && endX <= maxWidth
//        && endY <= maxHeight
//        && endX - startX >= 0.1 * maxWidth
//        && endY - startY >= 0.1 * maxHeight
//    ) {
//        result = true
//    }

    return result

}