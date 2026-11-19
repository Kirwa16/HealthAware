package com.healthaware.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.healthaware.app.ui.theme.HealthAwareTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { HealthAwareTheme { HealthAwareApp() } }
    }
}

private object Routes {
    const val HOME = "home"
    const val INTRO = "screening_intro"
    const val FORM = "questionnaire"
    const val REVIEW = "review"
    const val RESULT = "result"
    const val LEARN = "learn"
    const val PROFILE = "profile"
}

@Composable
private fun HealthAwareApp() {
    val nav = rememberNavController()
    val answers = remember { mutableStateOf(ScreeningAnswers()) }
    val consent = remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            val current = nav.currentBackStackEntryAsState().value?.destination?.route
            if (current in listOf(Routes.HOME, Routes.LEARN, Routes.PROFILE)) {
                NavigationBar {
                    NavigationBarItem(
                        selected = current == Routes.HOME,
                        onClick = { nav.navigate(Routes.HOME) { launchSingleTop = true } },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home") }
                    )
                    NavigationBarItem(
                        selected = current == Routes.LEARN,
                        onClick = { nav.navigate(Routes.LEARN) { launchSingleTop = true } },
                        icon = { Icon(Icons.Default.MenuBook, contentDescription = "Learn") },
                        label = { Text("Learn") }
                    )
                    NavigationBarItem(
                        selected = current == Routes.PROFILE,
                        onClick = { nav.navigate(Routes.PROFILE) { launchSingleTop = true } },
                        icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                        label = { Text("Profile") }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = nav,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(padding)
        ) {
            composable(Routes.HOME) {
                HomeScreen(
                    onStart = { consent.value = false; answers.value = ScreeningAnswers(); nav.navigate(Routes.INTRO) },
                    onLearn = { nav.navigate(Routes.LEARN) }
                )
            }
            composable(Routes.INTRO) {
                ScreeningIntroScreen(
                    accepted = consent.value,
                    onAccepted = { consent.value = it },
                    onContinue = { nav.navigate(Routes.FORM) },
                    onBack = { nav.popBackStack() }
                )
            }
            composable(Routes.FORM) {
                QuestionnaireScreen(
                    answers = answers.value,
                    onAnswersChanged = { answers.value = it },
                    onReview = { nav.navigate(Routes.REVIEW) },
                    onBack = { nav.popBackStack() }
                )
            }
            composable(Routes.REVIEW) {
                ReviewScreen(
                    answers = answers.value,
                    onEdit = { nav.popBackStack() },
                    onSubmit = { nav.navigate(Routes.RESULT) }
                )
            }
            composable(Routes.RESULT) {
                ResultScreen(onDone = {
                    nav.popBackStack(Routes.HOME, inclusive = false)
                })
            }
            composable(Routes.LEARN) { LearnScreen() }
            composable(Routes.PROFILE) { ProfileScreen() }
        }
    }
}

@Composable
private fun PageColumn(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        content = content
    )
}

