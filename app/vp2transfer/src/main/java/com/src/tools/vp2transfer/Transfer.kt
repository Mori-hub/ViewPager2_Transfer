package com.src.tools.vp2transfer

import android.graphics.Camera
import android.graphics.Matrix
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs


class Transfer : ViewPager2.PageTransformer {

    override fun transformPage(view: View, position: Float) {}

    class DepthPage : ViewPager2.PageTransformer {
        private val minScale = 0.75f
        override fun transformPage(page: View, position: Float) {
            val pageWidth = page.width

            when {
                position < -1 -> { // [ -Infinity,-1 )
                    // This page is way off-screen to the left.
                    page.alpha = 0f
                }
                position <= 0 -> { // [-1,0]
                    // Use the default slide transition when moving to the left page
                    page.alpha = 1f
                    page.translationX = 0f
                    page.scaleX = 1f
                    page.scaleY = 1f
                }
                position <= 1 -> { // (0,1]
                    // Fade the page out.
                    page.alpha = 1 - position

                    // Counteract the default slide transition
                    page.translationX = pageWidth * -position

                    // Scale the page down ( between MIN SCALE and 1 )
                    val scaleFactor: Float = (minScale
                            + (1 - minScale) * (1 - abs(position)))
                    page.scaleX = scaleFactor
                    page.scaleY = scaleFactor
                }
                else -> { // ( 1, +Infinity ]
                    // This page is way off-screen to the right.
                    page.alpha = 0f
                }
            }
        }
    }

    class ZoomOut : ViewPager2.PageTransformer {
        private val minScale = 0.85f
        override fun transformPage(view: View, position: Float) {
            val pageWidth: Int = view.width

            when {
                position < -1 -> { // [-Infinity,-1)
                    // This page is way off-screen to the left.
                    view.alpha = 0f
                }
                position <= 0 -> { // [-1,0]
                    // Use the default slide transition when moving to the left page
                    view.alpha = 1f
                    view.translationX = 0f
                    view.scaleX = 1f
                    view.scaleY = 1f
                }
                position <= 1 -> { // (0,1]
                    // Fade the page out.
                    view.alpha = 1 - position

                    // Counteract the default slide transition
                    view.translationX = pageWidth * -position

                    // Scale the page down (between MIN SCALE and 1)
                    val scaleFactor = (minScale
                            + (1 - minScale) * (1 - abs(position)))
                    view.scaleX = scaleFactor
                    view.scaleY = scaleFactor
                }
                else -> { // (1,+Infinity]
                    // This page is way off-screen to the right.
                    view.alpha = 0f
                }
            }
        }
    }

    class ZoomIn : ViewPager2.PageTransformer {
        override fun transformPage(page: View, pos: Float) {
            val scale: Float = if (pos < 0) pos + 1f else abs(1f - pos)
            page.scaleX = scale
            page.scaleY = scale
            page.pivotX = page.width * 0.5f
            page.pivotY = page.height * 0.5f
            page.alpha = if (pos < -1f || pos > 1f) 0f else 1f - (scale - 1f)
        }
    }

    class CubeIn : ViewPager2.PageTransformer {
        override fun transformPage(page: View, position: Float) {
            page.pivotX = (if (position > 0) 0 else page.width).toFloat()
            page.pivotY = 0f
            page.rotationY = -90f * position
        }
    }

    class CubeOut : ViewPager2.PageTransformer {
        override fun transformPage(page: View, pos: Float) {
            val v = if (pos < 0) page.width else 0
            page.pivotX = v.toFloat()
            page.pivotY = page.height * 0.5f
            page.rotationY = 90f * pos
        }
    }

    class FlipHorizontal : ViewPager2.PageTransformer {
        override fun transformPage(page: View, position: Float) {
            val rotation: Float = 180f * position
            val v = if (rotation > 90f || rotation < -90f) 0 else 1
            page.alpha = v.toFloat()
            page.pivotX = page.width * 0.5f
            page.pivotY = page.height * 0.5f
            page.rotationY = rotation
        }
    }

