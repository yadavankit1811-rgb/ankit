package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.holdinghub.ui.*
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {

    private val viewModel: HoldingHubViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppScreen(viewModel: HoldingHubViewModel) {
    val currentRole by viewModel.currentRole.collectAsState()
    val customerScreen by viewModel.customerScreen.collectAsState()

    val approvedSpaces by viewModel.approvedSpaces.collectAsState()
    val allSpacesAdmin by viewModel.allSpacesAdmin.collectAsState()
    val ownerSpaces by viewModel.ownerSpaces.collectAsState()

    val currentCustomer by viewModel.currentCustomer.collectAsState()
    val currentOwner by viewModel.currentOwner.collectAsState()

    val customerFavorites by viewModel.customerFavorites.collectAsState()
    val customerCampaigns by viewModel.customerCampaigns.collectAsState()
    val allBookings by viewModel.allBookings.collectAsState()
    val allPayments by viewModel.allPayments.collectAsState()
    val allProofs by viewModel.allProofs.collectAsState()
    val allOwners by viewModel.allOwners.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val allPromotions by viewModel.allPromotions.collectAsState()
    val allServices by viewModel.allServices.collectAsState()

    val selectedSpace by viewModel.selectedSpace.collectAsState()

    val density = androidx.compose.ui.platform.LocalDensity.current
    val isImeVisible = WindowInsets.ime.getBottom(density) > 0

    // Android System Back Navigation Handler
    BackHandler(enabled = customerScreen != CustomerScreen.HOME && currentRole == AppRole.CUSTOMER) {
        viewModel.navigateCustomer(CustomerScreen.HOME)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandNavy900),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            HoldingHubHeader(
                currentRole = currentRole,
                onRoleChange = { role -> viewModel.switchRole(role) }
            )
        },
        bottomBar = {
            if (currentRole == AppRole.CUSTOMER && !isImeVisible) {
                CustomerBottomNavBar(
                    currentScreen = customerScreen,
                    onNavigate = { screen -> viewModel.navigateCustomer(screen) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BrandNavy900)
        ) {
            when (currentRole) {
                AppRole.CUSTOMER -> {
                    when (customerScreen) {
                        CustomerScreen.HOME -> {
                            CustomerHomeScreen(
                                viewModel = viewModel,
                                spaces = approvedSpaces,
                                favorites = customerFavorites,
                                onSelectSpace = { sp ->
                                    viewModel.selectSpace(sp)
                                    viewModel.navigateCustomer(CustomerScreen.SPACE_DETAILS)
                                },
                                onBookNow = { sp ->
                                    viewModel.startBookingFlow(sp)
                                },
                                onNavigateSearch = { viewModel.navigateCustomer(CustomerScreen.SEARCH_SPACES) },
                                onListYourSpace = { viewModel.switchRole(AppRole.OWNER) }
                            )
                        }

                        CustomerScreen.SEARCH_SPACES -> {
                            SearchAndInventoryScreen(
                                viewModel = viewModel,
                                spaces = approvedSpaces,
                                favorites = customerFavorites,
                                onSelectSpace = { sp ->
                                    viewModel.selectSpace(sp)
                                    viewModel.navigateCustomer(CustomerScreen.SPACE_DETAILS)
                                },
                                onBookNow = { sp ->
                                    viewModel.startBookingFlow(sp)
                                }
                            )
                        }

                        CustomerScreen.SPACE_DETAILS -> {
                            if (selectedSpace != null) {
                                val isFav = customerFavorites.any { it.spaceId == selectedSpace!!.id }
                                SpaceDetailsScreen(
                                    space = selectedSpace!!,
                                    isFavorite = isFav,
                                    onFavoriteToggle = { viewModel.toggleFavorite(selectedSpace!!.id) },
                                    onBookNow = { viewModel.startBookingFlow(selectedSpace!!) },
                                    onBack = { viewModel.navigateCustomer(CustomerScreen.HOME) }
                                )
                            } else {
                                viewModel.navigateCustomer(CustomerScreen.HOME)
                            }
                        }

                        CustomerScreen.BOOKING_FLOW -> {
                            BookingFlowScreen(
                                viewModel = viewModel,
                                onBookingComplete = {
                                    viewModel.navigateCustomer(CustomerScreen.MY_CAMPAIGNS)
                                },
                                onCancel = {
                                    viewModel.navigateCustomer(CustomerScreen.HOME)
                                }
                            )
                        }

                        CustomerScreen.MY_CAMPAIGNS -> {
                            CampaignManagementScreen(
                                viewModel = viewModel,
                                campaigns = customerCampaigns,
                                allProofs = allProofs
                            )
                        }

                        CustomerScreen.AI_AD_STUDIO -> {
                            AiAdStudioScreen(viewModel = viewModel)
                        }

                        CustomerScreen.DASHBOARD -> {
                            CustomerDashboardScreen(
                                viewModel = viewModel,
                                customer = currentCustomer,
                                spaces = approvedSpaces,
                                favorites = customerFavorites,
                                onSelectSpace = { sp ->
                                    viewModel.selectSpace(sp)
                                    viewModel.navigateCustomer(CustomerScreen.SPACE_DETAILS)
                                },
                                onBookNow = { sp ->
                                    viewModel.startBookingFlow(sp)
                                },
                                onNavigateLegal = { viewModel.navigateCustomer(CustomerScreen.SUPPORT_LEGAL) }
                            )
                        }

                        CustomerScreen.SUPPORT_LEGAL -> {
                            SupportAndLegalScreen(
                                onBack = { viewModel.navigateCustomer(CustomerScreen.DASHBOARD) }
                            )
                        }

                        else -> {
                            CustomerHomeScreen(
                                viewModel = viewModel,
                                spaces = approvedSpaces,
                                favorites = customerFavorites,
                                onSelectSpace = { sp ->
                                    viewModel.selectSpace(sp)
                                    viewModel.navigateCustomer(CustomerScreen.SPACE_DETAILS)
                                },
                                onBookNow = { sp ->
                                    viewModel.startBookingFlow(sp)
                                },
                                onNavigateSearch = { viewModel.navigateCustomer(CustomerScreen.SEARCH_SPACES) },
                                onListYourSpace = { viewModel.switchRole(AppRole.OWNER) }
                            )
                        }
                    }
                }

                AppRole.OWNER -> {
                    OwnerPortalScreen(
                        viewModel = viewModel,
                        owner = currentOwner,
                        ownerSpaces = ownerSpaces,
                        allBookings = allBookings
                    )
                }

                AppRole.ADMIN -> {
                    AdminPanelScreen(
                        viewModel = viewModel,
                        spaces = allSpacesAdmin,
                        owners = allOwners,
                        customers = allUsers,
                        bookings = allBookings,
                        payments = allPayments,
                        proofs = allProofs,
                        promotions = allPromotions,
                        services = allServices
                    )
                }
            }
        }
    }
}

@Composable
fun CustomerBottomNavBar(
    currentScreen: CustomerScreen,
    onNavigate: (CustomerScreen) -> Unit
) {
    NavigationBar(
        containerColor = BrandNavy800,
        contentColor = Color.White,
        tonalElevation = 8.dp
    ) {
        val navItems = listOf(
            Triple(CustomerScreen.HOME, "Home", Icons.Default.Home),
            Triple(CustomerScreen.SEARCH_SPACES, "Search", Icons.Default.Search),
            Triple(CustomerScreen.MY_CAMPAIGNS, "Campaigns", Icons.Default.Campaign),
            Triple(CustomerScreen.AI_AD_STUDIO, "AI Studio", Icons.Default.AutoAwesome),
            Triple(CustomerScreen.DASHBOARD, "Account", Icons.Default.Person)
        )

        navItems.forEach { (screen, label, icon) ->
            val isSelected = currentScreen == screen
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(screen) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        modifier = Modifier.size(20.dp)
                    )
                },
                label = {
                    Text(
                        text = label,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = BrandNavy900,
                    selectedTextColor = BrandAmber400,
                    indicatorColor = BrandAmber500,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextMuted
                ),
                modifier = Modifier.testTag("nav_item_${label.lowercase()}")
            )
        }
    }
}
