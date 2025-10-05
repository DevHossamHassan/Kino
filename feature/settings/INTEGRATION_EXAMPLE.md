# Settings Module Integration Examples

## Example 1: Using Settings in a ViewModel

```kotlin
@HiltViewModel
class TaskViewModel @Inject constructor(
    private val settingsApi: SettingsApi,
    private val taskRepository: TaskRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()
    
    init {
        observeSettings()
    }
    
    private fun observeSettings() {
        viewModelScope.launch {
            // Observe theme settings for UI updates
            settingsApi.getThemeSettings()
                .collect { themeSettings ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            isDarkTheme = themeSettings.themeMode == ThemeMode.DARK,
                            useDynamicColors = themeSettings.useDynamicColors
                        )
                    }
                }
        }
        
        viewModelScope.launch {
            // Observe notification settings
            settingsApi.areNotificationsEnabled()
                .collect { notificationsEnabled ->
                    if (notificationsEnabled) {
                        // Schedule notifications for tasks
                        scheduleTaskNotifications()
                    } else {
                        // Cancel all notifications
                        cancelAllNotifications()
                    }
                }
        }
    }
    
    fun createTask(task: Task) {
        viewModelScope.launch {
            // Check if AI is enabled before creating task
            settingsApi.isAiEnabled()
                .first()
                .let { aiEnabled ->
                    if (aiEnabled) {
                        // Use AI to enhance task
                        val enhancedTask = enhanceTaskWithAI(task)
                        taskRepository.createTask(enhancedTask)
                    } else {
                        taskRepository.createTask(task)
                    }
                }
        }
    }
}
```

## Example 2: Theme Integration in MainActivity

```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var settingsApi: SettingsApi
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val settings by settingsApi.getSettings()
                .collectAsStateWithLifecycle(initialValue = null)
            
            when {
                settings == null -> {
                    // Show loading screen
                    LoadingScreen()
                }
                else -> {
                    AppTheme(
                        darkTheme = settings.theme.themeMode == ThemeMode.DARK,
                        useDynamicColors = settings.theme.useDynamicColors,
                        fontSize = settings.theme.fontSize
                    ) {
                        AppContent()
                    }
                }
            }
        }
    }
}

@Composable
fun AppTheme(
    darkTheme: Boolean,
    useDynamicColors: Boolean,
    fontSize: FontScale,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        useDynamicColors && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val typography = Typography.copy(
        // Apply font scale to typography
        displayLarge = Typography.displayLarge.copy(
            fontSize = Typography.displayLarge.fontSize * fontSize.scale
        ),
        // ... apply to other text styles
    )
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}
```

## Example 3: Notification Integration

```kotlin
@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val settingsApi: SettingsApi,
    private val notificationManager: NotificationManager
) : ViewModel() {
    
    init {
        observeNotificationSettings()
    }
    
    private fun observeNotificationSettings() {
        viewModelScope.launch {
            settingsApi.getNotificationSettings()
                .collect { notificationSettings ->
                    if (notificationSettings.enabled) {
                        setupNotifications(notificationSettings)
                    } else {
                        disableAllNotifications()
                    }
                }
        }
    }
    
    private fun setupNotifications(settings: NotificationSettings) {
        if (settings.taskReminders) {
            enableTaskReminders()
        }
        
        if (settings.achievements) {
            enableAchievementNotifications()
        }
        
        if (settings.quietHoursEnabled) {
            setupQuietHours(settings.quietHoursStart, settings.quietHoursEnd)
        }
    }
}
```

## Example 4: AI Feature Integration

```kotlin
@HiltViewModel
class AiAnalysisViewModel @Inject constructor(
    private val settingsApi: SettingsApi,
    private val aiService: AiService
) : ViewModel() {
    
    fun analyzeTask(task: Task) {
        viewModelScope.launch {
            settingsApi.getAiSettings()
                .first()
                .let { aiSettings ->
                    if (aiSettings.enableAiAnalysis) {
                        val analysis = if (aiSettings.useCloudAi) {
                            aiService.analyzeWithCloudAI(task)
                        } else {
                            aiService.analyzeWithOnDeviceAI(task)
                        }
                        
                        if (aiSettings.smartTaskBreakdown) {
                            val breakdown = aiService.breakdownTask(task)
                            // Apply breakdown
                        }
                    }
                }
        }
    }
}
```

## Example 5: Gamification Integration

```kotlin
@HiltViewModel
class GamificationViewModel @Inject constructor(
    private val settingsApi: SettingsApi,
    private val achievementService: AchievementService
) : ViewModel() {
    
    fun onTaskCompleted(task: Task) {
        viewModelScope.launch {
            settingsApi.isGamificationEnabled()
                .first()
                .let { gamificationEnabled ->
                    if (gamificationEnabled) {
                        val settings = settingsApi.getSettings().first()
                        if (settings.gamification.showStreaks) {
                            updateStreak()
                        }
                        
                        if (settings.gamification.showAchievements) {
                            checkAchievements(task)
                        }
                        
                        if (settings.gamification.showProgressCelebrations) {
                            showCelebration()
                        }
                    }
                }
        }
    }
}
```

## Example 6: Privacy Settings Integration

```kotlin
@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val settingsApi: SettingsApi,
    private val analyticsService: AnalyticsService
) : ViewModel() {
    
    init {
        observePrivacySettings()
    }
    
    private fun observePrivacySettings() {
        viewModelScope.launch {
            settingsApi.getSettings()
                .map { it.privacy }
                .collect { privacySettings ->
                    if (privacySettings.analyticsEnabled) {
                        analyticsService.enableAnalytics()
                    } else {
                        analyticsService.disableAnalytics()
                    }
                    
                    if (privacySettings.crashReportingEnabled) {
                        crashReportingService.enableCrashReporting()
                    } else {
                        crashReportingService.disableCrashReporting()
                    }
                }
        }
    }
}
```

## Example 7: Settings Screen Navigation

```kotlin
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    
    NavHost(navController = navController, startDestination = "kanban") {
        composable("kanban") {
            KanbanScreen(
                onNavigateToSettings = {
                    navController.navigate("settings")
                }
            )
        }
        
        composable("settings") {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
```

## Best Practices

1. **Always check settings before performing actions** - Don't assume settings are enabled
2. **Use `collectAsStateWithLifecycle`** - For UI state management
3. **Handle loading states** - Settings load asynchronously
4. **Provide fallbacks** - Always have default behavior when settings are null
5. **Test with different settings** - Ensure your features work with all setting combinations
6. **Use the public API only** - Don't access internal classes directly
7. **Consider performance** - Don't observe settings unnecessarily
8. **Handle errors gracefully** - Settings operations can fail
