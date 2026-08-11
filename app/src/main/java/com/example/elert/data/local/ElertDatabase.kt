package com.example.elert.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [RuleEntity::class],
    version = 3,
    exportSchema = false
)
abstract class ElertDatabase : RoomDatabase() {

    abstract fun ruleDao(): RuleDao

    companion object {
        @Volatile
        private var instance: ElertDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE rules ADD COLUMN keywordsJson TEXT NOT NULL DEFAULT '[]'"
                )
                db.execSQL(
                    "ALTER TABLE rules ADD COLUMN contactsJson TEXT NOT NULL DEFAULT '[]'"
                )
                db.execSQL(
                    """
                    UPDATE rules SET keywordsJson =
                        CASE
                            WHEN keyword IS NULL OR TRIM(keyword) = '' THEN '[]'
                            ELSE '["' || REPLACE(keyword, '"', '\"') || '"]'
                        END
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    UPDATE rules SET contactsJson =
                        CASE
                            WHEN contact IS NULL OR TRIM(contact) = '' THEN '[]'
                            ELSE '["' || REPLACE(contact, '"', '\"') || '"]'
                        END
                    """.trimIndent()
                )
                rebuildRulesTable(db)
            }
        }

        /** Fixes DBs that reached v2 with legacy keyword/contact columns still present. */
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                if (hasColumn(db, "rules", "keyword") || hasColumn(db, "rules", "contact")) {
                    rebuildRulesTable(db)
                }
            }
        }

        fun getInstance(context: Context): ElertDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    ElertDatabase::class.java,
                    "elert.db"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build()
                    .also { instance = it }
            }
        }

        private fun rebuildRulesTable(db: SupportSQLiteDatabase) {
            db.execSQL("DROP TABLE IF EXISTS rules_new")
            db.execSQL(
                """
                CREATE TABLE rules_new (
                    id TEXT NOT NULL PRIMARY KEY,
                    title TEXT NOT NULL,
                    appName TEXT NOT NULL,
                    packageName TEXT NOT NULL,
                    keywordsJson TEXT NOT NULL,
                    contactsJson TEXT NOT NULL,
                    isEnabled INTEGER NOT NULL,
                    startHour INTEGER NOT NULL,
                    endHour INTEGER NOT NULL
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                INSERT INTO rules_new (
                    id, title, appName, packageName, keywordsJson, contactsJson,
                    isEnabled, startHour, endHour
                )
                SELECT
                    id, title, appName, packageName, keywordsJson, contactsJson,
                    isEnabled, startHour, endHour
                FROM rules
                """.trimIndent()
            )
            db.execSQL("DROP TABLE rules")
            db.execSQL("ALTER TABLE rules_new RENAME TO rules")
        }

        private fun hasColumn(
            db: SupportSQLiteDatabase,
            table: String,
            column: String
        ): Boolean {
            db.query("PRAGMA table_info($table)").use { cursor ->
                val nameIndex = cursor.getColumnIndex("name")
                if (nameIndex < 0) return false
                while (cursor.moveToNext()) {
                    if (column.equals(cursor.getString(nameIndex), ignoreCase = true)) {
                        return true
                    }
                }
            }
            return false
        }
    }
}