@Composable
private fun HomeScreen(onStart: () -> Unit, onLearn: () -> Unit) {
    PageColumn {
        Text("HealthAware", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Understand your health. Make informed choices.", style = MaterialTheme.typography.bodyLarge)
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(Icons.Default.Favorite, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text("Diabetes health screening", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Answer a short set of questions about selected health and lifestyle indicators.")
                Button(onClick = onStart, modifier = Modifier.fillMaxWidth()) { Text("Start screening") }
            }
        }
        Text("What you can do", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        InfoCard("Screening", "Complete a questionnaire and receive an illustrative model classification.")
        InfoCard("Learn", "Read simple information about diabetes and healthy lifestyle choices.")
        OutlinedButton(onClick = onLearn, modifier = Modifier.fillMaxWidth()) { Text("Explore health information") }
        DisclaimerText()
    }
}

@Composable
private fun ScreeningIntroScreen(
    accepted: Boolean, onAccepted: (Boolean) -> Unit, onContinue: () -> Unit, onBack: () -> Unit
) {
    PageColumn {
        Text("Before you begin", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Please read this information before completing the questionnaire.")
        InfoCard("Purpose", "HealthAware is an educational screening and awareness project. It uses selected health and lifestyle answers to demonstrate a machine-learning classification.")
        InfoCard("Not a diagnosis", "The result cannot confirm or rule out diabetes. It does not replace a healthcare professional or a blood glucose test.")
        InfoCard("Your information", "This prototype processes your answers in the app session. Do not enter identifying information. Persistent storage and a privacy policy must be added before real deployment.")
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = accepted, onCheckedChange = onAccepted)
            Text("I understand the purpose and limitations of this screening.")
        }
        Button(onClick = onContinue, enabled = accepted, modifier = Modifier.fillMaxWidth()) {
            Text("Continue to questionnaire")
        }
        TextButton(onClick = onBack) { Text("Go back") }
    }
}

data class ScreeningAnswers(
    val age: String = "",
    val heightCm: String = "",
    val weightKg: String = "",
    val highBp: Boolean? = null,
    val highChol: Boolean? = null,
    val stroke: Boolean? = null,
    val heartDisease: Boolean? = null,
    val physicalActivity: Boolean? = null,
    val generalHealth: Int? = null,
    val physicalHealthDays: String = "",
    val mentalHealthDays: String = "",
    val difficultyWalking: Boolean? = null
) {
    fun bmi(): Double? {
        val h = heightCm.toDoubleOrNull()?.div(100.0) ?: return null
        val w = weightKg.toDoubleOrNull() ?: return null
        if (h <= 0 || w <= 0) return null
        return w / (h * h)
    }

    fun isComplete(): Boolean =
        age.toIntOrNull()?.let { it in 18..120 } == true &&
        heightCm.toDoubleOrNull()?.let { it in 80.0..250.0 } == true &&
        weightKg.toDoubleOrNull()?.let { it in 20.0..350.0 } == true &&
        highBp != null && highChol != null && stroke != null && heartDisease != null &&
        physicalActivity != null && generalHealth != null &&
        physicalHealthDays.toIntOrNull()?.let { it in 0..30 } == true &&
        mentalHealthDays.toIntOrNull()?.let { it in 0..30 } == true &&
        difficultyWalking != null
}

@Composable
private fun QuestionnaireScreen(
    answers: ScreeningAnswers,
    onAnswersChanged: (ScreeningAnswers) -> Unit,
    onReview: () -> Unit,
    onBack: () -> Unit
) {
    var section by remember { mutableIntStateOf(0) }
    val sections = listOf("About you", "Medical history", "Physical wellbeing")
    PageColumn {
        Text("Screening questionnaire", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        LinearProgressIndicator(progress = { (section + 1) / 3f }, modifier = Modifier.fillMaxWidth())
        Text("Section ${section + 1} of 3 · ${sections[section]}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)

        when (section) {
            0 -> {
                NumberField("1. Age (years)", answers.age, { onAnswersChanged(answers.copy(age = it)) }, "18–120")
                NumberField("2a. Height (cm)", answers.heightCm, { onAnswersChanged(answers.copy(heightCm = it)) }, "e.g. 170")
                NumberField("2b. Weight (kg)", answers.weightKg, { onAnswersChanged(answers.copy(weightKg = it)) }, "e.g. 70")
                val bmi = answers.bmi()
                Text(if (bmi != null && bmi.isFinite()) "Calculated BMI: ${"%.1f".format(bmi)}" else "BMI will be calculated after entering height and weight.")
            }
            1 -> {
                YesNoQuestion("3. Have you ever been told by a healthcare professional that you have high blood pressure?", answers.highBp) { onAnswersChanged(answers.copy(highBp = it)) }
                YesNoQuestion("4. Have you ever been told that you have high blood cholesterol?", answers.highChol) { onAnswersChanged(answers.copy(highChol = it)) }
                YesNoQuestion("5. Have you ever had a stroke?", answers.stroke) { onAnswersChanged(answers.copy(stroke = it)) }
                YesNoQuestion("6. Have you ever been diagnosed with coronary heart disease or had a heart attack?", answers.heartDisease) { onAnswersChanged(answers.copy(heartDisease = it)) }
            }
            2 -> {
                YesNoQuestion("7. During the past 30 days, have you participated in physical activity or exercise other than your regular job?", answers.physicalActivity) { onAnswersChanged(answers.copy(physicalActivity = it)) }
                Text("8. How would you describe your general health?")
                listOf("Excellent", "Very good", "Good", "Fair", "Poor").forEachIndexed { index, label ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = answers.generalHealth == index + 1, onClick = { onAnswersChanged(answers.copy(generalHealth = index + 1)) })
                        Text(label)
                    }
                }
                NumberField("9. In the past 30 days, how many days was your physical health not good?", answers.physicalHealthDays, { onAnswersChanged(answers.copy(physicalHealthDays = it)) }, "0–30")
                NumberField("10. In the past 30 days, how many days was your mental health not good?", answers.mentalHealthDays, { onAnswersChanged(answers.copy(mentalHealthDays = it)) }, "0–30")
                YesNoQuestion("11. Do you have serious difficulty walking or climbing stairs?", answers.difficultyWalking) { onAnswersChanged(answers.copy(difficultyWalking = it)) }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            if (section > 0) OutlinedButton(onClick = { section-- }, modifier = Modifier.weight(1f)) { Text("Previous") }
            else OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f)) { Text("Exit") }
            Button(
                onClick = { if (section < 2) section++ else onReview() },
                enabled = if (section == 0) {
                    answers.age.toIntOrNull()?.let { it in 18..120 } == true &&
                    answers.heightCm.toDoubleOrNull()?.let { it in 80.0..250.0 } == true &&
                    answers.weightKg.toDoubleOrNull()?.let { it in 20.0..350.0 } == true
                } else if (section == 1) answers.highBp != null && answers.highChol != null && answers.stroke != null && answers.heartDisease != null
                else answers.physicalActivity != null && answers.generalHealth != null &&
                    answers.physicalHealthDays.toIntOrNull()?.let { it in 0..30 } == true &&
                    answers.mentalHealthDays.toIntOrNull()?.let { it in 0..30 } == true && answers.difficultyWalking != null,
                modifier = Modifier.weight(1f)
            ) { Text(if (section == 2) "Review answers" else "Next") }
        }
    }
}

