# Elert — Android build image
# Builds the debug APK and runs JVM unit tests in a container.

# Stage 1: build
FROM eclipse-temurin:21-jdk AS build

ENV ANDROID_HOME=/opt/android-sdk \
    ANDROID_SDK_ROOT=/opt/android-sdk \
    GRADLE_USER_HOME=/gradle-home \
    GRADLE_OPTS="-Dorg.gradle.daemon=false"

# System dependencies
RUN apt-get update && apt-get install -y --no-install-recommends \
        unzip \
        curl \
        ca-certificates \
    && rm -rf /var/lib/apt/lists/*

# Android SDK command-line tools (cmdline-tools 16.0)
RUN mkdir -p ${ANDROID_HOME}/cmdline-tools \
    && curl -fsSL -o /tmp/cmdline-tools.zip \
        https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip \
    && unzip -q /tmp/cmdline-tools.zip -d /tmp/ \
    && mv /tmp/cmdline-tools ${ANDROID_HOME}/cmdline-tools/latest \
    && rm /tmp/cmdline-tools.zip

# Accept licenses and pre-install SDK packages
RUN yes | ${ANDROID_HOME}/cmdline-tools/latest/bin/sdkmanager --licenses \
        --sdk_root=${ANDROID_HOME} \
    && ${ANDROID_HOME}/cmdline-tools/latest/bin/sdkmanager \
        --sdk_root=${ANDROID_HOME} \
        "platform-tools" \
        "platforms;android-36" \
        "build-tools;36.0.0"

WORKDIR /workspace

# Gradle wrapper + build files first (layer caching)
COPY gradlew gradlew.bat settings.gradle.kts build.gradle.kts gradle.properties ./
COPY gradle ./gradle
COPY app ./app

RUN chmod +x ./gradlew

# Build debug APK + run JVM unit tests
RUN ./gradlew assembleDebug testDebugUnitTest

# Stage 2: extract the APK artifact
FROM alpine:3.20 AS artifact
COPY --from=build /workspace/app/build/outputs/apk/debug/app-debug.apk /out/app-debug.apk
