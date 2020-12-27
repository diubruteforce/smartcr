package io.github.diubruteforce.smartcr.ui.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import io.github.diubruteforce.smartcr.R
import io.github.diubruteforce.smartcr.utils.extension.toDateStringWeek
import java.util.*

class DatePickerBottomSheet(
    private val calendar: Calendar = Calendar.getInstance(),
    private val onResult: ((calender: Calendar) -> Unit)
) : BottomSheetDialogFragment() {
    private lateinit var rootView: View
    private val dateShowTxt: TextView by lazy { rootView.findViewById(R.id.dateShowTxt) }
    private val datePicker: DatePicker by lazy { rootView.findViewById(R.id.datePicker) }
    private val saveBtn: MaterialButton by lazy { rootView.findViewById(R.id.saveBtn) }
    private val closeImg: ImageView by lazy { rootView.findViewById(R.id.closeImg) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.bottomsheet_datepicker, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val date = Calendar.getInstance().apply {
            set(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
        }

        dateShowTxt.text = date.toDateStringWeek()
        datePicker.init(
            date.get(Calendar.YEAR),
            date.get(Calendar.MONTH),
            date.get(Calendar.DAY_OF_MONTH)
        ) { _, p1, p2, p3 ->
            date.set(p1, p2, p3)
            dateShowTxt.text = date.toDateStringWeek()
        }

        saveBtn.setOnClickListener {
            calendar.set(
                date.get(Calendar.YEAR),
                date.get(Calendar.MONTH),
                date.get(Calendar.DAY_OF_MONTH)
            )
            onResult.invoke(calendar)
            dismiss()
        }

        closeImg.setOnClickListener {
            dismiss()
        }
    }
}