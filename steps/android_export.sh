#!/bin/bash

set -e
source ~/.bash_profile

echo "<------Android Export------>"
rm -rf "${1}/build/android/Wingman.apk"
cd android && ./gradlew assembleRelease
mv ./app/build/outputs/apk/app-release.apk "${1}/build/android/Wingman.apk"

