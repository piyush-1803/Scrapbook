# 📖 Scrapbook

[![Kotlin](https://img.shields.io/badge/Kotlin-100%25-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://www.android.com/)
[![Build](https://img.shields.io/badge/Build-Gradle_Kotlin_DSL-02303A?style=for-the-badge&logo=gradle)](https://gradle.org/)

> A highly aesthetic journaling application for Android, built to make digital memory-keeping visual, personal, and seamless.

Scrapbook combines the raw charm of traditional journaling with a modern, high-fidelity digital layout. It is integrated with AI capabilities via Google AI Studio templates to help elevate, refine, and prompt your daily writing workflows.

---

## ✨ Features

* **Highly Aesthetic UI:** Minimalist yet expressive visual styling designed specifically for comfortable, immersive journaling.
* **Smart Prompting:** Built on top of Google AI Studio templates to provide context-aware reflections and writing inspiration.
* **Privacy First:** Localized configurations to keep your personal logs secure on your device.

---

## 🛠️ Tech Stack & Prerequisites

* **Language:** [Kotlin](https://kotlinlang.org/)
* **Build Configuration:** Gradle Kotlin DSL (`.gradle.kts`)
* **IDE Requirement:** [Android Studio](https://developer.android.com/studio)
* **AI Framework:** Google Gemini SDK via Google AI Studio

---

## 🚀 Getting Started & Local Setup

Follow these steps to get your local development environment up and running:

### 1. Clone the Repository
```bash
git clone [https://github.com/piyush-1803/Scrapbook.git](https://github.com/piyush-1803/Scrapbook.git)
cd Scrapbook
2. Import into Android Studio
Open Android Studio.

Select Open and choose the root directory containing this project.

Allow Android Studio to index and resolve any initial build dependencies or incompatibilities as it imports.

3. Configure Your Environment Variables
Duplicate the example environment file to hold your development keys:

Bash
cp .env.example .env
Open your newly created .env file and append your Gemini API Key:

Code snippet
GEMINI_API_KEY=your_actual_gemini_api_key_here
4. Adjust Build Signing (For Local Debugging)
To deploy smoothly onto a local emulator or a physical testing device without signature mismatch errors, open the app-level build.gradle.kts file (app/build.gradle.kts) and safely remove or comment out this line:

Kotlin
signingConfig = signingConfigs.getByName("debugConfig")
5. Run the App
Select your preferred target deployment device (Emulator or Physical device via ADB) in Android Studio and press Run (Shift + F10).

📂 Project Structure
Plaintext
├── .build-outputs/      # Compiled artifacts and local build variations
├── app/                 # Main Android application module (Source code, resources, manifests)
├── gradle/              # Gradle wrapper and global build dependencies
├── .env.example         # Template file for secret credentials
├── build.gradle.kts     # Root level build script
├── settings.gradle.kts  # Project-wide repository mappings and module definitions
└── metadata.json        # AI Studio platform configurations
🤖 AI Studio Deployment
This application interacts directly with the Google AI Studio ecosystem. If you have administrative or contributor access, you can manage the application model frameworks directly on the cloud environment:

View Workspace Dashboard: Scrapbook on AI Studio

📄 License
Maintained with 💙 by piyush-1803. Check the repository settings or individual source files for extended licensing agreements.
