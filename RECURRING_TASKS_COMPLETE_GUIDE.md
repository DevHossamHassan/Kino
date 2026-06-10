# Recurring Tasks - Complete Implementation Guide

## 🎯 Overview

The recurring tasks system has been completely rebuilt with **AlarmManager** for production-ready, precise task generation. This guide covers everything you need to know.

---

## ✅ What Was Implemented

### **1. Core Features**

#### **Comprehensive Template Fields**
- ✅ **Title** & **Rich Text Description** (with markdown support)
- ✅ **Section** (Personal/Work/Family)
- ✅ **Priority** (High/Medium/Low)
- ✅ **Default Column** (where tasks will be placed on the board)
- ✅ **Due Date Offset** (0, 1, 3, 7, 14, 30 days after creation)
- ✅ **Checklist Template** (items copied to each generated task)
- ✅ **Labels** (tags for categorization)
- ✅ **Recurrence Pattern** (Daily/Weekly/Monthly/Yearly)
- ✅ **Time of Day** (when task should be created)
- ✅ **Start/End Dates** (when recurrence begins/ends)

#### **24-Hour Format Support** ⭐ NEW
- ✅ **Automatically detects** device's 12-hour vs 24-hour setting
- ✅ **Time Picker** uses device preference
- ✅ **Display format** matches device setting
- ✅ **Storage** always uses 24-hour format internally for consistency
- ✅ **Timezone-aware** scheduling

```kotlin
// Device set to 24-hour: Shows "14:30"
// Device set to 12-hour: Shows "2:30 PM"
// Internally stored as: LocalTime(14, 30)
```

### **2. Production-Ready Scheduling**

#### **AlarmManager Implementation**
- ✅ **Exact timing** to the second (not "approximately")
- ✅ **Works in Doze mode** using `setExactAndAllowWhileIdle()`
- ✅ **Survives reboots** with `BootCompletedReceiver`
- ✅ **Battery optimized** with sliding window (7 days ahead)
- ✅ **Comprehensive logging** for debugging

#### **Permission Handling**
- ✅ **Android 12+**: `SCHEDULE_EXACT_ALARM` permission
- ✅ **Auto-detection**: Checks if permission is granted
- ✅ **User guidance**: Opens settings if permission needed
- ✅ **Fallback**: Graceful degradation if permission denied

#### **Architecture Components**

```
┌──────────────────────────────────────────────────────────┐
│ RecurringTaskAlarmScheduler                              │
│ - Schedules precise alarms                               │
│ - Manages sliding window (7 days)                        │
│ - Handles cancellation & rescheduling                    │
└───────────────┬──────────────────────────────────────────┘
                │
                ↓
┌───────────────▼──────────────────────────────────────────┐
│ RecurringTaskAlarmReceiver (BroadcastReceiver)           │
│ - Receives alarm broadcasts                              │
│ - Enqueues Worker for task creation                      │
└───────────────┬──────────────────────────────────────────┘
                │
                ↓
┌───────────────▼──────────────────────────────────────────┐
│ TaskInstanceCreatorWorker                                │
│ - Loads recurring task template                          │
│ - Creates actual task via KanbanApi                      │
│ - Updates last generated date                            │
│ - Schedules next occurrence                              │
└───────────────┬──────────────────────────────────────────┘
                │
                ↓
┌───────────────▼──────────────────────────────────────────┐
│ Task appears on Kanban board!                            │
└──────────────────────────────────────────────────────────┘
```

---

## 📋 Complete Task Creation Flow

### **User Creates Recurring Task:**

```
1. Fill in details:
   ├─ Title: "Daily Standup"
   ├─ Description: "Team sync meeting notes"
   ├─ Section: Work
   ├─ Priority: High
   ├─ Default Column: To Do This Week
   ├─ Due Date: Same day
   ├─ Checklist Template:
   │  ├─ ☐ Review yesterday's work
   │  ├─ ☐ Plan today's tasks
   │  └─ ☐ Identify blockers
   ├─ Frequency: Daily
   ├─ Time: 9:00 AM (device formats as "09:00" or "9:00 AM")
   ├─ Start Date: Tomorrow
   └─ End Date: (none - ongoing)

2. Click "Create" →
   ✅ Task saved to database
   ✅ Alarms scheduled for next 7 days
   ✅ Comprehensive logging shows schedule

3. Tomorrow at exactly 9:00:00 AM →
   ✅ Alarm fires
   ✅ Receiver triggers Worker
   ✅ Task "Daily Standup - Oct 07" created
   ✅ Placed in "To Do This Week" column
   ✅ Due date set to Oct 07, 9:00 AM
   ✅ 3 checklist items added (unchecked)
   ✅ Next alarm (Oct 08, 9:00 AM) scheduled
```

---

