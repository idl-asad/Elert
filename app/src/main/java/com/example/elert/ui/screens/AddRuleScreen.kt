package com.example.elert.ui.screens

import android.Manifest
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.elert.data.ContactsProvider
import com.example.elert.data.InstalledAppsProvider
import com.example.elert.data.model.ActiveHours
import com.example.elert.data.model.DeviceContact
import com.example.elert.data.model.InstalledApp
import com.example.elert.data.model.Rule
import com.example.elert.domain.RuleValidation
import com.example.elert.ui.components.ActiveHoursTimeParser
import com.example.elert.ui.components.ActiveHoursWizardStep
import com.example.elert.ui.components.WizardGlassTextField
import com.example.elert.ui.components.WizardInfoCard
import com.example.elert.ui.components.WizardLogicBadge
import com.example.elert.ui.components.WizardMainPanel
import com.example.elert.ui.components.WizardNavigationButtons
import com.example.elert.ui.components.WizardProgressTracker
import com.example.elert.ui.components.WizardRuleBadge
import com.example.elert.ui.components.WizardStepHeader
import com.example.elert.ui.components.WizardTipItem
import com.example.elert.ui.components.WizardTopBar
import com.example.elert.ui.theme.ElertBackground
import com.example.elert.ui.theme.ElertGlassBorderSubtle
import com.example.elert.ui.theme.ElertSurfaceContainerHigh
import com.example.elert.ui.theme.ElertSurfaceContainerLowest
import com.example.elert.viewmodel.RuleViewModel
import java.util.UUID

private enum class WizardStep(
    val headline: String,
    val description: String,
    val category: String = "Rule Configuration"
) {
    Title(
        headline = "Name Your Rule",
        description = "Give this alert a clear, memorable name so you can quickly identify it on your dashboard."
    ),
    App(
        headline = "Choose App",
        description = "Select which app Elert should monitor. Notifications from this app will be checked against your rule."
    ),
    Keyword(
        headline = "Define Keywords",
        description = "Elert will trigger an alarm whenever these terms are detected in notification title or body text."
    ),
    Contact(
        headline = "Select Contacts",
        description = "Optionally filter by sender name. Names are matched against notification title and body."
    ),
    ActiveHours(
        headline = "When should this rule run?",
        description = "Define the 24-hour window during which Elert monitors this specific rule."
    ),
    Review(
        headline = "Review & Save",
        description = "Confirm your rule settings before enabling real-time monitoring."
    )
}

