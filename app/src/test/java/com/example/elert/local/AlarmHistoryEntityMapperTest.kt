package com.example.elert.local

import com.example.elert.data.local.toEntity
import com.example.elert.data.local.toModel
import com.example.elert.data.model.AlarmHistory
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AlarmHistoryEntityMapperTest {

    @Test
    fun roundTrip_preservesAllFields() {
        val history = AlarmHistory(
            id = "h-1",
            ruleId = "r-1",
            ruleTitle = "PagerDuty Critical",
            appName = "PagerDuty",
            keywords = listOf("critical", "sev-1"),
            notificationTitle = "Incident 42",
            notificationBody = "SEV-1: database down",
            triggeredAt = 1_700_000_000_000L
        )

        val restored = history.toEntity().toModel()

        assertEquals(history, restored)
    }

    @Test
    fun roundTrip_handlesEmptyKeywords() {
        val history = AlarmHistory(
            id = "h-2",
            ruleId = "r-2",
            ruleTitle = "Silent rule",
            appName = "Slack",
            keywords = emptyList(),
            notificationTitle = "",
            notificationBody = "",
            triggeredAt = 1_700_000_000_000L
        )

        val restored = history.toEntity().toModel()

        assertEquals(history, restored)
    }
}
