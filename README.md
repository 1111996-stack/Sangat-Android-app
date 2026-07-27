# Sangat

An Android application for audio recording with foreground service and auto-start functionality.

## Features

- **Audio Recording**: Record audio with customizable settings
- **Foreground Service**: Keeps the app running in the background
- **Auto-start on Boot**: Automatically starts when device boots up
- **Battery Optimization**: Request to ignore battery optimizations for uninterrupted service
- **Modern UI**: Built with Jetpack Compose and Material3
- **Voice Change**: Change voice effects for recordings
- **Permission Management**: Easy permission request flow

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Material Design**: Material3
- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)
- **Build Tool**: Gradle with Kotlin DSL

## Requirements

- Android Studio Hedgehog or later
- JDK 17
- Android SDK 34

## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/sangat.git
   cd sangat
   ```

2. Open the project in Android Studio

3. Sync Gradle files

4. Build and run on your device or emulator:
   ```bash
   ./gradlew assembleDebug
   ```

## Permissions

This app requires the following permissions:

- `RECORD_AUDIO` - To record audio
- `POST_NOTIFICATIONS` - To show notifications
- `FOREGROUND_SERVICE` - To run background service
- `FOREGROUND_SERVICE_MEDIA_PLAYBACK` - For media playback foreground service
- `WAKE_LOCK` - To keep device awake during recording
- `MODIFY_AUDIO_SETTINGS` - To modify audio settings
- `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` - To request ignoring battery optimizations
- `RECEIVE_BOOT_COMPLETED` - To auto-start on device boot

## Project Structure

```
app/
├── src/main/java/com/sangat/app/
│   ├── MainActivity.kt
│   ├── BatteryService.kt
│   ├── BootReceiver.kt
│   ├── RecorderHelper.kt
│   ├── ui/
│   │   ├── components/
│   │   │   ├── Glass.kt
│   │   │   └── SangatLogo.kt
│   │   ├── screens/
│   │   │   ├── MainScreen.kt
│   │   │   ├── OptionScreen.kt
│   │   │   ├── PermissionSheet.kt
│   │   │   ├── RecorderWizard.kt
│   │   │   ├── SplashScreen.kt
│   │   │   └── ChangeVoiceDialog.kt
│   │   └── theme/
│   │       ├── Theme.kt
│   │       └── Type.kt
│   └── utils/
│       └── SharedPrefsHelper.kt
```

## Building

### Debug Build
```bash
./gradlew assembleDebug
```

### Release Build
```bash
./gradlew assembleRelease
```

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Author

- Your Name - [@yourusername](https://github.com/yourusername)
