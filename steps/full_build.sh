#!/bin/bash

set -e

echo "<------CLEAN CACHE - YARN INSTALL------>"
rm -rf node_modules/ && yarn cache clean && yarn install

echo "<------YARN TEST------>"
yarn flow check && yarn eslint . && yarn test:jest -- -u
