# ExifArmor Android

Native Android app for removing EXIF metadata from photos.

## Architecture

- **MVVM** with Jetpack Compose
- **Kotlin** with coroutines
- **ExifInterface** for EXIF read/write
- **Material Design 3**

## Building

### Requirements
- Android SDK 35 (compileSdk)
- minSdk 26
- JDK 11+

### Setup

```bash
# Copy and configure keystore
cp keystore.properties.template keystore.properties
# Edit keystore.properties with your signing key details
```

### Build

```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Bundle for Play Store
./gradlew bundleRelease
```

## Versioning

```bash
# Bump patch version and create git tag
./scripts/bump patch

# Or minor/major
./scripts/bump minor
./scripts/bump major
```

## Features

- Select multiple photos from gallery
- Preview EXIF metadata before stripping
- Strip selective metadata categories:
  - Location (GPS)
  - DateTime
  - Device info (make/model)
  - Camera settings (ISO, aperture, etc.)
- Presets: Strip All, Location Only, Privacy Focused
- Save cleaned images to gallery
- Shows count of fields removed

## Permissions

- `READ_MEDIA_IMAGES` (API 33+)
- `READ_EXTERNAL_STORAGE` (legacy)
- `WRITE_EXTERNAL_STORAGE` (legacy)

## Notes

- Cleaned images are saved with timestamped filenames to ExifArmor folder
- Original images are never modified
- Metadata stripping preserves image quality (no recompression)
