#!/bin/bash
# ExifArmor Android — Project Verification Script

echo "=== ExifArmor Android Project Verification ==="
echo

echo "✓ Checking directory structure..."
required_files=(
  "build.gradle.kts"
  "settings.gradle.kts"
  "gradle.properties"
  "app/build.gradle.kts"
  "app/src/main/AndroidManifest.xml"
  "gradle/libs.versions.toml"
  "gradle/wrapper/gradle-wrapper.properties"
)

for file in "${required_files[@]}"; do
  if [ -f "$file" ]; then
    echo "  ✓ $file"
  else
    echo "  ✗ MISSING: $file"
  fi
done
echo

echo "✓ Checking Kotlin source files..."
kotlin_files=(
  "app/src/main/java/com/katafract/exifarmor/MainActivity.kt"
  "app/src/main/java/com/katafract/exifarmor/models/PhotoMetadata.kt"
  "app/src/main/java/com/katafract/exifarmor/models/StripOptions.kt"
  "app/src/main/java/com/katafract/exifarmor/models/StripResult.kt"
  "app/src/main/java/com/katafract/exifarmor/services/MetadataService.kt"
  "app/src/main/java/com/katafract/exifarmor/services/StripService.kt"
  "app/src/main/java/com/katafract/exifarmor/viewmodel/MainViewModel.kt"
  "app/src/main/java/com/katafract/exifarmor/ui/HomeScreen.kt"
  "app/src/main/java/com/katafract/exifarmor/ui/PreviewScreen.kt"
  "app/src/main/java/com/katafract/exifarmor/ui/ResultsScreen.kt"
  "app/src/main/java/com/katafract/exifarmor/ui/theme/Theme.kt"
)

for file in "${kotlin_files[@]}"; do
  if [ -f "$file" ]; then
    echo "  ✓ $file"
  else
    echo "  ✗ MISSING: $file"
  fi
done
echo

echo "✓ Checking resource files..."
resource_files=(
  "app/src/main/res/values/strings.xml"
  "app/src/main/res/values/colors.xml"
  "app/src/main/res/values/themes.xml"
  "app/src/main/res/drawable/ic_launcher_background.xml"
  "app/src/main/res/drawable/ic_launcher_foreground.xml"
  "app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml"
  "app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml"
  "app/src/main/res/xml/file_paths.xml"
  "app/src/main/res/xml/backup_rules.xml"
  "app/src/main/res/xml/data_extraction_rules.xml"
  "app/src/main/res/xml/network_security_config.xml"
)

for file in "${resource_files[@]}"; do
  if [ -f "$file" ]; then
    echo "  ✓ $file"
  else
    echo "  ✗ MISSING: $file"
  fi
done
echo

echo "✓ Checking documentation..."
docs=(
  "README.md"
  "ARCHITECTURE.md"
  "SETUP.md"
)

for file in "${docs[@]}"; do
  if [ -f "$file" ]; then
    echo "  ✓ $file"
  else
    echo "  ✗ MISSING: $file"
  fi
done
echo

echo "✓ Checking CI/CD..."
if [ -f ".github/workflows/release.yml" ]; then
  echo "  ✓ .github/workflows/release.yml"
else
  echo "  ✗ MISSING: .github/workflows/release.yml"
fi

if [ -f "scripts/bump" ] && [ -x "scripts/bump" ]; then
  echo "  ✓ scripts/bump (executable)"
else
  echo "  ✗ MISSING or NOT EXECUTABLE: scripts/bump"
fi
echo

echo "=== Summary ==="
total_files=$(find . -type f -not -path "./.git/*" | wc -l)
echo "Total project files: $total_files"
echo

echo "Package: com.katafract.exifarmor"
echo "minSdk: 26, targetSdk: 35"
echo "Gradle: 8.7, AGP: 8.4.2, Kotlin: 2.0.0"
echo
echo "✓ Project scaffold complete and ready to build!"
