package com.example.holdinghub.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import coil.compose.AsyncImage
import com.example.holdinghub.model.*
import com.example.ui.theme.*

@Composable
fun AdminPanelScreen(
    viewModel: HoldingHubViewModel,
    spaces: List<AdSpaceEntity>,
    owners: List<OwnerProfileEntity>,
    customers: List<UserEntity>,
    bookings: List<BookingEntity>,
    payments: List<PaymentEntity>,
    proofs: List<InstallationProofEntity>,
    promotions: List<PromotionEntity>,
    services: List<ServiceProductEntity>
) {
    val activeTab by viewModel.adminScreen.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandNavy900)
    ) {
        // Admin Master Header
        Surface(
            color = BrandNavy800,
            tonalElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(BrandAmber500),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = BrandNavy900, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Holding Hub Command Center",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(BrandAmber500.copy(alpha = 0.2f))
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text("Ankit (Super Admin)", color = BrandAmber400, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Text("Full Marketplace Management & Operations", color = TextSecondary, fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Navigation Tabs
                val tabs = listOf(
                    AdminScreen.DASHBOARD to "Dashboard",
                    AdminScreen.SPACES to "Spaces (${spaces.size})",
                    AdminScreen.OWNERS to "Owners (${owners.size})",
                    AdminScreen.BOOKINGS to "Bookings (${bookings.size})",
                    AdminScreen.PAYMENTS to "Payments",
                    AdminScreen.CAMPAIGNS_PROOF to "Proofs (${proofs.size})",
                    AdminScreen.PROMOTIONS to "Promos",
                    AdminScreen.SERVICES to "Services",
                    AdminScreen.REPORTS to "Reports"
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(tabs) { (tab, label) ->
                        val isSelected = activeTab == tab
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) BrandAmber500 else BrandNavy700)
                                .clickable { viewModel.navigateAdmin(tab) }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .testTag("admin_tab_${tab.name.lowercase()}")
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) BrandNavy900 else Color.White,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // Subscreen Routing
        when (activeTab) {
            AdminScreen.DASHBOARD -> AdminDashboardView(spaces, owners, customers, bookings, payments)
            AdminScreen.SPACES -> AdminSpacesView(viewModel, spaces)
            AdminScreen.OWNERS -> AdminOwnersView(viewModel, owners)
            AdminScreen.BOOKINGS -> AdminBookingsView(viewModel, bookings)
            AdminScreen.PAYMENTS -> AdminPaymentsView(payments)
            AdminScreen.CAMPAIGNS_PROOF -> AdminProofsView(viewModel, proofs)
            AdminScreen.PROMOTIONS -> AdminPromotionsView(viewModel, promotions)
            AdminScreen.SERVICES -> AdminServicesView(viewModel, services)
            AdminScreen.REPORTS -> AdminReportsView(bookings, spaces)
            else -> AdminDashboardView(spaces, owners, customers, bookings, payments)
        }
    }
}

@Composable
private fun AdminDashboardView(
    spaces: List<AdSpaceEntity>,
    owners: List<OwnerProfileEntity>,
    customers: List<UserEntity>,
    bookings: List<BookingEntity>,
    payments: List<PaymentEntity>
) {
    val totalRevenue = remember(payments) { payments.filter { it.status == "paid" }.sumOf { it.amount } }
    val liveSpaces = spaces.count { it.status == "approved" }
    val pendingSpaces = spaces.count { it.status == "pending" }
    val verifiedOwners = owners.count { it.verificationStatus == "verified" }
    val pendingOwners = owners.count { it.verificationStatus == "pending" }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Text(text = "MARKETPLACE METRICS", color = BrandAmber400, fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(10.dp))

            // Grid Row 1: Inventory
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AdminStatCard(title = "Total Spaces", value = "${spaces.size}", subtitle = "$liveSpaces Live / $pendingSpaces Pending", modifier = Modifier.weight(1f))
                AdminStatCard(title = "Space Owners", value = "${owners.size}", subtitle = "$verifiedOwners Verified / $pendingOwners Pending", modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Grid Row 2: Business & Revenue
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AdminStatCard(title = "Total Bookings", value = "${bookings.size}", subtitle = "${bookings.count { it.bookingStatus == "confirmed" || it.bookingStatus == "installed" }} Active", modifier = Modifier.weight(1f))
                AdminStatCard(title = "GMV / Revenue", value = "₹${totalRevenue.toInt() / 1000}K", subtitle = "100% Escrow Backed", modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Fast Actions Bar
            Text(text = "OPERATIONAL STATUS", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = BorderStroke(1.dp, BorderSubtle)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = BrandSuccess, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Holding Hub Operations Guard: Online", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "All new spaces are staged in 'Pending' queue until admin approval. Verified badge is assigned only after PAN/GST KYC check.",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun AdminStatCard(title: String, value: String, subtitle: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = BrandNavy800),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, BorderSubtle)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
            Text(text = title, color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, color = BrandAmber400, fontSize = 20.sp, fontWeight = FontWeight.Black)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, color = TextSecondary, fontSize = 10.sp)
        }
    }
}

@Composable
private fun AdminSpacesView(viewModel: HoldingHubViewModel, spaces: List<AdSpaceEntity>) {
    var filterStatus by remember { mutableStateOf("All") }

    val filtered = remember(spaces, filterStatus) {
        if (filterStatus == "All") spaces else spaces.filter { it.status.equals(filterStatus, ignoreCase = true) }
    }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Space Management (${filtered.size})", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf("All", "pending", "approved").forEach { st ->
                        val isSel = filterStatus == st
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSel) BrandAmber500 else BrandNavy700)
                                .clickable { filterStatus = st }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(text = st.replaceFirstChar { it.uppercase() }, color = if (isSel) BrandNavy900 else Color.White, fontSize = 10.sp)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        items(filtered) { space ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = BorderStroke(1.dp, BorderSubtle),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = space.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.weight(1f))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (space.status == "approved") BrandSuccess.copy(alpha = 0.2f) else BrandAmber500.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(text = space.status.uppercase(), color = if (space.status == "approved") BrandSuccess else BrandAmber400, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "${space.area}, ${space.city} • ${space.ownerBusinessName}", color = TextSecondary, fontSize = 11.sp)
                    Text(text = "₹${space.monthlyPrice.toInt()}/month • ${space.widthFt}x${space.heightFt} ft", color = BrandCyan400, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)

                    Spacer(modifier = Modifier.height(8.dp))

                    // Admin Action Buttons
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        if (space.status != "approved") {
                            Button(
                                onClick = { viewModel.adminApproveSpace(space.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = BrandSuccess),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("Approve & Live", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        if (space.status != "rejected") {
                            OutlinedButton(
                                onClick = { viewModel.adminRejectSpace(space.id) },
                                shape = RoundedCornerShape(6.dp),
                                border = BorderStroke(1.dp, BrandError),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("Reject", color = BrandError, fontSize = 11.sp)
                            }
                        }
                        OutlinedButton(
                            onClick = { viewModel.adminDeleteSpace(space.id) },
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, BorderSubtle),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("Delete", color = TextMuted, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
private fun AdminOwnersView(viewModel: HoldingHubViewModel, owners: List<OwnerProfileEntity>) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Text(text = "Owner KYC & Verification (${owners.size})", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(12.dp))
        }

        items(owners) { owner ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = BorderStroke(1.dp, BorderSubtle)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = owner.businessName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (owner.verificationStatus == "verified") BrandSuccess.copy(alpha = 0.2f) else BrandAmber500.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(text = owner.verificationStatus.uppercase(), color = if (owner.verificationStatus == "verified") BrandSuccess else BrandAmber400, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Proprietor: ${owner.contactPerson} • ${owner.phone}", color = TextSecondary, fontSize = 11.sp)
                    Text(text = "PAN/GST: ${owner.panGst} • ${owner.city}", color = TextMuted, fontSize = 11.sp)

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (owner.verificationStatus != "verified") {
                            Button(
                                onClick = { viewModel.adminVerifyOwner(owner.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = BrandSuccess),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("Assign Verified Badge", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        if (owner.verificationStatus != "rejected") {
                            OutlinedButton(
                                onClick = { viewModel.adminRejectOwner(owner.id) },
                                shape = RoundedCornerShape(6.dp),
                                border = BorderStroke(1.dp, BrandError),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("Reject", color = BrandError, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
private fun AdminBookingsView(viewModel: HoldingHubViewModel, bookings: List<BookingEntity>) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Text(text = "All Marketplace Bookings (${bookings.size})", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(12.dp))
        }

        items(bookings) { booking ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = BorderStroke(1.dp, BorderSubtle)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "#${booking.bookingCode}", color = BrandAmber400, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(text = "Total: ₹${booking.totalAmount.toInt()}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "${booking.brandName} (${booking.customerName})", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Text(text = "Space: ${booking.spaceTitle} • ${booking.startDate} to ${booking.endDate}", color = TextSecondary, fontSize = 11.sp)
                    Text(text = "Status: ${booking.bookingStatus} • Payment: ${booking.paymentStatus}", color = BrandCyan400, fontSize = 11.sp)

                    Spacer(modifier = Modifier.height(8.dp))

                    // Status Transition Buttons
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("printing", "installed", "campaign_live", "completed").forEach { st ->
                            OutlinedButton(
                                onClick = { viewModel.adminUpdateBookingStatus(booking.id, st, "Updated by Admin Ankit") },
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(st.replace("_", " ").take(10), fontSize = 10.sp, color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
private fun AdminPaymentsView(payments: List<PaymentEntity>) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Text(text = "Payment Audit & Transaction Ledger (${payments.size})", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(12.dp))
        }

        items(payments) { p ->
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
                    Column {
                        Text(text = p.transactionRef, color = BrandAmber400, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(text = "Booking #${p.bookingCode} • ${p.paymentMethod}", color = TextSecondary, fontSize = 11.sp)
                        Text(text = p.gateway, color = TextMuted, fontSize = 10.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "₹${p.amount.toInt()}", color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
                        Text(text = p.status.uppercase(), color = BrandSuccess, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
private fun AdminProofsView(viewModel: HoldingHubViewModel, proofs: List<InstallationProofEntity>) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Text(text = "Installation Proof Verification (${proofs.size})", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(12.dp))
        }

        items(proofs) { proof ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = BorderStroke(1.dp, BorderSubtle)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
                    Text(text = "Geotag: ${proof.geoTag}", color = BrandAmber400, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Site Notes: ${proof.notes}", color = Color.White, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { viewModel.adminReviewProof(proof.id, true, "Approved by Ankit Yadav") },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandSuccess),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("Approve Proof", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        OutlinedButton(
                            onClick = { viewModel.adminReviewProof(proof.id, false, "Image not clear") },
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, BrandError),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("Reject", color = BrandError, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
private fun AdminPromotionsView(viewModel: HoldingHubViewModel, promotions: List<PromotionEntity>) {
    var newCode by remember { mutableStateOf("") }
    var newDiscount by remember { mutableStateOf("15") }
    var newMaxDisc by remember { mutableStateOf("10000") }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Text(text = "Promotional Offers & Coupons", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(10.dp))

            // Add promo card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BrandNavy800),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, BorderSubtle)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                    Text(text = "+ Create Promotional Coupon", color = BrandAmber400, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = newCode,
                            onValueChange = { newCode = it },
                            label = { Text("Code") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                        )
                        OutlinedTextField(
                            value = newDiscount,
                            onValueChange = { newDiscount = it },
                            label = { Text("Discount %") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            if (newCode.isNotBlank()) {
                                viewModel.adminCreatePromotion(
                                    code = newCode,
                                    name = "$newCode Campaign Offer",
                                    discountPercent = newDiscount.toIntOrNull() ?: 10,
                                    maxDiscount = newMaxDisc.toDoubleOrNull() ?: 5000.0,
                                    minBookingValue = 20000.0
                                )
                                newCode = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandAmber500),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("Add Coupon", color = BrandNavy900, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        items(promotions) { promo ->
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
                    Column {
                        Text(text = promo.code, color = BrandAmber400, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = "${promo.discountPercent}% OFF (Max ₹${promo.maxDiscount.toInt()})", color = Color.White, fontSize = 12.sp)
                    }
                    IconButton(onClick = { viewModel.adminDeletePromotion(promo.code) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = BrandError)
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
private fun AdminServicesView(viewModel: HoldingHubViewModel, services: List<ServiceProductEntity>) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Text(text = "Turnkey Service Pricing & Margin Config", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(12.dp))
        }

        items(services) { service ->
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
                        Text(text = service.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(text = "${service.category} • ${service.description}", color = TextSecondary, fontSize = 11.sp)
                    }
                    Text(
                        text = "₹${service.basePrice.toInt()} ${service.unit}",
                        color = BrandAmber400,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
private fun AdminReportsView(bookings: List<BookingEntity>, spaces: List<AdSpaceEntity>) {
    var selectedRange by remember { mutableStateOf("30 Days") }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Text(text = "BUSINESS INTELLIGENCE & REPORTS", color = BrandAmber400, fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Market Performance & City Trends", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)

            Spacer(modifier = Modifier.height(10.dp))

            // Date Range Filter
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("Today", "7 Days", "30 Days", "This Month").forEach { r ->
                    val isSel = selectedRange == r
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSel) BrandAmber500 else BrandNavy700)
                            .clickable { selectedRange = r }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Text(text = r, color = if (isSel) BrandNavy900 else Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Top Booked Locations (Delhi NCR & Metros)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))

            val locations = listOf(
                "Laxmi Nagar Vikas Marg (East Delhi)" to "18 Bookings (₹11.2L GMV)",
                "Preet Vihar Commercial Corner" to "14 Bookings (₹8.4L GMV)",
                "Noida Sector 18 Mega LED Spectacular" to "12 Bookings (₹17.4L GMV)",
                "Mayur Vihar Phase 1 DND Approach" to "9 Bookings (₹7.6L GMV)",
                "Mumbai Bandra Western Express Highway" to "6 Bookings (₹16.8L GMV)"
            )

            locations.forEach { (loc, stat) ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    border = BorderStroke(1.dp, BorderSubtle)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = loc, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        Text(text = stat, color = BrandAmber400, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
