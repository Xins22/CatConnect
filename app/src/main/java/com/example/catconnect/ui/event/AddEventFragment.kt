package com.example.catconnect.ui.event

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.catconnect.R
import com.example.catconnect.data.model.Event
import com.example.catconnect.data.repo.FakeRepository
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import java.util.*

class AddEventFragment : Fragment(R.layout.fragment_add_event) {

    private var pickedDateMillis: Long? = null
    private var pickedHour = 9
    private var pickedMinute = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etTitle = view.findViewById<EditText>(R.id.etTitle)
        val etDesc = view.findViewById<EditText>(R.id.etDesc)
        val etLocation = view.findViewById<EditText>(R.id.etLocation)
        val tvDate = view.findViewById<TextView>(R.id.tvDate)
        val btnPickDate = view.findViewById<MaterialButton>(R.id.btnPickDate)
        val btnPickTime = view.findViewById<MaterialButton>(R.id.btnPickTime)
        val btnSave = view.findViewById<MaterialButton>(R.id.btnSave)

        val cal = Calendar.getInstance()

        btnPickDate.setOnClickListener {
            DatePickerDialog(requireContext(),
                { _, y, m, d ->
                    cal.set(y, m, d, pickedHour, pickedMinute, 0)
                    cal.set(Calendar.MILLISECOND, 0)
                    pickedDateMillis = cal.timeInMillis
                    tvDate.text = Date(cal.timeInMillis).toString()
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        btnPickTime.setOnClickListener {
            TimePickerDialog(requireContext(),
                { _, h, min ->
                    pickedHour = h; pickedMinute = min
                    pickedDateMillis = (pickedDateMillis ?: System.currentTimeMillis()).let {
                        cal.timeInMillis = it
                        cal.set(Calendar.HOUR_OF_DAY, h)
                        cal.set(Calendar.MINUTE, min)
                        cal.set(Calendar.SECOND, 0)
                        cal.set(Calendar.MILLISECOND, 0)
                        cal.timeInMillis
                    }
                    tvDate.text = Date(pickedDateMillis!!).toString()
                },
                pickedHour, pickedMinute, true
            ).show()
        }

        btnSave.setOnClickListener {
            val title = etTitle.text?.toString()?.trim().orEmpty()
            val desc = etDesc.text?.toString()?.trim().orEmpty()
            val loc = etLocation.text?.toString()?.trim().orEmpty()
            val startAt = pickedDateMillis

            if (title.isBlank() || desc.isBlank() || loc.isBlank() || startAt == null) {
                Snackbar.make(view, "Lengkapi semua field & tanggal/waktu", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val e = Event(
                id = "e" + System.currentTimeMillis(),
                title = title,
                desc = desc,
                startAt = startAt,
                locationName = loc,
                bannerUrl = "https://picsum.photos/seed/event${System.currentTimeMillis()}/1000/600",
                organizerId = FakeRepository.currentUser.id
            )
            FakeRepository.upsertEvent(e)
            Snackbar.make(view, "Event tersimpan", Snackbar.LENGTH_SHORT).show()
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }
}
