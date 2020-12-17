package app.simple.felicit.util

import java.util.concurrent.TimeUnit

/**
 * @param timeValue strictly takes the long value of milliseconds and
 * formats them accordingly, if the [timeValue] is less than hour, it will
 * automatically be formatted as mm:ss format and if larger than that
 * it will be formatted as hh:mm:ss
 */
fun getFormattedTime(timeValue: Long): String {
    // mm:ss
    return if (timeValue < 3600000) {
        String.format(
            "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes("$timeValue".toLong()) % TimeUnit.HOURS.toMinutes(1),
            TimeUnit.MILLISECONDS.toSeconds("$timeValue".toLong()) % TimeUnit.MINUTES.toSeconds(1))
    } else {
        //hh:mm:ss
        String.format(
            "%02d:%02d:%02d",
            TimeUnit.MILLISECONDS.toHours(timeValue),
            TimeUnit.MILLISECONDS.toMinutes(timeValue) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeValue)),
            TimeUnit.MILLISECONDS.toSeconds(timeValue) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeValue)))
    }
}