    class FlipVertical : ViewPager2.PageTransformer {
        override fun transformPage(page: View, position: Float) {
            val rotation: Float = -180f * position

            page.alpha = if (rotation > 90f || rotation < -90f) 0f else 1f
            page.pivotX = page.width * 0.5f
            page.pivotY = page.height * 0.5f
            page.rotationX = rotation
        }
    }

    class ForegroundTo : ViewPager2.PageTransformer {
        override fun transformPage(page: View, position: Float) {
            val height = page.height.toFloat()
            val width = page.width.toFloat()
            val scale = kotlin.math.min(if (position > 0) 1f else abs(1f + position), 1f)

            page.scaleX = scale
            page.scaleY = scale
            page.pivotX = width * 0.5f
            page.pivotY = height * 0.5f
            page.translationX = if (position > 0) width * position else -width * position * 0.25f
        }
    }

    class BackgroundTo : ViewPager2.PageTransformer {
        override fun transformPage(page: View, position: Float) {
            val height = page.height.toFloat()
            val width = page.width.toFloat()
            val scale: Float =
                kotlin.math.min(if (position < 0) 1f else abs(1f - position), 1f)

            page.scaleX = scale
            page.scaleY = scale
            page.pivotX = width * 0.5f
            page.pivotY = height * 0.5f
            page.translationX = if (position < 0) width * position else -width * position * 0.25f
        }
    }

    class RotateUp : ViewPager2.PageTransformer {
        private val rotationScale = -15f
        override fun transformPage(page: View, position: Float) {
            val width = page.width
            val height = page.height
            val rotation = rotationScale * position * -1.25f

            page.pivotX = width * 0.5f
            page.pivotY = height.toFloat()
            page.rotation = rotation
        }
    }

    class RotateDown : ViewPager2.PageTransformer {
        private val rotationScale = -15f
        override fun transformPage(page: View, position: Float) {
            val width = page.width
            val rotation = rotationScale * position

            page.pivotX = width * 0.5f
            page.pivotY = 0f
            page.translationX = 0f
            page.rotation = rotation
        }
    }

    class TabletPage : ViewPager2.PageTransformer {
        private val matrix: Matrix = Matrix()
        private val camera: Camera = Camera()
        private val tempFloat = FloatArray(2)

        override fun transformPage(page: View, position: Float) {
            val rotation: Float = (if (position < 0) 30f else -30f) * abs(position)
            page.translationX = getOffsetX(rotation, page.width, page.height)
            page.pivotX = page.width * 0.5f
            page.pivotY = 0f
            page.rotationY = rotation
        }

        private fun getOffsetX(rotation: Float, width: Int, height: Int): Float {
            matrix.reset()
            camera.save()
            camera.rotateY(abs(rotation))
            camera.getMatrix(matrix)
            camera.restore()
            matrix.preTranslate(-width * 0.5f, -height * 0.5f)
            matrix.postTranslate(width * 0.5f, height * 0.5f)
            tempFloat[0] = width.toFloat()
            tempFloat[1] = height.toFloat()
            matrix.mapPoints(tempFloat)
            return (width - tempFloat[0]) * if (rotation > 0.0f) 1.0f else -1.0f
        }
    }

    class VerticalFlip : ViewPager2.PageTransformer {
        private val rotationScale = -15f
        override fun transformPage(page: View, position: Float) {
            page.translationX = -position * page.width

            page.cameraDistance = 20000F

            if (position < 0.5 && position > -0.5) {
                page.visibility = View.VISIBLE
            } else {
                page.visibility = View.INVISIBLE
            }

            when {
                position < -1 -> {     // [-Infinity,-1)
                    // This page is way off-screen to the left.
                    page.alpha = 0F
                }
                position <= 0 -> {    // [-1,0]
                    page.alpha = 1F
                    page.rotationX = 180 * (1 - abs(position) + 1)
                }
                position <= 1 -> {    // (0,1]
                    page.alpha = 1F
                    page.rotationX = -180 * (1 - abs(position) + 1)
                }
                else -> {    // (1,+Infinity]
                    // This page is way off-screen to the right.
                    page.alpha = 0F
                }
            }
        }
    }

