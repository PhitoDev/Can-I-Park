import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.DayOfWeekNames
import kotlinx.datetime.toLocalDateTime

object Utils {

    /**
     * This function returns the current system time in the format of "HH:mm a".
     * @return The current system time.
     */
    fun getCurrentSystemTime(): String {
        val now: Instant = Clock.System.now()
        val localDateTime = now.toLocalDateTime(TimeZone.currentSystemDefault())
        return LocalDateTime.Format {
            amPmHour()
            chars(":")
            minute()
            chars(" ")
            amPmMarker("AM", "PM")
            chars(" on ")
            dayOfWeek(DayOfWeekNames.ENGLISH_FULL)
        }.format(localDateTime)
    }
}