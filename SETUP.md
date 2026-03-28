## Setup Instructions

### Prerequisites
* Android Studio Hedgehog or later
* JDK 17
* Google account with Firebase Console access

### Step 1 — Firebase setup
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Create a project named "uzazi-prod"
3. Add an Android app with package name `com.uzazi.app`
4. Download `google-services.json` and place it in the `app/` folder
5. Enable **Authentication**: Phone and Google providers
6. Enable **Firestore Database** in production mode
7. Enable **Cloud Messaging**

### Step 2 — Backend URL
1. Open `local.properties` (create if it doesn't exist)
2. Add: `BASE_URL=https://your-cloud-run-url.run.app/`
3. For local dev with ngrok: `BASE_URL=https://xxxx.ngrok.io/`
4. Ensure `app/build.gradle.kts` uses this property via `buildConfigField`.

### Step 3 — Run the app
1. Open the project in Android Studio
2. Wait for Gradle sync to complete
3. Run on an emulator (API 26+) or physical device
4. The first launch will lead you through Onboarding → Auth → Home

### Step 4 — Connect to FastAPI locally
1. Run your FastAPI backend: `uvicorn main:app --reload --port 8000`
2. Install ngrok and run: `ngrok http 8000`
3. Copy the https URL into `local.properties` under `BASE_URL`

### Step 5 — Test night companion
1. Temporarily change `NightModeDetector.isNightTime()` to always return `true`.
2. Launch the app; the `DailyCtaCard` will show the Night Companion option.
3. Ensure your backend `/api/chat/stream` endpoint is functional.

### Hackathon Shortcuts (Demo Day)
1. **Mock Auth**: Skip Phone OTP by hardcoding a success result in `UserRepositoryImpl`.
2. **Instant Garden**: Set `totalPetals` to 29 in DataStore to demo the "Full Garden" unlock.
3. **Force Night**: Hardcode `isNightTime = true` in `MainActivity`.
4. **Mock CHW**: Add a dummy document in Firestore `users/{uid}` with `chwPhone="+123456789"`.
5. **Static QR**: Return a hardcoded Base64 string in `ShareViewModel` if the backend isn't ready.

### Known Issues
* **Version Conflict**: If `androidx.compose.material3` versions clash, force a specific version in `libs.versions.toml`.
* **Hilt Workers**: Ensure `androidx.hilt:hilt-work:1.2.0` is used if the worker fails to inject.
* **Manifest Merger**: If `UzaziMessagingService` conflicts, check that the `android:name` matches the package exactly.
