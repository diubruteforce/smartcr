package io.github.diubruteforce.smartcr.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import io.github.diubruteforce.smartcr.model.data.Event
import io.github.diubruteforce.smartcr.model.data.FeesSchedule
import io.github.diubruteforce.smartcr.utils.extension.whereActiveData
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ExtraFeatureRepository @Inject constructor(
    private val classRepository: ClassRepository
) {
    private val db by lazy { Firebase.firestore }
    private val feesSchedulePath = "feesSchedule"
    private val eventPath = "event"

    private suspend fun getFeesScheduleCollection() =
        db.collection(classRepository.departmentPath)
            .document(classRepository.getUserProfile().departmentId)
            .collection(classRepository.semesterPath)
            .document(classRepository.getSemesterId())
            .collection(feesSchedulePath)

    suspend fun getFees(currentDateMillis: Long): List<FeesSchedule> {
        return getFeesScheduleCollection()
            .whereActiveData()
            .whereGreaterThanOrEqualTo("dateTimeMillis", currentDateMillis)
            .get()
            .await()
            .map { it.toObject<FeesSchedule>().copy(id = it.id) }
    }

    suspend fun saveFeesSchedule(feesSchedule: FeesSchedule) {
        val newFeesSchedule = feesSchedule.copy(
            updatedOn = Timestamp.now(),
            updaterId = classRepository.getUserProfile().id,
            updaterEmail = classRepository.getUserProfile().diuEmail
        )

        val feesScheduleId = if (newFeesSchedule.id.isEmpty()) {
            getFeesScheduleCollection().add(newFeesSchedule).await().id
        } else {
            getFeesScheduleCollection()
                .document(newFeesSchedule.id)
                .set(newFeesSchedule, SetOptions.merge())

            newFeesSchedule.id
        }

        // Keeping the history
        getFeesScheduleCollection()
            .document(feesScheduleId)
            .collection(classRepository.historyPath)
            .add(newFeesSchedule.copy(id = feesScheduleId))
    }

    suspend fun getEvent(eventId: String?): Event {
        if (eventId == null) return Event()

        val response = db.collection(eventPath)
            .document(eventId)
            .get()
            .await()

        return response.toObject<Event>()!!.copy(id = response.id)
    }

    suspend fun saveEvent(event: Event) {
        val newEvent = event.copy(
            updatedOn = Timestamp.now(),
            updaterId = classRepository.getUserProfile().id,
            updaterEmail = classRepository.getUserProfile().diuEmail,
        )

        val eventId = if (newEvent.id.isEmpty()) {
            db.collection(eventPath)
                .add(newEvent)
                .await()
                .id
        } else {
            db.collection(eventPath)
                .document(newEvent.id)
                .set(newEvent, SetOptions.merge())
                .await()

            newEvent.id
        }

        // Keeping the history
        db.collection(eventPath)
            .document(eventId)
            .collection(classRepository.historyPath)
            .add(newEvent.copy(id = eventId))
    }

    suspend fun deleteEvent(event: Event) {
        val newEvent = event.copy(
            isActive = false,
            updatedOn = Timestamp.now(),
            updaterId = classRepository.getUserProfile().id,
            updaterEmail = classRepository.getUserProfile().diuEmail,
        )

        db.collection(eventPath)
            .document(newEvent.id)
            .set(newEvent, SetOptions.merge())
            .await()

        // Keeping the history
        db.collection(eventPath)
            .document(newEvent.id)
            .collection(classRepository.historyPath)
            .add(newEvent)
    }
}