/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.wear.watchface

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.icu.util.Calendar
import android.opengl.GLES20
import android.opengl.GLUtils

/** Helper for rendering a complication to a GLES20 texture. */
class GlesTextureComplication(
    val renderer: CanvasComplicationRenderer,
    textureWidth: Int,
    textureHeight: Int,

    /** The texture type, e.g. GLES20.GL_TEXTURE_2D */
    private val textureType: Int
) {
    private val texture = createTexture(textureType)
    private val bitmap = Bitmap.createBitmap(
        textureWidth,
        textureHeight,
        Bitmap.Config.ARGB_8888
    )
    private val canvas = Canvas(bitmap)
    private val bounds = Rect(0, 0, textureWidth, textureHeight)

    /** Renders the complication to an OpenGL texture. */
    fun renderToTexture(calendar: Calendar, @DrawMode drawMode: Int) {
        canvas.drawColor(Color.BLACK)
        renderer.render(canvas, bounds, calendar, drawMode)
        bind()
        GLUtils.texImage2D(textureType, 0, bitmap, 0)
    }

    /** Bind the texture to the active texture target. */
    fun bind() {
        GLES20.glBindTexture(textureType, texture)
    }

    /**
     * Creates an OpenGL texture handler.
     *
     * @return The OpenGL texture handler
     */
    private fun createTexture(textureType: Int): Int {
        val handle = IntArray(1)
        GLES20.glGenTextures(1, handle, 0)
        GLES20.glBindTexture(textureType, handle[0])
        GLES20.glTexParameteri(
            textureType,
            GLES20.GL_TEXTURE_WRAP_S,
            GLES20.GL_CLAMP_TO_EDGE
        )
        GLES20.glTexParameteri(
            textureType,
            GLES20.GL_TEXTURE_WRAP_T,
            GLES20.GL_CLAMP_TO_EDGE
        )
        GLES20.glTexParameteri(
            textureType,
            GLES20.GL_TEXTURE_MAG_FILTER,
            GLES20.GL_LINEAR
        )
        GLES20.glTexParameteri(
            textureType,
            GLES20.GL_TEXTURE_MIN_FILTER,
            GLES20.GL_LINEAR
        )
        return handle[0]
    }
}