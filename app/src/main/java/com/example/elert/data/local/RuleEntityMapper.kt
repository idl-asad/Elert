package com.example.elert.data.local

import com.example.elert.data.model.ActiveHours
import com.example.elert.data.model.Rule
import com.example.elert.domain.RuleValidation

fun RuleEntity.toRule(): Rule = Rule(
    id = id,
    title = title,
    appName = appName,
    packageName = packageName,
    keywords = RuleValidation.normalizeItems(StringListConverter.toStringList(keywordsJson)),
    contacts = RuleValidation.normalizeItems(StringListConverter.toStringList(contactsJson)),
    isEnabled = isEnabled,
    activeHours = ActiveHours(startHour = startHour, endHour = endHour)
)

fun Rule.toEntity(): RuleEntity = RuleEntity(
    id = id,
    title = title,
    appName = appName,
    packageName = packageName,
    keywordsJson = StringListConverter.fromStringList(RuleValidation.normalizeItems(keywords)),
    contactsJson = StringListConverter.fromStringList(RuleValidation.normalizeItems(contacts)),
    isEnabled = isEnabled,
    startHour = activeHours.startHour,
    endHour = activeHours.endHour
)
