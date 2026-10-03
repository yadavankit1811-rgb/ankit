package com.example.holdinghub.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.holdinghub.ai.AiAdCreativeResult
import com.example.holdinghub.ai.AiAdStudioEngine
import com.example.holdinghub.data.AppDatabase
import com.example.holdinghub.data.HoldingHubRepository
import com.example.holdinghub.data.SampleData
import com.example.holdinghub.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class AppRole(val label: String) {
    CUSTOMER("Brand / Advertiser"),
    OWNER("Space Owner / Vendor"),
    ADMIN("Admin (Ankit)")
}

enum class CustomerScreen {
    HOME,
    SEARCH_SPACES,
    SPACE_DETAILS,
    BOOKING_FLOW,
    MY_CAMPAIGNS,
    MY_BOOKINGS,
    FAVORITES,
    AI_AD_STUDIO,
    DASHBOARD,
    SUPPORT_LEGAL
}

enum class OwnerScreen {
    DASHBOARD,
    MY_SPACES,
    ADD_SPACE,
    BOOKINGS,
    EARNINGS,
    PROFILE
}

enum class AdminScreen {
    DASHBOARD,
    SPACES,
    OWNERS,
    CUSTOMERS,
    BOOKINGS,
    PAYMENTS,
    CAMPAIGNS_PROOF,
    PROMOTIONS,
    SERVICES,
    REPORTS
}

data class SearchFilterState(
    val query: String = "",
    val selectedCity: String = "All Cities",
    val selectedArea: String = "All Areas",
    val selectedAdType: String = "All Types",
    val minBudget: Double = 0.0,
    val maxBudget: Double = 300000.0,
    val isDigitalOnly: Boolean = false,
    val isIlluminatedOnly: Boolean = false,
    val verifiedOwnerOnly: Boolean = false,
    val showMap: Boolean = false
)

data class BookingDraft(
    val space: AdSpaceEntity? = null,
    val startDate: String = "2026-11-01",
    val endDate: String = "2026-11-30",
    val durationDays: Int = 30,
    val brandName: String = "Singhal Sweets & Namkeen",
    val campaignObjective: String = "Festive Brand Awareness & Store Footfall",
    val creativeNotes: String = "Bold typography with brand logo and golden sweets platter visual.",
    val selectedServices: Set<String> = setOf("Space booking", "Flex Printing", "Mounting & Installation"),
    val appliedCoupon: PromotionEntity? = null,
    val currentStep: Int = 1,
    val selectedPaymentMethod: String = "UPI",
    val isSubmitting: Boolean = false,
    val lastCreatedBookingId: String? = null
)

class HoldingHubViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: HoldingHubRepository

    init {
        val db = AppDatabase.getDatabase(application, viewModelScope)
        repository = HoldingHubRepository(db.holdingHubDao())

        // Initial check to ensure sample data is seeded if database was just created
        viewModelScope.launch(Dispatchers.IO) {
            // trigger first query
            val list = db.holdingHubDao().getSpaceByIdSync("space_del_1")
            if (list == null) {
                db.holdingHubDao().insertUsers(SampleData.users)
                db.holdingHubDao().insertOwners(SampleData.ownerProfiles)
                db.holdingHubDao().insertSpaces(SampleData.adSpaces)
                db.holdingHubDao().insertPromotions(SampleData.promotions)
                db.holdingHubDao().insertServices(SampleData.serviceProducts)
                SampleData.sampleBookings.forEach { db.holdingHubDao().insertBooking(it) }
                db.holdingHubDao().insertCampaigns(SampleData.sampleCampaigns)
                db.holdingHubDao().insertProofs(SampleData.sampleInstallationProof)
                db.holdingHubDao().insertBookingEvents(SampleData.sampleBookingEvents)
                db.holdingHubDao().insertNotifications(SampleData.sampleNotifications)
            }
        }
    }

    // Role state
    private val _currentRole = MutableStateFlow(AppRole.CUSTOMER)
    val currentRole: StateFlow<AppRole> = _currentRole.asStateFlow()

    // Navigation states
    private val _customerScreen = MutableStateFlow(CustomerScreen.HOME)
    val customerScreen: StateFlow<CustomerScreen> = _customerScreen.asStateFlow()

    private val _ownerScreen = MutableStateFlow(OwnerScreen.DASHBOARD)
    val ownerScreen: StateFlow<OwnerScreen> = _ownerScreen.asStateFlow()

    private val _adminScreen = MutableStateFlow(AdminScreen.DASHBOARD)
    val adminScreen: StateFlow<AdminScreen> = _adminScreen.asStateFlow()

    // Active User
    val currentCustomer: StateFlow<UserEntity> = MutableStateFlow(SampleData.users[0])
    val currentOwner: StateFlow<OwnerProfileEntity> = MutableStateFlow(SampleData.ownerProfiles[0])

    // Selected Space for details / booking
    private val _selectedSpace = MutableStateFlow<AdSpaceEntity?>(null)
    val selectedSpace: StateFlow<AdSpaceEntity?> = _selectedSpace.asStateFlow()

    // Selected Booking for details/proof inspection
    private val _selectedBooking = MutableStateFlow<BookingEntity?>(null)
    val selectedBooking: StateFlow<BookingEntity?> = _selectedBooking.asStateFlow()

    // Search & Filter State
    private val _filterState = MutableStateFlow(SearchFilterState())
    val filterState: StateFlow<SearchFilterState> = _filterState.asStateFlow()

    // Booking Flow Draft State
    private val _bookingDraft = MutableStateFlow(BookingDraft())
    val bookingDraft: StateFlow<BookingDraft> = _bookingDraft.asStateFlow()

    // AI Ad Studio State
    private val _aiStudioResult = MutableStateFlow<AiAdCreativeResult?>(null)
    val aiStudioResult: StateFlow<AiAdCreativeResult?> = _aiStudioResult.asStateFlow()
    val isGeneratingAi = MutableStateFlow(false)

    // Data streams from Repository
    val approvedSpaces: StateFlow<List<AdSpaceEntity>> = repository.approvedSpaces
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSpacesAdmin: StateFlow<List<AdSpaceEntity>> = repository.allSpacesAdmin
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val ownerSpaces: StateFlow<List<AdSpaceEntity>> = repository.getSpacesByOwner(SampleData.ownerProfiles[0].id)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val customerBookings: StateFlow<List<BookingEntity>> = repository.getBookingsByCustomer(SampleData.users[0].id)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBookings: StateFlow<List<BookingEntity>> = repository.allBookings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val customerCampaigns: StateFlow<List<CampaignEntity>> = repository.getCampaignsByCustomer(SampleData.users[0].id)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCampaigns: StateFlow<List<CampaignEntity>> = repository.allCampaigns
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allOwners: StateFlow<List<OwnerProfileEntity>> = repository.allOwners
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allUsers: StateFlow<List<UserEntity>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPayments: StateFlow<List<PaymentEntity>> = repository.allPayments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allProofs: StateFlow<List<InstallationProofEntity>> = repository.allProofs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activePromotions: StateFlow<List<PromotionEntity>> = repository.activePromotions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPromotions: StateFlow<List<PromotionEntity>> = repository.allPromotions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeServices: StateFlow<List<ServiceProductEntity>> = repository.activeServices
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allServices: StateFlow<List<ServiceProductEntity>> = repository.allServices
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val customerFavorites: StateFlow<List<FavoriteEntity>> = repository.getFavoritesForCustomer(SampleData.users[0].id)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<NotificationEntity>> = repository.getNotifications("all", SampleData.users[0].id)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Role Switching
    fun switchRole(role: AppRole) {
        _currentRole.value = role
    }

    // Customer Navigation
    fun navigateCustomer(screen: CustomerScreen) {
        _customerScreen.value = screen
    }

    fun navigateOwner(screen: OwnerScreen) {
        _ownerScreen.value = screen
    }

    fun navigateAdmin(screen: AdminScreen) {
        _adminScreen.value = screen
    }

    // Space Selection
    fun selectSpace(space: AdSpaceEntity) {
        _selectedSpace.value = space
    }

    fun selectBooking(booking: BookingEntity) {
        _selectedBooking.value = booking
    }

    // Search & Filter Actions
    fun updateSearchQuery(query: String) {
        _filterState.value = _filterState.value.copy(query = query)
    }

    fun updateCityFilter(city: String) {
        _filterState.value = _filterState.value.copy(selectedCity = city)
    }

    fun updateAreaFilter(area: String) {
        _filterState.value = _filterState.value.copy(selectedArea = area)
    }

    fun updateAdTypeFilter(adType: String) {
        _filterState.value = _filterState.value.copy(selectedAdType = adType)
    }

    fun toggleDigitalOnly(enabled: Boolean) {
        _filterState.value = _filterState.value.copy(isDigitalOnly = enabled)
    }

    fun toggleIlluminatedOnly(enabled: Boolean) {
        _filterState.value = _filterState.value.copy(isIlluminatedOnly = enabled)
    }

    fun toggleVerifiedOnly(enabled: Boolean) {
        _filterState.value = _filterState.value.copy(verifiedOwnerOnly = enabled)
    }

    fun toggleMapView(show: Boolean) {
        _filterState.value = _filterState.value.copy(showMap = show)
    }

    fun resetFilters() {
        _filterState.value = SearchFilterState()
    }

    // Favorite Actions
    fun toggleFavorite(spaceId: String) {
        viewModelScope.launch {
            repository.toggleFavorite(currentCustomer.value.id, spaceId)
        }
    }

    // Booking Flow
    fun startBookingFlow(space: AdSpaceEntity) {
        _selectedSpace.value = space
        _bookingDraft.value = BookingDraft(
            space = space,
            brandName = currentCustomer.value.company.ifBlank { "Singhal Retail" },
            currentStep = 1
        )
        _customerScreen.value = CustomerScreen.BOOKING_FLOW
    }

    fun updateBookingDates(startDate: String, endDate: String, durationDays: Int) {
        _bookingDraft.value = _bookingDraft.value.copy(
            startDate = startDate,
            endDate = endDate,
            durationDays = durationDays
        )
    }

    fun updateBookingBrand(brandName: String, objective: String, notes: String) {
        _bookingDraft.value = _bookingDraft.value.copy(
            brandName = brandName,
            campaignObjective = objective,
            creativeNotes = notes
        )
    }

    fun toggleBookingService(serviceName: String) {
        val current = _bookingDraft.value.selectedServices.toMutableSet()
        if (current.contains(serviceName)) {
            current.remove(serviceName)
        } else {
            current.add(serviceName)
        }
        _bookingDraft.value = _bookingDraft.value.copy(selectedServices = current)
    }

    fun applyCoupon(code: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val promo = repository.getPromotionByCode(code.trim().uppercase())
            if (promo != null && promo.isActive) {
                _bookingDraft.value = _bookingDraft.value.copy(appliedCoupon = promo)
                onResult(true, "Applied ${promo.discountPercent}% OFF coupon '${promo.code}'!")
            } else {
                onResult(false, "Invalid or expired coupon code.")
            }
        }
    }

    fun removeCoupon() {
        _bookingDraft.value = _bookingDraft.value.copy(appliedCoupon = null)
    }

    fun setBookingPaymentMethod(method: String) {
        _bookingDraft.value = _bookingDraft.value.copy(selectedPaymentMethod = method)
    }

    fun setBookingStep(step: Int) {
        _bookingDraft.value = _bookingDraft.value.copy(currentStep = step)
    }

    fun submitBookingAndPay(onSuccess: (String) -> Unit) {
        val draft = _bookingDraft.value
        val space = draft.space ?: return
        val customer = currentCustomer.value

        viewModelScope.launch {
            _bookingDraft.value = draft.copy(isSubmitting = true)

            // Calculate amounts
            val dailyRate = space.monthlyPrice / 30.0
            val spaceAmount = dailyRate * draft.durationDays

            // Calculate service costs
            var serviceAmount = 0.0
            draft.selectedServices.forEach { srv ->
                when {
                    srv.contains("Flex Printing") -> serviceAmount += (space.totalSqFt * 12.0)
                    srv.contains("Vinyl Printing") -> serviceAmount += (space.totalSqFt * 24.0)
                    srv.contains("Graphic Design") -> serviceAmount += 4500.0
                    srv.contains("Mounting") || srv.contains("Installation") -> serviceAmount += 3500.0
                    srv.contains("Delivery") -> serviceAmount += 1500.0
                    srv.contains("Removal") -> serviceAmount += 1800.0
                    srv.contains("Digital Creative") -> serviceAmount += 6000.0
                }
            }

            val discount = draft.appliedCoupon?.let { promo ->
                val disc = (spaceAmount + serviceAmount) * (promo.discountPercent / 100.0)
                disc.coerceAtMost(promo.maxDiscount)
            } ?: 0.0

            val tax = (spaceAmount + serviceAmount - discount).coerceAtLeast(0.0) * 0.18
            val total = (spaceAmount + serviceAmount - discount).coerceAtLeast(0.0) + tax

            val booking = repository.createBooking(
                space = space,
                customer = customer,
                brandName = draft.brandName,
                campaignObjective = draft.campaignObjective,
                creativeNotes = draft.creativeNotes,
                startDate = draft.startDate,
                endDate = draft.endDate,
                durationDays = draft.durationDays,
                selectedServices = draft.selectedServices.toList(),
                spaceAmount = spaceAmount,
                serviceAmount = serviceAmount,
                discountAmount = discount,
                totalAmount = total
            )

            // Direct process payment to simulate realistic instant Razorpay checkout
            repository.processPayment(
                bookingId = booking.id,
                paymentMethod = draft.selectedPaymentMethod,
                cardOrUpiRef = "UPI_APPTXN"
            )

            _bookingDraft.value = draft.copy(
                isSubmitting = false,
                lastCreatedBookingId = booking.id
            )
            onSuccess(booking.bookingCode)
            _customerScreen.value = CustomerScreen.MY_CAMPAIGNS
        }
    }

    // Owner Space Submission
    fun submitOwnerSpace(
        title: String,
        adType: String,
        city: String,
        area: String,
        landmark: String,
        address: String,
        widthFt: Int,
        heightFt: Int,
        monthlyPrice: Double,
        isDigital: Boolean,
        isIlluminated: Boolean,
        illuminationType: String,
        description: String,
        trafficImpressions: String,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            val totalSqFt = widthFt * heightFt
            val space = AdSpaceEntity(
                id = "",
                ownerId = currentOwner.value.id,
                ownerBusinessName = currentOwner.value.businessName,
                isOwnerVerified = currentOwner.value.verificationStatus == "verified",
                title = title,
                adType = adType,
                city = city,
                area = area,
                location = "$area, $city",
                landmark = landmark,
                address = address,
                latitude = 28.6200 + (Math.random() * 0.04),
                longitude = 77.2800 + (Math.random() * 0.04),
                widthFt = widthFt,
                heightFt = heightFt,
                totalSqFt = totalSqFt,
                isDigital = isDigital,
                isIlluminated = isIlluminated,
                illuminationType = illuminationType,
                monthlyPrice = monthlyPrice,
                minBookingDurationDays = 15,
                availableFrom = "Immediate",
                availableUntil = "31 Dec 2026",
                description = description,
                trafficDailyImpressions = trafficImpressions,
                imageUrl = "https://images.unsplash.com/photo-1542744094-3a31f272c490?auto=format&fit=crop&w=1000&q=80",
                status = "pending" // Under review by Admin
            )
            repository.submitNewSpace(space)
            onComplete()
            _ownerScreen.value = OwnerScreen.MY_SPACES
        }
    }

    // Owner Booking & Proof Management
    fun acceptBooking(bookingId: String) {
        viewModelScope.launch {
            repository.updateBookingAndCampaignStatus(
                bookingId = bookingId,
                newStatus = "confirmed",
                notes = "Owner confirmed booking schedule.",
                actorRole = "owner"
            )
        }
    }

    fun rejectBooking(bookingId: String, reason: String) {
        viewModelScope.launch {
            repository.updateBookingAndCampaignStatus(
                bookingId = bookingId,
                newStatus = "rejected",
                notes = reason.ifBlank { "Space not available on requested dates." },
                actorRole = "owner"
            )
        }
    }

    fun uploadProof(bookingId: String, spaceId: String, photoUrl: String, notes: String, geoTag: String) {
        viewModelScope.launch {
            repository.uploadInstallationProof(
                bookingId = bookingId,
                spaceId = spaceId,
                ownerId = currentOwner.value.id,
                photoUrl = photoUrl,
                geoTag = geoTag,
                notes = notes
            )
        }
    }

    // Admin Operations ("Ankit should be able to run Holding Hub from the Admin Panel")
    fun adminApproveSpace(spaceId: String) {
        viewModelScope.launch {
            repository.updateSpaceStatus(spaceId, "approved")
        }
    }

    fun adminRejectSpace(spaceId: String) {
        viewModelScope.launch {
            repository.updateSpaceStatus(spaceId, "rejected")
        }
    }

    fun adminSuspendSpace(spaceId: String) {
        viewModelScope.launch {
            repository.updateSpaceStatus(spaceId, "suspended")
        }
    }

    fun adminDeleteSpace(spaceId: String) {
        viewModelScope.launch {
            repository.deleteSpace(spaceId)
        }
    }

    fun adminVerifyOwner(ownerId: String) {
        viewModelScope.launch {
            repository.updateOwnerVerification(ownerId, "verified")
        }
    }

    fun adminRejectOwner(ownerId: String) {
        viewModelScope.launch {
            repository.updateOwnerVerification(ownerId, "rejected")
        }
    }

    fun adminSuspendOwner(ownerId: String) {
        viewModelScope.launch {
            repository.updateOwnerVerification(ownerId, "suspended")
        }
    }

    fun adminUpdateBookingStatus(bookingId: String, status: String, notes: String) {
        viewModelScope.launch {
            repository.updateBookingAndCampaignStatus(
                bookingId = bookingId,
                newStatus = status,
                notes = notes,
                actorRole = "admin"
            )
        }
    }

    fun adminReviewProof(proofId: String, approve: Boolean, notes: String) {
        viewModelScope.launch {
            repository.reviewInstallationProof(proofId, approve, notes)
        }
    }

    fun adminCreatePromotion(
        code: String,
        name: String,
        discountPercent: Int,
        maxDiscount: Double,
        minBookingValue: Double
    ) {
        viewModelScope.launch {
            repository.insertPromotion(
                PromotionEntity(
                    code = code.trim().uppercase(),
                    name = name,
                    discountPercent = discountPercent,
                    maxDiscount = maxDiscount,
                    minBookingValue = minBookingValue,
                    startDate = "2026-01-01",
                    endDate = "2026-12-31",
                    usageLimit = 500
                )
            )
        }
    }

    fun adminDeletePromotion(code: String) {
        viewModelScope.launch {
            repository.deletePromotion(code)
        }
    }

    fun adminUpdateServicePrice(service: ServiceProductEntity, newPrice: Double) {
        viewModelScope.launch {
            repository.updateService(service.copy(basePrice = newPrice))
        }
    }

    // AI Ad Studio Generator
    fun generateAiCreatives(
        brandName: String,
        productService: String,
        targetAudience: String,
        city: String,
        campaignObjective: String,
        offer: String,
        toneStyle: String
    ) {
        viewModelScope.launch {
            isGeneratingAi.value = true
            val result = AiAdStudioEngine.generateCreatives(
                brandName = brandName,
                productService = productService,
                targetAudience = targetAudience,
                city = city,
                campaignObjective = campaignObjective,
                offer = offer,
                toneStyle = toneStyle
            )
            _aiStudioResult.value = result
            isGeneratingAi.value = false
        }
    }

    fun updateCustomerProfile(company: String, gst: String, address: String) {
        viewModelScope.launch {
            val updated = currentCustomer.value.copy(
                company = company,
                gstNumber = gst,
                billingAddress = address
            )
            repository.updateUser(updated)
        }
    }

    fun eventsForBooking(bookingId: String): Flow<List<BookingEventEntity>> {
        return repository.getEventsForBooking(bookingId)
    }

    fun proofForBooking(bookingId: String): Flow<InstallationProofEntity?> {
        return repository.getProofForBooking(bookingId)
    }
}