    class VerticalShut : ViewPager2.PageTransformer {
        override fun transformPage(page: View, position: Float) {
            page.translationX = -position * page.width
            page.cameraDistance = 999999999f

            if (position < 0.5 && position > -0.5) {
                page.visibility = View.VISIBLE
            } else {
                page.visibility = View.INVISIBLE
            }

            when {
                position < -1 -> {     // [-Infinity,-1)
                    // This page is way off-screen to the left.
                    page.alpha = 0f

                }
                position <= 0 -> {    // [-1,0]
                    page.alpha = 1f
                    page.rotationX = 180 * (1 - abs(position) + 1)

                }
                position <= 1 -> {    // (0,1]
                    page.alpha = 1f
                    page.rotationX = -180 * (1 - abs(position) + 1)

                }
                else -> {    // (1,+Infinity]
                    // This page is way off-screen to the right.
                    page.alpha = 0f

                }
            }
        }
    }

    class Twirl : ViewPager2.PageTransformer {
        override fun transformPage(page: View, position: Float) {
            page.translationX = -position * page.width
            page.cameraDistance = 20000f

            if (position < 0.5 && position > -0.5) {
                page.visibility = View.VISIBLE

            } else {
                page.visibility = View.INVISIBLE

            }

            when {
                position < -1 -> {     // [-Infinity,-1)
                    // This page is way off-screen to the left.
                    page.alpha = 0f

                }
                position <= 0 -> {    // [-1,0]
                    page.alpha = 1f
                    page.scaleX = 0.4f.coerceAtLeast((1 - abs(position)))
                    page.scaleY = 0.4f.coerceAtLeast((1 - abs(position)))
                    (1080 * (1 - abs(position) + 1)).also { page.rotationX = it }
                    page.translationY = -1000 * abs(position)

                }
                position <= 1 -> {    // (0,1]
                    page.alpha = 1f
                    page.scaleX = 0.4f.coerceAtLeast((1 - abs(position)))
                    page.scaleY = 0.4f.coerceAtLeast((1 - abs(position)))
                    page.rotationX = -1080 * (1 - abs(position) + 1)
                    page.translationY = -1000 * abs(position)

                }
                else -> {    // (1,+Infinity]
                    // This page is way off-screen to the right.
                    page.alpha = 0f

                }
            }
        }
    }

    class FastShut : ViewPager2.PageTransformer {
        override fun transformPage(page: View, position: Float) {
            page.translationX = -position * page.width

            if (abs(position) < 0.5) {
                page.visibility = View.VISIBLE
                page.scaleX = 1 - abs(position)
                page.scaleY = 1 - abs(position)
            } else if (abs(position) > 0.5) {
                page.visibility = View.GONE
            }
        }
    }

    class Gate : ViewPager2.PageTransformer {

        override fun transformPage(page: View, position: Float) {
            page.translationX = (-position * page.width)

            when {
                position < -1 -> {    // [-Infinity,-1)
                    // This page is way off-screen to the left.
                    page.alpha = 0F

                }
                position <= 0 -> {    // [-1,0]
                    page.alpha = 1F
                    page.pivotX = 0F
                    page.rotationY = 90 * abs(position)

                }
                position <= 1 -> {    // (0,1]
                    page.alpha = 1F
                    page.pivotX = page.width.toFloat()
                    page.rotationY = -90 * abs(position)

                }
                else -> {    // (1,+Infinity]
                    // This page is way off-screen to the right.
                    page.alpha = 0F

                }
            }
        }
    }

    class MoveY : ViewPager2.PageTransformer {
        override fun transformPage(page: View, position: Float) {
            val v = if (position < 0) 0 else page.width
            page.pivotY = v.toFloat()
            val w = if (position < 0) 1 + position else 1 - position
            page.scaleY = w
        }
    }

    class MoveX : ViewPager2.PageTransformer {
        override fun transformPage(page: View, position: Float) {
            val v = if (position < 0) 0 else page.width
            page.pivotX = v.toFloat()
            val w = if (position < 0) 1 + position else 1 - position
            page.scaleX = w
        }
    }

