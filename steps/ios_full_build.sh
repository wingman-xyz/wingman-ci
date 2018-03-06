#!/bin/bash

set -e
source ~/.bash_profile

echo "<------CLEAN PODS - POD INSTALL------>"
cd ios && rm -rf Pods/ && pod repo update && pod install --verbose