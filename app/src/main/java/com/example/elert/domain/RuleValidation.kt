package com.example.elert.domain

object RuleValidation {

    const val MAX_ITEMS = 3

    fun normalizeItems(values: List<String>): List<String> =
        values.map { it.trim() }.filter { it.isNotEmpty() }.take(MAX_ITEMS)

    fun isValidForSave(keywords: List<String>, contacts: List<String>): Boolean =
        normalizeItems(keywords).isNotEmpty() || normalizeItems(contacts).isNotEmpty()

    fun canProceedFromKeywordStep(keywords: List<String>): Boolean =
        normalizeItems(keywords).isNotEmpty()

    fun canProceedFromContactStep(keywords: List<String>, contacts: List<String>): Boolean =
        normalizeItems(keywords).isNotEmpty() || normalizeItems(contacts).isNotEmpty()
}
