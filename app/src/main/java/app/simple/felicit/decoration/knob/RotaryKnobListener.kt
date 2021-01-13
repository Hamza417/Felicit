package app.simple.felicit.decoration.knob

import android.view.MotionEvent

interface RotaryKnobListener {
    fun onRotate(value: Int)
    fun onKnobTouchEvent(event: MotionEvent)
}