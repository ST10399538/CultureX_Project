# CultureX - Cultural Travel Etiquette Application

## Group Members
- Nathan Hani - ST10322054
- Inge Dafel - ST10399538
- Ryan Stratford - ST10364151

## Links
- **YouTube Demo**: [https://youtu.be/esRVgvUBwFw](https://youtu.be/esRVgvUBwFw)
- **API Documentation**: [https://culturex-api-a8f5g9avfjb3fyfy.southafricanorth-01.azurewebsites.net/swagger](https://culturex-api-a8f5g9avfjb3fyfy.southafricanorth-01.azurewebsites.net/swagger)

---

## Recent Changes and Updates

### Practice Mode with Text-to-Speech
A new interactive learning feature has been implemented for the **Etiquette Category Page**:
- **Text-to-Speech Integration**: Users can now listen to etiquette guidelines and cultural information being read aloud, enhancing accessibility and learning retention
- **Practice Mode**: An interactive mode that allows users to practice cultural etiquette scenarios with audio guidance
- **Hands-Free Learning**: Enables users to learn while multitasking or when reading is inconvenient
- **Language Support**: Text-to-speech functionality supports multiple languages for authentic pronunciation and cultural context
- **Improved Accessibility**: Makes cultural learning more accessible for users with visual impairments or reading difficulties

### About the Country Feature
The **Main Menu/Home Page** now includes comprehensive country information:
- **"About the Country" Button**: A dedicated button on the home page that provides detailed information about the selected country
- **Comprehensive Overview**: Displays essential country information including:
  - General country facts and statistics
  - Cultural background and history
  - Key cultural values and social norms
  - Geographic and demographic information
  - Important cultural considerations for travelers
- **Enhanced User Experience**: Provides context before diving into specific cultural categories, helping users understand the broader cultural landscape
- **Informed Travel Planning**: Allows users to gain foundational knowledge about their destination before exploring specific etiquette categories

### UI/UX Improvements
The application has undergone significant visual and user experience enhancements:
- **Modern Material Design 3**: Updated UI components following Google's latest Material Design guidelines for a more contemporary look
- **Enhanced Visual Hierarchy**: Improved layout structures with better spacing, typography, and color contrast for easier navigation
- **Polished Interface Elements**: Refined buttons, cards, and interactive components with smooth animations and transitions
- **Consistent Design Language**: Unified visual style across all screens for a more cohesive user experience
- **Improved Readability**: Optimized font sizes, line spacing, and content layouts for better content consumption
- **Enhanced Color Scheme**: Updated color palette that better reflects cultural diversity while maintaining excellent accessibility standards
- **Intuitive Navigation**: Streamlined navigation patterns with clearer visual cues and improved user flow

These updates enhance the app's educational value, visual appeal, and overall usability, providing users with a more engaging, accessible, and comprehensive cultural learning experience.

---

## Executive Summary

CultureX is a mobile application designed to bridge cultural gaps for international travellers by providing authentic, localized insights into cultural etiquette and customs worldwide. This report details the app's purpose, design considerations, technical architecture, and development methodology, including the utilization of GitHub and GitHub Actions for version control and continuous integration/continuous deployment (CI/CD).

## Project Purpose and Vision

### 1.1 Market Gap and Opportunity
The travel industry faces a significant challenge: tourists frequently commit cultural faux pas due to inadequate cultural education. Generic travel apps rely on automated content or superficial online resources, failing to provide the depth of cultural understanding necessary for meaningful interactions. CultureX addresses this gap by offering comprehensive, well-researched cultural information from verified local contributors rather than generic algorithms.

### 1.2 Core Value Proposition
CultureX transforms cultural education from passive information consumption to active learning through:
- **Authentic Cultural Guidance**: Content curated by verified local experts ensures accuracy and cultural sensitivity
- **Comprehensive Coverage**: Detailed information across multiple cultural dimensions (dress code, communication styles, etiquette, tipping norms, greetings)
- **Practical Application**: Real-world scenarios and interactive guides that prepare travellers for actual situations
- **Accessibility**: Offline functionality ensures cultural guidance remains available regardless of connectivity

### 1.3 Target Audience
The application targets culturally conscious travellers who:
- Value authentic local experiences over tourist attractions
- Seek to minimize cultural misunderstandings
- Desire deeper cultural understanding beyond surface-level tourist information
- Travel internationally for business or leisure

## Design Philosophy and Considerations

### 2.1 User-Centric Design Approach
Based on competitive analysis of Culture Trip, Withlocals, and TripAdvisor, CultureX incorporates best practices while addressing identified weaknesses:

**Strengths Adopted:**
- Visually intuitive interface with high-quality imagery (Culture Trip model)
- Personalized content delivery based on user preferences (all three apps)
- Integration of multimedia content (articles, videos, guides)
- Direct booking capabilities within the platform

**Weaknesses Addressed:**
- Robust offline functionality (addressing Culture Trip's connectivity dependence)
- Consistent search and filter functionality (improving on Culture Trip's limitations)
- Performance optimization for various device specifications (addressing TripAdvisor's issues)
- Verified, quality-controlled content (addressing TripAdvisor's fake review problem)

### 2.2 Visual Design Language
**Brand Identity**: The CultureX icon features a stylized globe with cultural symbols (greeting gestures, traditional patterns) rendered in warm, welcoming colors. The design emphasizes connection and cultural harmony with a modern, approachable aesthetic.

**Color Palette:**
- Primary: Warm earth tones suggesting cultural diversity and hospitality
- Secondary: Vibrant accent colors representing different cultures
- Neutral: Clean backgrounds ensuring content readability

**Typography:**
- Clear, accessible fonts supporting multilingual content
- Hierarchical text sizing for information prioritization
- Adequate spacing for mobile readability

### 2.3 Navigation Architecture
The application follows a hub-and-spoke navigation pattern:

- **Authentication Flow**: Load Screen → Login/Sign Up → Biometric Setup → Onboarding
- **Main Hub**: Home Page (country selection) serving as the central navigation point
- **Content Spokes**: Category-specific pages (Dress Code, Communication, Etiquette, Tipping, Greetings)
- **Utility Features**: Profile, Settings, Notifications, Travel Itinerary, Emergency Contacts, Map

This architecture ensures users can quickly access specific information while maintaining contextual awareness of their location within the app.

### 2.4 Interaction Design Principles
**Minimalist Complexity**: Complex information is presented through progressive disclosure, showing essential details immediately while providing access to deeper information through deliberate user actions.

**Contextual Awareness**: The app adapts to user context:
- Location-based emergency contact information
- Travel itinerary-triggered notifications
- Offline availability of previously accessed content

**Feedback and Confirmation**: All user actions receive immediate visual feedback through:
- Loading states for network operations
- Success/error messages for data modifications
- Progress indicators for multi-step processes

## Feature Implementation Details

### 3.1 Core Functional Requirements

#### Single Sign-On (SSO) Authentication
The authentication system provides seamless user onboarding through:
- Google OAuth 2.0 integration
- Facebook Login SDK integration
- Automatic account creation on first SSO login
- Secure session management with automatic token refresh
- Encrypted credential storage compliant with GDPR and international data protection standards

#### Biometric Authentication
Building upon SSO, biometric authentication offers:
- Fingerprint recognition using Android BiometricPrompt API
- Face recognition support on compatible devices
- Fallback to PIN/password authentication
- Local-only storage of biometric data (device hardware security module)
- Protection for sensitive features (saved itineraries, personal preferences)

#### Comprehensive Settings Management
The settings module provides extensive customization:
- Language preferences (English, Afrikaans, isiZulu)
- Notification preferences (types, frequency, quiet hours)
- Privacy controls (data sharing, analytics opt-out)
- Cultural content preferences (favorite countries, interest categories)
- Theme customization (light/dark mode)
- Real-time synchronization across devices

#### RESTful API Architecture
The custom-built REST API manages:
- CRUD operations for all cultural data
- User profile and preference management
- Rating and review system
- Proper HTTP status codes and error handling
- JSON response formatting with consistent structure
- Pagination for large datasets
- Intelligent caching strategies for optimal performance

#### Offline Mode with Intelligent Synchronization
Offline functionality ensures continuous access:
- Firebase Realtime Database for local storage
- Selective content download based on user itineraries
- Differential synchronization (only changes uploaded/downloaded)
- Conflict resolution for simultaneous edits
- Background sync when connectivity restored
- Storage optimization to minimize device space usage

#### Real-Time Push Notification System
The notification system provides contextual alerts:
- Firebase Cloud Messaging (FCM) integration
- Scheduled notifications for upcoming travel
- Cultural event alerts (holidays, festivals)
- Personalization based on user preferences and itineraries
- Notification categories (urgent cultural alerts, tips, content updates)
- User control over notification types and frequency
- Quiet hours respect

#### Multi-Language Support System
Full localization across three languages:
- English, Afrikaans, and isiZulu support
- Comprehensive translation of UI elements, cultural content, and system messages
- Right-to-left (RTL) layout support where applicable
- Language-specific content delivery
- Seamless language switching preserving app state
- Localized date, time, and number formatting

### 3.2 Additional Features

#### Advanced Favourites and Bookmarking System
Users can organize cultural information through:
- Country favorites for quick access
- Bookmarked specific cultural tips
- Personalized cultural reference collections
- Tagging and categorization by trip or theme
- Offline availability of bookmarked content
- Sharing capabilities for recommendations

### 3.3 Innovative Features

#### Comprehensive Cultural Database
Unlike competitors offering surface-level information, CultureX provides:
- Multiple category coverage (dress code, communication, etiquette, tipping, greetings)
- Scenario-based guidance for practical application
- Cultural context explaining the "why" behind customs
- Regular updates from local contributors
- Verification process ensuring accuracy

#### Interactive Cultural Scenarios
"What would you do?" guides present:
- Real-world cultural situations
- Multiple-choice responses with explanations
- Gamified learning approach
- Progressive difficulty levels
- Achievement system for engagement

#### Emergency Cultural Assistance
Red-tab emergency feature provides:
- Immediate access to critical cultural information
- Local emergency contacts by country
- Embassy/consulate information
- Common emergency phrases in local language
- Cultural considerations for emergency situations

#### Adaptive Cultural Calendar
The app learns and proactively provides:
- Travel pattern recognition
- Proactive cultural reminders based on upcoming trips
- Local holiday and cultural event notifications
- Festival etiquette guides
- Seasonal cultural considerations

#### Offline Cultural Passport
Comprehensive offline access through:
- Full country guide downloads
- Category-specific offline packages
- Media content caching
- Automatic updates when online
- Storage management tools




Images of the App:
- SignIn & SignUp Pages:
<img width="539" height="1060" alt="image" src="https://github.com/user-attachments/assets/c33e2879-4dce-4769-b0da-5dc4365d40ac" />
<img width="501" height="1071" alt="image" src="https://github.com/user-attachments/assets/0b244728-472c-46a7-be32-e77d4c7abf89" />

- Onboarding Page:
<img width="483" height="1056" alt="image" src="https://github.com/user-attachments/assets/c6f69e87-4fe3-4eb0-b35c-a6f7c3bf0924" />


- Home Page:
<img width="403" height="848" alt="image" src="https://github.com/user-attachments/assets/cc8ef75d-1d62-4ac0-bf22-f926669cdb6e" />
<img width="397" height="842" alt="image" src="https://github.com/user-attachments/assets/a1f29018-2882-4907-b7ad-57383a6476b9" />



- Profile & Edit Profile Pages:
<img width="484" height="1056" alt="image" src="https://github.com/user-attachments/assets/793a5ead-db36-412d-b35f-0e1a16e12ce1" />
<img width="484" height="1056" alt="image" src="https://github.com/user-attachments/assets/1256e3d3-48a1-44f7-86bb-5c5d1e2107c6" />

- Settings Page:
<img width="490" height="1053" alt="image" src="https://github.com/user-attachments/assets/ad2e1ef8-4849-485f-bce0-38a2a553cf0a" />

- Provate Policy Page:
<img width="490" height="1056" alt="image" src="https://github.com/user-attachments/assets/16077dae-f1c2-4d20-b99b-6caeba139cd8" />
<img width="490" height="1051" alt="image" src="https://github.com/user-attachments/assets/78981f15-b45b-49cd-8eaa-6b0bed14280e" />


- The Five Categories:
<img width="385" height="832" alt="image" src="https://github.com/user-attachments/assets/65776669-57b5-43ba-acd8-b7945a32d5dc" />
<img width="397" height="842" alt="image" src="https://github.com/user-attachments/assets/71c264a2-266f-4663-89ed-8654ef00e06a" />

<img width="395" height="842" alt="image" src="https://github.com/user-attachments/assets/4e9cf7d3-0cda-4636-a894-3d946e088ef1" />
<img width="382" height="835" alt="image" src="https://github.com/user-attachments/assets/ff734f79-593b-44de-8864-b54025f49764" />

<img width="392" height="835" alt="image" src="https://github.com/user-attachments/assets/a1ec1e64-2195-44c6-afaa-12e02994bbaf" />
<img width="385" height="838" alt="image" src="https://github.com/user-attachments/assets/68b23ff9-c3bb-4dfd-9f4f-3b350285b61a" /> 
<img width="402" height="841" alt="image" src="https://github.com/user-attachments/assets/2a24b485-05f8-4060-8fea-6bba891220ca" />

<img width="388" height="833" alt="image" src="https://github.com/user-attachments/assets/e60bc051-5d6c-4494-b0e4-e7269e7ee410" />
<img width="395" height="838" alt="image" src="https://github.com/user-attachments/assets/0727f196-27d3-46e1-87b9-5538b8436a7f" />
<img width="383" height="830" alt="image" src="https://github.com/user-attachments/assets/fb5d77a9-8b8f-4954-9db6-4b97ef7c4deb" />

<img width="387" height="825" alt="image" src="https://github.com/user-attachments/assets/52348e91-f110-4db0-bb91-37412c74fd97" />
<img width="388" height="832" alt="image" src="https://github.com/user-attachments/assets/97c2612f-d9bb-4979-a2d6-5b56034048be" />


- Tourist Attraction Page:
<img width="489" height="1046" alt="image" src="https://github.com/user-attachments/assets/f0de4300-47c4-4e3e-8bcb-aebd8c63c9a5" />
<img width="476" height="1048" alt="image" src="https://github.com/user-attachments/assets/f0101cc9-44fe-49ee-8b56-c4dc65cc03e1" />


- Saved Page:
<img width="387" height="838" alt="image" src="https://github.com/user-attachments/assets/e3f8a37b-b7a2-49e7-903e-48c43e274a75" />

- Notification Page:
<img width="393" height="823" alt="image" src="https://github.com/user-attachments/assets/25d89ccb-dbba-4f43-b37a-da82a4316c0f" />

- Emergency Page:
<img width="385" height="837" alt="image" src="https://github.com/user-attachments/assets/536c63f9-0fa2-4b47-8bea-69afc241272f" />
<img width="386" height="836" alt="image" src="https://github.com/user-attachments/assets/6ecad36e-cb33-4b4f-968f-b5a8c6b24a5d" />




Unit Testing: 

<img width="940" height="476" alt="image" src="https://github.com/user-attachments/assets/1f71a21b-3ac4-4815-9b64-5bb6caf0fac0" />
<img width="940" height="478" alt="image" src="https://github.com/user-attachments/assets/bc84a00a-132e-4ad3-a20d-7f89412d52be" />
<img width="940" height="476" alt="image" src="https://github.com/user-attachments/assets/72b8bf40-347f-4f0b-a024-a953065aacd1" />
<img width="940" height="472" alt="image" src="https://github.com/user-attachments/assets/af4a9ccb-dc6e-4e84-83cd-ed3754e56717" />


GitHub Workflow:
<img width="1918" height="928" alt="image" src="https://github.com/user-attachments/assets/5391cb78-c391-4384-8585-a5ff941fd3e2" />

Overview of GitHub Actions Implementation
The screenshot shows the Actions tab of the CultureX_Project repository, displaying successful workflow runs for continuous integration and continuous deployment (CI/CD) automation.
Workflow Configuration: "Android CI/CD"
Workflow Identification
The repository has implemented an "Android CI/CD" workflow that automates the build, test, and deployment pipeline for the Android application. This is visible in the left sidebar under "All workflows."

Benefits Demonstrated
1. Continuous Integration

Automatic validation of every code change
Immediate feedback on build failures
Consistent build environment across team members

2. Quality Assurance

All workflow runs show success (green checkmarks)
Automated testing prevents broken code from reaching production
Execution time consistency indicates stable build process

3. Development Velocity

Fast feedback loop (4-5 minutes per build)
Automated processes eliminate manual build steps
Team can focus on development rather than build management

4. Collaboration Features
The left sidebar shows additional GitHub Actions features available:

Caches: Build dependency caching for faster execution
Attestations: Build provenance and security verification
Runners: Execution environment configuration
Usage metrics: Resource consumption tracking
Performance metrics: Workflow optimization insights

Practical Impact on CultureX Development
Automated Quality Gates
Every commit to the main branch automatically:

Builds the Android application
Runs unit tests
Performs code quality checks
Generates distributable APK
Reports success/failure status

Developer Workflow
When a developer (e.g., ST10322054) pushes code:

Commits changes locally
Pushes to GitHub main branch
GitHub Actions automatically triggers
Receives notification of build success/failure
Can download build artifacts if needed

Team Collaboration
The Actions tab provides transparency:
All team members see build status
Failed builds are immediately visible
Build history tracks project health over time
Commit-to-build traceability ensures accountability


Conclusion: 
CultureX represents a comprehensive solution to cultural education for international travelers, combining authentic local expertise with modern mobile technology. The project's success relies on three fundamental pillars:
1. Technical Excellence:
The robust three-tier architecture, RESTful API design, and comprehensive offline functionality ensure scalability, reliability, and user accessibility. The utilization of GitHub and GitHub Actions provides automated quality assurance, streamlined deployment, and collaborative development efficiency.
2. User-Centric Design:
Drawing from competitive analysis and user-centered design principles, CultureX delivers an intuitive, accessible, and engaging experience. The combination of visual appeal, practical functionality, and cultural authenticity creates meaningful value for travelers.
3. Sustainable Development:
The structured 12-week development timeline, comprehensive risk management, and continuous improvement processes ensure project deliverability and long-term viability. The phased approach allows for iterative testing, refinement, and quality assurance at each stage.
The integration of GitHub Actions for CI/CD automation ensures code quality, deployment reliability, and development efficiency. Automated testing, quality checks, and deployment workflows reduce manual errors while accelerating the development cycle.
CultureX fills a genuine market need for authentic cultural education, positioning itself as an essential tool for culturally conscious travelers. The strong technical foundation, thoughtful feature design, and commitment to content quality establish a platform capable of scaling with user demand while maintaining the personalized, authentic experience that differentiates it from competitors.
Upon successful launch, CultureX will continue evolving through user feedback, expanding cultural coverage, and enhancing features to remain the definitive cultural etiquette resource for international travelers.

 







References:
Iarn Tech, 2022. Android Studio User Interface - Layout and XML. [video online]. Available at: https://www.youtube.com/watch?v=kn_7H8v4Emo [Accessed on 15 September 2025]
UiLover, 2023. Android UI Design Mobile dashboard UI Tutorial | android projects. [video online]. Available at: https://www.youtube.com/watch?v=5dDHplq9rss  [Accessed on 18 September 2025] 
WsCube Tech, 2021. Layouts in Android Studio (XML) | Android UI Design Explained. [video online]. Available at: https://www.youtube.com/watch?v=PeCOKgAua7A  [Accessed on 16 September 2025] 
CodeWithTS, 2024. View Binding and Data Binding in Android Studio using Kotlin. [video online]. Available at: https://www.youtube.com/watch?v=tIXSuoJbX-8  [Accessed on 20 September 2025]
Android Developers, 2025. Develop a UI with Views. [online]. Available at: https://developer.android.com/studio/write/layout-editor [Accessed on 15 September 2025] 
UiLover, 2025. Build a Coffee Shop app with Kotlin & Firebase in Android Studio Project. [video online]. Available at: https://www.youtube.com/watch?v=Pnw_9tZ2z4wn [Accessed on 16 September 2025]
Guedmioui, A. 2023. Retrofit Android Tutorial - Make API Calls. [video online]. Available at: https://www.youtube.com/watch?v=8IhNq0ng-wk 
Code Heroes, 2024. Integrate Google Maps API in Android Studio 2025 | Step-by-Step Tutorial for Beginners. [video online]. Available at: https://www.youtube.com/watch?v=QVCNTPNy-vs&t=137s [Accessed on 17 September 2025]
CodeSchmell, 2022. How to implement API in Android Studio tutorial. [video online]. Available at: https://www.youtube.com/watch?v=Kjeh47epMqI [Accessed on 17 September 2025]
UiLover, 2023. Travel App Android Studio Tutorial Project - Android Material Design. [video online]. Available at: https://www.youtube.com/watch?v=PPhuxay3OV0 [Accessed on 12 September 2025]
CodeWithTS, 2024. View Binding and Data Binding in Android Studio using Kotlin. [video online]. Available at: https://www.youtube.com/watch?v=tIXSuoJbX-8  [Accessed on 20 September 2025]
Android Developers, 2025. Develop a UI with Views. [online]. Available at: https://developer.android.com/studio/write/layout-editor [Accessed on 15 September 2025] 
