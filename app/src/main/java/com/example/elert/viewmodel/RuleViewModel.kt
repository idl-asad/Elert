package com.example.elert.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.elert.ElertApplication
import com.example.elert.data.model.Rule
import com.example.elert.data.repository.RuleRepository
import kotlinx.coroutines.flow.StateFlow

class RuleViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository: RuleRepository = ElertApplication.from(application).ruleRepository

    val rules: StateFlow<List<Rule>> = repository.getRules()

    fun addRule(rule: Rule) {
        repository.addRule(rule)
    }

    fun deleteRule(id: String) {
        repository.deleteRule(id)
    }

    fun toggleRuleEnabled(id: String) {
        repository.toggleRuleEnabled(id)
    }
}
