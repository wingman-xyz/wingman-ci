#!/bin/bash

set -e

buildNumber=$(/usr/libexec/PlistBuddy -c "Print CFBundleVersion" "${1}/ios/Wingman/Info.plist")
buildNumber=$(($buildNumber + 1))
/usr/libexec/PlistBuddy -c "Set :CFBundleVersion $buildNumber" "${1}/ios/Wingman/Info.plist"
