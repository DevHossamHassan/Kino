package com.letsgotoperfection.kino.navigation

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import com.letsgotoperfection.kino.feature.kanban.navigation.KanbanBoardRoute
import com.letsgotoperfection.kino.feature.media.navigation.MediaManagerRoute
import com.letsgotoperfection.kino.feature.notes.navigation.NotesListRoute
import com.letsgotoperfection.kino.feature.recurringtasks.navigation.RecurringTasksListRoute
import com.letsgotoperfection.kino.feature.settings.navigation.SettingsRoute
import com.letsgotoperfection.kino.feature.taskdetail.navigation.TaskDetailRoute
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class NavigationTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    private lateinit var navController: TestNavHostController
    
    @Before
    fun setupNavHost() {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current).apply {
                navigatorProvider.addNavigator(ComposeNavigator())
            }
            KinoNavHost(navController = navController)
        }
    }
    
    @Test
    fun navHost_verifyStartDestination() {
        composeTestRule.waitForIdle()
        
        // Verify start destination is KanbanBoardRoute
        val route = navController.currentBackStackEntry?.destination?.route
        assertEquals(KanbanBoardRoute::class.qualifiedName, route)
    }
    
    @Test
    fun navHost_navigateToTaskDetail_navigatesCorrectly() {
        composeTestRule.waitForIdle()
        
        val taskId = "test-task-123"
        
        // Navigate to task detail
        composeTestRule.runOnUiThread {
            navController.navigate(TaskDetailRoute(taskId))
        }
        
        composeTestRule.waitForIdle()
        
        // Verify navigation to TaskDetailRoute
        val route = navController.currentBackStackEntry?.destination?.route
        assertEquals(TaskDetailRoute::class.qualifiedName, route)
    }
    
    @Test
    fun navHost_navigateToNotes_navigatesCorrectly() {
        composeTestRule.waitForIdle()
        
        // Navigate to notes
        composeTestRule.runOnUiThread {
            navController.navigate(NotesListRoute)
        }
        
        composeTestRule.waitForIdle()
        
        // Verify navigation to NotesListRoute
        val route = navController.currentBackStackEntry?.destination?.route
        assertEquals(NotesListRoute::class.qualifiedName, route)
    }
    
    @Test
    fun navHost_navigateToMedia_navigatesCorrectly() {
        composeTestRule.waitForIdle()
        
        // Navigate to media
        composeTestRule.runOnUiThread {
            navController.navigate(MediaManagerRoute)
        }
        
        composeTestRule.waitForIdle()
        
        // Verify navigation to MediaManagerRoute
        val route = navController.currentBackStackEntry?.destination?.route
        assertEquals(MediaManagerRoute::class.qualifiedName, route)
    }
    
    @Test
    fun navHost_navigateToSettings_navigatesCorrectly() {
        composeTestRule.waitForIdle()
        
        // Navigate to settings
        composeTestRule.runOnUiThread {
            navController.navigate(SettingsRoute)
        }
        
        composeTestRule.waitForIdle()
        
        // Verify navigation to SettingsRoute
        val route = navController.currentBackStackEntry?.destination?.route
        assertEquals(SettingsRoute::class.qualifiedName, route)
    }
    
    @Test
    fun navHost_navigateToRecurringTasks_navigatesCorrectly() {
        composeTestRule.waitForIdle()
        
        // Navigate to recurring tasks
        composeTestRule.runOnUiThread {
            navController.navigate(RecurringTasksListRoute)
        }
        
        composeTestRule.waitForIdle()
        
        // Verify navigation to RecurringTasksListRoute
        val route = navController.currentBackStackEntry?.destination?.route
        assertEquals(RecurringTasksListRoute::class.qualifiedName, route)
    }
    
    @Test
    fun navHost_pressBack_navigatesToPreviousScreen() {
        composeTestRule.waitForIdle()
        
        // Navigate to settings
        composeTestRule.runOnUiThread {
            navController.navigate(SettingsRoute)
        }
        
        composeTestRule.waitForIdle()
        
        // Press back
        composeTestRule.runOnUiThread {
            navController.popBackStack()
        }
        
        composeTestRule.waitForIdle()
        
        // Verify back to KanbanBoardRoute
        val route = navController.currentBackStackEntry?.destination?.route
        assertEquals(KanbanBoardRoute::class.qualifiedName, route)
    }
    
    @Test
    fun navHost_completeNavigationFlow_navigatesCorrectly() {
        composeTestRule.waitForIdle()
        
        // 1. Start at kanban board
        assertEquals(KanbanBoardRoute::class.qualifiedName, navController.currentBackStackEntry?.destination?.route)
        
        // 2. Navigate to task detail
        composeTestRule.runOnUiThread {
            navController.navigate(TaskDetailRoute("test-task-123"))
        }
        composeTestRule.waitForIdle()
        assertEquals(TaskDetailRoute::class.qualifiedName, navController.currentBackStackEntry?.destination?.route)
        
        // 3. Navigate to media viewer
        composeTestRule.runOnUiThread {
            navController.navigate(com.letsgotoperfection.kino.feature.media.navigation.MediaViewerRoute("test-media-456"))
        }
        composeTestRule.waitForIdle()
        assertEquals(com.letsgotoperfection.kino.feature.media.navigation.MediaViewerRoute::class.qualifiedName, navController.currentBackStackEntry?.destination?.route)
        
        // 4. Navigate back to task detail
        composeTestRule.runOnUiThread {
            navController.popBackStack()
        }
        composeTestRule.waitForIdle()
        assertEquals(TaskDetailRoute::class.qualifiedName, navController.currentBackStackEntry?.destination?.route)
        
        // 5. Navigate back to kanban board
        composeTestRule.runOnUiThread {
            navController.popBackStack()
        }
        composeTestRule.waitForIdle()
        assertEquals(KanbanBoardRoute::class.qualifiedName, navController.currentBackStackEntry?.destination?.route)
    }
}
