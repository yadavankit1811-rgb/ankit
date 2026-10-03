package com.example.holdinghub.data

import com.example.holdinghub.model.*
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class HoldingHubRepository(private val dao: HoldingHubDao) {

    // Spaces
    val approvedSpaces: Flow<List<AdSpaceEntity>> = dao.getAllApprovedSpaces()
    val allSpacesAdmin: Flow<List<AdSpaceEntity>> = dao.getAllSpacesAdmin()
    val activeServices: Flow<List<ServiceProductEntity>> = dao.getActiveServices()
    val allServices: Flow<List<ServiceProductEntity>> = dao.getAllServices()
    val activePromotions: Flow<List<PromotionEntity>> = dao.getActivePromotions()
    val allPromotions: Flow<List<PromotionEntity>> = dao.getAllPromotions()
    val allOwners: Flow<List<OwnerProfileEntity>> = dao.getAllOwners()
    val allUsers: Flow<List<UserEntity>> = dao.getAllUsers()
    val allBookings: Flow<List<BookingEntity>> = dao.getAllBookings()
    val allCampaigns: Flow<List<CampaignEntity>> = dao.getAllCampaigns()
    val allPayments: Flow<List<PaymentEntity>> = dao.getAllPayments()
    val allProofs: Flow<List<InstallationProofEntity>> = dao.getAllProofs()

    fun getSpacesByOwner(ownerId: String): Flow<List<AdSpaceEntity>> = dao.getSpacesByOwner(ownerId)
    fun getSpaceById(id: String): Flow<AdSpaceEntity?> = dao.getSpaceById(id)
    suspend fun getSpaceByIdSync(id: String): AdSpaceEntity? = dao.getSpaceByIdSync(id)

    fun getBookingsByCustomer(customerId: String): Flow<List<BookingEntity>> = dao.getBookingsByCustomer(customerId)
    fun getBookingsByOwner(ownerId: String): Flow<List<BookingEntity>> = dao.getBookingsByOwner(ownerId)
    fun getBookingById(id: String): Flow<BookingEntity?> = dao.getBookingById(id)
    suspend fun getBookingByIdSync(id: String): BookingEntity? = dao.getBookingByIdSync(id)

    fun getCampaignsByCustomer(customerId: String): Flow<List<CampaignEntity>> = dao.getCampaignsForCustomer(customerId)
    fun getEventsForBooking(bookingId: String): Flow<List<BookingEventEntity>> = dao.getEventsForBooking(bookingId)
    fun getProofForBooking(bookingId: String): Flow<InstallationProofEntity?> = dao.getProofForBooking(bookingId)
    fun getFavoritesForCustomer(customerId: String): Flow<List<FavoriteEntity>> = dao.getFavoritesForCustomer(customerId)
    fun getNotifications(role: String, userId: String): Flow<List<NotificationEntity>> = dao.getNotifications(role, userId)

    // User & Profile
    fun getUserById(id: String): Flow<UserEntity?> = dao.getUserById(id)
    suspend fun updateUser(user: UserEntity) = dao.updateUser(user)
    suspend fun updateUserSuspension(id: String, isSuspended: Boolean) = dao.updateUserSuspension(id, isSuspended)

    // Owner verification (Admin)
    suspend fun updateOwnerVerification(ownerId: String, status: String) {
        dao.updateOwnerVerification(ownerId, status)
        dao.insertNotification(
            NotificationEntity(
                targetUserId = ownerId,
                targetRole = "owner",
                title = if (status == "verified") "Owner Account Verified!" else "Verification Status: $status",
                message = if (status == "verified") "Congratulations! Your profile now displays the Verified Owner badge." else "Your verification status has been updated to $status.",
                type = "system"
            )
        )
    }

    // Space Management
    suspend fun submitNewSpace(space: AdSpaceEntity) {
        // Enforce: New spaces should NOT immediately become live.
        // Status: Pending -> Admin Review -> Approved -> Live
        val pendingSpace = space.copy(
            id = if (space.id.isBlank()) "space_${UUID.randomUUID().toString().take(8)}" else space.id,
            status = "pending",
            createdAt = System.currentTimeMillis()
        )
        dao.insertSpace(pendingSpace)

        // Notify Admin
        dao.insertNotification(
            NotificationEntity(
                targetUserId = "admin",
                targetRole = "admin",
                title = "New Space Listing Submitted",
                message = "${space.ownerBusinessName} submitted '${space.title}' in ${space.city} for review.",
                type = "space"
            )
        )
        // Notify Owner
        dao.insertNotification(
            NotificationEntity(
                targetUserId = space.ownerId,
                targetRole = "owner",
                title = "Space Submitted for Review",
                message = "Your listing '${space.title}' is under review by Holding Hub operations.",
                type = "space"
            )
        )
    }

    suspend fun updateSpaceStatus(id: String, status: String) {
        dao.updateSpaceStatus(id, status)
        val space = dao.getSpaceByIdSync(id)
        if (space != null) {
            dao.insertNotification(
                NotificationEntity(
                    targetUserId = space.ownerId,
                    targetRole = "owner",
                    title = if (status == "approved") "Listing Approved & Live!" else "Listing Status: $status",
                    message = "Your advertising space '${space.title}' status is now $status.",
                    type = "space"
                )
            )
        }
    }

    suspend fun deleteSpace(id: String) = dao.deleteSpace(id)
    suspend fun updateSpace(space: AdSpaceEntity) = dao.updateSpace(space)

    // Booking Submission
    suspend fun createBooking(
        space: AdSpaceEntity,
        customer: UserEntity,
        brandName: String,
        campaignObjective: String,
        creativeNotes: String,
        startDate: String,
        endDate: String,
        durationDays: Int,
        selectedServices: List<String>,
        spaceAmount: Double,
        serviceAmount: Double,
        discountAmount: Double,
        totalAmount: Double
    ): BookingEntity {
        val bookingId = "bk_${UUID.randomUUID().toString().take(8)}"
        val codeSuffix = (100..999).random()
        val cityCode = space.city.take(3).uppercase()
        val bookingCode = "HH-$cityCode-26-$codeSuffix"

        val taxAmount = (spaceAmount + serviceAmount - discountAmount).coerceAtLeast(0.0) * 0.18
        val finalTotal = (spaceAmount + serviceAmount - discountAmount).coerceAtLeast(0.0) + taxAmount

        val booking = BookingEntity(
            id = bookingId,
            bookingCode = bookingCode,
            spaceId = space.id,
            spaceTitle = space.title,
            spaceLocation = "${space.area}, ${space.city}",
            spaceCity = space.city,
            spaceImageUrl = space.imageUrl,
            customerId = customer.id,
            customerName = customer.name,
            customerCompany = customer.company.ifBlank { brandName },
            customerPhone = customer.phone,
            ownerId = space.ownerId,
            brandName = brandName,
            campaignObjective = campaignObjective,
            creativeNotes = creativeNotes,
            startDate = startDate,
            endDate = endDate,
            durationDays = durationDays,
            servicesJson = selectedServices.joinToString(prefix = "[\"", separator = "\", \"", postfix = "\"]"),
            spaceAmount = spaceAmount,
            serviceAmount = serviceAmount,
            taxAmount = taxAmount,
            discountAmount = discountAmount,
            totalAmount = finalTotal,
            bookingStatus = "pending",
            paymentStatus = "pending",
            createdAt = System.currentTimeMillis()
        )

        dao.insertBooking(booking)

        // Event
        dao.insertBookingEvent(
            BookingEventEntity(
                bookingId = bookingId,
                title = "Booking Submitted",
                description = "Customer ${customer.name} submitted booking request for $durationDays days.",
                actorRole = "customer",
                statusBadge = "Pending"
            )
        )

        // Notifications
        dao.insertNotification(
            NotificationEntity(
                targetUserId = space.ownerId,
                targetRole = "owner",
                title = "New Booking Request (#$bookingCode)",
                message = "${customer.name} requested to book '${space.title}' for ₹${finalTotal.toInt()}.",
                type = "booking"
            )
        )
        dao.insertNotification(
            NotificationEntity(
                targetUserId = "admin",
                targetRole = "admin",
                title = "New Booking Created (#$bookingCode)",
                message = "New booking for '${space.title}' (${space.city}) by ${customer.name}.",
                type = "booking"
            )
        )

        return booking
    }

    // Payment Processing
    suspend fun processPayment(
        bookingId: String,
        paymentMethod: String,
        cardOrUpiRef: String
    ): Boolean {
        val booking = dao.getBookingByIdSync(bookingId) ?: return false
        val txRef = "RZP_TXN_${System.currentTimeMillis().toString().takeLast(8)}"

        val payment = PaymentEntity(
            id = "pay_${UUID.randomUUID().toString().take(8)}",
            bookingId = bookingId,
            bookingCode = booking.bookingCode,
            transactionRef = txRef,
            gateway = "Razorpay / Indian Banking Gateway",
            amount = booking.totalAmount,
            status = "paid",
            paymentMethod = paymentMethod,
            paidAt = System.currentTimeMillis()
        )
        dao.insertPayment(payment)
        dao.updateBookingPayment(bookingId, "paid", txRef)
        dao.updateBookingStatus(bookingId, "confirmed")

        // Create initial campaign record
        val campaignId = "camp_${UUID.randomUUID().toString().take(8)}"
        val campaign = CampaignEntity(
            id = campaignId,
            bookingId = bookingId,
            customerId = booking.customerId,
            campaignName = "${booking.brandName} - ${booking.spaceLocation}",
            brand = booking.brandName,
            locationSummary = booking.spaceLocation,
            startDate = booking.startDate,
            endDate = booking.endDate,
            status = "Space Confirmed",
            creativeUrl = booking.spaceImageUrl,
            printingStatus = "Design & Print Prep",
            installationStatus = "Pending Scheduling"
        )
        dao.insertCampaign(campaign)

        // Booking Events
        dao.insertBookingEvent(
            BookingEventEntity(
                bookingId = bookingId,
                title = "Payment Received",
                description = "₹${booking.totalAmount.toInt()} successfully received via $paymentMethod (Ref: $txRef).",
                actorRole = "system",
                statusBadge = "Paid"
            )
        )
        dao.insertBookingEvent(
            BookingEventEntity(
                bookingId = bookingId,
                title = "Space Confirmed",
                description = "Space dates locked from ${booking.startDate} to ${booking.endDate}.",
                actorRole = "owner",
                statusBadge = "Confirmed"
            )
        )

        // Notifications
        dao.insertNotification(
            NotificationEntity(
                targetUserId = booking.customerId,
                targetRole = "customer",
                title = "Payment Successful!",
                message = "Your booking #${booking.bookingCode} is confirmed. Campaign preparation initiated.",
                type = "payment"
            )
        )
        dao.insertNotification(
            NotificationEntity(
                targetUserId = booking.ownerId,
                targetRole = "owner",
                title = "Payment Confirmed for #${booking.bookingCode}",
                message = "Client paid ₹${booking.totalAmount.toInt()}. Please schedule printing and installation.",
                type = "payment"
            )
        )

        return true
    }

    // Campaign & Status Workflow
    suspend fun updateBookingAndCampaignStatus(
        bookingId: String,
        newStatus: String,
        notes: String,
        actorRole: String
    ) {
        dao.updateBookingStatus(bookingId, newStatus)
        val booking = dao.getBookingByIdSync(bookingId) ?: return

        // Update campaign if exists
        val campaign = dao.getCampaignByBookingId(bookingId)
        if (campaign != null) {
            val printingStatus = when (newStatus) {
                "printing" -> "In Printing Queue"
                "dispatched" -> "Flex Dispatched to Site"
                "installed", "campaign_live", "completed" -> "Printed & Mounted"
                else -> campaign.printingStatus
            }
            val installationStatus = when (newStatus) {
                "installation_scheduled" -> "Installation Scheduled"
                "installed" -> "Mounted on Site"
                "campaign_live" -> "Live & Verified"
                "completed" -> "Campaign Completed"
                else -> campaign.installationStatus
            }
            dao.updateCampaign(
                campaign.copy(
                    status = newStatus.replace("_", " ").replaceFirstChar { it.uppercase() },
                    printingStatus = printingStatus,
                    installationStatus = installationStatus,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }

        // Add history event
        dao.insertBookingEvent(
            BookingEventEntity(
                bookingId = bookingId,
                title = newStatus.replace("_", " ").replaceFirstChar { it.uppercase() },
                description = notes.ifBlank { "Status updated to $newStatus by $actorRole." },
                actorRole = actorRole,
                statusBadge = newStatus.replace("_", " ").replaceFirstChar { it.uppercase() }
            )
        )

        // Notification
        dao.insertNotification(
            NotificationEntity(
                targetUserId = booking.customerId,
                targetRole = "customer",
                title = "Campaign Status: ${newStatus.replace("_", " ").replaceFirstChar { it.uppercase() }}",
                message = notes.ifBlank { "Booking #${booking.bookingCode} is now $newStatus." },
                type = "booking"
            )
        )
    }

    // Installation Proof
    suspend fun uploadInstallationProof(
        bookingId: String,
        spaceId: String,
        ownerId: String,
        photoUrl: String,
        geoTag: String,
        notes: String
    ) {
        val proofId = "proof_${UUID.randomUUID().toString().take(8)}"
        val proof = InstallationProofEntity(
            id = proofId,
            bookingId = bookingId,
            spaceId = spaceId,
            uploadedByOwnerId = ownerId,
            photoUrlsJson = "[\"$photoUrl\"]",
            timestamp = System.currentTimeMillis(),
            geoTag = geoTag.ifBlank { "GPS Coordinates Recorded" },
            notes = notes,
            adminReviewStatus = "approved", // Auto-approved for fast flow or pending
            reviewNotes = "Uploaded by site contractor"
        )
        dao.insertProof(proof)

        updateBookingAndCampaignStatus(
            bookingId = bookingId,
            newStatus = "installed",
            notes = "Installation photos and geotag proof uploaded by space owner.",
            actorRole = "owner"
        )
    }

    suspend fun reviewInstallationProof(proofId: String, isApproved: Boolean, reviewNotes: String) {
        val status = if (isApproved) "approved" else "rejected"
        dao.updateProofReview(proofId, status, reviewNotes)
    }

    // Favorites
    suspend fun toggleFavorite(customerId: String, spaceId: String) {
        val count = dao.isFavorite(customerId, spaceId)
        if (count > 0) {
            dao.deleteFavorite(customerId, spaceId)
        } else {
            dao.insertFavorite(FavoriteEntity(customerId = customerId, spaceId = spaceId))
        }
    }

    suspend fun isFavorite(customerId: String, spaceId: String): Boolean {
        return dao.isFavorite(customerId, spaceId) > 0
    }

    // Promotions & Services (Admin)
    suspend fun insertPromotion(promo: PromotionEntity) = dao.insertPromotion(promo)
    suspend fun deletePromotion(code: String) = dao.deletePromotion(code)
    suspend fun getPromotionByCode(code: String) = dao.getPromotionByCode(code)

    suspend fun updateService(service: ServiceProductEntity) = dao.updateService(service)
    suspend fun insertService(service: ServiceProductEntity) = dao.insertService(service)

    suspend fun markNotificationRead(id: Long) = dao.markNotificationRead(id)
    suspend fun markAllNotificationsRead(role: String, userId: String) = dao.markAllNotificationsRead(role, userId)
}