private fun wizardContinueLabel(step: WizardStep, stepIndex: Int): String {
    if (step == WizardStep.Review) return "Save rule"
    val nextStep = WizardStep.entries.getOrNull(stepIndex + 1) ?: return "Continue"
    return when (nextStep) {
        WizardStep.App -> "Next: Choose app"
        WizardStep.Keyword -> "Next: Define keywords"
        WizardStep.Contact -> "Next: Select contacts"
        WizardStep.ActiveHours -> "Next: Active hours"
        WizardStep.Review -> "Next: Review rule"
        WizardStep.Title -> "Continue"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRuleScreen(
    onNavigateBack: () -> Unit,
    viewModel: RuleViewModel = viewModel()
) {
    var stepIndex by rememberSaveable { mutableIntStateOf(0) }
    var title by rememberSaveable { mutableStateOf("") }
    var appName by rememberSaveable { mutableStateOf("") }
    var packageName by rememberSaveable { mutableStateOf("") }
    var startHourText by rememberSaveable { mutableStateOf("08:00") }
    var endHourText by rememberSaveable { mutableStateOf("18:00") }

    val keywordFields = rememberSaveable(
        saver = listSaver(
            save = { it.toList() },
            restore = { saved ->
                if (saved.isEmpty()) mutableStateListOf("") else saved.toMutableStateList()
            }
        )
    ) { mutableStateListOf("", "", "") }

    val manualContactFields = rememberSaveable(
        saver = listSaver(
            save = { it.toList() },
            restore = { saved ->
                if (saved.isEmpty()) mutableStateListOf("") else saved.toMutableStateList()
            }
        )
    ) { mutableStateListOf("") }

    val selectedContactNames = rememberSaveable(
        saver = listSaver(
            save = { it.toList() },
            restore = { it.toMutableStateList() }
        )
    ) { mutableStateListOf<String>() }

    val context = LocalContext.current
    var hasContactsPermission by remember {
        mutableStateOf(ContactsProvider.hasContactsPermission(context))
    }
    var deviceContacts by remember(hasContactsPermission) {
        mutableStateOf(
            if (hasContactsPermission) ContactsProvider.loadContacts(context) else emptyList()
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasContactsPermission = granted
        if (granted) {
            deviceContacts = ContactsProvider.loadContacts(context)
            manualContactFields.clear()
            if (manualContactFields.isEmpty()) manualContactFields.add("")
        }
    }

    val keywords = RuleValidation.normalizeItems(keywordFields.toList())
    val contactInputs = if (hasContactsPermission) {
        selectedContactNames.toList()
    } else {
        manualContactFields.toList()
    }
    val contacts = RuleValidation.normalizeItems(contactInputs)
    val isContactOnlyRule = contacts.isNotEmpty() && keywords.isEmpty()
    var showContactOnlyWarning by remember { mutableStateOf(false) }

    val step = WizardStep.entries[stepIndex]
    val totalSteps = WizardStep.entries.size
    val currentStepNumber = stepIndex + 1

    fun goBack() {
        if (stepIndex == 0) onNavigateBack() else stepIndex--
    }

    fun goNext() {
        if (stepIndex < WizardStep.entries.lastIndex) stepIndex++
    }

    fun persistRule() {
        if (!RuleValidation.isValidForSave(keywordFields.toList(), contactInputs)) return
        val startHour = ActiveHoursTimeParser.parseHour(startHourText) ?: 0
        val endHour = ActiveHoursTimeParser.parseHour(endHourText) ?: 23
        viewModel.addRule(
            Rule(
                id = UUID.randomUUID().toString(),
                title = title.trim(),
                appName = appName,
                packageName = packageName,
                keywords = RuleValidation.normalizeItems(keywordFields.toList()),
                contacts = contacts,
                isEnabled = true,
                activeHours = ActiveHours(startHour = startHour, endHour = endHour)
            )
        )
        onNavigateBack()
    }

    fun onSaveClicked() {
        if (!RuleValidation.isValidForSave(keywordFields.toList(), contactInputs)) return
        if (isContactOnlyRule) {
            showContactOnlyWarning = true
        } else {
            persistRule()
        }
    }

    val canProceed = when (step) {
        WizardStep.Title -> title.isNotBlank()
        WizardStep.App -> packageName.isNotBlank()
        WizardStep.Keyword -> true
        WizardStep.Contact -> RuleValidation.canProceedFromContactStep(
            keywordFields.toList(),
            contactInputs
        )
        WizardStep.ActiveHours -> ActiveHoursTimeParser.isValidRange(startHourText, endHourText)
        WizardStep.Review -> RuleValidation.isValidForSave(keywordFields.toList(), contactInputs)
    }

    val continueLabel = wizardContinueLabel(step, stepIndex)

    if (showContactOnlyWarning) {
        AlertDialog(
            onDismissRequest = { showContactOnlyWarning = false },
            title = { Text("No keywords selected") },
            text = {
                Text(
                    "The alarm will trigger for any message notification from " +
                        "${contacts.joinToString(", ")} on $appName. " +
                        "Add at least one keyword to require a specific message match " +
                        "(contact and keyword must both match)."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showContactOnlyWarning = false
                        persistRule()
                    }
                ) {
                    Text("Save anyway")
                }
            },
            dismissButton = {
                TextButton(onClick = { showContactOnlyWarning = false }) {
                    Text("Go back")
                }
            }
        )
    }

    Scaffold(
        containerColor = ElertBackground,
        topBar = {
            WizardTopBar(
                onBackClick = ::goBack,
                onCloseClick = onNavigateBack,
                showBack = stepIndex > 0
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp)
        ) {
            if (step != WizardStep.ActiveHours) {
                WizardProgressTracker(
                    currentStep = currentStepNumber,
                    totalSteps = totalSteps,
                    categoryLabel = step.category,
                    modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
                )
            } else {
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (step == WizardStep.ActiveHours) {
                ActiveHoursWizardStep(
                    startTimeText = startHourText,
                    endTimeText = endHourText,
                    onStartTimeChange = { startHourText = it },
                    onEndTimeChange = { endHourText = it },
                    onContinue = ::goNext,
                    continueLabel = continueLabel,
                    canContinue = canProceed,
                    stepNumber = currentStepNumber,
                    totalSteps = totalSteps
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    WizardRuleBadge(ruleTitle = title.trim())
                }
            } else {
                WizardStepHeader(
                    headline = step.headline,
                    description = step.description,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                WizardMainPanel {
                    when (step) {
                        WizardStep.Title -> TitleStepContent(
                            title = title,
                            onTitleChange = { title = it }
                        )
                        WizardStep.App -> AppStepContent(
                            selectedPackageName = packageName,
                            onSelect = { app ->
                                appName = app.label
                                packageName = app.packageName
                            }
                        )
                        WizardStep.Keyword -> KeywordStepContent(
                            keywordFields = keywordFields,
                            hasContacts = contacts.isNotEmpty()
                        )
                        WizardStep.Contact -> ContactStepContent(
                            hasPermission = hasContactsPermission,
                            deviceContacts = deviceContacts,
                            selectedNames = selectedContactNames,
                            manualFields = manualContactFields,
                            onRequestPermission = {
                                permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                            },
                            onOpenSettings = {
                                context.startActivity(
                                    android.content.Intent(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                        Uri.fromParts("package", context.packageName, null)
                                    ).addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                                )
                            }
                        )
                        WizardStep.Review -> ReviewStepContent(
                            title = title.trim(),
                            appName = appName,
                            packageName = packageName,
                            keywords = keywords,
                            contacts = contacts,
                            startHourText = startHourText,
                            endHourText = endHourText
                        )
                        WizardStep.ActiveHours -> Unit
                    }

                    if (step != WizardStep.ActiveHours) {
                        WizardNavigationButtons(
                            onContinue = {
                                if (step == WizardStep.Review) onSaveClicked() else goNext()
                            },
                            continueLabel = continueLabel,
                            continueEnabled = canProceed
                        )
                    }
                }

                StepSidePanels(
                    step = step,
                    hasContacts = contacts.isNotEmpty(),
                    hasKeywords = keywords.isNotEmpty(),
                    modifier = Modifier.padding(top = 12.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    WizardRuleBadge(ruleTitle = title.trim())
                }
            }
        }
    }
}

@Composable
private fun StepSidePanels(
    step: WizardStep,
    hasContacts: Boolean,
    hasKeywords: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        when (step) {
            WizardStep.Keyword -> {
                WizardInfoCard(
                    title = "Match Logic",
                    titleIcon = Icons.Default.Psychology,
                    showGlow = true
                ) {
                    Text(
                        text = "Matches are case-insensitive. Any one keyword can trigger the rule (OR logic between fields).",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    WizardLogicBadge(text = "OR logic applied between fields")
                }
                WizardInfoCard(title = "Pro Tips", titleIcon = Icons.Default.Psychology) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        WizardTipItem("Use specific terms like \"URGENT\" or \"CRITICAL\" for higher signal accuracy.")
                        WizardTipItem("Avoid generic terms like \"Update\" or \"Log\" which might cause false alarms.")
                    }
                }
            }
            WizardStep.Contact -> {
                WizardInfoCard(
                    title = "Match Logic",
                    titleIcon = Icons.Default.Psychology,
                    showGlow = true
                ) {
                    Text(
                        text = if (hasKeywords) {
                            "When both contacts and keywords are set, both must match (AND logic)."
                        } else {
                            "Contact-only rules trigger on any message from the selected contact."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    WizardLogicBadge(
                        text = if (hasKeywords) "AND logic with keywords" else "Contact name match only"
                    )
                }
            }
            WizardStep.App -> {
                WizardInfoCard(title = "Pro Tips", titleIcon = Icons.Default.Apps) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        WizardTipItem("Pick the app where urgent notifications arrive — WhatsApp, Slack, Gmail, etc.")
                        WizardTipItem("Only notifications from the selected app are checked against this rule.")
                    }
                }
            }
            WizardStep.Review -> {
                WizardInfoCard(
                    title = "Match Logic",
                    titleIcon = Icons.Default.Psychology,
                    showGlow = true
                ) {
                    val matchText = when {
                        hasKeywords && hasContacts -> "Contact AND any keyword must match"
                        hasContacts -> "Any message from selected contacts"
                        hasKeywords -> "Any keyword matches (OR between keywords)"
                        else -> "Add at least one keyword or contact"
                    }
                    WizardLogicBadge(text = matchText)
                }
            }
            WizardStep.Title -> Unit
            WizardStep.ActiveHours -> Unit
        }
    }
}

@Composable
private fun TitleStepContent(
    title: String,
    onTitleChange: (String) -> Unit
) {
    WizardGlassTextField(
        value = title,
        onValueChange = onTitleChange,
        label = "Rule Name",
        placeholder = "e.g. Critical Infrastructure Monitor",
        leadingIcon = Icons.Default.Title
    )
}

@Composable
private fun KeywordStepContent(
    keywordFields: MutableList<String>,
    hasContacts: Boolean
) {
    val labels = listOf("Primary Keyword", "Secondary Keyword", "Tertiary Keyword")
    val placeholders = listOf("e.g. Server Outage", "e.g. Critical Error", "Optional term")

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        labels.forEachIndexed { index, label ->
            val value = keywordFields.getOrElse(index) { "" }
            WizardGlassTextField(
                value = value,
                onValueChange = { newValue ->
                    while (keywordFields.size <= index) keywordFields.add("")
                    keywordFields[index] = newValue
                },
                label = label,
                placeholder = placeholders[index]
            )
        }
        if (hasContacts) {
            Text(
                text = "Required when contacts are set — contact and keyword must both match.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun AppStepContent(
    selectedPackageName: String,
    onSelect: (InstalledApp) -> Unit
) {
    val context = LocalContext.current
    val installedApps = remember { InstalledAppsProvider.getLaunchableApps(context) }
    var searchQuery by rememberSaveable { mutableStateOf("") }

    val filteredApps = remember(installedApps, searchQuery) {
        if (searchQuery.isBlank()) {
            installedApps
        } else {
            installedApps.filter { app ->
                app.label.contains(searchQuery, ignoreCase = true) ||
                    app.packageName.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        WizardGlassTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = "Search Apps",
            placeholder = "Search by name or package",
            leadingIcon = Icons.Default.Search
        )
        if (filteredApps.isEmpty()) {
            Text(
                text = "No apps found",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Column(
                modifier = Modifier.height(280.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(filteredApps, key = { it.packageName }) { app ->
                        WizardAppListItem(
                            app = app,
                            selected = selectedPackageName == app.packageName,
                            onClick = { onSelect(app) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WizardAppListItem(
    app: InstalledApp,
    selected: Boolean,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val icon = remember(app.packageName) {
        runCatching {
            context.packageManager
                .getApplicationIcon(app.packageName)
                .toBitmap(width = 96, height = 96)
                .asImageBitmap()
        }.getOrNull()
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (selected) ElertSurfaceContainerHigh.copy(alpha = 0.5f)
                else ElertSurfaceContainerLowest.copy(alpha = 0.3f)
            )
            .border(
                width = 1.dp,
                color = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                else ElertGlassBorderSubtle,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.primary,
                unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        if (icon != null) {
            Image(
                bitmap = icon,
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        }
        Column(modifier = Modifier.padding(start = 12.dp)) {
            Text(
                text = app.label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
            )
            Text(
                text = app.packageName,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
private fun ContactStepContent(
    hasPermission: Boolean,
    deviceContacts: List<DeviceContact>,
    selectedNames: MutableList<String>,
    manualFields: MutableList<String>,
    onRequestPermission: () -> Unit,
    onOpenSettings: () -> Unit
) {
    if (hasPermission) {
        var searchQuery by rememberSaveable { mutableStateOf("") }
        val filtered = remember(deviceContacts, searchQuery) {
            if (searchQuery.isBlank()) deviceContacts
            else deviceContacts.filter { it.displayName.contains(searchQuery, ignoreCase = true) }
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            WizardGlassTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = "Search Contacts",
                placeholder = "Filter by name",
                leadingIcon = Icons.Default.Search
            )
            if (selectedNames.isNotEmpty()) {
                Text(
                    text = "Selected: ${selectedNames.joinToString(", ")}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Column(
                modifier = Modifier.height(240.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                if (filtered.isEmpty()) {
                    Text(
                        text = "No contacts found",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        items(filtered, key = { it.id }) { contact ->
                            val checked = selectedNames.contains(contact.displayName)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        if (checked) {
                                            selectedNames.remove(contact.displayName)
                                        } else if (selectedNames.size < RuleValidation.MAX_ITEMS) {
                                            selectedNames.add(contact.displayName)
                                        }
                                    }
                                    .padding(vertical = 4.dp, horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = checked,
                                    onCheckedChange = { isChecked ->
                                        if (isChecked && selectedNames.size < RuleValidation.MAX_ITEMS) {
                                            selectedNames.add(contact.displayName)
                                        } else {
                                            selectedNames.remove(contact.displayName)
                                        }
                                    },
                                    enabled = checked || selectedNames.size < RuleValidation.MAX_ITEMS,
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = MaterialTheme.colorScheme.primary,
                                        checkmarkColor = MaterialTheme.colorScheme.onPrimary
                                    )
                                )
                                Text(
                                    text = contact.displayName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "Grant contacts permission to pick from your address book, or enter names manually.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onRequestPermission) { Text("Allow contacts") }
                TextButton(onClick = onOpenSettings) { Text("App settings") }
            }
            val labels = listOf("Primary Contact", "Secondary Contact", "Tertiary Contact")
            labels.forEachIndexed { index, label ->
                val value = manualFields.getOrElse(index) { "" }
                WizardGlassTextField(
                    value = value,
                    onValueChange = { newValue ->
                        while (manualFields.size <= index) manualFields.add("")
                        manualFields[index] = newValue
                    },
                    label = label,
                    placeholder = "Contact name",
                    leadingIcon = Icons.Default.Person
                )
            }
        }
    }
}

@Composable
private fun ReviewStepContent(
    title: String,
    appName: String,
    packageName: String,
    keywords: List<String>,
    contacts: List<String>,
    startHourText: String,
    endHourText: String
) {
    val matchMode = when {
        keywords.isNotEmpty() && contacts.isNotEmpty() -> "Contact AND any keyword"
        contacts.isNotEmpty() -> "Contact only (any message)"
        keywords.isNotEmpty() -> "Any keyword (OR)"
        else -> "—"
    }

    val rows = listOf(
        "Title" to title,
        "App" to appName,
        "Package" to packageName,
        "Keywords" to keywords.joinToString(", ").ifEmpty { "—" },
        "Contacts" to contacts.joinToString(", ").ifEmpty { "—" },
        "Match mode" to matchMode,
        "Active hours" to "${formatActiveHoursDisplay(startHourText)} – ${formatActiveHoursDisplay(endHourText)}"
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        rows.forEach { (label, value) ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(ElertSurfaceContainerLowest.copy(alpha = 0.3f))
                    .border(1.dp, ElertGlassBorderSubtle, RoundedCornerShape(8.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = label.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

private fun formatActiveHoursDisplay(text: String): String {
    val hour = ActiveHoursTimeParser.parseHour(text) ?: return text
    return ActiveHoursTimeParser.formatHour(hour)
}
