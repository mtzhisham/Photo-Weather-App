package dev.moataz.photoweather.helper

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.media.Image
import android.view.View
import java.nio.ByteBuffer


fun Image.toBitmap(): Bitmap {
    val buffer: ByteBuffer = this.planes[0].buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)

    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}

/*
* For simplicity draw the entire rendered view.
* A more sophisticated may is to use the graphics API and draw each component of the photo separate
* on Canvas, but his requires too many matrix operation to handel
* */
fun View.getBitmap(): Bitmap? {
    var bitmap =
        Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
    var canvas = Canvas(bitmap)
    this.draw(canvas)
    return bitmap
}