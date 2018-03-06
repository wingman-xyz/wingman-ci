# wingman-ci
Continuous integration and deployment for Wingman App

### Prereqs
-	Using MacOS High Sierra
-	Setup Jenkins with recommended plugins - https://jenkins.io/download/
-	Login to Jenkins Users and start the steps below.
-	Global install Homebrew, Maven, Node (v8.9.4), NPM, Yarn, Watchman, React Native CLI, JDK 8 - https://facebook.github.io/react-native/docs/getting-started.html
-	Add iOS Release Certificate, Provisioning
-	Add keystore for signed APK - https://facebook.github.io/react-native/docs/signed-apk-android.html

### Jenkins Configuration (Mange Jenkins > Configure System)
-	Add Global Properties
-	Config Jenkins Location
-	Config Extended E-mail Notification (admin's gmail should be turned on 2-Steps Auth, Less secure app, Generating App Password)

### Jenkins Nodes (Mange Jenkins > Manage Nodes)
-	Add iOS node
-	Download agent.jar and add launch command
-	Add Node Properties
-	Add similar Android node 

### Job Configuration
-	Create a multibranch pipeline job.
-	Add Github source by using Username/Personal Github Tokens
-	Enter the Owner and choose Repository
-	Build Configuration should be Jenkinsfile mode
-	Check Scan Multibranch Pipeline Triggers with desired time interval

### Notes:
- Add this Jenkinsfile to root of RN project
- Using this repo for executing Appium scripts on Kobiton Cloud Devices
- Register Kobiton account and use recommended capabilities for automation cloud test
- Prepare your iTunesConnect, Google Alpha, Codepush be ready for release the success build
