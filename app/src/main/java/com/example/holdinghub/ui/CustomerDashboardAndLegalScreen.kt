package com.example.holdinghub.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.holdinghub.model.AdSpaceEntity
import com.example.holdinghub.model.FavoriteEntity
import com.example.holdinghub.model.UserEntity
import com.example.ui.theme.*

@Composable
fun CustomerDashboardScreen(
    viewModel: HoldingHubViewModel,
    customer: UserEntity,
    spaces: List<AdSpaceEntity>,
    favorites: List<FavoriteEntity>,
    onSelectSpace: (AdSpaceEntity) -> Unit,
    onBookNow: (AdSpaceEntity) -> Unit,
    onNavigateLegal: () -> Unit
) {
    var company by remember { mutableStateOf(customer.company) }
    var gstNumber by remember { mutableStateOf(customer.gstNumber) }
    var billingAddress by remember { mutableStateOf(customer.billingAddress) }
    var showSavedMessage by remember { mutableStateOf(false) }

    val favSpaces = remember(spaces, favorites) {
        val ids = favorites.map { it.spaceId }.toSet()
        spaces.filter { ids.contains(it.id) }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandNavy900)
            .padding(16.dp)
    ) {
        // User Profile Header
        item {
            Text(text = "ADVERTISER ACCOUNT", color = BrandAmber400, fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = customer.name, color = Color.White, fontWeight = FontWeight.Black, fontSize = 20.sp)
            Text(text = "${customer.email} • ${customer.phone}", color = TextSecondary, fontSize = 12.sp)

            Spacer(modifier = Modifier.height(16.dp))

            // Billing & GST Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = BorderStroke(1.dp, BorderSubtle)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
                    Text(text = "Corporate Billing & GST Details", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = company,
                        onValueChange = { company = it },
                        label = { Text("Brand / Registered Entity Name") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = gstNumber,
                        onValueChange = { gstNumber = it },
                        label = { Text("GSTIN (For 18% Input Tax Credit)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = billingAddress,
                        onValueChange = { billingAddress = it },
                        label = { Text("Registered Billing Address") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            viewModel.updateCustomerProfile(company, gstNumber, billingAddress)
                            showSavedMessage = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandAmber500),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Save Billing Profile", color = BrandNavy900, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    if (showSavedMessage) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Profile updated successfully!", color = BrandSuccess, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Saved Favorites Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Saved Advertising Spaces (${favSpaces.size})", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (favSpaces.isEmpty()) {
            item {
                Text("No saved spaces yet. Tap the heart icon on any hoarding to save it here.", color = TextMuted, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(16.dp))
            }
        } else {
            items(favSpaces) { space ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = BrandNavy800),
                    border = BorderStroke(1.dp, BorderSubtle)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = space.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(text = "${space.area}, ${space.city} • ₹${space.monthlyPrice.toInt()}/mo", color = BrandAmber400, fontSize = 11.sp)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Button(
                                onClick = { onBookNow(space) },
                                colors = ButtonDefaults.buttonColors(containerColor = BrandAmber500),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("Book", color = BrandNavy900, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }

        // Support & Policy Links
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, BorderSubtle)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
                    Text(text = "Help, Compliance & Trust", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    LegalRowItem(title = "Privacy Policy & GDPR", onClick = onNavigateLegal)
                    LegalRowItem(title = "Terms & Advertising Conditions", onClick = onNavigateLegal)
                    LegalRowItem(title = "Refund & Cancellation Policy", onClick = onNavigateLegal)
                    LegalRowItem(title = "Delhi NCR Operations Hub & Contact Desk", onClick = onNavigateLegal)
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun LegalRowItem(title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, color = TextSecondary, fontSize = 13.sp)
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
    }
}

@Composable
fun SupportAndLegalScreen(onBack: () -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandNavy900)
            .padding(16.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Legal, Trust & Helpdesk", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = BorderStroke(1.dp, BorderSubtle)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Text(text = "Delhi NCR Operations Office", color = BrandAmber400, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "Holding Hub Technologies Pvt Ltd", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(text = "Vikas Marg & Patparganj Commercial Hub, East Delhi 110092", color = TextSecondary, fontSize = 12.sp)
                    Text(text = "Noida Extension Office: Sector 18 Commercial Complex, Noida 201301", color = TextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "Support Helpline: +91 98101-HOLDING (98101-46534)", color = BrandCyan400, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text(text = "Email: support@holdinghub.in / ops@holdinghub.in", color = BrandCyan400, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = BrandNavy800),
                border = BorderStroke(1.dp, BorderSubtle)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Text(text = "1. Transparent Marketplace Escrow", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(
                        text = "Advertiser funds are securely held in escrow and released to space owners only after GPS-tagged installation proof is inspected and verified.",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = "2. Cancellation & Refund Policy", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(
                        text = "Cancellations made 7+ days prior to scheduled campaign start receive a 90% refund. Once printing or mounting operations commence, space rental is non-refundable.",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = "3. Municipal Permits & Indemnity", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(
                        text = "All listed hoardings are vetted for municipal corporation permits (MCD / Noida Authority / MMRDA). Holding Hub provides 100% replacement guarantee in case of regulatory disruption.",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
