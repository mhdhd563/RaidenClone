# Raiden Clone - Build Instructions

## Prerequisites
- **JDK 17+** (OpenJDK or Oracle JDK)
- **Gradle 8.5+** (or use included wrapper)
- **Android SDK** (for Android build, API 34)

## Quick Start (Desktop)
```bash
cd /mnt/downloads/opencode
./gradlew desktop:run
```

## Build Android APK

### Option 1: Using Gradle Wrapper (Recommended)
```bash
cd /mnt/downloads/opencode
./gradlew android:assembleDebug
```
**Output:** `android/build/outputs/apk/debug/android-debug.apk`

### Option 2: Using System Gradle
```bash
cd /mnt/downloads/opencode
gradle android:assembleDebug
```

### Option 3: Release Build (Signed)
```bash
# Create keystore first:
keytool -genkey -v -keystore release.keystore -alias raiden -keyalg RSA -keysize 2048 -validity 10000

# Configure signing in android/build.gradle:
# android {
#     signingConfigs {
#         release {
#             storeFile file("../release.keystore")
#             storePassword "your_password"
#             keyAlias "raiden"
#             keyPassword "your_password"
#         }
#     }
#     buildTypes {
#         release {
#             signingConfig signingConfigs.release
#             minifyEnabled true
#             proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
#         }
#     }
# }

./gradlew android:assembleRelease
```
**Output:** `android/build/outputs/apk/release/android-release.apk`

## Project Structure
```
opencode/
├── build.gradle           # Root build config
├── settings.gradle        # Module includes
├── gradle.properties      # JVM args, AndroidX
├── gradlew / gradlew.bat  # Gradle wrapper scripts
├── gradle/wrapper/        # Wrapper JAR & properties
├── core/                  # Game logic (cross-platform)
│   ├── build.gradle
│   └── src/com/raidenclone/game/
│       ├── RaidenClone.java        # Main game class
│       ├── entities/               # Player, Enemy, Bullet, Particle, Powerup
│       ├── screens/                # MenuScreen, GameScreen
│       ├── systems/                # EntityManager, WaveManager, CollisionSystem, BombSystem
│       └── utils/                  # AssetManager (procedural graphics/sounds)
├── desktop/               # Desktop launcher
│   ├── build.gradle
│   └── src/com/raidenclone/game/DesktopLauncher.java
└── android/               # Android launcher
    ├── build.gradle
    ├── proguard-rules.pro
    └── src/main/
        ├── AndroidManifest.xml
        ├── java/com/raidenclone/game/AndroidLauncher.java
        └── res/values/{strings.xml, themes.xml}
```

## Game Features Implemented
- **Player**: Multi-level powerups, invulnerability frames, engine trail particles
- **Enemies**: 4 types (Basic, Fast, Heavy, Boss) with unique AI patterns
- **Waves**: 5 formation patterns + Boss every 5 waves
- **Bomb System**: **Unlimited screen-clear bombs** (double-tap/SPACE)
- **Particles**: Explosions, sparks, engine trails, bomb shockwave
- **Controls**: Touch drag + auto-fire, double-tap bomb, keyboard WASD+SPACE
- **UI**: Score, wave, lives, bombs, power level, game over screen
- **Graphics**: 100% procedural (no external assets needed)

## Troubleshooting

### JAVA_HOME not set
```bash
export JAVA_HOME=/path/to/jdk17
export PATH=$JAVA_HOME/bin:$PATH
```

### Gradle wrapper permission denied
```bash
chmod +x gradlew
```

### Android SDK not found
Set ANDROID_HOME or create `local.properties`:
```
sdk.dir=/path/to/Android/Sdk
```

### Out of memory
Increase heap in `gradle.properties`:
```
org.gradle.jvmargs=-Xmx4g -Dfile.encoding=UTF-8
```

## Testing on Device
```bash
# Install debug APK
adb install android/build/outputs/apk/debug/android-debug.apk

# Or run directly
./gradlew android:installDebug
```

## Requirements for Google Play Release
1. Sign with release keystore
2. Enable minifyEnabled + ProGuard
3. Test on multiple API levels (21-34)
4. Add app icons in `android/src/main/res/mipmap-*`
5. Update versionCode/versionName in `android/build.gradle`

---
**Note:** This project uses 100% procedural generation for all graphics and sounds - no external asset files required!