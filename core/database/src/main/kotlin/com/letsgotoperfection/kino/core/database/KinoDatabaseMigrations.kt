package com.letsgotoperfection.kino.core.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Manual Room migrations for [KinoDatabase].
 *
 * Each migration matches the diff between the exported schema JSONs in
 * `core/database/schemas`. Keep them in sync whenever the database version
 * is bumped — never rely on destructive fallback in production.
 */
object KinoDatabaseMigrations {

    /**
     * v1 → v2: recurring tasks introduced.
     * - `tasks` gains nullable `recurringTaskId` and `scheduledDate` linkage columns
     * - new `recurring_tasks` table
     */
    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE `tasks` ADD COLUMN `recurringTaskId` TEXT")
            db.execSQL("ALTER TABLE `tasks` ADD COLUMN `scheduledDate` INTEGER")
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `recurring_tasks` (
                    `id` TEXT NOT NULL,
                    `title` TEXT NOT NULL,
                    `description` TEXT NOT NULL,
                    `section` TEXT NOT NULL,
                    `priority` TEXT NOT NULL,
                    `frequency` TEXT NOT NULL,
                    `interval` INTEGER NOT NULL,
                    `daysOfWeek` TEXT NOT NULL,
                    `dayOfMonth` INTEGER,
                    `monthOfYear` INTEGER,
                    `timeOfDay` TEXT NOT NULL,
                    `startDate` INTEGER NOT NULL,
                    `endDate` INTEGER,
                    `isActive` INTEGER NOT NULL,
                    `createdAt` INTEGER NOT NULL,
                    `updatedAt` INTEGER NOT NULL,
                    `lastGeneratedDate` INTEGER,
                    PRIMARY KEY(`id`)
                )
                """.trimIndent()
            )
        }
    }

    /**
     * v2 → v3: new `sections` table.
     */
    private val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `sections` (
                    `id` TEXT NOT NULL,
                    `name` TEXT NOT NULL,
                    `orderIndex` INTEGER NOT NULL,
                    PRIMARY KEY(`id`)
                )
                """.trimIndent()
            )
        }
    }

    /**
     * v3 → v4: performance pass.
     * - indices on `tasks` and `notes`
     * - `task_labels` / `note_labels` recreated with CASCADE foreign keys + indices
     */
    private val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Task indices
            db.execSQL("CREATE INDEX IF NOT EXISTS `idx_task_section` ON `tasks` (`section`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `idx_task_column` ON `tasks` (`column`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `idx_task_updated_at` ON `tasks` (`updatedAt`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `idx_task_due_date` ON `tasks` (`dueDate`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `idx_task_section_column` ON `tasks` (`section`, `column`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `idx_task_recurring_task_id` ON `tasks` (`recurringTaskId`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `idx_task_scheduled_date` ON `tasks` (`scheduledDate`)")

            // Note indices
            db.execSQL("CREATE INDEX IF NOT EXISTS `idx_note_is_pinned` ON `notes` (`isPinned`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `idx_note_updated_at` ON `notes` (`updatedAt`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `idx_note_pinned_updated` ON `notes` (`isPinned`, `updatedAt`)")

            // task_labels: recreate with foreign keys, dropping orphaned rows
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `task_labels_new` (
                    `taskId` TEXT NOT NULL,
                    `labelId` TEXT NOT NULL,
                    PRIMARY KEY(`taskId`, `labelId`),
                    FOREIGN KEY(`taskId`) REFERENCES `tasks`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE,
                    FOREIGN KEY(`labelId`) REFERENCES `labels`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                INSERT OR IGNORE INTO `task_labels_new` (`taskId`, `labelId`)
                SELECT `taskId`, `labelId` FROM `task_labels`
                WHERE `taskId` IN (SELECT `id` FROM `tasks`)
                  AND `labelId` IN (SELECT `id` FROM `labels`)
                """.trimIndent()
            )
            db.execSQL("DROP TABLE `task_labels`")
            db.execSQL("ALTER TABLE `task_labels_new` RENAME TO `task_labels`")
            db.execSQL("CREATE INDEX IF NOT EXISTS `idx_task_label_task_id` ON `task_labels` (`taskId`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `idx_task_label_label_id` ON `task_labels` (`labelId`)")

            // note_labels: recreate with foreign keys, dropping orphaned rows
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `note_labels_new` (
                    `noteId` TEXT NOT NULL,
                    `labelId` TEXT NOT NULL,
                    PRIMARY KEY(`noteId`, `labelId`),
                    FOREIGN KEY(`noteId`) REFERENCES `notes`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE,
                    FOREIGN KEY(`labelId`) REFERENCES `labels`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                INSERT OR IGNORE INTO `note_labels_new` (`noteId`, `labelId`)
                SELECT `noteId`, `labelId` FROM `note_labels`
                WHERE `noteId` IN (SELECT `id` FROM `notes`)
                  AND `labelId` IN (SELECT `id` FROM `labels`)
                """.trimIndent()
            )
            db.execSQL("DROP TABLE `note_labels`")
            db.execSQL("ALTER TABLE `note_labels_new` RENAME TO `note_labels`")
            db.execSQL("CREATE INDEX IF NOT EXISTS `idx_note_label_note_id` ON `note_labels` (`noteId`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `idx_note_label_label_id` ON `note_labels` (`labelId`)")
        }
    }

    /**
     * v4 → v5: drag-to-reorder support.
     * - `tasks` gains `orderPosition` + `(column, orderPosition)` index
     */
    private val MIGRATION_4_5 = object : Migration(4, 5) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE `tasks` ADD COLUMN `orderPosition` INTEGER NOT NULL DEFAULT 0")
            db.execSQL("CREATE INDEX IF NOT EXISTS `idx_task_column_order` ON `tasks` (`column`, `orderPosition`)")
        }
    }

    /**
     * v5 → v6: recurring task templates.
     * - `recurring_tasks` gains `defaultColumn`, `checklistTemplate` (JSON list) and `dueDateOffsetDays`
     */
    private val MIGRATION_5_6 = object : Migration(5, 6) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "ALTER TABLE `recurring_tasks` ADD COLUMN `defaultColumn` TEXT NOT NULL DEFAULT 'TODO_THIS_WEEK'"
            )
            db.execSQL(
                "ALTER TABLE `recurring_tasks` ADD COLUMN `checklistTemplate` TEXT NOT NULL DEFAULT '[]'"
            )
            db.execSQL(
                "ALTER TABLE `recurring_tasks` ADD COLUMN `dueDateOffsetDays` INTEGER NOT NULL DEFAULT 0"
            )
        }
    }

    /**
     * v6 → v7: media manager.
     * - new `media` table with source and date indices
     */
    private val MIGRATION_6_7 = object : Migration(6, 7) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `media` (
                    `id` TEXT NOT NULL,
                    `uri` TEXT NOT NULL,
                    `filename` TEXT NOT NULL,
                    `mimeType` TEXT NOT NULL,
                    `size` INTEGER NOT NULL,
                    `dateAdded` INTEGER NOT NULL,
                    `dateModified` INTEGER NOT NULL,
                    `width` INTEGER,
                    `height` INTEGER,
                    `duration` INTEGER,
                    `thumbnailUri` TEXT,
                    `sourceType` TEXT NOT NULL,
                    `sourceId` TEXT NOT NULL,
                    PRIMARY KEY(`id`)
                )
                """.trimIndent()
            )
            db.execSQL("CREATE INDEX IF NOT EXISTS `idx_media_source` ON `media` (`sourceType`, `sourceId`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `idx_media_date_added` ON `media` (`dateAdded`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `idx_media_mime_type` ON `media` (`mimeType`)")
        }
    }

    /** All migrations, in order. Pass to [androidx.room.RoomDatabase.Builder.addMigrations]. */
    val ALL: Array<Migration> = arrayOf(
        MIGRATION_1_2,
        MIGRATION_2_3,
        MIGRATION_3_4,
        MIGRATION_4_5,
        MIGRATION_5_6,
        MIGRATION_6_7
    )
}
