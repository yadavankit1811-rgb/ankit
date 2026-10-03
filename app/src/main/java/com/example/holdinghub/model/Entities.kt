package com.example.holdinghub.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val role: String, // "customer", "owner", "admin"
    val name: String,
    val email: String,
    val phone: String,
    val company: String = "",
    val gstNumber: String = "",
    val billingAddress: String = "",
    val savedLocationsJson: String = "[]",
    val isSuspended: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "owner_profiles")
data class OwnerProfileEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val businessName: String,
    val contactPerson: String,
    val phone: String,
    val email: String,
    val panGst: String,
    val address: String,
    val city: String,
    val bankAccount: String = "",
    val bankIfsc: String = "",
    val verificationStatus: String = "pending", // "pending", "verified", "rejected", "suspended"
    val verificationDocsJson: String = "[]",
    val rating: Float = 4.8f,
    val completedCampaigns: Int = 12,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "ad_spaces")
data class AdSpaceEntity(
    @PrimaryKey val id: String,
    val ownerId: String,
    val ownerBusinessName: String,
    val isOwnerVerified: Boolean = false,
    val title: String,
    val adType: String, // "Hoarding", "Billboard", "Unipole", "Bus shelter", "Wall painting", "LED/Digital billboard", "Pole kiosk", "Mall advertising", "Metro advertising", "Airport advertising"
    val city: String,
    val area: String,
    val location: String,
    val landmark: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val widthFt: Int,
    val heightFt: Int,
    val totalSqFt: Int,
    val isDigital: Boolean,
    val isIlluminated: Boolean,
    val illuminationType: String = "Front-lit", // "Front-lit", "Back-lit", "Digital LED", "Non-illuminated"
    val monthlyPrice: Double,
    val minBookingDurationDays: Int = 30,
    val availableFrom: String,
    val availableUntil: String,
    val description: String,
    val trafficDailyImpressions: String,
    val trafficFootfall: String = "High Commercial",
    val facingDirection: String = "Main Traffic Facing",
    val imageUrl: String,
    val additionalImagesJson: String = "[]",
    val facilitiesJson: String = "[]",
    val terms: String = "Standard municipal permit approved. Print mounting charges extra.",
    val status: String = "approved", // "pending", "approved", "rejected", "suspended"
    val isFeatured: Boolean = false,
    // Digital Inventory fields
    val screenResolution: String = "",
    val loopDurationSec: Int = 0,
    val slotDurationSec: Int = 0,
    val operatingHours: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey val id: String,
    val bookingCode: String,
    val spaceId: String,
    val spaceTitle: String,
    val spaceLocation: String,
    val spaceCity: String,
    val spaceImageUrl: String,
    val customerId: String,
    val customerName: String,
    val customerCompany: String,
    val customerPhone: String,
    val ownerId: String,
    val brandName: String,
    val campaignObjective: String,
    val creativeNotes: String,
    val startDate: String,
    val endDate: String,
    val durationDays: Int,
    val servicesJson: String, // list of service names selected
    val spaceAmount: Double,
    val serviceAmount: Double,
    val taxAmount: Double, // 18% GST
    val discountAmount: Double,
    val totalAmount: Double,
    val bookingStatus: String = "pending", // "pending", "confirmed", "rejected", "printing", "dispatched", "installation_scheduled", "installed", "completed", "cancelled"
    val paymentStatus: String = "pending", // "pending", "paid", "failed", "refunded", "cancelled"
    val transactionRef: String = "",
    val installationScheduledDate: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "booking_events")
data class BookingEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bookingId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val title: String,
    val description: String,
    val actorRole: String, // "system", "customer", "owner", "admin"
    val statusBadge: String = ""
)

@Entity(tableName = "payments")
data class PaymentEntity(
    @PrimaryKey val id: String,
    val bookingId: String,
    val bookingCode: String,
    val transactionRef: String,
    val gateway: String = "Razorpay",
    val amount: Double,
    val status: String = "pending", // "pending", "paid", "failed", "refunded"
    val paymentMethod: String = "UPI", // "UPI", "NetBanking", "Credit/Debit Card", "NEFT/RTGS"
    val paidAt: Long = 0
)

@Entity(tableName = "campaigns")
data class CampaignEntity(
    @PrimaryKey val id: String,
    val bookingId: String,
    val customerId: String,
    val campaignName: String,
    val brand: String,
    val locationSummary: String,
    val startDate: String,
    val endDate: String,
    val status: String, // Timeline status
    val creativeUrl: String = "",
    val installationProofId: String = "",
    val printingStatus: String = "Pending",
    val installationStatus: String = "Pending",
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "installation_proof")
data class InstallationProofEntity(
    @PrimaryKey val id: String,
    val bookingId: String,
    val spaceId: String,
    val uploadedByOwnerId: String,
    val photoUrlsJson: String,
    val timestamp: Long = System.currentTimeMillis(),
    val geoTag: String = "",
    val notes: String = "",
    val adminReviewStatus: String = "approved", // "pending", "approved", "rejected"
    val reviewNotes: String = ""
)

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val customerId: String,
    val spaceId: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val targetUserId: String, // specific user or "all"
    val targetRole: String, // "customer", "owner", "admin", "all"
    val title: String,
    val message: String,
    val type: String, // "booking", "payment", "installation", "space", "system"
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "promotions")
data class PromotionEntity(
    @PrimaryKey val code: String,
    val name: String,
    val discountPercent: Int,
    val maxDiscount: Double,
    val minBookingValue: Double,
    val startDate: String,
    val endDate: String,
    val usageLimit: Int,
    val usedCount: Int = 0,
    val isActive: Boolean = true
)

@Entity(tableName = "service_products")
data class ServiceProductEntity(
    @PrimaryKey val id: String,
    val name: String,
    val code: String,
    val category: String, // "Creative", "Print", "Logistics", "Operations"
    val basePrice: Double,
    val unit: String, // "per sqft", "fixed", "per site"
    val description: String,
    val isActive: Boolean = true
)
