package app.simple.felicit.decoration.knob

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import app.simple.felicit.R
import app.simple.felicit.helper.ImageHelper.toBitmapDrawable
import app.simple.felicit.math.Angle.getAngle
import kotlin.math.atan2

@SuppressLint("ClickableViewAccessibility")
class RotaryKnobView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : RelativeLayout(context, attrs, defStyleAttr) {

    var maxValue = 99
    private var minValue = 0
    private var lastDialAngle = 0F
    private var startAngle = 0F
    private var knobImageView: ImageView
    var knobDrawable: TransitionDrawable
    var listener: RotaryKnobListener? = null
    var value = 130
    private var divider = 300f / (maxValue - minValue)

    init {
        this.maxValue = maxValue + 1

        val view = LayoutInflater.from(context).inflate(R.layout.view_knob, this, true)
        knobImageView = view.findViewById(R.id.knobImageView)

        context.theme.obtainStyledAttributes(attrs, R.styleable.RotaryKnobView, 0, 0).apply {
            try {
                minValue = getInt(R.styleable.RotaryKnobView_minValue, 0)
                maxValue = getInt(R.styleable.RotaryKnobView_maxValue, 100) + 1
                divider = 300f / (maxValue - minValue)
                value = getInt(R.styleable.RotaryKnobView_initialValue, 50)

                knobDrawable = TransitionDrawable(arrayOf<Drawable>(
                    R.drawable.view_knob.toBitmapDrawable(context, resources.getDimensionPixelSize(R.dimen.volume_knob_dimension)),
                    R.drawable.view_knob_pressed.toBitmapDrawable(context, resources.getDimensionPixelSize(R.dimen.volume_knob_dimension))
                ))
                knobDrawable.isCrossFadeEnabled = true

                knobImageView.setImageDrawable(knobDrawable)
            } finally {
                recycle()
            }
        }
        this.setOnTouchListener(MyOnTouchListener())
    }

    /**
     * We're only interested in e2 - the coordinates of the end movement.
     * We calculate the polar angle (Theta) from these coordinates and use these to animate the
     * knob movement and calculate the value
     */
    private inner class MyOnTouchListener : OnTouchListener {
        override fun onTouch(v: View?, event: MotionEvent): Boolean {

            listener?.onKnobTouchEvent(event)

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    knobDrawable.startTransition(200)
                    lastDialAngle = knobImageView.rotation
                    startAngle = getAngle(event.x.toDouble(), event.y.toDouble(), knobImageView.width.toFloat(), knobImageView.height.toFloat())
                    return true
                }
                MotionEvent.ACTION_MOVE -> {
                    val currentAngle = getAngle(event.x.toDouble(), event.y.toDouble(), knobImageView.width.toFloat(), knobImageView.height.toFloat())
                    val finalAngle = ((currentAngle - startAngle) + lastDialAngle)

                    println("$finalAngle : $currentAngle : $startAngle")

                    // use only -150 to 150 range (knob min/max points
                    if (knobImageView.rotation >= -150 && knobImageView.rotation <= 150) {
                        setKnobPosition(finalAngle.coerceIn(-150F, 150F))

                    //    println(knobImageView.rotation)

                        // Calculate rotary value
                        // The range is the 300 degrees between -150 and 150, so we'll add 150 to adjust the
                        // range to 0 - 300
                        val valueRangeDegrees = finalAngle + 150
                        value = ((valueRangeDegrees / divider) + minValue).toInt().coerceIn(0, 100)
                        listener!!.onRotate(value)
                    }

                    return true
                }
                MotionEvent.ACTION_UP -> {
                    knobDrawable.reverseTransition(200)
                    return true
                }
            }

            return true
        }
    }

    /**
     * Calculate the angle from x,y coordinates of the touch event
     * The 0,0 coordinates in android are the top left corner of the view.
     * Dividing x and y by height and width we normalize them to the range of 0 - 1 values:
     * (0,0) top left, (1,1) bottom right.
     * While x's direction is correct - going up from left to right, y's isn't - it's
     * lowest value is at the top. W
     * So we reverse it by subtracting y from 1.
     * Now x is going from 0 (most left) to 1 (most right),
     * and Y is going from 0 (most downwards) to 1 (most upwards).
     * We now need to bring 0,0 to the middle - so subtract 0.5 from both x and y.
     * now 0,0 is in the middle, 0, 0.5 is at 12 o'clock and 0.5, 0 is at 3 o'clock.
     * Now that we have the coordinates in proper cartesian coordinate system - and we can calculate
     * "theta" - the angle between the x axis and the point by calculating atan2(y,x).
     * However, theta is the angle between the x axis and the point, and it rises as we turn
     * counter-clockwise. In addition, atan2 returns (in radians) angles in the range of -180
     * through 180 degrees (-PI through PI). And we want 0 to be at 12 o'clock.
     * So we reverse the direction of the angle by prefixing it with a minus sign,
     * and add 90 to move the "zero degrees" point north (taking care to handling the range between
     * 180 and 270 degrees, bringing them to their proper values of -180 .. -90 by adding 360 to the
     * value.
     *
     * @param x - x coordinate of the touch event
     * @param y - y coordinate of the touch event
     * @return
     */
    private fun calculateAngle(x: Float, y: Float): Float {
        val px = (x / width.toFloat()) - 0.5
        val py = (1 - y / height.toFloat()) - 0.5
        var angle = -(Math.toDegrees(atan2(py, px)))
            .toFloat() + 90
        if (angle > 180) angle -= 360
        return angle
    }

    private fun setKnobPosition(deg: Float) {
        knobImageView.rotation = deg
    }

    fun setCurrentPosition(value: Float) {
        //knobImageView.rotation = ((value / 100F) * 300F)
    }
}