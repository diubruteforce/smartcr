package io.github.diubruteforce.smartcr.model.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class CounselingHourState(
    override val id: String = "",
    override val isActive: Boolean = true,
    @ServerTimestamp override val updatedOn: Timestamp = Timestamp.now(),
    override val updaterId: String = "admin",
    override val updaterEmail: String = "admin",
    val startTime: String = "8:00 AM",
    val endTime: String = "11:00 AM",
    val day: String = "Saturday"
) : EditableModel() {
    companion object {
        fun timeToString(hour: Int, minute: Int, meridiem: String) =
            "%02d:%02d %s".format(hour, minute, meridiem)

        fun separateTime(time: String): Triple<Int, Int, String> {
            val array = time.split(":")
            val hour = array[0].toInt()

            val array2 = array[1].split(" ")
            val minute = array2[0].toInt()
            val meridiem = array2[1]

            return Triple(hour, minute, meridiem)
        }
    }
}