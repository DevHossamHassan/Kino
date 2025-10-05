# Settings Module

A comprehensive settings module using modern Android DataStore for managing app preferences and configuration.

## Features

- **Theme Management**: Light/Dark/System theme with Material You dynamic colors support
- **Notification Settings**: Granular control over notification types and timing
- **AI Settings**: Configure AI features and cloud vs on-device processing
- **Gamification**: Control productivity gamification features
- **Privacy Settings**: Manage analytics, crash reporting, and data backup
- **General Settings**: Default task sections, auto-archive, language preferences

## Architecture

```
feature/settings/
├── api/                           # Public API for other modules
│   └── SettingsApi.kt
├── internal/
│   ├── domain/                    # Business logic
│   │   ├── model/                 # Domain models
│   │   ├── usecase/              # Use cases
│   │   └── repository/           # Repository interface
│   ├── data/                     # Data layer
│   │   ├── datastore/           # DataStore implementation
│   │   └── repository/          # Repository implementation
│   └── presentation/            # UI layer
│       ├── ui/                  # Compose screens
│       ├── viewmodel/           # ViewModels
│       └── state/               # UI state classes
└── di/                          # Dependency injection
    └── SettingsModule.kt
```

## Usage

### In Other Feature Modules

```kotlin
@HiltViewModel
class SomeViewModel @Inject constructor(
    private val settingsApi: SettingsApi
) : ViewModel() {
    
    fun checkIfNotificationsEnabled() {
        viewModelScope.launch {
            settingsApi.areNotificationsEnabled()
                .collect { enabled ->
                    if (enabled) {
                        // Show notification
                    }
                }
        }
    }
    
    fun checkThemeMode() {
        viewModelScope.launch {
            settingsApi.getThemeSettings()
                .collect { themeSettings ->
                    // Apply theme settings
                }
        }
    }
}
```

### In MainActivity

```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var settingsApi: SettingsApi
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val settings by settingsApi.getSettings().collectAsStateWithLifecycle()
            
            settings?.let { appSettings ->
                AppTheme(
                    darkTheme = appSettings.theme.themeMode == ThemeMode.DARK,
                    useDynamicColors = appSettings.theme.useDynamicColors
                ) {
                    AppContent()
                }
            }
        }
    }
}
```

## DataStore Implementation

The module uses Android DataStore (Preferences) instead of SharedPreferences for:

- **Type Safety**: Compile-time type checking
- **Reactive**: Flow-based reactive updates
- **Coroutines**: Built-in coroutine support
- **Migration**: Easy migration from SharedPreferences
- **Performance**: Better performance than SharedPreferences

## Settings Categories

### Theme Settings
- Theme mode (Light/Dark/System)
- Dynamic colors (Material You)
- Font scale

### Notification Settings
- Master notification toggle
- Task reminders
- Smart suggestions
- Achievements notifications
- Note reminders
- Quiet hours
- Notification frequency

### AI Settings
- Enable AI analysis
- Cloud vs on-device processing
- Auto-analyze tasks
- Smart task breakdown

### Gamification Settings
- Enable gamification
- Show streaks
- Show achievements
- Progress celebrations

### Privacy Settings
- Analytics enabled
- Crash reporting
- Data backup
- Backup frequency

### General Settings
- Default task section
- Default task column
- Auto-archive completed tasks
- Archive after days
- Language preference

## Testing

The module includes comprehensive testing:

- **Unit Tests**: ViewModels, Use Cases, Repository
- **Integration Tests**: DataStore operations
- **UI Tests**: Compose screen testing
- **Screenshot Tests**: Visual regression testing

## Dependencies

- DataStore Preferences
- Hilt for DI
- Compose for UI
- Coroutines for async operations
- Material3 for design system

## Migration from SharedPreferences

If migrating from SharedPreferences:

1. Create migration logic in `SettingsDataStore`
2. Use `DataStore.migrateSharedPreferences()`
3. Test migration thoroughly
4. Remove old SharedPreferences code

## Best Practices

1. **Always use the public API** - Don't access internal classes
2. **Handle loading states** - Settings are loaded asynchronously
3. **Provide defaults** - Always have sensible default values
4. **Test thoroughly** - Settings affect the entire app
5. **Document changes** - Settings changes should be documented
6. **Version settings** - Consider versioning for breaking changes
