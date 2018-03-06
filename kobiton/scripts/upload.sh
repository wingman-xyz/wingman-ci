#!/bin/bash

set -e

curl -T ${1} -H "Content-Type: application/octet-stream" -H "x-amz-tagging: unsaved=true" -X PUT ${2} --verbose
