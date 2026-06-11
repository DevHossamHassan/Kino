package com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.letsgotoperfection.kino.core.model.TaskColumn
import com.letsgotoperfection.kino.feature.recurringtasks.api.RecurringTaskNotFoundException
import com.letsgotoperfection.kino.feature.recurringtasks.internal.domain.calculator.RecurrenceCalculator
import com.letsgotoperfection.kino.feature.recurringtasks.internal.fake.FakeKanbanApi
import com.letsgotoperfection.kino.feature.recurringtasks.internal.fake.FakeRecurringTaskAlarmScheduler
import com.letsgotoperfection.kino.feature.recurringtasks.internal.fake.FakeRecurringTaskNotificationService
import com.letsgotoperfection.kino.feature.recurringtasks.internal.fake.FakeRecurringTasksRepository
import com.letsgotoperfection.kino.feature.recurringtasks.internal.fake.FakeSettingsApi
import com.letsgotoperfection.kino.feature.recurringtasks.internal.fake.RecurringTaskTestData
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

class GenerateTaskInstanceUseCaseTest {

    private lateinit var repository: FakeRecurringTasksRepository
    private lateinit var kanbanApi: FakeKanbanApi
    private lateinit var alarmScheduler: FakeRecurringTaskAlarmScheduler
    private lateinit var notificationService: FakeRecurringTaskNotificationService
    private lateinit var settingsApi: FakeSettingsApi
    private lateinit var useCase: GenerateTaskInstanceUseCase

    private val scheduledDate = LocalDate.of(2026, 6, 15)

    @BeforeEach
    fun setup() {
        repository = FakeRecurringTasksRepository()
        kanbanApi = FakeKanbanApi()
        alarmScheduler = FakeRecurringTaskAlarmScheduler()
        notificationService = FakeRecurringTaskNotificationService()
        settingsApi = FakeSettingsApi()
        useCase = GenerateTaskInstanceUseCase(
            repository = repository,
            kanbanApi = kanbanApi,
            recurrenceCalculator = RecurrenceCalculator(),
            alarmScheduler = alarmScheduler,
            notificationService = notificationService,
            settingsApi = settingsApi
        )
    }

    @Test
    fun `fails when template does not exist`() = runTest {
        val result = useCase("missing-id", scheduledDate)

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(RecurringTaskNotFoundException::class.java)
        assertThat(kanbanApi.createdTasks).isEmpty()
    }

    @Test
    fun `skips date before start but schedules next occurrence`() = runTest {
        val template = RecurringTaskTestData.dailyTask(startDate = LocalDate.of(2026, 7, 1))
        repository.addTask(template)

        val result = useCase(template.id, LocalDate.of(2026, 6, 15))

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isNull()
        assertThat(kanbanApi.createdTasks).isEmpty()
        assertThat(alarmScheduler.scheduledGenerations.map { it.second })
            .containsExactly(LocalDate.of(2026, 7, 1))
    }

    @Test
    fun `skips duplicate instance but schedules next occurrence`() = runTest {
        val template = RecurringTaskTestData.dailyTask()
        repository.addTask(template)
        repository.markInstanceExists(template.id, scheduledDate)

        val result = useCase(template.id, scheduledDate)

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isNull()
        assertThat(kanbanApi.createdTasks).isEmpty()
        assertThat(alarmScheduler.scheduledGenerations.map { it.second })
            .containsExactly(scheduledDate.plusDays(1))
    }

    @Test
    fun `creates task honoring template column, checklist and due date offset`() = runTest {
        val template = RecurringTaskTestData.dailyTask(
            defaultColumn = TaskColumn.IN_PROGRESS,
            checklistTemplate = listOf("Prepare notes", "Share update"),
            dueDateOffsetDays = 2
        )
        repository.addTask(template)

        val result = useCase(template.id, scheduledDate)

        assertThat(result.isSuccess).isTrue()
        val createdTaskId = result.getOrNull()
        assertThat(createdTaskId).isNotNull()

        val task = kanbanApi.createdTasks.single()
        assertThat(task.id).isEqualTo(createdTaskId)
        assertThat(task.recurringTaskId).isEqualTo(template.id)
        assertThat(task.scheduledDate).isEqualTo(scheduledDate)
        assertThat(task.column).isEqualTo(TaskColumn.IN_PROGRESS)
        assertThat(task.title).startsWith(template.title)
        assertThat(task.dueDate).isEqualTo(
            LocalDateTime.of(scheduledDate.plusDays(2), LocalTime.of(9, 0))
        )
        assertThat(task.checklist.map { it.text })
            .containsExactly("Prepare notes", "Share update")
            .inOrder()
        assertThat(task.checklist.map { it.taskId }).containsExactly(task.id, task.id)
    }

    @Test
    fun `instance id is deterministic for the same template and date`() = runTest {
        val template = RecurringTaskTestData.dailyTask()
        repository.addTask(template)

        val result = useCase(template.id, scheduledDate)

        val expectedId = UUID.nameUUIDFromBytes(
            "${template.id}_${scheduledDate.toEpochDay()}".toByteArray()
        ).toString()
        assertThat(result.getOrNull()).isEqualTo(expectedId)
    }

    @Test
    fun `updates last generated date and schedules next occurrence on success`() = runTest {
        val template = RecurringTaskTestData.dailyTask()
        repository.addTask(template)

        useCase(template.id, scheduledDate)

        assertThat(repository.getRecurringTaskById(template.id)?.lastGeneratedDate)
            .isEqualTo(scheduledDate)
        assertThat(alarmScheduler.scheduledGenerations.map { it.second })
            .containsExactly(scheduledDate.plusDays(1))
    }

    @Test
    fun `sends notification when recurring task notifications are enabled`() = runTest {
        val template = RecurringTaskTestData.dailyTask()
        repository.addTask(template)
        settingsApi.recurringTaskNotificationsEnabled.value = true

        val result = useCase(template.id, scheduledDate)

        val notification = notificationService.singleNotifications.single()
        assertThat(notification.taskId).isEqualTo(result.getOrNull())
        assertThat(notification.column).isEqualTo(template.defaultColumn)
        assertThat(notification.section).isEqualTo(template.section)
    }

    @Test
    fun `does not notify when recurring task notifications are disabled`() = runTest {
        val template = RecurringTaskTestData.dailyTask()
        repository.addTask(template)
        settingsApi.recurringTaskNotificationsEnabled.value = false

        useCase(template.id, scheduledDate)

        assertThat(notificationService.singleNotifications).isEmpty()
    }

    @Test
    fun `does not notify when notify flag is false`() = runTest {
        val template = RecurringTaskTestData.dailyTask()
        repository.addTask(template)

        useCase(template.id, scheduledDate, notify = false)

        assertThat(notificationService.singleNotifications).isEmpty()
    }

    @Test
    fun `does not schedule next occurrence when scheduleNext flag is false`() = runTest {
        val template = RecurringTaskTestData.dailyTask()
        repository.addTask(template)

        useCase(template.id, scheduledDate, scheduleNext = false)

        assertThat(alarmScheduler.scheduledGenerations).isEmpty()
    }

    @Test
    fun `propagates kanban failure without side effects`() = runTest {
        val template = RecurringTaskTestData.dailyTask()
        repository.addTask(template)
        kanbanApi.shouldFailCreate = true

        val result = useCase(template.id, scheduledDate)

        assertThat(result.isFailure).isTrue()
        assertThat(repository.getRecurringTaskById(template.id)?.lastGeneratedDate).isNull()
        assertThat(notificationService.singleNotifications).isEmpty()
        assertThat(alarmScheduler.scheduledGenerations).isEmpty()
    }
}