    class PageCorner : ViewPager2.PageTransformer {
        override fun transformPage(page: View, position: Float) {
            page.pivotY = (if (position > 0) 0 else page.width).toFloat()
            page.pivotX = 0f
            page.rotationY = -45f * position
        }
    }

    class CloseRollUp : ViewPager2.PageTransformer {
        override fun transformPage(page: View, position: Float) {
            page.scaleX = if (position < 0) 1 + position else 1 - position
        }
    }

    class UpDown : ViewPager2.PageTransformer {
        override fun transformPage(view: View, position: Float) {
            when {
                position < -1 -> { // [-Infinity,-1)

                    view.alpha = 0f
                }
                position <= 0 -> { // [-1,0]

                    view.alpha = 1f

                    view.translationX = view.width * -position


                    val yPosition: Float = position * view.height
                    view.translationY = yPosition
                    view.scaleX = 1f
                    view.scaleY = 1f
                }
                position <= 1 -> { // [0,1]
                    view.alpha = 1 - position


                    view.translationX = view.width * -position


                    val scaleFactor: Float = (0.75f
                            + (1 - 0.75f) * (1 - abs(position)))
                    view.scaleX = scaleFactor
                    view.scaleY = scaleFactor
                }
                else -> { // (1,+Infinity]

                    view.alpha = 0f
                }
            }
        }
    }

    class Fly : ViewPager2.PageTransformer {
        override fun transformPage(view: View, position: Float) {
            when {
                position < -1 -> { // [-Infinity,-1)

                    view.alpha = 0f
                }
                position <= 0 -> { // [-1,0]

                    view.alpha = 1f

                    view.translationX = view.width * -position


                    val xPosition: Float = position * view.width
                    view.translationX = xPosition
                    view.scaleX = 1f
                    view.scaleY = 1f
                }
                position <= 1 -> { // [0,1]
                    view.alpha = 1 - position


                    view.translationY = view.height * -position


                    val scaleFactor: Float = (0.75f
                            + (1 - 0.75f) * (1 - abs(position)))
                    view.scaleX = scaleFactor
                    view.scaleY = scaleFactor
                }
                else -> {

                    view.alpha = 0f
                }
            }
        }
    }

    class FarToNear : ViewPager2.PageTransformer {
        private val beta = 0.45f
        override fun transformPage(view: View, position: Float) {
            when {
                position < -1 -> { // [-Infinity,-1)

                    view.alpha = 0f
                }
                position <= 0 -> { // [-1,0]

                    view.alpha = 1f

                    view.translationY = view.width * -position


                    val xPosition: Float = position * view.width
                    view.translationY = xPosition
                    view.scaleX = 1f
                    view.scaleY = 1f
                }
                position <= 1 -> { // [0,1]
                    view.alpha = 1 - position


                    view.translationY = view.height * -position


                    val scaleFactor: Float = (beta
                            + (1 - beta) * (1 - abs(position)))
                    view.scaleX = scaleFactor
                    view.scaleY = scaleFactor
                }
                else -> {

                    view.alpha = 0f
                }
            }
        }
    }

    class Lucid : ViewPager2.PageTransformer {
        private val beta = 0.95f
        override fun transformPage(view: View, position: Float) {
            when {
                position < -1 -> { // [-Infinity,-1)

                    view.alpha = 0f
                }
                position <= 0 -> { // [-1,0]

                    view.alpha = 1f

                    view.translationY = view.width * -position


                    val xPosition: Float = position * view.width
                    view.translationY = xPosition
                    view.scaleX = 1f
                    view.scaleY = 1f
                }
                position <= 1 -> { // [0,1]
                    view.alpha = 1 - position


                    view.translationY = view.height * -position


                    val scaleFactor: Float = (beta
                            + (1 - beta) * (1 - abs(position)))
                    view.scaleX = scaleFactor
                    view.scaleY = scaleFactor
                }
                else -> {

                    view.alpha = 0f
                }
            }
        }
    }

}



