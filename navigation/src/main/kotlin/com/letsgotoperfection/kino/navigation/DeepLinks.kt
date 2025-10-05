package com.letsgotoperfection.kino.navigation

object DeepLinks {
    private const val SCHEME = "kino"
    private const val HOST = "app"
    
    // Kanban deep links
    const val KANBAN_BOARD = "$SCHEME://$HOST/kanban"
    fun taskDetail(taskId: String) = "$SCHEME://$HOST/task/$taskId"
    
    // Notes deep links
    const val NOTES_LIST = "$SCHEME://$HOST/notes"
    fun noteDetail(noteId: String) = "$SCHEME://$HOST/note/$noteId"
    
    // Media deep links
    const val MEDIA_MANAGER = "$SCHEME://$HOST/media"
    fun mediaViewer(mediaId: String) = "$SCHEME://$HOST/media/$mediaId"
    
    
    // Notifications deep links
    const val NOTIFICATIONS_LIST = "$SCHEME://$HOST/notifications"
    fun notificationDetail(notificationId: String) = "$SCHEME://$HOST/notification/$notificationId"
}
