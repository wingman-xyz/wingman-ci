#!/bin/bash

set -e
source ~/.bash_profile

echo "<------POD INSTALL------>"
cd ios && pod install --verbose
