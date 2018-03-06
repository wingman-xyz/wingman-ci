#!/bin/bash

set -e
source ~/.profile

altool="$(dirname "$(xcode-select -p)")/Applications/Application Loader.app/Contents/Frameworks/ITunesSoftwareService.framework/Support/altool"
ipa="${1}/build/ios/WingmanRelease.ipa"

echo "<------VALIDATING APP------>"
time "$altool" --validate-app --type ios --file "$ipa" --username "${ITC_USER}" --password "${ITC_PASSWORD}"

echo "<------UPLOADING APP TO iTC------>"
time "$altool" --upload-app --type ios --file "$ipa" --username "${ITC_USER}" --password "${ITC_PASSWORD}"
