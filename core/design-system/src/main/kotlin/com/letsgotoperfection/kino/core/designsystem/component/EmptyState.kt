package com.letsgotoperfection.kino.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.ImageNotSupported
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Empty state component for displaying when there's no content
 */
@Composable
fun EmptyState(
    title: String,
    description: String,
    icon: ImageVector,
    primaryAction: EmptyStateAction? = null,
    secondaryAction: EmptyStateAction? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        if (primaryAction != null || secondaryAction != null) {
            Spacer(modifier = Modifier.height(32.dp))
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                primaryAction?.let { action ->
                    Button(
                        onClick = action.onClick,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        action.icon?.let { icon ->
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                        }
                        Text(action.text)
                    }
                }
                
                secondaryAction?.let { action ->
                    OutlinedButton(
                        onClick = action.onClick,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        action.icon?.let { icon ->
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                        }
                        Text(action.text)
                    }
                }
            }
        }
    }
}

/**
 * Data class for empty state actions
 */
data class EmptyStateAction(
    val text: String,
    val onClick: () -> Unit,
    val icon: ImageVector? = null
)

/**
 * Predefined empty states for common scenarios
 */
object EmptyStates {
    
    @Composable
    fun TasksEmpty(
        onCreateTask: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        EmptyState(
            title = "No tasks yet",
            description = "Create your first task to get started with organizing your work and personal life.",
            icon = Icons.Default.TaskAlt,
            primaryAction = EmptyStateAction(
                text = "Create Task",
                onClick = onCreateTask,
                icon = Icons.Default.Add
            ),
            modifier = modifier
        )
    }
    
    @Composable
    fun NotesEmpty(
        onCreateNote: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        EmptyState(
            title = "No notes yet",
            description = "Start capturing your thoughts and ideas with rich text notes.",
            icon = Icons.Default.Notes,
            primaryAction = EmptyStateAction(
                text = "Create Note",
                onClick = onCreateNote,
                icon = Icons.Default.Add
            ),
            modifier = modifier
        )
    }
    
    @Composable
    fun MediaEmpty(
        onAddMedia: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        EmptyState(
            title = "No media files",
            description = "Upload images, documents, and other files to keep them organized and accessible.",
            icon = Icons.Default.ImageNotSupported,
            primaryAction = EmptyStateAction(
                text = "Add Media",
                onClick = onAddMedia,
                icon = Icons.Default.Add
            ),
            modifier = modifier
        )
    }
    
    @Composable
    fun BookmarksEmpty(
        onAddBookmark: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        EmptyState(
            title = "No bookmarks",
            description = "Save important links and resources for quick access later.",
            icon = Icons.Default.BookmarkBorder,
            primaryAction = EmptyStateAction(
                text = "Add Bookmark",
                onClick = onAddBookmark,
                icon = Icons.Default.Add
            ),
            modifier = modifier
        )
    }
    
    @Composable
    fun FoldersEmpty(
        onCreateFolder: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        EmptyState(
            title = "No folders",
            description = "Organize your content by creating folders and categories.",
            icon = Icons.Default.FolderOpen,
            primaryAction = EmptyStateAction(
                text = "Create Folder",
                onClick = onCreateFolder,
                icon = Icons.Default.Add
            ),
            modifier = modifier
        )
    }
}