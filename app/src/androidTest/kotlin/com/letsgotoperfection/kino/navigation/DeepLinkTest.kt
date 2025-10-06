package com.letsgotoperfection.kino.navigation

import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import com.letsgotoperfection.kino.feature.kanban.navigation.KanbanBoardRoute
import com.letsgotoperfection.kino.feature.media.navigation.MediaManagerRoute
import com.letsgotoperfection.kino.feature.media.navigation.MediaViewerRoute
import com.letsgotoperfection.kino.feature.notes.navigation.NoteDetailRoute
import com.letsgotoperfection.kino.feature.notes.navigation.NotesListRoute
import com.letsgotoperfection.kino.feature.recurringtasks.navigation.RecurringTasksListRoute
import com.letsgotoperfection.kino.feature.settings.navigation.SettingsRoute
import com.letsgotoperfection.kino.feature.taskdetail.navigation.TaskDetailRoute
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class DeepLinkTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun deepLink_movieDetails_navigatesCorrectly() {
        lateinit var navController: TestNavHostController
        
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current).apply {
                navigatorProvider.addNavigator(ComposeNavigator())
            }
            KinoNavHost(navController = navController)
        }
        
        composeTestRule.waitForIdle()
        
        // Simulate deep link
        val taskId = "test-task-123"
        val intent = Intent().apply {
            data = Uri.parse("kino://app/task/$taskId")
        }
        
        DeepLinkHandler.handleDeepLink(intent, navController)
        
        composeTestRule.waitForIdle()
        
        // Verify navigation
        val route = navController.currentBackStackEntry?.destination?.route
        assertEquals(TaskDetailRoute::class.qualifiedName, route)
    }
    
    @Test
    fun deepLink_noteDetails_navigatesCorrectly() {
        lateinit var navController: TestNavHostController
        
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current).apply {
                navigatorProvider.addNavigator(ComposeNavigator())
            }
            KinoNavHost(navController = navController)
        }
        
        composeTestRule.waitForIdle()
        
        // Simulate deep link
        val noteId = "test-note-456"
        val intent = Intent().apply {
            data = Uri.parse("kino://app/note/$noteId")
        }
        
        DeepLinkHandler.handleDeepLink(intent, navController)
        
        composeTestRule.waitForIdle()
        
        // Verify navigation
        val route = navController.currentBackStackEntry?.destination?.route
        assertEquals(NoteDetailRoute::class.qualifiedName, route)
    }
    
    @Test
    fun deepLink_mediaViewer_navigatesCorrectly() {
        lateinit var navController: TestNavHostController
        
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current).apply {
                navigatorProvider.addNavigator(ComposeNavigator())
            }
            KinoNavHost(navController = navController)
        }
        
        composeTestRule.waitForIdle()
        
        // Simulate deep link
        val mediaId = "test-media-789"
        val intent = Intent().apply {
            data = Uri.parse("kino://app/media/viewer/$mediaId")
        }
        
        DeepLinkHandler.handleDeepLink(intent, navController)
        
        composeTestRule.waitForIdle()
        
        // Verify navigation
        val route = navController.currentBackStackEntry?.destination?.route
        assertEquals(MediaViewerRoute::class.qualifiedName, route)
    }
    
    @Test
    fun deepLink_kanbanBoard_navigatesCorrectly() {
        lateinit var navController: TestNavHostController
        
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current).apply {
                navigatorProvider.addNavigator(ComposeNavigator())
            }
            KinoNavHost(navController = navController)
        }
        
        composeTestRule.waitForIdle()
        
        // Simulate deep link
        val intent = Intent().apply {
            data = Uri.parse("kino://app/kanban")
        }
        
        DeepLinkHandler.handleDeepLink(intent, navController)
        
        composeTestRule.waitForIdle()
        
        // Verify navigation
        val route = navController.currentBackStackEntry?.destination?.route
        assertEquals(KanbanBoardRoute::class.qualifiedName, route)
    }
    
    @Test
    fun deepLink_notesList_navigatesCorrectly() {
        lateinit var navController: TestNavHostController
        
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current).apply {
                navigatorProvider.addNavigator(ComposeNavigator())
            }
            KinoNavHost(navController = navController)
        }
        
        composeTestRule.waitForIdle()
        
        // Simulate deep link
        val intent = Intent().apply {
            data = Uri.parse("kino://app/notes")
        }
        
        DeepLinkHandler.handleDeepLink(intent, navController)
        
        composeTestRule.waitForIdle()
        
        // Verify navigation
        val route = navController.currentBackStackEntry?.destination?.route
        assertEquals(NotesListRoute::class.qualifiedName, route)
    }
    
    @Test
    fun deepLink_mediaManager_navigatesCorrectly() {
        lateinit var navController: TestNavHostController
        
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current).apply {
                navigatorProvider.addNavigator(ComposeNavigator())
            }
            KinoNavHost(navController = navController)
        }
        
        composeTestRule.waitForIdle()
        
        // Simulate deep link
        val intent = Intent().apply {
            data = Uri.parse("kino://app/media")
        }
        
        DeepLinkHandler.handleDeepLink(intent, navController)
        
        composeTestRule.waitForIdle()
        
        // Verify navigation
        val route = navController.currentBackStackEntry?.destination?.route
        assertEquals(MediaManagerRoute::class.qualifiedName, route)
    }
    
    @Test
    fun deepLink_settings_navigatesCorrectly() {
        lateinit var navController: TestNavHostController
        
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current).apply {
                navigatorProvider.addNavigator(ComposeNavigator())
            }
            KinoNavHost(navController = navController)
        }
        
        composeTestRule.waitForIdle()
        
        // Simulate deep link
        val intent = Intent().apply {
            data = Uri.parse("kino://app/settings")
        }
        
        DeepLinkHandler.handleDeepLink(intent, navController)
        
        composeTestRule.waitForIdle()
        
        // Verify navigation
        val route = navController.currentBackStackEntry?.destination?.route
        assertEquals(SettingsRoute::class.qualifiedName, route)
    }
    
    @Test
    fun deepLink_recurringTasks_navigatesCorrectly() {
        lateinit var navController: TestNavHostController
        
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current).apply {
                navigatorProvider.addNavigator(ComposeNavigator())
            }
            KinoNavHost(navController = navController)
        }
        
        composeTestRule.waitForIdle()
        
        // Simulate deep link
        val intent = Intent().apply {
            data = Uri.parse("kino://app/recurring_tasks")
        }
        
        DeepLinkHandler.handleDeepLink(intent, navController)
        
        composeTestRule.waitForIdle()
        
        // Verify navigation
        val route = navController.currentBackStackEntry?.destination?.route
        assertEquals(RecurringTasksListRoute::class.qualifiedName, route)
    }
    
    @Test
    fun deepLink_parseTaskDetails_returnsCorrectRoute() {
        val uri = Uri.parse("kino://app/task/test-task-123")
        val result = DeepLinkHandler.parseDeepLink(uri)
        
        assertNotNull(result)
        assert(result is TaskDetailRoute)
        assertEquals("test-task-123", (result as TaskDetailRoute).taskId)
    }
    
    @Test
    fun deepLink_parseNoteDetails_returnsCorrectRoute() {
        val uri = Uri.parse("kino://app/note/test-note-456")
        val result = DeepLinkHandler.parseDeepLink(uri)
        
        assertNotNull(result)
        assert(result is NoteDetailRoute)
        assertEquals("test-note-456", (result as NoteDetailRoute).noteId)
    }
    
    @Test
    fun deepLink_parseMediaViewer_returnsCorrectRoute() {
        val uri = Uri.parse("kino://app/media/viewer/test-media-789")
        val result = DeepLinkHandler.parseDeepLink(uri)
        
        assertNotNull(result)
        assert(result is MediaViewerRoute)
        assertEquals("test-media-789", (result as MediaViewerRoute).mediaId)
    }
    
    @Test
    fun deepLink_parseKanbanBoard_returnsCorrectRoute() {
        val uri = Uri.parse("kino://app/kanban")
        val result = DeepLinkHandler.parseDeepLink(uri)
        
        assertNotNull(result)
        assert(result is KanbanBoardRoute)
    }
    
    @Test
    fun deepLink_parseNotesList_returnsCorrectRoute() {
        val uri = Uri.parse("kino://app/notes")
        val result = DeepLinkHandler.parseDeepLink(uri)
        
        assertNotNull(result)
        assert(result is NotesListRoute)
    }
    
    @Test
    fun deepLink_parseMediaManager_returnsCorrectRoute() {
        val uri = Uri.parse("kino://app/media")
        val result = DeepLinkHandler.parseDeepLink(uri)
        
        assertNotNull(result)
        assert(result is MediaManagerRoute)
    }
    
    @Test
    fun deepLink_parseSettings_returnsCorrectRoute() {
        val uri = Uri.parse("kino://app/settings")
        val result = DeepLinkHandler.parseDeepLink(uri)
        
        assertNotNull(result)
        assert(result is SettingsRoute)
    }
    
    @Test
    fun deepLink_parseRecurringTasks_returnsCorrectRoute() {
        val uri = Uri.parse("kino://app/recurring_tasks")
        val result = DeepLinkHandler.parseDeepLink(uri)
        
        assertNotNull(result)
        assert(result is RecurringTasksListRoute)
    }
    
    @Test
    fun deepLink_invalidScheme_returnsNull() {
        val uri = Uri.parse("https://app/task/test-task-123")
        val result = DeepLinkHandler.parseDeepLink(uri)
        
        assertNull(result)
    }
    
    @Test
    fun deepLink_invalidHost_returnsNull() {
        val uri = Uri.parse("kino://invalid/task/test-task-123")
        val result = DeepLinkHandler.parseDeepLink(uri)
        
        assertNull(result)
    }
    
    @Test
    fun deepLink_unknownPath_returnsNull() {
        val uri = Uri.parse("kino://app/unknown")
        val result = DeepLinkHandler.parseDeepLink(uri)
        
        assertNull(result)
    }
}
