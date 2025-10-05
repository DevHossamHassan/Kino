# AI Analysis Module

This module provides AI-powered task analysis capabilities for the Kino task management app.

## Features

- **Task Breakdown**: Automatically breaks down complex tasks into manageable micro-tasks
- **Urgency Analysis**: Calculates task urgency based on deadline, priority, and progress
- **Complexity Estimation**: Estimates task complexity using text analysis
- **Motivational Messages**: Generates personalized motivational messages
- **Dual AI Support**: Both cloud-based (Gemini) and on-device analysis

## Architecture

```
feature/ai-analysis/
├── api/                           # Public API interface
│   └── AiAnalysisApi.kt
├── internal/
│   ├── domain/
│   │   ├── model/                 # Domain models
│   │   │   ├── TaskAnalysis.kt
│   │   │   └── MicroTask.kt
│   │   └── analyzer/              # Analyzer interface
│   │       └── TaskAnalyzer.kt
│   ├── ai/
│   │   ├── gemini/                # Cloud AI implementation
│   │   │   └── GeminiTaskAnalyzer.kt
│   │   └── ondevice/              # On-device ML implementation
│   │       └── OnDeviceTaskAnalyzer.kt
│   └── data/
│       └── repository/
│           └── AiAnalysisApiImpl.kt
└── di/
    └── AiAnalysisModule.kt
```

## Usage

### Basic Task Analysis

```kotlin
@Inject
lateinit var aiAnalysisApi: AiAnalysisApi

// Analyze a task
val analysis = aiAnalysisApi.analyzeTask(task)
analysis.onSuccess { taskAnalysis ->
    // Use micro-tasks and recommendations
    taskAnalysis.microTasks.forEach { microTask ->
        // Schedule micro-task notifications
    }
}
```

### Generate Micro-tasks

```kotlin
val microTasks = aiAnalysisApi.generateMicroTasks(task)
microTasks.onSuccess { tasks ->
    // Display micro-tasks to user
}
```

### Motivational Messages

```kotlin
val message = aiAnalysisApi.generateMotivationalMessage(
    taskTitle = "Design Landing Page",
    progress = 75,
    streakDays = 5,
    timeUntilDeadline = Duration.ofHours(24),
    completedToday = 3
)
```

## AI Implementations

### Cloud AI (Gemini)

- Uses Google's Gemini API for sophisticated analysis
- Requires internet connection
- Higher quality results
- More natural language processing

### On-Device AI

- Uses template matching and heuristics
- Works offline
- Faster response times
- Privacy-friendly

### Hybrid Approach

The system automatically chooses the best available analyzer:
1. Try cloud AI if available
2. Fallback to on-device analysis
3. Always available for basic functionality

## Configuration

### API Keys

Set your Gemini API key in environment variables:

```bash
export GEMINI_API_KEY="your_api_key_here"
```

### Preferences

```kotlin
val preferences = AnalysisPreferences(
    useCloudAi = true,
    maxMicroTasks = 5,
    minMicroTaskDuration = Duration.ofMinutes(15),
    maxMicroTaskDuration = Duration.ofMinutes(45),
    includeMotivationalTitles = true
)
```

## Testing

Run the tests to verify functionality:

```bash
./gradlew :feature:ai-analysis:test
```

## Dependencies

- Google AI Generative AI SDK
- TensorFlow Lite (for on-device ML)
- Kotlinx Serialization
- Hilt for dependency injection

## Privacy

- On-device analysis keeps all data local
- Cloud analysis sends only task descriptions to Gemini
- No personal data is stored or transmitted
- Users can disable cloud AI in preferences
