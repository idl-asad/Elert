# Elert

Android MVP alarm app: **notification → rule match → loud alarm**.

When a matching notification arrives (for example from WhatsApp), Elert can wake the phone and play a looping alarm until you dismiss it. Useful when you must not miss specific people or keywords while the phone is locked or silenced.

## Features

- **Rules** with app package, up to 3 contacts, and up to 3 keywords
- **Matching logic**
  - Contact only → any message from that contact (with a save-time warning)
  - Keyword only → any of the keywords in title/body
  - Contact + keyword → **contact AND any keyword** must both match
- **Notification listener** reads status-bar notifications in the background
- **Call filtering** skips WhatsApp/phone call-style notifications
- **In-use skip** does not fire the loud alarm when the screen is on and unlocked
- **Lock-screen alarm** via foreground service, full-screen intent, and dismiss UI
- **Dismiss** from the alarm screen or notification actions (Open / Dismiss)
- **Room** persistence for rules
- **Settings** for notification access, post-notification permission, and full-screen alarm permission

## Requirements

- Android Studio (recent stable)
- JDK 11+
- Device or emulator: **minSdk 26**, targetSdk 36
- Permissions to grant on device:
  - Notification access (Notification Listener)
  - Notifications (Android 13+)
  - Full-screen intent / display over lock screen (Android 14+ where required)
  - Contacts (optional; for picking contacts when creating a rule)
  - Disable battery restrictions for Elert on aggressive OEMs (Oppo/OnePlus/etc.)

## Build & run

Open the project in Android Studio, sync Gradle, then Run on a device.

Or from the project root:

```bash
./gradlew assembleDebug
```

Debug APK:

```text
app/build/outputs/apk/debug/app-debug.apk
```

Install:

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## How to use

1. Open **Settings** in the app and enable **notification access**.
2. Allow notifications (and full-screen alarm permission if prompted).
3. Tap **+** to create a rule: title → app → keywords → contacts → active hours → review/save.
4. Keep Elert allowed to run in the background.
5. When a matching notification arrives while the phone is locked / screen off, Elert starts the alarm.
6. Dismiss from the alarm screen or the notification **Dismiss** action.

## Project structure (high level)

```text
app/src/main/java/com/example/elert/
  alarm/           # Foreground service, session (sound/vibrate), dismiss UI
  data/            # Room DB, repositories, contacts/apps providers
  domain/          # RuleMatcher, validation
  notification/    # Listener helpers, text extraction, call filter
  service/         # NotificationListenerService
  ui/              # Compose screens (Home, Add Rule, Settings, Alarm)
```

## Tech stack

- Kotlin, Jetpack Compose, Material 3
- Room + KSP
- Navigation Compose
- NotificationListenerService + foreground service (`mediaPlayback`)

## Notes / limitations (MVP)

- Matching uses notification **title + body** text (messaging apps vary by OEM/version).
- Alarm is skipped when the device is actively in use (screen on + unlocked).
- WhatsApp “2 new messages” summaries are handled where possible via message extras.
- Aggressive battery savers may delay or block background alarms; whitelist Elert.

## License

Private / unlicensed unless otherwise stated by the repository owner.
