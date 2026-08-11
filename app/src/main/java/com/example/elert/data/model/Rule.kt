package com.example.elert.data.model

data class Rule(
    val id: String,
    val title: String,
    val appName: String,
    val packageName: String,
    val keywords: List<String> = emptyList(),
    val contacts: List<String> = emptyList(),
    val isEnabled: Boolean = true,
    val activeHours: ActiveHours = ActiveHours()
) {
    fun displayKeywords(): String =
        keywords.joinToString(", ").ifEmpty { "—" }

    fun displayContacts(): String =
        contacts.joinToString(", ").ifEmpty { "—" }
}
