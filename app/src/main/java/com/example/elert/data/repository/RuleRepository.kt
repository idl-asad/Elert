package com.example.elert.data.repository

import com.example.elert.data.model.Rule
import kotlinx.coroutines.flow.StateFlow

interface RuleRepository {
    fun getRules(): StateFlow<List<Rule>>
    fun currentRules(): List<Rule>
    fun addRule(rule: Rule)
    fun deleteRule(id: String)
    fun toggleRuleEnabled(id: String)
}
