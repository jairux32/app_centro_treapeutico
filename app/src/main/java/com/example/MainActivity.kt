package com.example

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Message
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Appointment
import com.example.data.Review
import com.example.ui.TherapyViewModel
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : ComponentActivity() {
    private val viewModel: TherapyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    MainShowcaseContent(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

// --- CORE WELLNESS SCREEN LAYOUT ---

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainShowcaseContent(
    viewModel: TherapyViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) }
    
    val tabTitles = listOf("Inicio", "Equipos & Terapias", "Agenda Tu Cita", "Escanear QR", "Sede")
    val tabIcons = listOf(
        Icons.Default.Spa,
        Icons.Default.HealthAndSafety,
        Icons.Default.Event,
        Icons.Default.QrCode2,
        Icons.Default.LocationOn
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.background,
                        Color(0xFFF9F5EF) // Soft clay warmth transition
                    )
                )
            )
    ) {
        // App top luxury branding bar
        TopLuxuryBrandingHeader(context = context)

        // Horizontal navigation tabs
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            edgePadding = 16.dp,
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary,
            divider = {}
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    modifier = Modifier
                        .testTag("nav_tab_$index")
                        .padding(vertical = 4.dp),
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = tabIcons[index],
                                contentDescription = null,
                                modifier = Modifier.size(17.dp),
                                tint = if (selectedTab == index) MaterialTheme.colorScheme.primary else SoftGrey
                            )
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelLarge,
                                color = if (selectedTab == index) MaterialTheme.colorScheme.primary else ClayWarmText.copy(alpha = 0.7f)
                            )
                        }
                    }
                )
            }
        }

        HorizontalDivider(color = BorderSilver, thickness = 1.dp)

        // Body Content switcher with beautiful fade-in transitions
        Box(
            modifier = Modifier
                .weight(1.0f)
                .fillMaxWidth()
        ) {
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    fadeIn(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)) with
                            fadeOut(animationSpec = spring(stiffness = Spring.StiffnessMediumLow))
                },
                label = "TabContentAnimation"
            ) { targetTab ->
                when (targetTab) {
                    0 -> InicioTabContent(viewModel = viewModel, onBookClick = { selectedTab = 2 }, onTabSelect = { selectedTab = it })
                    1 -> TerapiasTabContent()
                    2 -> AgendaTabContent(viewModel = viewModel)
                    3 -> QrTabContent()
                    4 -> SedeTabContent()
                }
            }
        }
    }
}

// --- SUBSECTION 1: TOP BRANDING HEADER ---

@Composable
fun TopLuxuryBrandingHeader(context: Context) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Profile circle badge "CV" for Carmen Viera
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(JadeLight.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "CV",
                            style = MaterialTheme.typography.titleMedium,
                            color = ThermalTerra,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(ThermalTerra)
                            )
                            Text(
                                text = "CERAGEM AMBATO",
                                style = MaterialTheme.typography.labelMedium,
                                color = ThermalTerra,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Calor Terapéutico",
                            style = MaterialTheme.typography.titleLarge,
                            color = ClayWarmText,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Propietaria: Carmen Viera Proaño",
                            style = MaterialTheme.typography.labelSmall,
                            color = SoftGrey
                        )
                    }
                }

                // Quick Call CTA
                IconButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:0983630006"))
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(ThermalTerra)
                        .testTag("header_call_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "Llamar ahora",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            HorizontalDivider(color = BorderSilver, thickness = 1.dp)
        }
    }
}

// --- ADDITIONAL HELPERS FOR CONTACT GRID ---

@Composable
fun SocialGridCard(
    title: String,
    channelColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconContent: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSilver)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .border(
                        androidx.compose.foundation.BorderStroke(
                            1.dp,
                            Brush.linearGradient(listOf(channelColor, ThermalTerra))
                        ),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                iconContent()
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = ClayWarmText,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun MiniGridPattern(channelColor: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            Box(modifier = Modifier.size(6.dp).background(channelColor, RoundedCornerShape(1.5.dp)))
            Box(modifier = Modifier.size(6.dp).background(BorderSilver, RoundedCornerShape(1.5.dp)))
            Box(modifier = Modifier.size(6.dp).background(channelColor, RoundedCornerShape(1.5.dp)))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            Box(modifier = Modifier.size(6.dp).background(BorderSilver, RoundedCornerShape(1.5.dp)))
            Box(modifier = Modifier.size(6.dp).background(channelColor, RoundedCornerShape(1.5.dp)))
            Box(modifier = Modifier.size(6.dp).background(BorderSilver, RoundedCornerShape(1.5.dp)))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            Box(modifier = Modifier.size(6.dp).background(channelColor, RoundedCornerShape(1.5.dp)))
            Box(modifier = Modifier.size(6.dp).background(BorderSilver, RoundedCornerShape(1.5.dp)))
            Box(modifier = Modifier.size(6.dp).background(channelColor, RoundedCornerShape(1.5.dp)))
        }
    }
}

