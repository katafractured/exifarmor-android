# ExifArmor Android Architecture

## Project Structure

```
exifarmor-android/
├── app/
│   ├── build.gradle.kts                 # App build config
│   ├── proguard-rules.pro               # Minification rules
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml      # App manifest
│           ├── java/com/katafract/exifarmor/
│           │   ├── MainActivity.kt      # Entry activity
│           │   ├── models/              # Data classes
│           │   │   ├── PhotoMetadata.kt
│           │   │   ├── StripOptions.kt
│           │   │   └── StripResult.kt
│           │   ├── services/            # Business logic
│           │   │   ├── MetadataService.kt
│           │   │   └── StripService.kt
│           │   ├── ui/                  # Compose screens
│           │   │   ├── HomeScreen.kt
│           │   │   ├── PreviewScreen.kt
│           │   │   ├── ResultsScreen.kt
│           │   │   └── theme/
│           │   │       └── Theme.kt
│           │   └── viewmodel/
│           │       └── MainViewModel.kt
│           └── res/
│               ├── drawable/
│               ├── mipmap-anydpi-v26/
│               ├── values/
│               └── xml/
├── gradle/                              # Gradle wrapper
├── build.gradle.kts                     # Root build config
├── settings.gradle.kts                  # Project settings
├── gradle.properties                    # Gradle properties
└── .github/workflows/                   # CI/CD

```

## Architecture Pattern: MVVM + Compose

### Data Flow

```
MainActivity (UI Host)
    ↓
MainViewModel (State Management)
    ↓
Services (Business Logic)
    ├── MetadataService (Read EXIF)
    └── StripService (Remove EXIF)
    ↓
Models (Data Structures)
```

### Screen State Machine

```
HOME
  ↓ (user picks photos)
PREVIEW
  ├─ (user clicks Strip)
  ↓
PROCESSING
  ↓
DONE
  └─ (user clicks Scan Again) → HOME
```

## Key Components

### MainViewModel
- Manages screen navigation state (HOME, PREVIEW, PROCESSING, DONE)
- Holds reactive state:
  - `photoList`: Selected photos with metadata
  - `stripResults`: Results after stripping
  - `stripOptions`: Current strip preferences
  - `processingProgress`: Async progress (0.0-1.0)
- Functions:
  - `loadPhotos(uris)`: Read EXIF from selected images
  - `updateOptions(options)`: Change strip settings
  - `startStrip()`: Execute async strip operation
  - `reset()`: Return to home

### MetadataService
- Reads EXIF data from image URI
- Extracts:
  - GPS: latitude, longitude, altitude
  - Device: make, model, software
  - DateTime: original, digitized
  - Camera: focal length, aperture, ISO, exposure time
  - Dimensions: pixel width/height
- Returns nullable `PhotoMetadata` on success

### StripService
- Removes EXIF tags based on `StripOptions`
- Steps:
  1. Read source bytes from URI
  2. Create temp file
  3. Clear selected EXIF tags using `ExifInterface.setAttribute(tag, null)`
  4. Save cleaned copy to MediaStore
  5. Return `StripResult` with counts
- Tag categories:
  - **Location**: GPS_LATITUDE, GPS_LONGITUDE, GPS_ALTITUDE, GPS_*_REF, GPS_TIMESTAMP, GPS_DATESTAMP
  - **DateTime**: DATETIME, DATETIME_ORIGINAL, DATETIME_DIGITIZED
  - **Device**: MAKE, MODEL, SOFTWARE, LENS_MODEL
  - **Camera**: FOCAL_LENGTH, APERTURE_VALUE, EXPOSURE_TIME, ISO_SPEED_RATINGS

### Models
- **PhotoMetadata**: Source image + extracted EXIF fields
- **StripOptions**: Boolean flags for removal categories + presets (ALL, LOCATION_ONLY, PRIVACY_FOCUSED)
- **StripResult**: Source URI + cleaned URI + success/error + fields removed count

## UI Screens

### HomeScreen
- Empty state with icon + app name
- "Select Photos" button → photo picker
- `ActivityResultContracts.GetMultipleContents()` for image/* MIME type

### PreviewScreen
- List of selected photos with thumbnails
- GPS warning badge (red) if location detected
- Device/DateTime info snippets
- Strip option preset chips (All/Location/Privacy)
- "Strip Metadata" action button

### ResultsScreen
- List of results per photo (success/error)
- "X fields removed" count per photo
- Summary: "N/M photos cleaned, Y fields removed total"
- "Scan Again" → reset to HOME
- "Share" → share cleaned images (TODO)

## Permissions

- `READ_MEDIA_IMAGES` (API 33+): Read gallery photos
- `READ_EXTERNAL_STORAGE` (legacy): Fallback for API <33
- `WRITE_EXTERNAL_STORAGE` (legacy): Save to Pictures

## EXIF Tag Reference

Standard tags from `androidx.exifinterface.media.ExifInterface`:
- `TAG_GPS_LATITUDE`, `TAG_GPS_LONGITUDE`, `TAG_GPS_ALTITUDE`
- `TAG_GPS_LATITUDE_REF`, `TAG_GPS_LONGITUDE_REF`, `TAG_GPS_ALTITUDE_REF`
- `TAG_GPS_TIMESTAMP`, `TAG_GPS_DATESTAMP`
- `TAG_DATETIME`, `TAG_DATETIME_ORIGINAL`, `TAG_DATETIME_DIGITIZED`
- `TAG_MAKE`, `TAG_MODEL`, `TAG_SOFTWARE`, `TAG_LENS_MODEL`
- `TAG_FOCAL_LENGTH`, `TAG_APERTURE_VALUE`, `TAG_EXPOSURE_TIME`, `TAG_ISO_SPEED_RATINGS`
- `TAG_IMAGE_WIDTH`, `TAG_IMAGE_LENGTH`

## Intent Handling

App responds to `ACTION_SEND` and `ACTION_SEND_MULTIPLE` with `image/*` MIME type:
- Single share: extract URI from `EXTRA_STREAM`
- Multiple share: extract ArrayList<Uri> from `EXTRA_STREAM`
- Both trigger `viewModel.loadPhotos(uris)` → PREVIEW screen

## Async/Coroutines

- `viewModelScope.launch(Dispatchers.IO)` for file I/O
- `StateFlow` for reactive UI state
- Services are synchronous; ViewModel handles threading

## Build & Release

- Gradle: 8.7 with Kotlin 2.0.0
- AGP: 8.4.2
- Signing: via `keystore.properties` (release signing config)
- GitHub Actions: Tag push → build APK/AAB → GitHub Release
- Version bumping: `./scripts/bump [major|minor|patch]`

## Next Steps (TODO)

- Share cleaned images (intent, FileProvider)
- Video EXIF stripping (MP4/MOV support)
- Advanced filters (selective field removal)
- Batch operations optimization
- Analytics/telemetry (optional)
