package com.letsgotoperfection.kino.feature.media.api

/**
 * Navigation destinations for Media feature
 */
object MediaDestinations {
    const val MEDIA_LIST = "media_list"
    const val MEDIA_VIEWER = "media_viewer/{mediaId}"
    
    fun mediaViewerRoute(mediaId: String) = "media_viewer/$mediaId"
}

