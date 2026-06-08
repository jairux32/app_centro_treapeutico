package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.Appointment
import com.example.data.Review
import com.example.data.TherapyRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TherapyViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TherapyRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = TherapyRepository(database)
        
        // Pre-seed some beautiful, realistic therapeutic reviews if the table is empty
        viewModelScope.launch {
            val currentReviews = repository.reviews.first()
            if (currentReviews.isEmpty()) {
                val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val today = formatter.format(Date())
                
                // Add default seed reviews based on client testimonies & poster details
                repository.addReview(
                    Review(
                        author = "María Elvira Toaquiza",
                        rating = 5,
                        comment = "¡La camilla Ceragem con piedras de jade cambió mi vida! Tenía dolores crónicos en la columna y cintura por años. Después de 5 sesiones, la inflamación bajó por completo.",
                        dateString = today,
                        isUserGenerated = false
                    )
                )
                repository.addReview(
                    Review(
                        author = "Segundo Guamán",
                        rating = 5,
                        comment = "Excelente atención de la Sra. Carmen Carmen Viera. Las botas de presoterapia Chikimi me ayudaron con la mala circulación y várices en mis piernas. Siento alivio inmediato.",
                        dateString = today,
                        isUserGenerated = false
                    )
                )
                repository.addReview(
                    Review(
                        author = "Rosa Chimborazo",
                        rating = 5,
                        comment = "El sillón de masajes caliente y el masajeador de pies Chikimi es relajante al extremo. Ideal para combatir el estrés diario en Ambato.",
                        dateString = today,
                        isUserGenerated = false
                    )
                )
                repository.addReview(
                    Review(
                        author = "Carlos Alomoto",
                        rating = 5,
                        comment = "Excelente el escaneo de columna y el análisis cuántico rápido QRM. Me ayudó a detectar mi hígado graso y corregir hábitos. ¡Atención de primer nivel nacional!",
                        dateString = today,
                        isUserGenerated = false
                    )
                )
            }
        }
    }

    // Connect DB states reactively to the Compose UI Flow
    val appointments: StateFlow<List<Appointment>> = repository.appointments
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val reviews: StateFlow<List<Review>> = repository.reviews
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Actions
    fun bookAppointment(name: String, phone: String, service: String, date: String, slot: String, note: String) {
        viewModelScope.launch {
            repository.bookAppointment(
                Appointment(
                    clientName = name,
                    phone = phone,
                    serviceName = service,
                    dateString = date,
                    timeSlot = slot,
                    note = note
                )
            )
        }
    }

    fun cancelAppointment(id: Int) {
        viewModelScope.launch {
            repository.cancelAppointment(id)
        }
    }

    fun submitReview(author: String, rating: Int, comment: String) {
        viewModelScope.launch {
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            repository.addReview(
                Review(
                    author = author,
                    rating = rating,
                    comment = comment,
                    dateString = formatter.format(Date()),
                    isUserGenerated = true
                )
            )
        }
    }

    fun deleteReview(id: Int) {
        viewModelScope.launch {
            repository.removeReview(id)
        }
    }
}
