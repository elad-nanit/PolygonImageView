package com.wwdablu.soumya.extimageview.nanit

import android.graphics.Path
import android.graphics.PointF
import android.view.View

internal fun getPolygonPath(topLeft: PointF, topRight: PointF, bottomRight: PointF, bottomLeft: PointF): Path =
    Path().apply {
        moveTo(topLeft.x, topLeft.y)
        lineTo(topRight.x, topRight.y)
        lineTo(bottomRight.x, bottomRight.y)
        lineTo(bottomLeft.x, bottomLeft.y)
        close()
    }

internal fun updateRoiCoordinates(
    motionRoi: View,
    selectedHandle: MotionRoiWidget.Handle,
    topLeft: PointF,
    topRight: PointF,
    bottomLeft: PointF,
    bottomRight: PointF,
    dx: Float,
    dy: Float
) {

    val maxWidth = motionRoi.width - MotionRoiWidget.ROI_MARGINS
    val maxHeight = motionRoi.height - MotionRoiWidget.ROI_MARGINS

    when (selectedHandle) {
        MotionRoiWidget.Handle.TopLeft -> {
            topLeft.coerce(dx, dy, maxWidth, maxHeight)
        }
        MotionRoiWidget.Handle.TopRight -> {
            topRight.coerce(dx, dy, maxWidth, maxHeight)
        }
        MotionRoiWidget.Handle.BottomLeft -> {
            bottomLeft.coerce(dx, dy, maxWidth, maxHeight)
        }
        MotionRoiWidget.Handle.BottomRight -> {
            bottomRight.coerce(dx, dy, maxWidth, maxHeight)
        }
        MotionRoiWidget.Handle.InsideArea -> {
            topLeft.coerce(dx, dy, maxWidth, maxHeight)
            topRight.coerce(dx, dy, maxWidth, maxHeight)
            bottomLeft.coerce(dx, dy, maxWidth, maxHeight)
            bottomRight.coerce(dx, dy, maxWidth, maxHeight)
        }
    }
    motionRoi.invalidate()
}

private fun PointF.coerce(dx: Float, dy: Float, width: Float, height: Float) {
    set(
        (x + dx).coerced(width),
        (y + dy).coerced(height),
    )
}

private fun Float.coerced(size: Float): Float =
    coerceAtLeast(MotionRoiWidget.ROI_MARGINS).coerceAtMost(size)
