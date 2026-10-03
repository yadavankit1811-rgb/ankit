package com.example.holdinghub.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.holdinghub.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        OwnerProfileEntity::class,
        AdSpaceEntity::class,
        BookingEntity::class,
        BookingEventEntity::class,
        PaymentEntity::class,
        CampaignEntity::class,
        InstallationProofEntity::class,
        FavoriteEntity::class,
        NotificationEntity::class,
        PromotionEntity::class,
        ServiceProductEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun holdingHubDao(): HoldingHubDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "holding_hub_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.holdingHubDao())
                    }
                }
            }

            suspend fun populateDatabase(dao: HoldingHubDao) {
                dao.insertUsers(SampleData.users)
                dao.insertOwners(SampleData.ownerProfiles)
                dao.insertSpaces(SampleData.adSpaces)
                dao.insertPromotions(SampleData.promotions)
                dao.insertServices(SampleData.serviceProducts)
                SampleData.sampleBookings.forEach { dao.insertBooking(it) }
                dao.insertCampaigns(SampleData.sampleCampaigns)
                dao.insertProofs(SampleData.sampleInstallationProof)
                dao.insertBookingEvents(SampleData.sampleBookingEvents)
                dao.insertNotifications(SampleData.sampleNotifications)
            }
        }
    }
}
