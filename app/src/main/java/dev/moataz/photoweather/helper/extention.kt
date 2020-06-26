package dev.moataz.photoweather.helper

import android.content.Context
import android.graphics.Point
import android.view.WindowManager

var screenSize: Point? = null
fun getScreenSize(ctx: Context): Point? {

    if (screenSize == null) {
        screenSize = Point()
        val windowManager = ctx.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getSize(screenSize)
    }

    return screenSize


}