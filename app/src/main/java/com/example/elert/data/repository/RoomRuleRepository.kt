package com.example.elert.data.repository

import com.example.elert.data.local.RuleDao
import com.example.elert.data.local.toRule
import com.example.elert.data.local.toEntity
import com.example.elert.data.model.Rule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RoomRuleRepository(
    private val dao: RuleDao,
    private val scope: CoroutineScope
) : RuleRepository {

    private val _rules = MutableStateFlow<List<Rule>>(emptyList())

    init {
        scope.launch {
            dao.observeAll().collect { entities ->
                _rules.value = entities.map { it.toRule() }
            }
        }
    }

    override fun getRules(): StateFlow<List<Rule>> = _rules.asStateFlow()

    override fun currentRules(): List<Rule> = _rules.value

    override fun addRule(rule: Rule) {
        scope.launch {
            dao.insert(rule.toEntity())
        }
    }

    override fun deleteRule(id: String) {
        scope.launch {
            dao.deleteById(id)
        }
    }

    override fun toggleRuleEnabled(id: String) {
        scope.launch {
            dao.toggleEnabled(id)
        }
    }
}
