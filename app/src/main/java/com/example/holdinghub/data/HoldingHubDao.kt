package com.example.holdinghub.data

import androidx.room.*
import com.example.holdinghub.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface HoldingHubDao {

    // === AD SPACES ===
    @Query("SELECT * FROM ad_spaces WHERE status = 'approved' ORDER BY isFeatured DESC, monthlyPrice ASC")
    fun getAllApprovedSpaces(): Flow<List<AdSpaceEntity>>

    @Query("SELECT * FROM ad_spaces ORDER BY createdAt DESC")
    fun getAllSpacesAdmin(): Flow<List<AdSpaceEntity>>

    @Query("SELECT * FROM ad_spaces WHERE ownerId = :ownerId ORDER BY createdAt DESC")
    fun getSpacesByOwner(ownerId: String): Flow<List<AdSpaceEntity>>

    @Query("SELECT * FROM ad_spaces WHERE id = :id LIMIT 1")
    fun getSpaceById(id: String): Flow<AdSpaceEntity?>

    @Query("SELECT * FROM ad_spaces WHERE id = :id LIMIT 1")
    suspend fun getSpaceByIdSync(id: String): AdSpaceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpace(space: AdSpaceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpaces(spaces: List<AdSpaceEntity>)

    @Update
    suspend fun updateSpace(space: AdSpaceEntity)

    @Query("UPDATE ad_spaces SET status = :status WHERE id = :id")
    suspend fun updateSpaceStatus(id: String, status: String)

    @Query("DELETE FROM ad_spaces WHERE id = :id")
    suspend fun deleteSpace(id: String)

    // === USERS & OWNERS ===
    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun getUserById(id: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserByIdSync(id: String): UserEntity?

    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET isSuspended = :isSuspended WHERE id = :id")
    suspend fun updateUserSuspension(id: String, isSuspended: Boolean)

    @Query("SELECT * FROM owner_profiles ORDER BY createdAt DESC")
    fun getAllOwners(): Flow<List<OwnerProfileEntity>>

    @Query("SELECT * FROM owner_profiles WHERE id = :id LIMIT 1")
    fun getOwnerById(id: String): Flow<OwnerProfileEntity?>

    @Query("SELECT * FROM owner_profiles WHERE id = :id LIMIT 1")
    suspend fun getOwnerByIdSync(id: String): OwnerProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOwner(owner: OwnerProfileEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOwners(owners: List<OwnerProfileEntity>)

    @Query("UPDATE owner_profiles SET verificationStatus = :status WHERE id = :id")
    suspend fun updateOwnerVerification(id: String, status: String)

    // === BOOKINGS ===
    @Query("SELECT * FROM bookings ORDER BY createdAt DESC")
    fun getAllBookings(): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE customerId = :customerId ORDER BY createdAt DESC")
    fun getBookingsByCustomer(customerId: String): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE ownerId = :ownerId ORDER BY createdAt DESC")
    fun getBookingsByOwner(ownerId: String): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE id = :id LIMIT 1")
    fun getBookingById(id: String): Flow<BookingEntity?>

    @Query("SELECT * FROM bookings WHERE id = :id LIMIT 1")
    suspend fun getBookingByIdSync(id: String): BookingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: BookingEntity)

    @Update
    suspend fun updateBooking(booking: BookingEntity)

    @Query("UPDATE bookings SET bookingStatus = :status WHERE id = :id")
    suspend fun updateBookingStatus(id: String, status: String)

    @Query("UPDATE bookings SET paymentStatus = :paymentStatus, transactionRef = :txRef WHERE id = :id")
    suspend fun updateBookingPayment(id: String, paymentStatus: String, txRef: String)

    // === BOOKING EVENTS ===
    @Query("SELECT * FROM booking_events WHERE bookingId = :bookingId ORDER BY timestamp ASC")
    fun getEventsForBooking(bookingId: String): Flow<List<BookingEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookingEvent(event: BookingEventEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookingEvents(events: List<BookingEventEntity>)

    // === PAYMENTS ===
    @Query("SELECT * FROM payments ORDER BY paidAt DESC")
    fun getAllPayments(): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payments WHERE bookingId = :bookingId")
    fun getPaymentsForBooking(bookingId: String): Flow<List<PaymentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentEntity)

    @Update
    suspend fun updatePayment(payment: PaymentEntity)

    // === CAMPAIGNS ===
    @Query("SELECT * FROM campaigns WHERE customerId = :customerId ORDER BY updatedAt DESC")
    fun getCampaignsForCustomer(customerId: String): Flow<List<CampaignEntity>>

    @Query("SELECT * FROM campaigns ORDER BY updatedAt DESC")
    fun getAllCampaigns(): Flow<List<CampaignEntity>>

    @Query("SELECT * FROM campaigns WHERE bookingId = :bookingId LIMIT 1")
    suspend fun getCampaignByBookingId(bookingId: String): CampaignEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCampaign(campaign: CampaignEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCampaigns(campaigns: List<CampaignEntity>)

    @Update
    suspend fun updateCampaign(campaign: CampaignEntity)

    // === INSTALLATION PROOF ===
    @Query("SELECT * FROM installation_proof WHERE bookingId = :bookingId LIMIT 1")
    fun getProofForBooking(bookingId: String): Flow<InstallationProofEntity?>

    @Query("SELECT * FROM installation_proof ORDER BY timestamp DESC")
    fun getAllProofs(): Flow<List<InstallationProofEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProof(proof: InstallationProofEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProofs(proofs: List<InstallationProofEntity>)

    @Query("UPDATE installation_proof SET adminReviewStatus = :status, reviewNotes = :notes WHERE id = :id")
    suspend fun updateProofReview(id: String, status: String, notes: String)

    // === FAVORITES ===
    @Query("SELECT * FROM favorites WHERE customerId = :customerId")
    fun getFavoritesForCustomer(customerId: String): Flow<List<FavoriteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE customerId = :customerId AND spaceId = :spaceId")
    suspend fun deleteFavorite(customerId: String, spaceId: String)

    @Query("SELECT COUNT(*) FROM favorites WHERE customerId = :customerId AND spaceId = :spaceId")
    suspend fun isFavorite(customerId: String, spaceId: String): Int

    // === NOTIFICATIONS ===
    @Query("SELECT * FROM notifications WHERE targetRole = :role OR targetRole = 'all' OR targetUserId = :userId ORDER BY createdAt DESC")
    fun getNotifications(role: String, userId: String): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationEntity>)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationRead(id: Long)

    @Query("UPDATE notifications SET isRead = 1 WHERE targetRole = :role OR targetUserId = :userId")
    suspend fun markAllNotificationsRead(role: String, userId: String)

    // === PROMOTIONS ===
    @Query("SELECT * FROM promotions WHERE isActive = 1 ORDER BY discountPercent DESC")
    fun getActivePromotions(): Flow<List<PromotionEntity>>

    @Query("SELECT * FROM promotions ORDER BY discountPercent DESC")
    fun getAllPromotions(): Flow<List<PromotionEntity>>

    @Query("SELECT * FROM promotions WHERE code = :code LIMIT 1")
    suspend fun getPromotionByCode(code: String): PromotionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPromotion(promotion: PromotionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPromotions(promotions: List<PromotionEntity>)

    @Update
    suspend fun updatePromotion(promotion: PromotionEntity)

    @Query("DELETE FROM promotions WHERE code = :code")
    suspend fun deletePromotion(code: String)

    // === SERVICES ===
    @Query("SELECT * FROM service_products WHERE isActive = 1 ORDER BY basePrice ASC")
    fun getActiveServices(): Flow<List<ServiceProductEntity>>

    @Query("SELECT * FROM service_products ORDER BY basePrice ASC")
    fun getAllServices(): Flow<List<ServiceProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertService(service: ServiceProductEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServices(services: List<ServiceProductEntity>)

    @Update
    suspend fun updateService(service: ServiceProductEntity)
}