## 🔍 Debugging Guide

### **Check Logs (Logcat)**

Filter by tag: `RecurringTask`

**Expected logs when creating recurring task:**

```
I/CreateRecurringTaskVM: Creating recurring task with 7 upcoming occurrences
I/RecurringTaskAlarmScheduler: ✅ ALARM SCHEDULED for 'Daily Standup'
I/RecurringTaskAlarmScheduler:    📅 Date: 2025-10-07 at 09:00
I/RecurringTaskAlarmScheduler:    ⏰ Trigger in: 1320 minutes
I/RecurringTaskAlarmScheduler:    🆔 Request Code: 1234567890
```

**Expected logs when alarm fires:**

```
I/RecurringTaskAlarmReceiver: Alarm triggered for task abc123 at 2025-10-07
I/RecurringTaskAlarmReceiver: Enqueued task creation work for abc123
I/TaskInstanceCreator: Creating task instance for abc123 at 2025-10-07
I/TaskInstanceCreator: Created task def456 from recurring task abc123
I/RecurringTaskAlarmScheduler: ✅ ALARM SCHEDULED for 'Daily Standup'
I/RecurringTaskAlarmScheduler:    📅 Date: 2025-10-08 at 09:00
```

### **Check Alarm Permission (Android 12+)**

```kotlin
// Via AlarmPermissionManager
canScheduleExactAlarms() // Returns true if granted
```

**If permission not granted:**
1. System settings will NOT allow exact alarms
2. Tasks will NOT generate at exact time
3. User must grant permission manually

**To request permission:**
```kotlin
alarmPermissionManager.openAlarmPermissionSettings()
```

### **Manual Testing**

#### **Test 1: Create Task for 2 Minutes from Now**

1. Open recurring task creation
2. Set frequency: Daily
3. Set time: [Current time + 2 minutes]
4. Set start date: Today
5. Click Create
6. **Expected**: In 2 minutes, task appears on board
7. **Check logs**: See alarm scheduled → fired → task created

#### **Test 2: Check 24-Hour Format**

1. Change device to 24-hour format (Settings > System > Date & Time)
2. Create recurring task
3. **Expected**: Time picker shows 24-hour format (00-23)
4. **Expected**: Time displays as "14:30" not "2:30 PM"

5. Change device to 12-hour format
6. Create recurring task
7. **Expected**: Time picker shows 12-hour format with AM/PM
8. **Expected**: Time displays as "2:30 PM" not "14:30"

#### **Test 3: Reboot Survival**

1. Create recurring task with alarm 1 hour from now
2. Reboot device
3. **Expected**: After boot, alarm is rescheduled
4. **Check logs**: See "Rescheduling X active recurring tasks"
5. **Expected**: Task still generates at scheduled time

---

## ⚠️ Common Issues & Solutions

### **Issue 1: Tasks Not Being Created**

**Symptoms:**
- Alarms scheduled but tasks don't appear
- Logs show "Alarm scheduled" but no "Task created"

**Solutions:**

1. **Check Alarm Permission (Android 12+)**
   ```
   Settings > Apps > Kino > Alarms & reminders
   Must be "Allowed"
   ```

2. **Check Logs**
   ```
   Look for errors in TaskInstanceCreatorWorker
   Check if KanbanApi.createTask() is being called
   ```

3. **Verify Time Zone**
   ```
   Scheduled time must be in the future
   Check device time zone matches expected
   ```

### **Issue 2: Wrong Time Format**

**Symptoms:**
- Shows "14:30" on 12-hour device
- Shows "2:30 PM" on 24-hour device

**Solution:**
- App automatically detects device setting
- If not working, check `android.text.format.DateFormat.is24HourFormat(context)`

### **Issue 3: Alarms Not Surviving Reboot**

**Symptoms:**
- After reboot, scheduled tasks don't generate

**Solutions:**

1. **Check Boot Permission**
   ```xml
   <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
   ```

2. **Check Receiver Registered**
   ```xml
   <receiver android:name="...BootCompletedReceiver" android:exported="true">
       <intent-filter>
           <action android:name="android.intent.action.BOOT_COMPLETED" />
       </intent-filter>
   </receiver>
   ```

3. **Check Logs After Boot**
   ```
   Look for: "Device boot completed, rescheduling recurring task alarms"
   ```

---

## 📱 User Experience

### **Time Display Examples**

| Device Setting | Input | Display |
|---------------|-------|---------|
| 24-hour | 14:30 | `14:30` |
| 12-hour | 14:30 | `2:30 PM` |
| 24-hour | 09:00 | `09:00` |
| 12-hour | 09:00 | `9:00 AM` |
| 24-hour | 00:00 | `00:00` |
| 12-hour | 00:00 | `12:00 AM` |

### **Recurrence Preview**

