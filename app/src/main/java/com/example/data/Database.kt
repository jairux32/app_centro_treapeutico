package com.example.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

// --- entities ---

@Entity(tableName = "appointments")
data class Appointment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val clientName: String,
    val phone: String,
    val serviceName: String,
    val dateString: String,
    val timeSlot: String,
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "reviews")
data class Review(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val author: String,
    val rating: Int,
    val comment: String,
    val dateString: String,
    val isUserGenerated: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

// --- DAOs ---

@Dao
interface AppointmentDao {
    @Query("SELECT * FROM appointments ORDER BY timestamp DESC")
    fun getAllAppointments(): Flow<List<Appointment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointment(appointment: Appointment)

    @Query("DELETE FROM appointments WHERE id = :id")
    suspend fun deleteAppointment(id: Int)
}

@Dao
interface ReviewDao {
    @Query("SELECT * FROM reviews ORDER BY timestamp DESC")
    fun getAllReviews(): Flow<List<Review>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: Review)

    @Query("DELETE FROM reviews WHERE id = :id")
    suspend fun deleteReview(id: Int)
}

// --- Database Configuration ---

@Database(entities = [Appointment::class, Review::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appointmentDao(): AppointmentDao
    abstract fun reviewDao(): ReviewDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "calor_terapeutico_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// --- Repository Block ---

class TherapyRepository(private val db: AppDatabase) {
    val appointments: Flow<List<Appointment>> = db.appointmentDao().getAllAppointments()
    val reviews: Flow<List<Review>> = db.reviewDao().getAllReviews()

    suspend fun bookAppointment(appointment: Appointment) {
        db.appointmentDao().insertAppointment(appointment)
    }

    suspend fun cancelAppointment(id: Int) {
        db.appointmentDao().deleteAppointment(id)
    }

    suspend fun addReview(review: Review) {
        db.reviewDao().insertReview(review)
    }

    suspend fun removeReview(id: Int) {
        db.reviewDao().deleteReview(id)
    }
}