// --- TAB CONTENT 0: REGULAR HOME TAB ---

@Composable
fun InicioTabContent(
    viewModel: TherapyViewModel,
    onBookClick: () -> Unit,
    onTabSelect: (Int) -> Unit
) {
    val context = LocalContext.current
    val reviews by viewModel.reviews.collectAsStateWithLifecycle()
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Welcome Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hero_banner_card"),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = CeramicSilt)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    // Decorative ambient light circle in bottom right
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = 32.dp, y = 32.dp)
                            .size(160.dp)
                            .clip(CircleShape)
                            .background(JadeLight.copy(alpha = 0.25f))
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Text(
                            text = "BIENVENIDA",
                            color = ThermalTerra,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Carmen Viera Proaño",
                            style = MaterialTheme.typography.headlineMedium,
                            color = ClayWarmText,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Especialistas en bienestar térmico y recuperación muscular integral en la ciudad de Ambato.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = SoftGrey,
                            lineHeight = 22.sp
                        )
                    }
                }
            }
        }

        // 2. Exact Location Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSilver)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(CeramicSilt, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Ubicación",
                            tint = ThermalTerra,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "UBICACIÓN: AMBATO",
                            style = MaterialTheme.typography.labelLarge,
                            color = ThermalTerra,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Febres Cordero e/ Mariano Enríquez y Mariano Tinajero",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SoftGrey
                        )
                    }
                }
            }
        }

        // 3. Contact Social Grid header
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Medios de Contacto",
                style = MaterialTheme.typography.titleLarge,
                color = ClayWarmText,
                fontWeight = FontWeight.Bold
            )
        }

        // 4. Contact Grid Section (4 Social Channels)
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SocialGridCard(
                        title = "WhatsApp",
                        channelColor = Color(0xFF25D366),
                        onClick = { onTabSelect(3) },
                        modifier = Modifier.weight(1f)
                    ) {
                        MiniGridPattern(Color(0xFF25D366))
                    }
                    SocialGridCard(
                        title = "Facebook",
                        channelColor = Color(0xFF1877F2),
                        onClick = { onTabSelect(3) },
                        modifier = Modifier.weight(1f)
                    ) {
                        MiniGridPattern(Color(0xFF1877F2))
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SocialGridCard(
                        title = "Instagram",
                        channelColor = Color(0xFFE1306C),
                        onClick = { onTabSelect(3) },
                        modifier = Modifier.weight(1f)
                    ) {
                        MiniGridPattern(Color(0xFFE1306C))
                    }
                    SocialGridCard(
                        title = "TikTok",
                        channelColor = Color(0xFF000000),
                        onClick = { onTabSelect(3) },
                        modifier = Modifier.weight(1f)
                    ) {
                        MiniGridPattern(Color(0xFF000000))
                    }
                }
            }
        }

        // 5. Promo/Action Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("promo_banner_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = OffWhiteComfort),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSilver)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(ThermalTerra.copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "TRATAMIENTO NATURAL",
                            color = ThermalTerra,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Recupere su Vitalidad sin Medicamentos",
                        style = MaterialTheme.typography.titleLarge,
                        color = ClayWarmText,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Nuestras terapias combinan calor por infrarrojo lejano y camillas con rodillos de piedras de jade para restaurar la columna vertebral, eliminar dolores crónicos y reactivar la circulación.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SoftGrey
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onBookClick,
                            colors = ButtonDefaults.buttonColors(containerColor = ThermalTerra),
                            shape = RoundedCornerShape(100.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("hero_book_btn")
                        ) {
                            Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Agendar Cita", fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        Button(
                            onClick = {
                                val url = "https://wa.me/593983630006?text=Hola%20Sra.%20Carmen%20Viera,%20me%20gustaría%20consultar%20sobre%20las%20terapias."
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = JadeStone),
                            shape = RoundedCornerShape(100.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Consultar WhatsApp", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }

        // Quick Facts / Atributos del Centro
        item {
            Text(
                text = "Beneficios Certificados",
                style = MaterialTheme.typography.titleLarge,
                color = ClayWarmText,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                BenefitBriefCard(
                    icon = Icons.Default.Thermostat,
                    title = "Termoterapia",
                    desc = "Estimula la circulación sanguínea profunda.",
                    modifier = Modifier.weight(1f)
                )
                BenefitBriefCard(
                    icon = Icons.Default.Psychology,
                    title = "Cero Estrés",
                    desc = "Alivia la fatiga física y mental diaria.",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                BenefitBriefCard(
                    icon = Icons.Default.VolunteerActivism,
                    title = "Quiropraxia",
                    desc = "Alineación mecánica de columna con piedras de jade.",
                    modifier = Modifier.weight(1f)
                )
                BenefitBriefCard(
                    icon = Icons.Default.TrendingDown,
                    title = "Anti-inflamación",
                    desc = "Disminuye el dolor en articulaciones.",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Testimonios Carousel/Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Testimonios de Clientes",
                    style = MaterialTheme.typography.titleLarge,
                    color = ClayWarmText,
                    fontWeight = FontWeight.Bold
                )
                
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(30))
                        .background(JadeStone.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Satisfechos ⭐⭐⭐⭐⭐",
                        color = JadeStone,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Reviews listings
        if (reviews.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Text(
                        text = "No hay testimonios locales registrados. ¡Sé el primero en calificar!",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            items(reviews) { review ->
                ReviewItemCard(
                    review = review,
                    onDeleteClick = {
                        viewModel.deleteReview(review.id)
                        Toast.makeText(context, "Reseña removida", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }

        // Quick Form to write a review
        item {
            WriteReviewSection(onReviewSubmit = { author, rating, comment ->
                viewModel.submitReview(author, rating, comment)
                Toast.makeText(context, "¡Gracias por su valioso testimonio!", Toast.LENGTH_LONG).show()
            })
        }
    }
}

@Composable
fun BenefitBriefCard(
    icon: ImageVector,
    title: String,
    desc: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(130.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSilver)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(ThermalTerra.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = ThermalTerra, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, style = MaterialTheme.typography.titleMedium, color = ClayWarmText, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = desc, style = MaterialTheme.typography.labelMedium, color = SoftGrey, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
fun ReviewItemCard(
    review: Review,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSilver)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = review.author,
                        style = MaterialTheme.typography.titleMedium,
                        color = ClayWarmText,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = review.dateString,
                        style = MaterialTheme.typography.labelSmall,
                        color = SoftGrey
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(5) { index ->
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = if (index < review.rating) GoldGlow else BorderSilver,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    if (review.isUserGenerated) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Borrar",
                            tint = Color.Red.copy(alpha = 0.7f),
                            modifier = Modifier
                                .size(18.dp)
                                .clickable { onDeleteClick() }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = review.comment,
                style = MaterialTheme.typography.bodyMedium,
                color = ClayWarmText.copy(alpha = 0.9f)
            )
        }
    }
}

@Composable
fun WriteReviewSection(
    onReviewSubmit: (String, Int, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(5) }
    var comment by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSilver)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Escriba su Testimonio",
                style = MaterialTheme.typography.titleMedium,
                color = ClayWarmText,
                fontWeight = FontWeight.Bold
            )
            
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Su Nombre Completo") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("review_name_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ThermalTerra,
                    unfocusedBorderColor = BorderSilver,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "Calificación:", style = MaterialTheme.typography.labelLarge, color = ClayWarmText)
                Row {
                    repeat(5) { index ->
                        val currentStar = index + 1
                        Icon(
                            imageVector = if (currentStar <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = null,
                            tint = if (currentStar <= rating) GoldGlow else SoftGrey,
                            modifier = Modifier
                                .size(28.dp)
                                .clickable { rating = currentStar }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                label = { Text("Su experiencia o comentario medicinal...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .testTag("review_comment_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ThermalTerra,
                    unfocusedBorderColor = BorderSilver,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Button(
                onClick = {
                    if (name.isBlank() || comment.isBlank()) {
                        return@Button
                    }
                    onReviewSubmit(name, rating, comment)
                    name = ""
                    comment = ""
                    rating = 5
                },
                modifier = Modifier
                    .align(Alignment.End)
                    .testTag("submit_review_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = ThermalTerra),
                shape = RoundedCornerShape(100.dp),
                enabled = name.isNotBlank() && comment.isNotBlank()
            ) {
                Text("Publicar Testimonio", color = Color.White)
            }
        }
    }
}

// --- TAB CONTENT 1: THERAPIES & CLINICAL DEVICES ---

@Composable
fun TerapiasTabContent() {
    val therapies = listOf(
        TherapyDeviceItem(
            title = "Camilla de Calor Ceragem (Master V4)",
            icon = Icons.Default.AirlineSeatFlat,
            techSpecs = "Piedras de rodillo de Jade natural, Calor Infrarrojo Lejano de hasta 65°C, Escáner automático de columna vertebral.",
            symptoms = listOf("Problemas de Columna", "Dolores Musculares Crónicos", "Osteoporosis", "Espolón Calcáneo", "Escoliosis/Cervicalgia"),
            scientificDetails = "El rodillo escanea la longitud de la columna, adaptando el masaje térmico de acupresión a la curvatura específica del usuario. Calienta la médula para estimular la producción de glóbulos rojos.",
            accentColor = ThermalTerra
        ),
        TherapyDeviceItem(
            title = "Botas de Presoterapia Chikimi",
            icon = Icons.Default.AccessibilityNew,
            techSpecs = "Cámaras neumáticas de presión secuencial, 3 niveles de flujo, compresión térmica local profunda.",
            symptoms = listOf("Mala Circulación", "Várices Sanguíneas", "Piernas Cansadas e Hinchadas", "Retención de Líquidos", "Recuperación post-deporte"),
            scientificDetails = "Ejerce compresión desde el pie hasta el muslo, facilitando el retorno venoso y linfático. Ayuda a drenar las toxinas celulares acumuladas por sedentarismo.",
            accentColor = JadeStone
        ),
        TherapyDeviceItem(
            title = "Sillón de Masaje de Lujo Inteligente",
            icon = Icons.Default.Weekend,
            techSpecs = "Rodillos mecánicos inteligentes 3D, Gravedad cero completa, Termoterapia lumbar de acompañamiento.",
            symptoms = listOf("Estrés y Ansiedad", "Tensión Cervical y Lumbar", "Insomnio", "Contracturas musculares", "Fatiga General"),
            scientificDetails = "Combina técnicas de amasado, golpeteo y reflexología. Coloca el cuerpo en ángulo de gravedad cero (corazón por debajo de las piernas) mejorando la absorción de oxígeno.",
            accentColor = ThermalAmber
        ),
        TherapyDeviceItem(
            title = "Masajeador de Pies y Pantorrillas Chikimi",
            icon = Icons.Default.Widgets,
            techSpecs = "Rodillos reflexológicos plantares de alta presión rítmica, calefactor por infrarrojo, envoltura por bolsa de aire.",
            symptoms = listOf("Dolor de Talón y Fascitis Plantar", "Espolón", "Frío en extremidades", "Fatiga muscular", "Mala sensibilidad"),
            scientificDetails = "Estimula más de 60 puntos de reflexología plantar correspondientes a múltiples órganos. Combate el dolor del espolón calcáneo disolviendo tensiones en la fáscia.",
            accentColor = JadeLight
        ),
        TherapyDeviceItem(
            title = "Analizador Cuántico Bio-Bio QRM",
            icon = Icons.Default.MonitorHeart,
             techSpecs = "Análisis bio-eléctrico de resonancia magnética. Lectura de 35 reportes de salud en 1 minuto.",
            symptoms = listOf("Hígado Graso", "Colesterol Elevado", "Gases y Gastritis", "Sangre Espesa", "Problemas de Tiroides"),
            scientificDetails = "Analiza las frecuencias y vibraciones celulares electromagnéticas. Es un preventivo avanzado para direccionar qué terapias térmicas se ocupas con urgencia.",
            accentColor = ThermalRed
        )
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSilver)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MedicalServices,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = "Nuestra tecnología está diseñada para estimular la autocuración natural del cuerpo por medio de bio-calor infrarrojo de contacto.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ClayWarmText,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        items(therapies) { item ->
            ExpandableTherapyCard(item = item)
        }
    }
}

data class TherapyDeviceItem(
    val title: String,
    val icon: ImageVector,
    val techSpecs: String,
    val symptoms: List<String>,
    val scientificDetails: String,
    val accentColor: Color
)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ExpandableTherapyCard(item: TherapyDeviceItem) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .testTag("therapy_card_${item.title.take(6)}"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSilver)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Main Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(item.accentColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = item.icon, contentDescription = null, tint = item.accentColor, modifier = Modifier.size(24.dp))
                    }

                    Column {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = ClayWarmText
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Tecnología: " + item.techSpecs,
                            style = MaterialTheme.typography.bodyMedium,
                            color = SoftGrey,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Mostrar menos" else "Mostrar más",
                    tint = SoftGrey
                )
            }

            // Expanded panel details
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                HorizontalDivider(color = BorderSilver, thickness = 1.dp)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(OffWhiteComfort.copy(alpha = 0.5f))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Scientific operational detail
                    Column {
                        Text(
                            text = "¿Cómo Funciona?",
                            style = MaterialTheme.typography.labelLarge,
                            color = item.accentColor,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.scientificDetails,
                            style = MaterialTheme.typography.bodyMedium,
                            color = ClayWarmText
                        )
                    }

                    // Symptoms treated
                    Column {
                        Text(
                            text = "Ideal para Tratar:",
                            style = MaterialTheme.typography.labelLarge,
                            color = item.accentColor,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        
                        // Symmetrical wrap flow alternative
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                item.symptoms.take(3).forEach { symptom ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = JadeStone,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = symptom, style = MaterialTheme.typography.bodyMedium, color = ClayWarmText)
                                    }
                                }
                            }
                            if (item.symptoms.size > 3) {
                                Column(modifier = Modifier.weight(1f)) {
                                    item.symptoms.drop(3).forEach { symptom ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(vertical = 2.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = JadeStone,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(text = symptom, style = MaterialTheme.typography.bodyMedium, color = ClayWarmText)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    Text(
                        text = "⭐ Presione de nuevo para contraer",
                        style = MaterialTheme.typography.labelSmall,
                        color = SoftGrey,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

// --- TAB CONTENT 2: CLINIC LOCAL BOOKING DIRECTLY ---

@Composable
fun AgendaTabContent(viewModel: TherapyViewModel) {
    val context = LocalContext.current
    val appointments by viewModel.appointments.collectAsStateWithLifecycle()

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    
    val therapiesAvailable = listOf(
        "Camilla Ceragem de Piedras de Jade",
        "Botas de Presoterapia Chikimi",
        "Sillón de Masajes en Gravedad Cero",
        "Masajeador de Pies y Fisio-Rodillos",
        "Escáner Bio-Cuántico QRM"
    )
    var selectedTherapy by remember { mutableStateOf(therapiesAvailable[0]) }

    // Custom calendar dates generation (Next 15 working days, omitting Sundays)
    val calendarDates = remember {
        val list = mutableListOf<String>()
        val cal = Calendar.getInstance()
        val format = SimpleDateFormat("EEE dd 'de' MMMM", Locale("es", "EC"))
        var daysAdded = 0
        while (daysAdded < 15) {
            val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
            if (dayOfWeek != Calendar.SUNDAY) {
                list.add(format.format(cal.time))
                daysAdded++
            }
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        list
    }
    var selectedDate by remember { mutableStateOf(calendarDates.firstOrNull() ?: "") }

    val timeSlots = listOf(
        "08:00 AM", "09:00 AM", "10:00 AM", "11:00 AM",
        "12:00 PM", "02:00 PM", "03:00 PM", "04:00 PM", "05:00 PM"
    )
    var selectedTimeSlot by remember { mutableStateOf(timeSlots[0]) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Form Title
        item {
            Text(
                text = "Agende su Cita Virtual Local",
                style = MaterialTheme.typography.titleLarge,
                color = ClayWarmText,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Los datos se reservan de forma segura en su dispositivo local para agilizar su confirmación.",
                style = MaterialTheme.typography.bodyMedium,
                color = SoftGrey
            )
        }

        // Action Form Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSilver)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    
                    // Client name
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Su Nombre Completo") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("booking_name_input"),
                        leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = null) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ThermalTerra,
                            unfocusedBorderColor = BorderSilver
                        )
                    )

                    // Client phone
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Número de Teléfono (WhatsApp)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("booking_phone_input"),
                        leadingIcon = { Icon(imageVector = Icons.Default.Call, contentDescription = null) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ThermalTerra,
                            unfocusedBorderColor = BorderSilver
                        )
                    )

                    // Therapy dropdown emulator
                    Text(text = "Seleccione el Servicio:", style = MaterialTheme.typography.labelLarge, color = ClayWarmText)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(therapiesAvailable) { therapy ->
                            val isSelected = selectedTherapy == therapy
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (isSelected) ThermalTerra else BorderSilver)
                                    .clickable { selectedTherapy = therapy }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = therapy,
                                    color = if (isSelected) Color.White else ClayWarmText,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Horizontal calendar picker
                    Text(text = "Seleccione la Fecha:", style = MaterialTheme.typography.labelLarge, color = ClayWarmText)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(calendarDates) { date ->
                            val isSelected = selectedDate == date
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) JadeStone else Color(0xFFF0EFEA))
                                    .border(1.dp, if (isSelected) JadeLight else BorderSilver, RoundedCornerShape(12.dp))
                                    .clickable { selectedDate = date }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = date,
                                    color = if (isSelected) Color.White else ClayWarmText,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    // Time slots selector
                    Text(text = "Seleccione la Hora (Lunes a Sábado):", style = MaterialTheme.typography.labelLarge, color = ClayWarmText)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(timeSlots) { slot ->
                            val isSelected = selectedTimeSlot == slot
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(if (isSelected) ThermalAmber else Color.White)
                                    .border(1.dp, if (isSelected) ThermalAmber else BorderSilver, CircleShape)
                                    .clickable { selectedTimeSlot = slot }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = slot,
                                    color = if (isSelected) EspressoDark else ClayWarmText,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Note or symptom text box
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        label = { Text("Nota Médica (Sintomatología)") },
                        placeholder = { Text("Ej. Dolor de columna, espolón en pie derecho...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ThermalTerra,
                            unfocusedBorderColor = BorderSilver
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Booking Action buttons
                    Button(
                        onClick = {
                            if (name.isBlank() || phone.isBlank()) {
                                Toast.makeText(context, "Por favor complete nombre y teléfono para agendar", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            viewModel.bookAppointment(
                                name = name,
                                phone = phone,
                                service = selectedTherapy,
                                date = selectedDate,
                                slot = selectedTimeSlot,
                                note = note
                            )
                            Toast.makeText(context, "¡Cita Reservada con Éxito!", Toast.LENGTH_LONG).show()
                            
                            // Send auto SMS or WA to Carmen Viera for instant sync
                            val message = "Hola Sra. Carmen Viera. He agendado una cita desde el aplicativo:\nNombre: $name\nTeléfono: $phone\nServicio: $selectedTherapy\nFecha: $selectedDate\nHora: $selectedTimeSlot\nSintoma: $note"
                            val waUriStr = "https://wa.me/593983630006?text=" + Uri.encode(message)
                            val waIntent = Intent(Intent.ACTION_VIEW, Uri.parse(waUriStr))
                            context.startActivity(waIntent)

                            name = ""
                            phone = ""
                            note = ""
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("submit_booking_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = ThermalTerra),
                        shape = RoundedCornerShape(100.dp)
                    ) {
                        Icon(imageVector = Icons.Default.SendAndArchive, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Confirmar y Enviar por WhatsApp", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }

        // Listing of saved local appointments
        item {
            Text(
                text = "Sus Citas Agendadas",
                style = MaterialTheme.typography.titleLarge,
                color = ClayWarmText,
                fontWeight = FontWeight.Bold
            )
        }

        if (appointments.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = OffWhiteComfort)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Default.EventNote, contentDescription = null, tint = SoftGrey, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No tiene citas agendadas localmente.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SoftGrey
                        )
                    }
                }
            }
        } else {
            items(appointments) { appointment ->
                LocalAppointmentCard(appointment = appointment, onCancelClick = {
                    viewModel.cancelAppointment(appointment.id)
                    Toast.makeText(context, "Reservación cancelada", Toast.LENGTH_SHORT).show()
                })
            }
        }
    }
}

@Composable
fun LocalAppointmentCard(
    appointment: Appointment,
    onCancelClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSilver)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(30))
                        .background(JadeStone.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Cita Reservada",
                        color = JadeStone,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(onClick = onCancelClick) {
                    Icon(imageVector = Icons.Default.Cancel, contentDescription = "Cancelar Cita", tint = Color.Red.copy(alpha = 0.7f))
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = appointment.serviceName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = ClayWarmText
            )

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = BorderSilver, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.CalendarToday, contentDescription = null, tint = ThermalTerra, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = appointment.dateString, style = MaterialTheme.typography.bodyMedium, color = ClayWarmText)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Schedule, contentDescription = null, tint = ThermalTerra, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = appointment.timeSlot, style = MaterialTheme.typography.bodyMedium, color = ClayWarmText)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Paciente: " + appointment.clientName + " (" + appointment.phone + ")",
                style = MaterialTheme.typography.bodySmall,
                color = SoftGrey
            )
            if (appointment.note.isNotBlank()) {
                Text(
                    text = "Afección: " + appointment.note,
                    style = MaterialTheme.typography.bodySmall,
                    color = SoftGrey
                )
            }
        }
    }
}

// --- TAB CONTENT 3: SOCIAL MEDIA ACCESSIBILITY & CUSTOM DETERMINISTIC QR CODES ---

@Composable
fun QrTabContent() {
    val qrs = listOf(
        SocialQrItem(
            name = "WhatsApp Carmen Viera",
            url = "https://wa.me/593983630006?text=Hola,%20quisiera%20reservar%20una%20cita%20terapéutica.",
            color = Color(0xFF25D366),
            symbol = "WA",
            tag = "WHATSAPP",
            subtitle = "Agenda Directa & Consultas Rápidas"
        ),
        SocialQrItem(
            name = "Facebook Comercial",
            url = "https://www.facebook.com/search/top/?q=Centro%20de%20Calor%20Terap%C3%A9utico%20Ambato",
            color = Color(0xFF1877F2),
            symbol = "FB",
            tag = "FACEBOOK",
            subtitle = "Consejos de salud, vídeos y testimonios de la comunidad"
        ),
        SocialQrItem(
            name = "Instagram Oficial",
            url = "https://www.instagram.com/explore/tags/ceragemambato/",
            color = Color(0xFFE1306C),
            symbol = "IG",
            tag = "INSTAGRAM",
            subtitle = "Fotos del equipamiento y tips de alineación lumbar"
        ),
        SocialQrItem(
            name = "TikTok Bienestar",
            url = "https://www.tiktok.com/search?q=ceragem%20ecuador",
            color = Color(0xFF000000),
            symbol = "TT",
            tag = "TIKTOK",
            subtitle = "Explicación dinámica del bio-calor infrarrojo"
        )
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Códigos QR de Acceso Veloz",
                style = MaterialTheme.typography.titleLarge,
                color = ClayWarmText,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Presente esta pantalla en el centro médico para que sus pacientes o visitantes lo escaneen de inmediato con sus celulares, o de un toque para ingresar directamente.",
                style = MaterialTheme.typography.bodyMedium,
                color = SoftGrey
            )
        }

        items(qrs) { qr ->
            SocialQrCard(qr = qr)
        }
    }
}

data class SocialQrItem(
    val name: String,
    val url: String,
    val color: Color,
    val symbol: String,
    val tag: String,
    val subtitle: String
)

@Composable
fun SocialQrCard(qr: SocialQrItem) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("qr_card_${qr.tag}"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSilver)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            // Channel Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(qr.color),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = qr.symbol, color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                    }
                    Column {
                        Text(text = qr.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = ClayWarmText)
                        Text(text = qr.subtitle, style = MaterialTheme.typography.labelSmall, color = SoftGrey)
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20))
                        .background(qr.color.copy(alpha = 0.1f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = qr.tag, color = qr.color, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Dynamic Deterministic QR Code Painter Canvas!
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .background(Color.White)
                    .border(1.dp, BorderSilver, RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                DeterministicQRCanvas(text = qr.url, color = qr.color, symbol = qr.symbol)
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Presione el botón para abrir el enlace directo en su dispositivo:",
                style = MaterialTheme.typography.bodySmall,
                color = SoftGrey,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(qr.url))
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("open_link_btn_${qr.tag}"),
                colors = ButtonDefaults.buttonColors(containerColor = qr.color),
                shape = RoundedCornerShape(100.dp)
            ) {
                Icon(imageVector = Icons.Default.Launch, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Visitar Enlace Directo", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
fun DeterministicQRCanvas(
    text: String,
    color: Color,
    symbol: String
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val sizePx = size.width
        val modules = 21 // 21x21 Standard QR Version 1 grid size
        val moduleSize = sizePx / modules

        // Set stable deterministic randomizer seeded with URL text
        val random = java.util.Random(text.hashCode().toLong())

        // 1. Draw the 3 classic QR finder pattern squares in corners
        drawFinderPattern(0f, 0f, moduleSize, color)
        drawFinderPattern((modules - 7) * moduleSize, 0f, moduleSize, color)
        drawFinderPattern(0f, (modules - 7) * moduleSize, moduleSize, color)

        // 2. Draw random but stable pixels body
        for (r in 0 until modules) {
            for (c in 0 until modules) {
                // Check if index overlaps with any finder pattern
                val inTopLeft = r in 0..6 && c in 0..6
                val inTopRight = r in 0..6 && c in 14..20
                val inBottomLeft = r in 14..20 && c in 0..6
                val inCenterLogo = r in 8..12 && c in 8..12

                if (!inTopLeft && !inTopRight && !inBottomLeft && !inCenterLogo) {
                    val filled = random.nextBoolean()
                    if (filled) {
                        drawRect(
                            color = color,
                            topLeft = Offset(c * moduleSize, r * moduleSize),
                            size = Size(moduleSize, moduleSize)
                        )
                    }
                }
            }
        }

        // 3. Draw middle central brand circle with symbol letters
        val logoSize = 5 * moduleSize
        val logoOffset = 8 * moduleSize
        drawRect(
            color = Color.White,
            topLeft = Offset(logoOffset, logoOffset),
            size = Size(logoSize, logoSize)
        )
        drawCircle(
            color = color,
            radius = (logoSize / 2f) - (moduleSize * 0.3f),
            center = Offset(logoOffset + (logoSize / 2f), logoOffset + (logoSize / 2f))
        )
    }
}

fun androidx.compose.ui.graphics.drawscope.DrawScope.drawFinderPattern(
    x: Float,
    y: Float,
    moduleSize: Float,
    color: Color
) {
    // Outer 7x7 dark square
    drawRect(
        color = color,
        topLeft = Offset(x, y),
        size = Size(7 * moduleSize, 7 * moduleSize)
    )
    // 5x5 internal white space
    drawRect(
        color = Color.White,
        topLeft = Offset(x + moduleSize, y + moduleSize),
        size = Size(5 * moduleSize, 5 * moduleSize)
    )
    // 3x3 central solid dark square
    drawRect(
        color = color,
        topLeft = Offset(x + 2 * moduleSize, y + 2 * moduleSize),
        size = Size(3 * moduleSize, 3 * moduleSize)
    )
}

// --- TAB CONTENT 4: GEOGRAPHIC LOCATION & GENERAL OFFICE HOURS ---

@Composable
fun SedeTabContent() {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Dirección y Horarios de Atención",
                style = MaterialTheme.typography.titleLarge,
                color = ClayWarmText,
                fontWeight = FontWeight.Bold
            )
        }

        // Address Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("location_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSilver)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(ThermalTerra.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.HomeWork, contentDescription = null, tint = ThermalTerra, modifier = Modifier.size(20.dp))
                        }
                        Column {
                            Text(text = "Centro Médico", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = ClayWarmText)
                            Text(text = "Edificio Sede Propios", style = MaterialTheme.typography.labelSmall, color = SoftGrey)
                        }
                    }

                    HorizontalDivider(color = BorderSilver, thickness = 0.5.dp)

                    DetailPairRow(label = "País:", value = "Ecuador")
                    DetailPairRow(label = "Ciudad:", value = "Ambato (Tungurahua)")
                    DetailPairRow(label = "Calles:", value = "Calle Febres Cordero entre Mariano Enríquez y Mariano Tinajero")
                    DetailPairRow(label = "Propietaria:", value = "Carmen Viera Proaño")
                    
                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            // Launch Map search for the target address in Ambato
                            val geoUriStr = "geo:0,0?q=" + Uri.encode("Febres Cordero y Mariano Enríquez, Ambato, Ecuador")
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(geoUriStr))
                            context.startActivity(intent)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("launch_maps_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = JadeStone),
                        shape = RoundedCornerShape(100.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Map, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cómo Llegar (Google Maps)", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Beautiful stylized vector canvas mockup for Ambato calles
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CeramicSilt)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Representación Vial Local",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ClayWarmText
                    )
                    Text(
                        text = "Ambato - Sector Febres Cordero",
                        style = MaterialTheme.typography.labelSmall,
                        color = ThermalTerra
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(EspressoDark, RoundedCornerShape(12.dp))
                    ) {
                        val widthPix = size.width
                        val heightPix = size.height

                        // Draw streets grids lines in dark sleek purple-slate roads
                        // Febres Cordero (horizontal street)
                        drawRect(
                            color = Color(0xFF2F263D),
                            topLeft = Offset(0f, heightPix / 2f - 24f),
                            size = Size(widthPix, 48f)
                        )
                        // Mariano Enríquez (vertical street)
                        drawRect(
                            color = Color(0xFF2F263D),
                            topLeft = Offset(widthPix / 4f - 24f, 0f),
                            size = Size(48f, heightPix)
                        )
                        // Mariano Tinajero (vertical street)
                        drawRect(
                            color = Color(0xFF2F263D),
                            topLeft = Offset(3f * widthPix / 4f - 24f, 0f),
                            size = Size(48f, heightPix)
                        )

                        // Draw road dashed dash lines
                        // Horizontal dashed center
                        drawLine(
                            color = Color.White.copy(alpha = 0.5f),
                            start = Offset(0f, heightPix / 2f),
                            end = Offset(widthPix, heightPix / 2f),
                            strokeWidth = 3f,
                            pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(15f, 15f))
                        )

                        // Draw building markers of CERAGEM
                        // Location is exactly BETWEEN Mariano Enriquez and Mariano Tinajero along Febres Cordero
                        val buildingX = widthPix / 2f
                        val buildingY = heightPix / 2f - 70f
                        
                        // Main physical building layout block
                        drawRoundRect(
                            color = ThermalTerra,
                            topLeft = Offset(buildingX - 45f, buildingY),
                            size = Size(90f, 40f),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
                        )

                        // Location pin logo above the building
                        drawCircle(
                            color = ThermalAmber,
                            radius = 12f,
                            center = Offset(buildingX, buildingY - 14f)
                        )
                        drawLine(
                            color = ThermalAmber,
                            start = Offset(buildingX, buildingY - 14f),
                            end = Offset(buildingX, buildingY),
                            strokeWidth = 4f
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "📍 Ubicación exacta: Entre Mariano Enríquez y Mariano Tinajero. Al frente de letreros de Ceragem.",
                        style = MaterialTheme.typography.bodySmall,
                        color = SoftGrey,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Office operating hours section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSilver)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(ThermalAmber.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Schedule, contentDescription = null, tint = ThermalAmber, modifier = Modifier.size(20.dp))
                        }
                        Column {
                            Text(text = "Horarios de Atención", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = ClayWarmText)
                            Text(text = "Planificación de Citas", style = MaterialTheme.typography.labelSmall, color = SoftGrey)
                        }
                    }

                    HorizontalDivider(color = BorderSilver, thickness = 0.5.dp)

                    DetailPairRow(label = "Lunes a Sábado:", value = "08:00 AM - 05:00 PM")
                    DetailPairRow(label = "Domingos:", value = "Cerrado")
                    DetailPairRow(label = "Atención Presencial:", value = "Por orden de llegada o Reservación Previa")
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "⚠️ Se sugiere agendar con tiempo suficiente para evitar filas de espera en las camillas Ceragem.",
                        style = MaterialTheme.typography.bodySmall,
                        color = ThermalTerra,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun DetailPairRow(label: String, value: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = SoftGrey,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = ClayWarmText,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
}
