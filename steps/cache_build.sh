#!/bin/bash

set -e

echo "<------YARN INSTALL------>"
yarn install

echo "<------YARN TEST------>"
yarn flow check && yarn eslint . && yarn test:jest -- -u
