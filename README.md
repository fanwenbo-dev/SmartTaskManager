# SmartTaskManager

## Overview
SmartTaskManager is an innovative, accessible task management Android app designed to help users efficiently organize and track their daily tasks and appointments. By integrating advanced features such as speech-to-text input, location-based reminders, calendar sync, and an AI-powered chatbot, this app ensures users stay productive with minimal effort.

Designed with accessibility and usability in mind, SmartTaskManager caters to students, professionals, and individuals with diverse needs, including those with physical or cognitive challenges.

---

## Key Features

- **Speech-to-Text Input**  
  Easily add tasks by speaking instead of typing, improving accessibility and saving time.

- **RecyclerView with CardView**  
  Displays tasks in a clean, scrollable list with visually appealing cards that show task details clearly.

- **Selectable Tasks**  
  Long-press to select tasks for editing or deletion, enabling efficient task management.

- **Local Phone Calendar Integration**  
  Sync tasks with your phone’s calendar to centralize reminders and stay updated.

- **Firebase Cloud Database**  
  Securely store and sync your data in real time across devices.

- **Google Geofencing**  
  Receive task reminders based on your location, ideal for location-specific activities.

- **SQLite Offline Storage**  
  Access and manage tasks even without an internet connection.

- **Generative AI Chatbot**  
  Interact with an AI assistant for personalized task suggestions, motivational tips, and productivity guidance.

- **Splash Screen with Music**  
  Enhances user engagement with an interactive and welcoming app launch.

- **Bottom Navigation Bar**  
  Easy navigation between Home, Calendar, and Settings sections.

---

## Installation

1. Clone this repository:
   ```bash
   git clone https://github.com/yourusername/SmartTaskManager.git
Open the project in Android Studio.
Set up Firebase for your app by following Firebase Setup Guide.
Obtain Google Maps API key and configure Geofencing as per Google Maps API documentation.
Build and run the app on an Android device or emulator.
Usage

Tap the mic icon to add tasks via speech.
View tasks in the list; long-press to select and edit or delete.
Sync tasks with your phone’s calendar for unified reminders.
Receive notifications when you enter or leave specified locations.
Chat with the AI assistant to get smart productivity suggestions.
Technologies Used

Android Studio (Java/Kotlin)
Firebase Cloud Firestore
Google Maps & Geofencing API
SQLite
Generative AI API
Challenges & Solutions

Challenge	Solution
Smooth RecyclerView item selection	Used optimized RecyclerView adapters and tested extensively
Firebase sync issues	Implemented efficient data reads/writes and offline persistence
Geofencing reliability	Tested geofence triggers in real-world scenarios with various device states
Demo Video

Watch the app in action:
SmartTaskManager Demo

