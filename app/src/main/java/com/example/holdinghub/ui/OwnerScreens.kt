package com.example.holdinghub.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.holdinghub.model.AdSpaceEntity
import com.example.holdinghub.model.BookingEntity
import com.example.holdinghub.model.OwnerProfileEntity
import com.example.ui.theme.*

@Composable
fun OwnerPortalScreen(
    viewModel: HoldingHubViewModel,
    owner: OwnerProfileEntity,
    ownerSpaces: List<AdSpaceEntity>,
    allBookings: List<BookingEntity>
) {
    val activeScreen by viewModel.ownerScreen.collectAsState()
    val ownerBookings = remember(allBookings, owner.id) {
        allBookings.filter { it.ownerId == owner.id }
    }

    var selectedBookingForProof by remember { mutableStateOf<BookingEntity?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandNavy900)
    ) {
        // Owner Profile Header & Verification Status
        Surface(
            color = BrandNavy800,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = owner.businessName,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            if (owner.verificationStatus == "verified") {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "Verified",
                                    tint = BrandSuccess,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Text(
                            text = "Proprietor: ${owner.contactPerson} • ${owner.city}",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (owner.verificationStatus == "verified") BrandSuccess.copy(alpha = 0.2f)
                                else BrandAmber500.copy(alpha = 0.2f)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (owner.verificationStatus == "verified") "Verified Owner" else "Verification: ${owner.verificationStatus.replaceFirstChar { it.uppercase() }}",
                            color = if (owner.verificationStatus == "verified") BrandSuccess else BrandAmber400,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Owner Navigation Tabs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OwnerNavPill(
                        label = "Overview",
                        isSelected = activeScreen == OwnerScreen.DASHBOARD,
                        onClick = { viewModel.navigateOwner(OwnerScreen.DASHBOARD) }
                    )
                    OwnerNavPill(
                        label = "My Spaces",
                        isSelected = activeScreen == OwnerScreen.MY_SPACES,
                        onClick = { viewModel.navigateOwner(OwnerScreen.MY_SPACES) }
                    )
                    OwnerNavPill(
                        label = "+ Add Space",
                        isSelected = activeScreen == OwnerScreen.ADD_SPACE,
                        onClick = { viewModel.navigateOwner(OwnerScreen.ADD_SPACE) }
                    )
                    OwnerNavPill(
                        label = "Bookings",
                        isSelected = activeScreen == OwnerScreen.BOOKINGS,
                        onClick = { viewModel.navigateOwner(OwnerScreen.BOOKINGS) }
                    )
                }
            }
        }

        // Subscreens
        when (activeScreen) {
            OwnerScreen.DASHBOARD -> {
                OwnerDashboardTab(
                    owner = owner,
                    ownerSpaces = ownerSpaces,
                    bookings = ownerBookings,
                    onNavigateAdd = { viewModel.navigateOwner(OwnerScreen.ADD_SPACE) },
                    onNavigateBookings = { viewModel.navigateOwner(OwnerScreen.BOOKINGS) }
                )
            }
            OwnerScreen.MY_SPACES -> {
                OwnerMySpacesTab(
                    spaces = ownerSpaces,
                    onAddNew = { viewModel.navigateOwner(OwnerScreen.ADD_SPACE) }
                )
            }
            OwnerScreen.ADD_SPACE -> {
                OwnerAddSpaceTab(
                    onSubmit = { title, adType, city, area, landmark, address, width, height, price, isDigital, isIlluminated, illuminationType, desc, traffic ->
                        viewModel.submitOwnerSpace(
                            title = title,
                            adType = adType,
                            city = city,
                            area = area,
                            landmark = landmark,
                            address = address,
                            widthFt = width,
                            heightFt = height,
                            monthlyPrice = price,
                            isDigital = isDigital,
                            isIlluminated = isIlluminated,
                            illuminationType = illuminationType,
                            description = desc,
                            trafficImpressions = traffic
                        ) {
                            // Completed callback
                        }
                    }
                )
            }
            OwnerScreen.BOOKINGS -> {
                OwnerBookingsTab(
                    bookings = ownerBookings,
                    onAccept = { bId -> viewModel.acceptBooking(bId) },
                    onReject = { bId -> viewModel.rejectBooking(bId, "Space conflict") },
                    onUploadProof = { b -> selectedBookingForProof = b }
                )
            }
            else -> {
                OwnerDashboardTab(
                    owner = owner,
                    ownerSpaces = ownerSpaces,
                    bookings = ownerBookings,
                    onNavigateAdd = { viewModel.navigateOwner(OwnerScreen.ADD_SPACE) },
                    onNavigateBookings = { viewModel.navigateOwner(OwnerScreen.BOOKINGS) }
                )
            }
        }
    }

    // Proof Upload Dialog for Owner
    if (selectedBookingForProof != null) {
        val b = selectedBookingForProof!!
        var proofPhotoUrl by remember { mutableStateOf("https://images.unsplash.com/photo-1542744094-3a31f272c490?auto=format&fit=crop&w=1000&q=80") }
        var proofNotes by remember { mutableStateOf("Mounted at 04:00 AM. 6 halogen spotlights verified. Tension cables adjusted for wind loads.") }
        var proofGeo by remember { mutableStateOf("28.6304° N, 77.2773° E (Laxmi Nagar Vikas Marg Site)") }

        AlertDialog(
            onDismissRequest = { selectedBookingForProof = null },
            title = { Text("Upload Installation Proof", color = Color.White) },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Campaign: ${b.brandName} (#${b.bookingCode})", color = BrandAmber400, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = proofPhotoUrl,
                        onValueChange = { proofPhotoUrl = it },
                        label = { Text("Site Photo URL") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = proofGeo,
                        onValueChange = { proofGeo = it },
                        label = { Text("GPS Coordinates / Geotag") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = proofNotes,
                        onValueChange = { proofNotes = it },
                        label = { Text("Installation Notes & Rigging Check") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.uploadProof(
                            bookingId = b.id,
                            spaceId = b.spaceId,
                            photoUrl = proofPhotoUrl,
                            notes = proofNotes,
                            geoTag = proofGeo
                        )
                        selectedBookingForProof = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandAmber500)
                ) {
                    Text("Submit Proof for Admin Verification", color = BrandNavy900, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedBookingForProof = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = BrandNavy800
        )
    }
}

@Composable
private fun OwnerNavPill(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (isSelected) BrandAmber500 else BrandNavy700)
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            color = if (isSelected) BrandNavy900 else Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun OwnerDashboardTab(
    owner: OwnerProfileEntity,
    ownerSpaces: List<AdSpaceEntity>,
    bookings: List<BookingEntity>,
    onNavigateAdd: () -> Unit,
    onNavigateBookings: () -> Unit
) {
    val totalEarnings = remember(bookings) {
        bookings.filter { it.paymentStatus == "paid" }.sumOf { it.spaceAmount }
    }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Text(text = "BUSINESS OVERVIEW", color = BrandAmber400, fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(10.dp))

            // Metric Cards
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OwnerMetricCard(title = "Total Spaces", value = "${ownerSpaces.size}", modifier = Modifier.weight(1f))
                OwnerMetricCard(title = "Active Bookings", value = "${bookings.count { it.bookingStatus != "completed" }}", modifier = Modifier.weight(1f))
                OwnerMetricCard(title = "Total Earnings", value = "₹${totalEarnings.toInt() / 1000}K", modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, BorderSubtle)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Text(text = "Bank & Settlement Account", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Account: ${owner.bankAccount}", color = TextSecondary, fontSize = 12.sp)
                    Text(text = "IFSC: ${owner.bankIfsc}", color = TextSecondary, fontSize = 12.sp)
                    Text(text = "PAN/GST: ${owner.panGst}", color = TextSecondary, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onNavigateAdd,
                    colors = ButtonDefaults.buttonColors(containerColor = BrandAmber500),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = BrandNavy900)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Space", color = BrandNavy900, fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = onNavigateBookings,
                    border = BorderStroke(1.dp, BrandCyan400),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("View Bookings", color = BrandCyan400, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun OwnerMetricCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = BrandNavy800),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, BorderSubtle)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
            Text(text = title, color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, color = BrandAmber400, fontSize = 18.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun OwnerMySpacesTab(
    spaces: List<AdSpaceEntity>,
    onAddNew: () -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "My Advertising Spaces (${spaces.size})", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Button(
                    onClick = onAddNew,
                    colors = ButtonDefaults.buttonColors(containerColor = BrandAmber500),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("+ Add Space", color = BrandNavy900, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        items(spaces) { space ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = BorderStroke(1.dp, BorderSubtle)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = space.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(text = "${space.area}, ${space.city} • ${space.widthFt}x${space.heightFt} ft", color = TextSecondary, fontSize = 11.sp)
                        Text(text = "₹${space.monthlyPrice.toInt()}/month", color = BrandAmber400, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                when (space.status) {
                                    "approved" -> BrandSuccess.copy(alpha = 0.2f)
                                    "pending" -> BrandAmber500.copy(alpha = 0.2f)
                                    else -> BrandError.copy(alpha = 0.2f)
                                }
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = when (space.status) {
                                "approved" -> "Live"
                                "pending" -> "In Review"
                                else -> space.status
                            },
                            color = when (space.status) {
                                "approved" -> BrandSuccess
                                "pending" -> BrandAmber400
                                else -> BrandError
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
private fun OwnerAddSpaceTab(
    onSubmit: (String, String, String, String, String, String, Int, Int, Double, Boolean, Boolean, String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var adType by remember { mutableStateOf("Billboard") }
    var city by remember { mutableStateOf("Delhi NCR") }
    var area by remember { mutableStateOf("Preet Vihar") }
    var landmark by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var widthStr by remember { mutableStateOf("30") }
    var heightStr by remember { mutableStateOf("15") }
    var priceStr by remember { mutableStateOf("55000") }
    var isDigital by remember { mutableStateOf(false) }
    var isIlluminated by remember { mutableStateOf(true) }
    var illuminationType by remember { mutableStateOf("Front-lit LED") }
    var description by remember { mutableStateOf("") }
    var traffic by remember { mutableStateOf("120,000+ Vehicles/Day") }
    var submittedNotice by remember { mutableStateOf(false) }

    val adTypes = listOf("Billboard", "Hoarding", "Unipole", "LED/Digital billboard", "Bus shelter", "Pole kiosk")

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Text(text = "LIST NEW ADVERTISING SPACE", color = BrandAmber400, fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Submit Hoarding for Verification", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Note: New spaces are submitted to Holding Hub operations for approval before going live.",
                color = TextSecondary,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Space Title (e.g. Vikas Marg Metro Billboard)") },
                modifier = Modifier.fillMaxWidth().testTag("add_space_title_input"),
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Ad Type Selector
            Text(text = "Advertising Type", color = TextMuted, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                adTypes.forEach { type ->
                    val isSel = adType == type
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSel) BrandAmber500 else BrandNavy800)
                            .clickable { adType = type }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Text(text = type, color = if (isSel) BrandNavy900 else Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("City (e.g. Delhi NCR, Noida)") },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )
                OutlinedTextField(
                    value = area,
                    onValueChange = { area = it },
                    label = { Text("Area (e.g. Laxmi Nagar)") },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = landmark,
                onValueChange = { landmark = it },
                label = { Text("Prominent Landmark & Metro Gate") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = widthStr,
                    onValueChange = { widthStr = it },
                    label = { Text("Width (ft)") },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )
                OutlinedTextField(
                    value = heightStr,
                    onValueChange = { heightStr = it },
                    label = { Text("Height (ft)") },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )
                OutlinedTextField(
                    value = priceStr,
                    onValueChange = { priceStr = it },
                    label = { Text("Monthly Rent (₹)") },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = isDigital,
                    onCheckedChange = { isDigital = it },
                    colors = CheckboxDefaults.colors(checkedColor = BrandCyan500)
                )
                Text(text = "Digital LED Screen", color = Color.White, fontSize = 12.sp)

                Spacer(modifier = Modifier.width(16.dp))

                Checkbox(
                    checked = isIlluminated,
                    onCheckedChange = { isIlluminated = it },
                    colors = CheckboxDefaults.colors(checkedColor = BrandAmber500)
                )
                Text(text = "Illuminated at Night", color = Color.White, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = traffic,
                onValueChange = { traffic = it },
                label = { Text("Daily Traffic / Commuter Reach") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description & Sightline Highlights") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val w = widthStr.toIntOrNull() ?: 30
                        val h = heightStr.toIntOrNull() ?: 15
                        val pr = priceStr.toDoubleOrNull() ?: 50000.0
                        onSubmit(title, adType, city, area, landmark, address, w, h, pr, isDigital, isIlluminated, illuminationType, description, traffic)
                        submittedNotice = true
                    }
                },
                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("submit_space_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = BrandAmber500),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Submit for Admin Review", color = BrandNavy900, fontWeight = FontWeight.Bold)
            }

            if (submittedNotice) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Space submitted successfully! Status set to 'Pending Review'. Ankit will review in Admin Panel.",
                    color = BrandSuccess,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun OwnerBookingsTab(
    bookings: List<BookingEntity>,
    onAccept: (String) -> Unit,
    onReject: (String) -> Unit,
    onUploadProof: (BookingEntity) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Text(text = "Incoming & Active Bookings (${bookings.size})", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (bookings.isEmpty()) {
            item {
                Text(text = "No bookings received yet.", color = TextSecondary, fontSize = 13.sp)
            }
        } else {
            items(bookings) { booking ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    border = BorderStroke(1.dp, BorderSubtle)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = booking.bookingCode, color = BrandAmber400, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(BrandSuccess.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(text = booking.bookingStatus.replaceFirstChar { it.uppercase() }, color = BrandSuccess, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "${booking.brandName} - ${booking.spaceTitle}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = "Dates: ${booking.startDate} to ${booking.endDate} (${booking.durationDays} Days)", color = TextSecondary, fontSize = 12.sp)
                        Text(text = "Payout: ₹${booking.spaceAmount.toInt()} (Paid via Escrow)", color = BrandCyan400, fontWeight = FontWeight.Bold, fontSize = 12.sp)

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { onUploadProof(booking) },
                                colors = ButtonDefaults.buttonColors(containerColor = BrandAmber500),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.CloudUpload, contentDescription = null, tint = BrandNavy900, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Upload Proof", color = BrandNavy900, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}
