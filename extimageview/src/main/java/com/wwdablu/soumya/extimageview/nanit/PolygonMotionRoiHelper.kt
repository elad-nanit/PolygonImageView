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
    selectedHandle: PolygonMotionRoiWidget.Handle,
    topLeft: PointF,
    topRight: PointF,
    bottomLeft: PointF,
    bottomRight: PointF,
    dx: Float,
    dy: Float
) {

    val maxWidth = motionRoi.width - PolygonMotionRoiWidget.ROI_MARGINS
    val maxHeight = motionRoi.height - PolygonMotionRoiWidget.ROI_MARGINS

    when (selectedHandle) {
        PolygonMotionRoiWidget.Handle.TopLeft -> {
            topLeft.coerce(dx, dy, maxWidth, maxHeight)
        }
        PolygonMotionRoiWidget.Handle.TopRight -> {
            topRight.coerce(dx, dy, maxWidth, maxHeight)
        }
        PolygonMotionRoiWidget.Handle.BottomLeft -> {
            bottomLeft.coerce(dx, dy, maxWidth, maxHeight)
        }
        PolygonMotionRoiWidget.Handle.BottomRight -> {
            bottomRight.coerce(dx, dy, maxWidth, maxHeight)
        }
        PolygonMotionRoiWidget.Handle.InsideArea -> {
            val points = listOf(topLeft, topRight, bottomLeft, bottomRight)
            //by this check, we avoid situation that points are updated partially on being moved toward edges
            if (points.all {
                    (it.x + dx) in PolygonMotionRoiWidget.ROI_MARGINS..maxWidth
                            && (it.y + dy) in PolygonMotionRoiWidget.ROI_MARGINS..maxHeight
                }) {
                topLeft.coerce(dx, dy, maxWidth, maxHeight)
                topRight.coerce(dx, dy, maxWidth, maxHeight)
                bottomLeft.coerce(dx, dy, maxWidth, maxHeight)
                bottomRight.coerce(dx, dy, maxWidth, maxHeight)
            }
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
    coerceAtLeast(PolygonMotionRoiWidget.ROI_MARGINS).coerceAtMost(size)
