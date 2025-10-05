# Media Manager Feature

A complete media management system for the Kino task management app that handles images, videos, and documents using **Scoped Storage** (Android 10+), modern permissions, and proper coroutine-based I/O operations.

## Features

- **Scoped Storage Compliance**: Uses MediaStore API and ContentResolver for Android 10+
- **Modern Permissions**: Supports Android 13+ granular media permissions
- **Photo Picker Integration**: Uses Android 13+ Photo Picker API when available
- **Grid/List Views**: Responsive UI with toggle between grid and list views
- **Media Filtering**: Filter by type (image, video, document) and source (task, note)
- **Search**: Search media by filename
- **Navigation**: Navigate to source task or note from media
- **Error Handling**: Comprehensive error handling with user-friendly messages

## Architecture

The media manager follows Clean Architecture principles with:

- **Domain Layer**: Models, repositories, use cases
- **Data Layer**: Repository implementation, Room database, MediaStore integration
- **Presentation Layer**: ViewModels, Compose UI, navigation
- **API Layer**: Public interfaces for other feature modules

## Usage

### From Other Features

```kotlin
// Inject MediaApi in your ViewModel or UseCase
@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val mediaApi: MediaApi
) : ViewModel() {
    
    fun attachMedia(uris: List<Uri>) {
        viewModelScope.launch {
            mediaApi.attachMedia(
                uris = uris,
                sourceType = MediaSourceType.TASK,
                sourceId = taskId
            ).forEach { result ->
                result.fold(
                    onSuccess = { media ->
                        // Media attached successfully
                    },
                    onFailure = { error ->
                        // Handle error
                    }
                )
            }
        }
    }
}
```

### Photo Picker Integration

```kotlin
@Composable
fun TaskDetailScreen() {
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 5)
    ) { uris ->
        if (uris.isNotEmpty()) {
            viewModel.attachMedia(uris)
        }
    }
    
    Button(onClick = {
        photoPickerLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
        )
    }) {
        Text("Attach Media")
    }
}
```

### Navigation

```kotlin
// In your navigation graph
composable(MediaDestinations.MEDIA_MANAGER) {
    MediaManagerScreen(
        onNavigateBack = { navController.popBackStack() },
        onNavigateToTask = { taskId ->
            navController.navigate("task/$taskId")
        },
        onNavigateToNote = { noteId ->
            navController.navigate("note/$noteId")
        },
        onNavigateToViewer = { mediaId ->
            navController.navigate(MediaDestinations.mediaViewerRoute(mediaId))
        }
    )
}
```

## Key Components

### MediaStoreManager
Handles all media access using Scoped Storage:
- Queries MediaStore for images and videos
- Copies media to app-specific storage
- Handles file metadata extraction
- Manages deletion with proper permissions

### PhotoPickerManager
Provides Photo Picker integration:
- Checks availability on different Android versions
- Creates appropriate intents for media selection
- Supports multiple selection and MIME type filtering

### MediaPermissionHandler
Manages permissions across Android versions:
- Android 13+: Granular media permissions
- Android 10-12: READ_EXTERNAL_STORAGE
- Below Android 10: READ_EXTERNAL_STORAGE + WRITE_EXTERNAL_STORAGE

### MediaRepository
Central data access layer:
- Uses Room for metadata storage
- Integrates with MediaStore for file access
- Provides reactive Flow-based queries
- Handles error scenarios gracefully

## Database Schema

The media feature adds a `media` table to the main database:

```sql
CREATE TABLE media (
    id TEXT PRIMARY KEY,
    uri TEXT NOT NULL,
    filename TEXT NOT NULL,
    mimeType TEXT NOT NULL,
    size INTEGER NOT NULL,
    dateAdded INTEGER NOT NULL,
    dateModified INTEGER NOT NULL,
    width INTEGER,
    height INTEGER,
    duration INTEGER,
    thumbnailUri TEXT,
    sourceType TEXT NOT NULL,
    sourceId TEXT NOT NULL
);
```

## Permissions

The feature requires the following permissions:

```xml
<!-- Android 13+ -->
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
<uses-permission android:name="android.permission.READ_MEDIA_VIDEO" />

<!-- Android 10-12 -->
<uses-permission 
    android:name="android.permission.READ_EXTERNAL_STORAGE"
    android:maxSdkVersion="32" />
```

## FileProvider Configuration

The feature uses FileProvider for sharing media files:

```xml
<provider
    android:name="androidx.core.content.FileProvider"
    android:authorities="${applicationId}.fileprovider"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/file_paths" />
</provider>
```

## Testing

The media manager includes comprehensive testing support:

- **Unit Tests**: ViewModels, Use Cases, Repository
- **Integration Tests**: Database operations, MediaStore queries
- **UI Tests**: Compose UI components, navigation
- **Screenshot Tests**: Visual regression testing

## Dependencies

- **Coil**: Image loading and caching
- **Accompanist Permissions**: Permission handling
- **Room**: Local database storage
- **Hilt**: Dependency injection
- **Compose**: Modern UI toolkit

## Future Enhancements

- [ ] Video thumbnail generation
- [ ] Document preview support
- [ ] Cloud storage integration
- [ ] Advanced filtering options
- [ ] Bulk operations
- [ ] Media compression
- [ ] Offline sync support
