package io.github.diubruteforce.smartcr.ui.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import io.github.diubruteforce.smartcr.R
import io.github.diubruteforce.smartcr.model.data.CounselingHourState
import io.github.diubruteforce.smartcr.model.data.Meridiem

class TimePicker(
    private val time: String,
    private val onResult: (String) -> Unit
) : BottomSheetDialogFragment() {
    private lateinit var rootView: View
    private val titleText: TextView by lazy { rootView.findViewById(R.id.titleText) }
    private val saveBtn: MaterialButton by lazy { rootView.findViewById(R.id.saveBtn) }
    private val closeImg: ImageView by lazy { rootView.findViewById(R.id.closeImg) }

    private val hourPicker: NumberPicker by lazy { rootView.findViewById(R.id.hourPicker) }
    private val minutePicker: NumberPicker by lazy { rootView.findViewById(R.id.minutePicker) }
    private val meridiemPicker: NumberPicker by lazy { rootView.findViewById(R.id.meridiemPicker) }

    private val meridiemArray = Meridiem.values().map { it.name }.toTypedArray()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        rootView = inflater.inflate(R.layout.bottomsheet_dosepicker, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val timeTriple = CounselingHourState.separateTime(time)

        hourPicker.minValue = 1
        hourPicker.maxValue = 12
        hourPicker.value = timeTriple.first

        minutePicker.minValue = 0
        minutePicker.maxValue = 59
        minutePicker.value = timeTriple.second

        meridiemPicker.minValue = 0
        meridiemPicker.maxValue = 1
        meridiemPicker.displayedValues = meridiemArray
        meridiemPicker.value = meridiemArray.indexOf(timeTriple.third)

        hourPicker.setOnValueChangedListener { _, _, _ -> updateText() }
        minutePicker.setOnValueChangedListener { _, _, _ -> updateText() }
        meridiemPicker.setOnValueChangedListener { _, _, _ -> updateText() }
        updateText()

        saveBtn.setOnClickListener {
            val hour = hourPicker.value
            val minute = minutePicker.value
            val meridiem = Meridiem.values()[meridiemPicker.value]

            val newTime = CounselingHourState.timeToString(
                hour = hour,
                minute = minute,
                meridiem = meridiem.name
            )

            onResult.invoke(newTime)
            dismiss()
        }

        closeImg.setOnClickListener {
            dismiss()
        }
    }

    private fun updateText() {
        val hour = hourPicker.value
        val minute = minutePicker.value
        val meridiem = Meridiem.values()[meridiemPicker.value]

        titleText.text = "%02d:%02d %s".format(hour, minute, meridiem.name)
    }
}