Shows next occurrences based on recurrence rule:
```
Next occurrences:
• Oct 07, 2025 at 9:00 AM
• Oct 08, 2025 at 9:00 AM
• Oct 09, 2025 at 9:00 AM
```

### **Sliding Window Scheduling**

To optimize battery, only 7 days of alarms are scheduled at a time:

```
Day 1: ✅ Alarm scheduled
Day 2: ✅ Alarm scheduled
...
Day 7: ✅ Alarm scheduled
Day 8: ⏳ Will be scheduled after Day 1 fires
```

---

## 🔧 Technical Details

### **Time Storage**

```kotlin
// ALWAYS stored in 24-hour format internally
val timeOfDay: LocalTime = LocalTime.of(14, 30) // 2:30 PM

// Database: Stored as "14:30:00"
// Display: Formatted based on device setting
```

### **Alarm Scheduling**

```kotlin
// Calculate trigger time
val triggerTime = LocalDateTime.of(date, time)
    .atZone(ZoneId.systemDefault())
    .toInstant()
    .toEpochMilli()

// Schedule exact alarm
AlarmManagerCompat.setExactAndAllowWhileIdle(
    alarmManager,
    AlarmManager.RTC_WAKEUP,
    triggerTime,
    pendingIntent
)
```

### **Task Creation**

```kotlin
val task = Task(
    id = UUID.randomUUID().toString(),
    title = "${template.title} - ${date.format(formatter)}",
    description = template.description,
    section = template.section,
    column = template.defaultColumn, // NEW
    priority = template.priority,
    dueDate = LocalDateTime.of(date, time) 
        .plusDays(template.dueDateOffsetDays), // NEW
    labels = template.labels,
    checklist = template.checklistTemplate.map { /* convert to ChecklistItem */ },
    // ... other fields
)
```

---

## 📊 Performance Considerations

### **Battery Impact**
- ✅ **Minimal**: Only schedules 7 days ahead
- ✅ **Efficient**: Uses `setExactAndAllowWhileIdle()` (battery-optimized)
- ✅ **Smart**: After each task creation, schedules just the next one

### **Memory Impact**
- ✅ **Low**: BroadcastReceiver runs briefly, delegates to Worker
- ✅ **Safe**: Worker has proper timeout and retry logic
- ✅ **Optimized**: Only loads required data from database

### **Network Impact**
- ✅ **None**: All operations are local
- ✅ **Offline-first**: Tasks created even without network

---

## 🎯 Best Practices

### **For Users**

1. ✅ Grant "Alarms & reminders" permission on first use
2. ✅ Keep app installed (don't force stop)
3. ✅ Verify device time & timezone are correct
4. ✅ Test with a short interval first (e.g., 2 minutes)

### **For Developers**

1. ✅ Always use `android.text.format.DateFormat.is24HourFormat()`
2. ✅ Store times in 24-hour format (`LocalTime`)
3. ✅ Format for display based on device preference
4. ✅ Add comprehensive logging for debugging
5. ✅ Handle permission checks before scheduling
6. ✅ Test on Android 12+ devices (permission required)
7. ✅ Test reboot scenario
8. ✅ Test timezone changes

---

## 📝 Summary

| Feature | Status | Details |
|---------|--------|---------|
| **Basic Recurring Tasks** | ✅ Complete | Daily/Weekly/Monthly/Yearly |
| **Template Fields** | ✅ Complete | All useful fields included |
| **24-Hour Format** | ✅ Complete | Auto-detects device setting |
| **Precise Timing** | ✅ Complete | AlarmManager with exact timing |
| **Doze Mode** | ✅ Complete | Works even in Doze mode |
| **Reboot Survival** | ✅ Complete | Reschedules after boot |
| **Permission Handling** | ✅ Complete | Checks & requests as needed |
| **Comprehensive Logging** | ✅ Complete | Easy debugging |
| **Task Creation** | ✅ Complete | Actually creates tasks! |
| **Battery Optimization** | ✅ Complete | Sliding window approach |
| **Error Handling** | ✅ Complete | Retry logic + graceful degradation |

---

## 🚀 Next Steps (Optional Enhancements)

1. **UI Improvements**
   - Show "Next task will be created in X minutes"
   - Visual indicator for alarm permission status
   - Test button for immediate task creation

2. **Advanced Features**
   - Skip specific dates (holidays)
   - Custom recurrence patterns (e.g., "every 2nd Monday")
   - Notification when task is created
   - Bulk edit recurring tasks

3. **Analytics**
   - Track task generation success rate
   - Monitor alarm reliability
   - User engagement metrics

---

**The recurring tasks system is now production-ready!** 🎉

All tasks will be generated at the exact specified time, survive reboots, work in Doze mode, and respect the user's device time format preference.





