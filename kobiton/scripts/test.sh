#!/bin/bash

set -e

cd ${1} && mvn -Dtest=TestBatch -DenvSystem=true -DenvSessionName="Automation test session" -DenvScreenShots=true -DkobitonServerUrl="https://thanh.skroot:77d543d1-bd50-409f-aa10-7518bea4d433@api.kobiton.com/wd/hub" -DdeviceOrientation=portrait -DdeviceGroup=KOBITON -DdeviceName="${2}" -DplatformVersion=${3} -DplatformName=${4} -DtestingApp="kobiton-store:v${5}" test
