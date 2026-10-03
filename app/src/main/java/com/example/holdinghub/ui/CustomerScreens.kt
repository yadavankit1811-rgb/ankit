package com.example.holdinghub.ui

import androidx.compose.animation.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.holdinghub.model.*
import com.example.ui.theme.*

@Composable
fun CustomerHomeScreen(
    viewModel: HoldingHubViewModel,
    spaces: List<AdSpaceEntity>,
    favorites: List<FavoriteEntity>,
    onSelectSpace: (AdSpaceEntity) -> Unit,
    onBookNow: (AdSpaceEntity) -> Unit,
    onNavigateSearch: () -> Unit,
    onListYourSpace: () -> Unit
) {
    var searchKeyword by remember { mutableStateOf("") }
    var selectedCityTab by remember { mutableStateOf("Delhi NCR") }
    var selectedAdTypeFilter by remember { mutableStateOf("All") }

    val keyboardController = androidx.compose.ui.platform.LocalSoftwareKeyboardController.current

    val cities = listOf("All", "Delhi NCR", "Noida", "Mumbai", "Bangalore")
    val adTypes = listOf("All", "Billboard", "Unipole", "LED/Digital billboard", "Hoarding", "Bus shelter", "Pole kiosk")

    val favIds = remember(favorites) { favorites.map { it.spaceId }.toSet() }

    val filteredSpaces = remember(spaces, searchKeyword, selectedCityTab, selectedAdTypeFilter) {
        spaces.filter { space ->
            (searchKeyword.isBlank() || space.title.contains(searchKeyword, ignoreCase = true) ||
                    space.area.contains(searchKeyword, ignoreCase = true) ||
                    space.location.contains(searchKeyword, ignoreCase = true) ||
                    space.city.contains(searchKeyword, ignoreCase = true)) &&
            (selectedCityTab == "All" || space.city.contains(selectedCityTab, ignoreCase = true)) &&
            (selectedAdTypeFilter == "All" || space.adType.equals(selectedAdTypeFilter, ignoreCase = true))
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandNavy900)
    ) {
        // Hero Section
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = BrandNavy800),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BrandAmber500.copy(alpha = 0.4f), BorderSubtle)))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(BrandAmber500)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "INDIA’S LOCAL ADVERTISING MARKETPLACE",
                            color = BrandAmber400,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Put Your Brand Where People See It.",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        lineHeight = 30.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Discover prime hoardings & billboards, connect directly with verified local owners, and run high-impact campaigns transparently.",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onNavigateSearch,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandAmber500),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("hero_find_space_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = BrandNavy900,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Find Space",
                                color = BrandNavy900,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        OutlinedButton(
                            onClick = onListYourSpace,
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, BrandCyan400),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("hero_list_space_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddBusiness,
                                contentDescription = null,
                                tint = BrandCyan400,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "List Your Space",
                                color = BrandCyan400,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        // Trust points banner
        item {
            TrustPointsBanner()
        }

        // Search Bar & Filter Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Search Input Box
                OutlinedTextField(
                    value = searchKeyword,
                    onValueChange = { searchKeyword = it },
                    placeholder = {
                        Text(
                            "Search city, area (Laxmi Nagar, Noida...), landmark...",
                            color = TextMuted,
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = BrandAmber500
                        )
                    },
                    trailingIcon = {
                        if (searchKeyword.isNotBlank()) {
                            IconButton(onClick = { searchKeyword = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = TextSecondary
                                )
                            }
                        }
                    },
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        imeAction = androidx.compose.ui.text.input.ImeAction.Search
                    ),
                    keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                        onSearch = { keyboardController?.hide() }
                    ),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = BrandNavy800,
                        unfocusedContainerColor = BrandNavy800,
                        focusedBorderColor = BrandAmber500,
                        unfocusedBorderColor = BorderSubtle,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_search_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // City Filter Tabs
                Text(
                    text = "SELECT MARKET / CITY",
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(cities) { city ->
                        val isSelected = selectedCityTab == city
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCityTab = city },
                            label = {
                                Text(
                                    text = if (city == "Delhi NCR") "Delhi NCR (Focus Hub)" else city,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BrandAmber500,
                                selectedLabelColor = BrandNavy900,
                                containerColor = BrandNavy800,
                                labelColor = TextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                selectedBorderColor = BrandAmber500,
                                borderColor = BorderSubtle
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Advertising Type Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(adTypes) { type ->
                        val isSelected = selectedAdTypeFilter == type
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) BrandCyan500 else BrandNavy700)
                                .clickable { selectedAdTypeFilter = type }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = type,
                                color = if (isSelected) BrandNavy900 else Color.White,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // Section Title: Featured Inventory
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "High-Impact Advertising Spaces",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Verified inventory with transparent rates",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(BrandNavy800)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${filteredSpaces.size} Available",
                        color = BrandAmber400,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Listings
        if (filteredSpaces.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.SearchOff,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No spaces found matching your search.",
                        color = TextSecondary,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    TextButton(onClick = {
                        searchKeyword = ""
                        selectedCityTab = "All"
                        selectedAdTypeFilter = "All"
                    }) {
                        Text("Reset Filters", color = BrandAmber400)
                    }
                }
            }
        } else {
            items(filteredSpaces) { space ->
                val isFav = favIds.contains(space.id)
                AdSpaceCard(
                    space = space,
                    isFavorite = isFav,
                    onFavoriteToggle = { viewModel.toggleFavorite(space.id) },
                    onViewDetails = { onSelectSpace(space) },
                    onBookNow = { onBookNow(space) }
                )
            }
        }

        // Footer spacer
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun SearchAndInventoryScreen(
    viewModel: HoldingHubViewModel,
    spaces: List<AdSpaceEntity>,
    favorites: List<FavoriteEntity>,
    onSelectSpace: (AdSpaceEntity) -> Unit,
    onBookNow: (AdSpaceEntity) -> Unit
) {
    val filterState by viewModel.filterState.collectAsState()
    val favIds = remember(favorites) { favorites.map { it.spaceId }.toSet() }
    val keyboardController = androidx.compose.ui.platform.LocalSoftwareKeyboardController.current

    var selectedMapSpaceId by remember { mutableStateOf<String?>(null) }

    val filtered = remember(spaces, filterState) {
        spaces.filter { sp ->
            val matchQuery = filterState.query.isBlank() ||
                    sp.title.contains(filterState.query, ignoreCase = true) ||
                    sp.area.contains(filterState.query, ignoreCase = true) ||
                    sp.location.contains(filterState.query, ignoreCase = true) ||
                    sp.city.contains(filterState.query, ignoreCase = true) ||
                    sp.landmark.contains(filterState.query, ignoreCase = true)

            val matchCity = filterState.selectedCity == "All Cities" || sp.city.contains(filterState.selectedCity, ignoreCase = true)
            val matchArea = filterState.selectedArea == "All Areas" || sp.area.contains(filterState.selectedArea, ignoreCase = true)
            val matchType = filterState.selectedAdType == "All Types" || sp.adType.equals(filterState.selectedAdType, ignoreCase = true)
            val matchBudget = sp.monthlyPrice in filterState.minBudget..filterState.maxBudget
            val matchDigital = !filterState.isDigitalOnly || sp.isDigital
            val matchIlluminated = !filterState.isIlluminatedOnly || sp.isIlluminated
            val matchVerified = !filterState.verifiedOwnerOnly || sp.isOwnerVerified

            matchQuery && matchCity && matchArea && matchType && matchBudget && matchDigital && matchIlluminated && matchVerified
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandNavy900)
    ) {
        // Header Search Controls
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BrandNavy800)
                    .padding(16.dp)
            ) {
                Text(
                    text = "SEARCH OUTDOOR INVENTORY",
                    color = BrandAmber400,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = filterState.query,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    placeholder = { Text("Search by City, Area, Road, Landmark, Pin code...", color = TextMuted, fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = BrandAmber500) },
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        imeAction = androidx.compose.ui.text.input.ImeAction.Search
                    ),
                    keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                        onSearch = { keyboardController?.hide() }
                    ),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = BrandNavy900,
                        unfocusedContainerColor = BrandNavy900,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = BrandAmber500,
                        unfocusedBorderColor = BorderSubtle
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("inventory_search_bar")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Fast Filter Pills
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = filterState.isDigitalOnly,
                            onClick = { viewModel.toggleDigitalOnly(!filterState.isDigitalOnly) },
                            label = { Text("LED Digital", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BrandCyan500,
                                selectedLabelColor = BrandNavy900,
                                containerColor = BrandNavy700,
                                labelColor = TextSecondary
                            )
                        )
                        FilterChip(
                            selected = filterState.verifiedOwnerOnly,
                            onClick = { viewModel.toggleVerifiedOnly(!filterState.verifiedOwnerOnly) },
                            label = { Text("Verified Only", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BrandSuccess,
                                selectedLabelColor = Color.White,
                                containerColor = BrandNavy700,
                                labelColor = TextSecondary
                            )
                        )
                    }

                    // Map Toggle
                    IconButton(
                        onClick = { viewModel.toggleMapView(!filterState.showMap) },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (filterState.showMap) BrandAmber500 else BrandNavy700)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Map,
                            contentDescription = "Toggle Map",
                            tint = if (filterState.showMap) BrandNavy900 else Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Map View (if toggled)
        if (filterState.showMap) {
            item {
                InteractiveMapDisplay(
                    spaces = filtered,
                    selectedSpaceId = selectedMapSpaceId,
                    onSelectSpace = { sp ->
                        selectedMapSpaceId = sp.id
                        onSelectSpace(sp)
                    }
                )
            }
        }

        // Result count header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${filtered.size} Advertising Spaces Found",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                if (filterState.query.isNotBlank() || filterState.isDigitalOnly || filterState.verifiedOwnerOnly) {
                    TextButton(onClick = { viewModel.resetFilters() }) {
                        Text("Clear All", color = BrandAmber400, fontSize = 12.sp)
                    }
                }
            }
        }

        // Listings
        items(filtered) { space ->
            val isFav = favIds.contains(space.id)
            AdSpaceCard(
                space = space,
                isFavorite = isFav,
                onFavoriteToggle = { viewModel.toggleFavorite(space.id) },
                onViewDetails = { onSelectSpace(space) },
                onBookNow = { onBookNow(space) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun SpaceDetailsScreen(
    space: AdSpaceEntity,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    onBookNow: () -> Unit,
    onBack: () -> Unit
) {
    var showEnquiryDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandNavy900)
    ) {
        // Top App Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BrandNavy800)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Text(
                    text = "Space Details",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onFavoriteToggle) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) BrandError else Color.White
                    )
                }
            }
        }

        // Hero Image
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            ) {
                AsyncImage(
                    model = space.imageUrl,
                    contentDescription = space.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, BrandNavy900)
                            )
                        )
                )

                // Overlays
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(BrandAmber500)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = space.adType,
                            color = BrandNavy900,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                    if (space.isOwnerVerified) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(BrandSuccess)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Verified Owner",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // Title & Location
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = space.title,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = BrandAmber500, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${space.location}, ${space.city}",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                }

                if (space.landmark.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.NearMe, contentDescription = null, tint = BrandCyan400, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Landmark: ${space.landmark}",
                            color = BrandCyan400,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Specs Grid
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = BorderStroke(1.dp, BorderSubtle)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
                    Text(
                        text = "TECHNICAL SPECIFICATIONS",
                        color = BrandAmber400,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        SpecItem(label = "Dimensions", value = "${space.widthFt} W x ${space.heightFt} H ft", modifier = Modifier.weight(1f))
                        SpecItem(label = "Total Display Area", value = "${space.totalSqFt} sq.ft", modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        SpecItem(label = "Illumination", value = space.illuminationType, modifier = Modifier.weight(1f))
                        SpecItem(label = "Traffic Density", value = space.trafficDailyImpressions, modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        SpecItem(label = "Facing Direction", value = space.facingDirection, modifier = Modifier.weight(1f))
                        SpecItem(label = "Minimum Booking", value = "${space.minBookingDurationDays} Days", modifier = Modifier.weight(1f))
                    }

                    // Digital Specifics
                    if (space.isDigital) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Divider(color = BorderSubtle)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "DIGITAL SCREEN ATTRIBUTES",
                            color = BrandCyan400,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            SpecItem(label = "Resolution", value = space.screenResolution.ifBlank { "Full HD" }, modifier = Modifier.weight(1f))
                            SpecItem(label = "Slot Duration", value = "${space.slotDurationSec} Seconds", modifier = Modifier.weight(1f))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            SpecItem(label = "Loop Length", value = "${space.loopDurationSec} Sec Cycle", modifier = Modifier.weight(1f))
                            SpecItem(label = "Operating Hours", value = space.operatingHours.ifBlank { "06:00 AM - 11:30 PM" }, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        // Description & Terms
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "About This Advertising Space",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = space.description,
                    color = TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Owner & Agency Profile",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = BrandNavy800),
                    border = BorderStroke(1.dp, BorderSubtle),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(BrandNavy600),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Business, contentDescription = null, tint = BrandAmber500)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = space.ownerBusinessName,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = if (space.isOwnerVerified) "Verified Holding Hub Partner" else "Under Regular Audit",
                                color = if (space.isOwnerVerified) BrandSuccess else TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Terms & Compliance",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = space.terms,
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
        }

        // Bottom CTA Sticky Bar
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = BrandNavy800),
                border = BorderStroke(1.dp, BrandAmber500.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "MONTHLY TARIFF",
                            color = TextMuted,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "₹${space.monthlyPrice.toInt().toString().replace(Regex("(\\d)(?=(\\d{3})+(?!\\d))"), "$1,")}",
                            color = BrandAmber400,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { showEnquiryDialog = true },
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, BorderSubtle)
                        ) {
                            Text("Enquire", color = Color.White, fontSize = 12.sp)
                        }
                        Button(
                            onClick = onBookNow,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandAmber500),
                            modifier = Modifier.testTag("details_book_now_btn")
                        ) {
                            Text("Book Space", color = BrandNavy900, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(60.dp))
        }
    }

    if (showEnquiryDialog) {
        AlertDialog(
            onDismissRequest = { showEnquiryDialog = false },
            title = { Text("Direct Agency Enquiry", color = Color.White) },
            text = {
                Text(
                    "Your enquiry for '${space.title}' has been logged with Holding Hub support desk. Our East Delhi / Noida operations manager will call you within 30 minutes.",
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = { showEnquiryDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandAmber500)
                ) {
                    Text("OK", color = BrandNavy900, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = BrandNavy800
        )
    }
}

@Composable
private fun SpecItem(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = label, color = TextMuted, fontSize = 10.sp)
        Text(text = value, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun BookingFlowScreen(
    viewModel: HoldingHubViewModel,
    onBookingComplete: () -> Unit,
    onCancel: () -> Unit
) {
    val draft by viewModel.bookingDraft.collectAsState()
    val space = draft.space ?: return

    var couponInput by remember { mutableStateOf("") }
    var couponMessage by remember { mutableStateOf<String?>(null) }
    var isCouponSuccess by remember { mutableStateOf(false) }

    // Pricing calculation
    val dailyRate = space.monthlyPrice / 30.0
    val spaceAmount = dailyRate * draft.durationDays

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
        val d = (spaceAmount + serviceAmount) * (promo.discountPercent / 100.0)
        d.coerceAtMost(promo.maxDiscount)
    } ?: 0.0

    val subtotal = (spaceAmount + serviceAmount - discount).coerceAtLeast(0.0)
    val gstAmount = subtotal * 0.18
    val totalAmount = subtotal + gstAmount

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandNavy900)
    ) {
        // Step Indicator Top Bar
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BrandNavy800)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "BOOKING FLOW (STEP ${draft.currentStep} OF 6)",
                        color = BrandAmber400,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp
                    )
                    IconButton(onClick = onCancel, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Cancel", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Progress Bar
                LinearProgressIndicator(
                    progress = { draft.currentStep / 6f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = BrandAmber500,
                    trackColor = BrandNavy700
                )
            }
        }

        when (draft.currentStep) {
            1 -> {
                // Step 1: Space Confirmation
                item {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Text(text = "Step 1: Confirm Advertising Space", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(12.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, BorderSubtle)
                        ) {
                            Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
                                Text(text = space.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "${space.area}, ${space.city}", color = BrandAmber400, fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = "Dimensions: ${space.widthFt}x${space.heightFt} ft (${space.totalSqFt} sq.ft) • ${space.adType}", color = TextSecondary, fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = "Monthly Rent: ₹${space.monthlyPrice.toInt()}/month", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { viewModel.setBookingStep(2) },
                            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("step_1_next_btn"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandAmber500)
                        ) {
                            Text("Next: Select Dates", color = BrandNavy900, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            2 -> {
                // Step 2: Campaign Dates
                item {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Text(text = "Step 2: Campaign Duration & Dates", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Select your desired campaign start and duration.", color = TextSecondary, fontSize = 13.sp)

                        Spacer(modifier = Modifier.height(16.dp))

                        val durations = listOf(15, 30, 60, 90)
                        Text(text = "SELECT DURATION", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            durations.forEach { days ->
                                val isSelected = draft.durationDays == days
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) BrandAmber500 else BrandNavy800)
                                        .border(1.dp, if (isSelected) BrandAmber500 else BorderSubtle, RoundedCornerShape(8.dp))
                                        .clickable {
                                            viewModel.updateBookingDates(
                                                startDate = draft.startDate,
                                                endDate = "2026-11-${days.coerceAtMost(30)}",
                                                durationDays = days
                                            )
                                        }
                                        .padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$days Days",
                                        color = if (isSelected) BrandNavy900 else Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = draft.startDate,
                            onValueChange = { viewModel.updateBookingDates(it, draft.endDate, draft.durationDays) },
                            label = { Text("Campaign Start Date (YYYY-MM-DD)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = BrandAmber500,
                                unfocusedBorderColor = BorderSubtle
                            )
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedButton(
                                onClick = { viewModel.setBookingStep(1) },
                                modifier = Modifier.weight(1f).height(48.dp),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Back", color = Color.White)
                            }
                            Button(
                                onClick = { viewModel.setBookingStep(3) },
                                modifier = Modifier.weight(1f).height(48.dp).testTag("step_2_next_btn"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BrandAmber500)
                            ) {
                                Text("Next: Brand Info", color = BrandNavy900, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            3 -> {
                // Step 3: Brand & Campaign Info
                item {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Text(text = "Step 3: Brand & Campaign Details", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = draft.brandName,
                            onValueChange = { viewModel.updateBookingBrand(it, draft.campaignObjective, draft.creativeNotes) },
                            label = { Text("Brand / Business Name") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = BrandAmber500,
                                unfocusedBorderColor = BorderSubtle
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = draft.campaignObjective,
                            onValueChange = { viewModel.updateBookingBrand(draft.brandName, it, draft.creativeNotes) },
                            label = { Text("Campaign Objective (e.g. Brand Launch, Festive Sale)") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = BrandAmber500,
                                unfocusedBorderColor = BorderSubtle
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = draft.creativeNotes,
                            onValueChange = { viewModel.updateBookingBrand(draft.brandName, draft.campaignObjective, it) },
                            label = { Text("Creative & Design Instructions / Tagline") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = BrandAmber500,
                                unfocusedBorderColor = BorderSubtle
                            )
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedButton(
                                onClick = { viewModel.setBookingStep(2) },
                                modifier = Modifier.weight(1f).height(48.dp),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Back", color = Color.White)
                            }
                            Button(
                                onClick = { viewModel.setBookingStep(4) },
                                modifier = Modifier.weight(1f).height(48.dp).testTag("step_3_next_btn"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BrandAmber500)
                            ) {
                                Text("Next: Services", color = BrandNavy900, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            4 -> {
                // Step 4: Required Services
                item {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Text(text = "Step 4: Select Required Services", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "Choose turnkey services managed end-to-end by Holding Hub.", color = TextSecondary, fontSize = 13.sp)

                        Spacer(modifier = Modifier.height(14.dp))

                        val availableServices = listOf(
                            "Space booking" to "Mandatory municipal display rights",
                            "Flex Printing" to "Heavy duty Star Frontlit 320 GSM flex (₹12/sq.ft)",
                            "Graphic Design" to "High-impact outdoor creative by senior art director (₹4,500)",
                            "Mounting & Installation" to "Rigging crew, safety harness, spotlights alignment (₹3,500)",
                            "Delivery to Site" to "Direct transport & crane access (₹1,500)",
                            "Removal & Dismantle" to "Takedown & eco-friendly recycling after campaign (₹1,800)"
                        )

                        availableServices.forEach { (srvName, srvDesc) ->
                            val isChecked = draft.selectedServices.contains(srvName)
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        if (srvName != "Space booking") {
                                            viewModel.toggleBookingService(srvName)
                                        }
                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isChecked) BrandNavy700 else BrandNavy800
                                ),
                                border = BorderStroke(1.dp, if (isChecked) BrandAmber500 else BorderSubtle),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = isChecked,
                                        onCheckedChange = {
                                            if (srvName != "Space booking") {
                                                viewModel.toggleBookingService(srvName)
                                            }
                                        },
                                        colors = CheckboxDefaults.colors(checkedColor = BrandAmber500)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = srvName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(text = srvDesc, color = TextSecondary, fontSize = 11.sp)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedButton(
                                onClick = { viewModel.setBookingStep(3) },
                                modifier = Modifier.weight(1f).height(48.dp),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Back", color = Color.White)
                            }
                            Button(
                                onClick = { viewModel.setBookingStep(5) },
                                modifier = Modifier.weight(1f).height(48.dp).testTag("step_4_next_btn"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BrandAmber500)
                            ) {
                                Text("Next: Price Breakdown", color = BrandNavy900, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            5 -> {
                // Step 5: Complete Price Breakdown
                item {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Text(text = "Step 5: Complete Price Breakdown", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(14.dp))

                        // Coupon Code Input
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = couponInput,
                                onValueChange = { couponInput = it },
                                placeholder = { Text("Coupon code (e.g. DELHIHUB20)", color = TextMuted, fontSize = 12.sp) },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = BrandAmber500,
                                    unfocusedBorderColor = BorderSubtle
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    viewModel.applyCoupon(couponInput) { success, msg ->
                                        isCouponSuccess = success
                                        couponMessage = msg
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = BrandCyan500),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(52.dp)
                            ) {
                                Text("Apply", color = BrandNavy900, fontWeight = FontWeight.Bold)
                            }
                        }

                        if (couponMessage != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = couponMessage!!,
                                color = if (isCouponSuccess) BrandSuccess else BrandError,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, BorderSubtle)
                        ) {
                            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                                Text(text = "ESTIMATED CAMPAIGN BILLING", color = BrandAmber400, fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 1.sp)
                                Spacer(modifier = Modifier.height(12.dp))

                                BillRow(label = "Space Rental (${draft.durationDays} Days)", amount = spaceAmount)
                                BillRow(label = "Selected Turnkey Services", amount = serviceAmount)
                                if (discount > 0) {
                                    BillRow(label = "Promotional Discount", amount = -discount, isDiscount = true)
                                }
                                BillRow(label = "Subtotal", amount = subtotal)
                                BillRow(label = "GST (18% Statutory Tax)", amount = gstAmount)

                                Spacer(modifier = Modifier.height(8.dp))
                                Divider(color = BorderSubtle)
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "Total Payable Amount", color = Color.White, fontWeight = FontWeight.Black, fontSize = 15.sp)
                                    Text(
                                        text = "₹${totalAmount.toInt().toString().replace(Regex("(\\d)(?=(\\d{3})+(?!\\d))"), "$1,")}",
                                        color = BrandAmber400,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 18.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedButton(
                                onClick = { viewModel.setBookingStep(4) },
                                modifier = Modifier.weight(1f).height(48.dp),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Back", color = Color.White)
                            }
                            Button(
                                onClick = { viewModel.setBookingStep(6) },
                                modifier = Modifier.weight(1f).height(48.dp).testTag("step_5_next_btn"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BrandAmber500)
                            ) {
                                Text("Next: Payment", color = BrandNavy900, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            6 -> {
                // Step 6: Payment Method & Submit
                item {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Text(text = "Step 6: Secure Payment & Confirmation", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "Select payment gateway method to lock space dates and initiate campaign.", color = TextSecondary, fontSize = 13.sp)

                        Spacer(modifier = Modifier.height(14.dp))

                        val methods = listOf(
                            "UPI (Google Pay / PhonePe / Paytm / BHIM)" to Icons.Default.QrCodeScanner,
                            "Net Banking (All Indian Commercial Banks)" to Icons.Default.AccountBalance,
                            "Corporate Credit / Debit Card" to Icons.Default.CreditCard,
                            "NEFT / RTGS Transfer" to Icons.Default.ReceiptLong
                        )

                        methods.forEach { (mName, mIcon) ->
                            val isSelected = draft.selectedPaymentMethod.startsWith(mName.take(3))
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable { viewModel.setBookingPaymentMethod(mName.take(10)) },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) BrandNavy700 else BrandNavy800
                                ),
                                border = BorderStroke(1.dp, if (isSelected) BrandAmber500 else BorderSubtle),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = mIcon, contentDescription = null, tint = if (isSelected) BrandAmber500 else TextSecondary)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(text = mName, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, modifier = Modifier.weight(1f))
                                    if (isSelected) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BrandAmber500)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Trust Guarantee badge
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(BrandNavy800)
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = BrandSuccess, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "100% Escrow Protection: Payout is transferred to space owner only after geotagged installation photo proof is verified.",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                viewModel.submitBookingAndPay { code ->
                                    onBookingComplete()
                                }
                            },
                            enabled = !draft.isSubmitting,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("pay_and_confirm_btn"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandAmber500)
                        ) {
                            if (draft.isSubmitting) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = BrandNavy900)
                            } else {
                                Text(
                                    text = "Pay ₹${totalAmount.toInt()} & Confirm Booking",
                                    color = BrandNavy900,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BillRow(label: String, amount: Double, isDiscount: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = TextSecondary, fontSize = 12.sp)
        Text(
            text = if (isDiscount) "-₹${Math.abs(amount).toInt()}" else "₹${amount.toInt()}",
            color = if (isDiscount) BrandSuccess else Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun CampaignManagementScreen(
    viewModel: HoldingHubViewModel,
    campaigns: List<CampaignEntity>,
    allProofs: List<InstallationProofEntity>
) {
    var inspectingProof by remember { mutableStateOf<InstallationProofEntity?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandNavy900)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BrandNavy800)
                    .padding(16.dp)
            ) {
                Text(
                    text = "MY CAMPAIGNS & TRACKING",
                    color = BrandAmber400,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Live Outdoor Advertising Operations",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }

        if (campaigns.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(50.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.Campaign, contentDescription = null, tint = TextMuted, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No campaigns running yet.", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text("Book an advertising space to launch your first outdoor campaign.", color = TextSecondary, fontSize = 12.sp, textAlign = TextAlign.Center)
                }
            }
        } else {
            items(campaigns) { campaign ->
                val proof = allProofs.firstOrNull { it.bookingId == campaign.bookingId }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    border = BorderStroke(1.dp, BorderSubtle)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = campaign.brand,
                                color = BrandAmber400,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        when (campaign.status) {
                                            "Installed", "Campaign Live" -> BrandSuccess.copy(alpha = 0.2f)
                                            "Printing" -> BrandCyan500.copy(alpha = 0.2f)
                                            else -> BrandAmber500.copy(alpha = 0.2f)
                                        }
                                    )
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = campaign.status,
                                    color = when (campaign.status) {
                                        "Installed", "Campaign Live" -> BrandSuccess
                                        "Printing" -> BrandCyan400
                                        else -> BrandAmber400
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = campaign.campaignName,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Site: ${campaign.locationSummary}",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Campaign Timeline Stages
                        CampaignTimelineBar(currentStatus = campaign.status)

                        Spacer(modifier = Modifier.height(14.dp))
                        Divider(color = BorderSubtle)
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "DATES", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Text(text = "${campaign.startDate} to ${campaign.endDate}", color = Color.White, fontSize = 11.sp)
                            }

                            if (proof != null) {
                                Button(
                                    onClick = { inspectingProof = proof },
                                    colors = ButtonDefaults.buttonColors(containerColor = BrandCyan500),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.Verified, contentDescription = null, tint = BrandNavy900, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("View Proof", color = BrandNavy900, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }

    // Proof Modal Inspection Dialog
    if (inspectingProof != null) {
        val p = inspectingProof!!
        AlertDialog(
            onDismissRequest = { inspectingProof = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Verified, contentDescription = null, tint = BrandSuccess)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Verified Installation Proof", color = Color.White, fontSize = 16.sp)
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    AsyncImage(
                        model = "https://images.unsplash.com/photo-1542744094-3a31f272c490?auto=format&fit=crop&w=1000&q=80",
                        contentDescription = "Installation proof image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("GPS Coordinates / Geotag:", color = BrandAmber400, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    Text(p.geoTag, color = Color.White, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Owner Contractor Notes:", color = TextMuted, fontSize = 11.sp)
                    Text(p.notes, color = TextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Operations Review:", color = TextMuted, fontSize = 11.sp)
                    Text(p.reviewNotes, color = BrandSuccess, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            },
            confirmButton = {
                Button(
                    onClick = { inspectingProof = null },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandAmber500)
                ) {
                    Text("Close", color = BrandNavy900, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = BrandNavy800
        )
    }
}

@Composable
private fun CampaignTimelineBar(currentStatus: String) {
    val steps = listOf("Booking", "Payment", "Printing", "Installed", "Live")
    val currentIdx = when (currentStatus.lowercase()) {
        "booking submitted" -> 0
        "space confirmed", "creative approved" -> 1
        "printing", "dispatched" -> 2
        "installation scheduled", "installed" -> 3
        "campaign live", "completed" -> 4
        else -> 1
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEachIndexed { index, step ->
            val isDone = index <= currentIdx
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(if (isDone) BrandSuccess else BrandNavy600),
                    contentAlignment = Alignment.Center
                ) {
                    if (isDone) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(11.dp))
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = step,
                    color = if (isDone) Color.White else TextMuted,
                    fontSize = 9.sp,
                    fontWeight = if (isDone) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun AiAdStudioScreen(viewModel: HoldingHubViewModel) {
    var brandName by remember { mutableStateOf("Singhal Sweets & Namkeen") }
    var productService by remember { mutableStateOf("Festive Mithai, Dry Fruits & Gift Hampers") }
    var targetAudience by remember { mutableStateOf("Families, Corporate gifting & Festive Shoppers in Delhi NCR") }
    var city by remember { mutableStateOf("Delhi NCR (East Delhi & Noida)") }
    var campaignObjective by remember { mutableStateOf("Drive High Footfall & Bulk Gifting Orders") }
    var offer by remember { mutableStateOf("Flat 20% OFF on Gift Boxes + Free Home Delivery") }
    var toneStyle by remember { mutableStateOf("Festive, Grand, Premium Indian Taste") }

    val aiResult by viewModel.aiStudioResult.collectAsState()
    val isGenerating by viewModel.isGeneratingAi.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandNavy900)
            .padding(16.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = BrandAmber500)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = "AI AD STUDIO", color = BrandAmber400, fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 1.sp)
                    Text(text = "Outdoor Creative & Headline Generator", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Generate high-contrast billboard copy, 7-word roadside slogans, and local Indian market hooks optimized for driver & commuter recall.",
                color = TextSecondary,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(14.dp))
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = BrandNavy800),
                border = BorderStroke(1.dp, BorderSubtle)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
                    OutlinedTextField(
                        value = brandName,
                        onValueChange = { brandName = it },
                        label = { Text("Brand / Business Name") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = productService,
                        onValueChange = { productService = it },
                        label = { Text("Product / Service") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = { Text("Target Market / City") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = offer,
                        onValueChange = { offer = it },
                        label = { Text("Key Promotional Offer / Hook") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            viewModel.generateAiCreatives(
                                brandName = brandName,
                                productService = productService,
                                targetAudience = targetAudience,
                                city = city,
                                campaignObjective = campaignObjective,
                                offer = offer,
                                toneStyle = toneStyle
                            )
                        },
                        enabled = !isGenerating,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandAmber500),
                        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("generate_ai_creative_btn")
                    ) {
                        if (isGenerating) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = BrandNavy900)
                        } else {
                            Icon(Icons.Default.Bolt, contentDescription = null, tint = BrandNavy900)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Generate High-Impact Outdoor Copy", color = BrandNavy900, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Generated Results
        if (aiResult != null) {
            val res = aiResult!!
            item {
                Spacer(modifier = Modifier.height(16.dp))
                // AI connection status indicator
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(8.dp).clip(CircleShape).background(if (res.isRealAiConnected) BrandSuccess else BrandCyan400)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (res.isRealAiConnected) "Generated using Gemini Pro AI Engine" else "Holding Hub OOH High-Recall Copy Engine",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(text = "1. AI Outdoor Slogans (7-Word Formula)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(6.dp))
                res.headlines.forEach { headline ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                        border = BorderStroke(1.dp, BorderSubtle)
                    ) {
                        Text(text = headline, color = BrandAmber400, fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.padding(12.dp))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text(text = "2. Visual Layout & Art Direction", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(6.dp))
                res.visualDirections.forEach { dir ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                        colors = CardDefaults.cardColors(containerColor = BrandNavy800),
                        border = BorderStroke(1.dp, BorderSubtle)
                    ) {
                        Text(text = dir, color = TextPrimary, fontSize = 12.sp, modifier = Modifier.padding(12.dp))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text(text = "3. Local Hindi/English Catchphrases", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(6.dp))
                res.localCatchphrases.forEach { phrase ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                        border = BorderStroke(1.dp, BorderSubtle)
                    ) {
                        Text(text = phrase, color = BrandCyan400, fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.padding(12.dp))
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}