@Composable
private fun NumberField(label: String, value: String, onValueChange: (String) -> Unit, supporting: String) {
    OutlinedTextField(
        value = value, onValueChange = { input -> if (input.all { it.isDigit() } && input.length <= 3) onValueChange(input) },
        label = { Text(label) }, supportingText = { Text(supporting) },
        modifier = Modifier.fillMaxWidth(), singleLine = true
    )
}

@Composable
private fun YesNoQuestion(question: String, value: Boolean?, onSelect: (Boolean) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(question, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = value == true, onClick = { onSelect(true) }); Text("Yes")
            }
            Spacer(Modifier.width(20.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = value == false, onClick = { onSelect(false) }); Text("No")
            }
        }
    }
}

@Composable
private fun ReviewScreen(answers: ScreeningAnswers, onEdit: () -> Unit, onSubmit: () -> Unit) {
    PageColumn {
        Text("Review your answers", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Check your responses before submitting.")
        ReviewRow("Age", "${answers.age} years")
        ReviewRow("Height", "${answers.heightCm} cm")
        ReviewRow("Weight", "${answers.weightKg} kg")
        ReviewRow("Calculated BMI", answers.bmi()?.let { "%.1f".format(it) } ?: "Not available")
        ReviewRow("High blood pressure", yesNo(answers.highBp))
        ReviewRow("High cholesterol", yesNo(answers.highChol))
        ReviewRow("Previous stroke", yesNo(answers.stroke))
        ReviewRow("Heart disease / heart attack", yesNo(answers.heartDisease))
        ReviewRow("Physical activity", yesNo(answers.physicalActivity))
        ReviewRow("General health", listOf("", "Excellent", "Very good", "Good", "Fair", "Poor").getOrElse(answers.generalHealth ?: 0) { "Not answered" })
        ReviewRow("Poor physical health days", answers.physicalHealthDays)
        ReviewRow("Poor mental health days", answers.mentalHealthDays)
        ReviewRow("Difficulty walking", yesNo(answers.difficultyWalking))
        InfoCard("Prototype notice", "This build uses a demonstration result. Your answers are not sent to a trained machine-learning model.")
        Button(onClick = onSubmit, modifier = Modifier.fillMaxWidth()) { Text("Submit screening") }
        OutlinedButton(onClick = onEdit, modifier = Modifier.fillMaxWidth()) { Text("Edit answers") }
    }
}

private fun yesNo(value: Boolean?): String = when (value) { true -> "Yes"; false -> "No"; null -> "Not answered" }

@Composable
private fun ReviewRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, modifier = Modifier.weight(1.2f), color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium)
    }
    HorizontalDivider()
}

@Composable
private fun ResultScreen(onDone: () -> Unit) {
    PageColumn {
        Text("Your screening result", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(Icons.Default.Info, contentDescription = null)
                Text("Demonstration result", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("The machine-learning model has not yet been connected. No classification has been calculated from your answers.")
            }
        }
        Text("What to do next", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        Text("If you have concerns about diabetes or your health, contact a qualified healthcare professional for appropriate assessment. A questionnaire cannot diagnose diabetes.")
        DisclaimerText()
        Button(onClick = onDone, modifier = Modifier.fillMaxWidth()) { Text("Return to home") }
    }
}

@Composable
private fun LearnScreen() {
    PageColumn {
        Text("Learn", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("General health information")
        InfoCard("What is diabetes?", "Diabetes is a condition involving elevated blood glucose. Diagnosis requires appropriate clinical assessment and testing.")
        InfoCard("Healthy habits", "Regular physical activity, balanced eating, adequate sleep and routine healthcare can support general wellbeing.")
        InfoCard("Know your numbers", "A healthcare professional can advise whether blood glucose testing or other checks are appropriate for you.")
        DisclaimerText()
    }
}

@Composable
private fun ProfileScreen() {
    PageColumn {
        Text("Profile & information", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        InfoCard("About HealthAware", "A student project for diabetes-related screening awareness using selected health and lifestyle indicators.")
        InfoCard("Data and privacy", "This prototype does not create an account or save screening history. Privacy controls must be reviewed before real-world deployment.")
        InfoCard("Limitations", "The model is based on a US survey dataset. Its performance and relevance for people in Kenya have not been established.")
        DisclaimerText()
    }
}

@Composable
private fun InfoCard(title: String, body: String) {
    Card {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(body, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun DisclaimerText() {
    Text(
        "HealthAware is an educational screening prototype, not a medical device or diagnostic service. It does not replace professional medical advice, diagnosis or treatment.",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}
