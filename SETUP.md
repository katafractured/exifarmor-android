# ExifArmor Android — Setup Instructions

## Prerequisites

- Android SDK 35 (API level 35)
- Android SDK Platform 26+ (for minSdk)
- JDK 11 or higher
- Android Studio (latest)

## Initial Setup

### 1. Clone/Initialize Repository

```bash
cd /home/artemis/dev/exifarmor-android
git init
git add .
git commit -m "Initial commit: ExifArmor Android scaffold"
```

### 2. Create Signing Config

For release builds, create `keystore.properties`:

```bash
cp keystore.properties.template keystore.properties
```

Edit `keystore.properties` with your signing key:
```properties
storeFile=/absolute/path/to/exifarmor.jks
storePassword=your_store_password
keyAlias=your_key_alias
keyPassword=your_key_password
```

For debug builds (no signature required), you can skip this — debug signing is auto-configured.

### 3. Build

#### Debug APK
```bash
./gradlew assembleDebug
# Output: app/build/outputs/apk/debug/app-debug.apk
```

#### Release APK
```bash
./gradlew assembleRelease
# Output: app/build/outputs/apk/release/app-release.apk
```

#### App Bundle (for Play Store)
```bash
./gradlew bundleRelease
# Output: app/build/outputs/bundle/release/app-release.aab
```

### 4. Test on Device/Emulator

```bash
# Install debug APK
adb install app/build/outputs/apk/debug/app-debug.apk

# Run
adb shell am start -n com.katafract.exifarmor/com.katafract.exifarmor.MainActivity
```

## Development Workflow

### Running in Android Studio

1. Open project in Android Studio
2. SDK Manager → Ensure SDK 35 + build tools installed
3. File → Sync Now
4. Run → Run 'app'

### Running Tests

```bash
./gradlew test              # Unit tests
./gradlew connectedAndroidTest  # Instrumented tests
```

### Updating Dependencies

- Edit `gradle/libs.versions.toml` for version numbers
- Edit `app/build.gradle.kts` for dependency declarations
- Run `./gradlew refreshVersions` to update catalogs

## Version Management

### Bumping Version

```bash
# Patch bump (1.0.0 → 1.0.1)
./scripts/bump patch

# Minor bump (1.0.0 → 1.1.0)
./scripts/bump minor

# Major bump (1.0.0 → 2.0.0)
./scripts/bump major
```

Script creates git commit + tag. Push to trigger CI:
```bash
git push origin main
git push origin --tags
```

## CI/CD — GitHub Actions

Tag pushes automatically:
1. Run `./gradlew assembleRelease bundleRelease`
2. Create GitHub Release with APK + AAB artifacts

Ensure `GITHUB_TOKEN` has `contents:write` permission.

## Project Structure Reference

```
exifarmor-android/
├── app/src/main/java/com/katafract/exifarmor/
│   ├── MainActivity.kt          (entry point, intent handling)
│   ├── models/                  (data classes)
│   ├── services/                (business logic)
│   ├── viewmodel/               (MVVM state)
│   └── ui/                      (Compose screens)
├── app/src/main/res/            (resources: drawable, xml, values)
├── app/build.gradle.kts         (app config, dependencies)
└── gradle/                      (version catalog, wrapper)
```

## Common Issues

### "Unsupported class-file format major version 65"
- Update Java: requires JDK 11+
- Check `java -version`

### "Android SDK not found"
- Set `ANDROID_SDK_ROOT=/path/to/sdk` env var
- Or configure in Android Studio → SDK Manager

### Gradle sync fails
- File → Invalidate Caches → Restart
- Delete `.gradle/` folder
- Run `./gradlew clean`

### Build fails: "No signingConfig"
- Create `keystore.properties` (see step 2)
- Or remove signing from `buildTypes.release` for debug-only builds

## IDE Setup

### Android Studio
1. Import project (File → Open)
2. Wait for indexing
3. SDK Manager → Install API 35
4. Run → Run 'app'

### VS Code (with Remote SSH)
- Extension: Remote - SSH
- Install official Android extension pack
- Connect to artemis, open `/home/artemis/dev/exifarmor-android`

## Next Steps

- [ ] Add firebase-analytics (optional telemetry)
- [ ] Implement share functionality in ResultsScreen
- [ ] Add video EXIF support (MP4/MOV)
- [ ] Test on multiple Android versions (8.0, 10, 12, 14)
- [ ] Battery/memory profiling
- [ ] Play Store release preparation
