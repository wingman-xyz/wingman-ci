#!/bin/bash
node index.js | grep 'PASS' &> /dev/null
if [ $? == 0 ]; then
    echo 'matched'
else 
    echo 'unmatched'
fi