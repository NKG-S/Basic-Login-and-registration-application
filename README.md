
# Firebase Authentication & User Profile System

## Project Overview

This Android application demonstrates a complete Firebase authentication system with user profile management. It includes:

- Email/password authentication (login/registration)
- Password reset functionality
- User profile display
- Secure data storage in Firestore
- Proper navigation flow

## Firebase Configuration

### 1. Firestore Security Rules

Here are the recommended security rules for your Firestore database:

```javascript
rules_version = '2';

service cloud.firestore {
  match /databases/{database}/documents {
    // User-specific data access
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
      allow create: if request.auth == null; // For registration
    }

    // Public data (example)
    match /public/{document} {
      allow read: if true;
      allow write: if request.auth != null;
    }

    // Temporary testing rule (remove before production)
    match /{document=**} {
      allow read, write: if false; // Disabled by default
      // For testing: allow read, write: if request.time < timestamp.date(2025, 7, 12);
    }
  }
}
```

### 2. Firebase Setup Steps

1. **Create a Firebase Project**:
   - Go to [Firebase Console](https://console.firebase.google.com/)
   - Click "Add project" and follow the wizard

2. **Enable Authentication**:
   - In Firebase Console, go to the Authentication section
   - Click "Get Started"
   - Enable the "Email/Password" provider

3. **Configure Firestore**:
   - Go to the Firestore section
   - Click "Create database"
   - Start in production mode
   - Set location closest to your users

4. **Add Android App**:
   - Click the Android icon in the project overview
   - Enter your package name
   - Download `google-services.json` and place it in the `app/` directory

5. **Add Firebase SDK**:
   Add these dependencies to your `build.gradle` files:

   **Project-level (`build.gradle`)**:
   ```gradle
   dependencies {
       classpath 'com.google.gms:google-services:4.3.15'
   }
   ```

   **App-level (`app/build.gradle`)**:
   ```gradle
   plugins {
       id 'com.google.gms.google-services'
   }

   dependencies {
       implementation platform('com.google.firebase:firebase-bom:32.3.1')
       implementation 'com.google.firebase:firebase-auth'
       implementation 'com.google.firebase:firebase-firestore'
   }
   ```

## Application Features

### Authentication Flow

1. **Registration**:
   - Collects name, email, password, address, and mobile
   - Creates a Firebase Auth user
   - Stores additional user data in Firestore
   - Password validation (6+ chars, special characters)

2. **Login**:
   - Email/password authentication
   - Redirects to the profile page on success
   - Forgot password option

3. **Profile Page**:
   - Displays user information
   - Logout functionality
   - Automatically redirects if not authenticated

## Code Structure

### Key Files

1. `MainActivity.java` - Profile display and logout
2. `Login.java` - Authentication handling
3. `Registration.java` - User registration
4. `ForgotPassword.java` - Password reset
5. `AndroidManifest.xml` - Activity configuration

### Data Model

User documents in Firestore are stored with this structure:

```javascript
{
  name: "String",
  email: "String",
  address: "String",
  mobile: "String",
  createdAt: "Timestamp"
}
```

## Testing Considerations

1. **Test Users**:
   - Create test accounts with different email domains
   - Test the password reset flow

2. **Security Rules**:
   - Use the Firebase Rules Playground to test rules
   - Verify users can only access their own data

3. **Edge Cases**:
   - Network connectivity issues
   - Invalid email formats
   - Weak password attempts

## Production Checklist

Before deploying to production:
1. Remove any temporary security rules
2. Enable email verification if needed
3. Set up proper error logging
4. Add analytics events
5. Implement a proper password recovery flow

## Troubleshooting

Common issues and solutions:

1. **Authentication failures**:
   - Verify Email/Password provider is enabled
   - Check Firestore rules allow user creation

2. **Missing user data**:
   - Confirm Firestore document is created during registration
   - Check for errors in Logcat

3. **App crashes on launch**:
   - Verify `google-services.json` is in the correct location
   - Check package names match in the manifest and Firebase config

For additional help, refer to [Firebase documentation](https://firebase.google.com/docs).
