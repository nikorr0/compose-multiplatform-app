# Compose Multiplatform Application

This is a cross-platform application built using **JetBrains Compose Multiplatform**. It targets both Android and Desktop (JVM) environments, providing a unified UI experience across platforms.

The application communicates with a RESTful API built using the Laravel framework. This backend handles user authentication, post management, comments, friendships, and feed generation. All data displayed in the Compose Multiplatform app is fetched from [this Laravel-powered API](https://github.com/nikorr0/web_laravel).

---

## Features

- Shared UI codebase using Jetpack Compose
- Android support via Android Studio
- Desktop support via JVM
- Integration with Laravel API for dynamic content

---

## Application Showcase

**The main page of the application**
![Main page](https://github.com/nikorr0/compose-multiplatform-app/blob/main/screenshots/main_page.png)

**View a post**
![View a post](https://github.com/nikorr0/compose-multiplatform-app/blob/main/screenshots/view_card.png)

**Create a post**
![Create a card](https://github.com/nikorr0/compose-multiplatform-app/blob/main/screenshots/create_card.png)

---

## Before running!
 - install JDK 17 or higher on your machine
 - add `local.properties` file to the project root and set a path to Android SDK there (optional)

### Desktop
Run the desktop application: `./gradlew :composeApp:run`
Run desktop UI tests: `./gradlew :composeApp:jvmTest`

If you use IntelliJ IDEA, add a run configuration as shown below:
![Run configuration](https://github.com/nikorr0/compose-multiplatform-app/blob/main/screenshots/run_configuration.png)

### Android
To run the application on android device/emulator:  
 - open project in Android Studio and run imported android run configuration

To build the application bundle:
 - run `./gradlew :composeApp:assembleDebug`
 - find `.apk` file in `composeApp/build/outputs/apk/debug/composeApp-debug.apk`
Run android UI tests on the connected device: `./gradlew :composeApp:connectedDebugAndroidTest`

