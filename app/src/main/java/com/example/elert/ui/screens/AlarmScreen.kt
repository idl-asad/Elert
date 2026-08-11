package com.example.elert.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun AlarmScreen(
    ruleTitle: String = "",
    appName: String = "",
    keywords: List<String> = emptyList(),
    contacts: List<String> = emptyList(),
    notificationTitle: String = "",
    notificationBody: String = "",
    onDismiss: () -> Unit
) {
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Elert Alarm",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.error
            )
            Text(
                text = ruleTitle.ifEmpty { "Rule matched" },
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp)
            )
            if (appName.isNotBlank()) {
                Text(
                    text = appName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            if (keywords.isNotEmpty()) {
                Text(
                    text = "Keywords: ${keywords.joinToString(", ")}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            if (contacts.isNotEmpty()) {
                Text(
                    text = "Contacts: ${contacts.joinToString(", ")}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            if (notificationTitle.isNotBlank() || notificationBody.isNotBlank()) {
                Text(
                    text = notificationTitle.ifEmpty { "—" },
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 24.dp)
                )
                Text(
                    text = notificationBody.ifEmpty { "—" },
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp)
            ) {
                Text("Dismiss")
            }
        }
    }
}
