#!/bin/bash

set -e

echo "<------iOS Export------>"
echo $1
echo "$2/ios/Wingman/Info.plist"
rm -rf "${1}/archive/Wingman.xcarchive" && rm -rf "${1}/build/ios/"
cd ios && xcodebuild clean
${1}/steps/ios_increase_build.sh ${2}
security unlock-keychain -p wingman ~/Library/Keychains/login.keychain-db
security set-keychain-settings ~/Library/Keychains/login.keychain-db
security set-key-partition-list -S apple-tool:,apple:,codesign: -s -k wingman ~/Library/Keychains/login.keychain-db
xcodebuild -workspace Wingman.xcworkspace -scheme WingmanRelease archive -archivePath "${1}/archive/Wingman.xcarchive"
xcodebuild -exportArchive -archivePath "${1}/archive/Wingman.xcarchive" -exportPath "${1}/build/ios" Wingman -exportOptionsPlist ExportOptions.plist
