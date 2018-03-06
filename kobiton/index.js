const fetch = require('node-fetch');
const btoa = require('btoa');
const util = require('util');
const execSync = util.promisify(require('child_process').execSync);
const exec = util.promisify(require('child_process').exec);
const xlsx = require('xlsx');
const paths = require('./const');
const iPhones = require('./devices').iPhones;
const Androids = require('./devices').Androids;
const iOS = require('./platforms').iOS;
const Android = require('./platforms').Android;

const base64EncodedBasicAuth = btoa('KOBITON_USERNAME:KOBITON_API_KEY');
const headers = {
    'Authorization': `Basic ${base64EncodedBasicAuth}`,
    'Content-Type':'application/json',
    'Accept':'application/json',
};

function generateUploadURL(filename, appId) {
    return fetch('https://api.kobiton.com/v1/apps/uploadUrl', {
        method: 'POST',
        headers: headers,
        body: JSON.stringify({
            "filename": filename,
            "appId": appId
        })
    });
};

async function uploadFile(filename, uploadURL) {
    const filePath = filename == iOS.filename ? paths.iosFilePath : paths.androidFilePath;
    await execSync(`sh ${paths.execUploadPath} "${filePath}/${filename}" "${uploadURL}"`);
};

function createApplication(filename, appPath) {
    return fetch('https://api.kobiton.com/v1/apps', {
        method: 'POST',
        headers: headers,
        body: JSON.stringify({
            "filename": filename,
            "appPath": appPath
        })
    });
};

function getApplicationVersion(versionId) {
    return fetch(`https://api.kobiton.com/v1/app/versions/${versionId}`, {
        method: 'GET',
        headers: headers
    });
};

function readExcelResult() {
    const workbook = xlsx.readFile(`${paths.appiumResultPath}`);
    const isTestSuitsPassed = workbook.Sheets['TestSuites'].G2.v == '0'; // Check fail test
    return isTestSuitsPassed;
}

function executeAppiumTest(devices, versionId) {
    let isPass = true;

    devices.every((device) => {
        try {
            if (devices == iPhones) {
                execSync(`sh ${paths.execTestPath} "${paths.appiumPath}" "${device.deviceName}" "${device.platformVersion}" "${device.platformName}" "${versionId}"`);
            
                if(!readExcelResult()) {
                    console.log('Test fail: ', device);
                    isPass = false;
                    return false;
                }
            }

            console.log(`<------${device.deviceName} ${device.platformVersion} has been completely tested------>`);
            
            return true;
        } catch (e) {
            console.log(e);
        }
    });

    if (!isPass) throw new Error('<------Test Failed------>');
};

function checkApplicationStatus(state, execTestCallback, recursiveCallback) {

    switch (state) {
        case 'OK':
            execTestCallback();
            break;

        case 'PARSING': case 'SCANNING':
            setTimeout(() => recursiveCallback(), 3500);
            break;

        case 'CRITICAL':
            execTestCallback();
            // throw new Error('THIS APP HAS CRITICAL ISSUE');
            break;

        default:
            throw new Error('UNKOWN UPLOAD ISSUE');
            break;

    }
}

function checkApplicationVersion(devices, versionId) {
    getApplicationVersion(versionId)
    .then(function(res) {
        return res.json();
    }).then(function(body) {
        console.log('Status: ', body)
        const { state } = body;
        checkApplicationStatus(
            state,
            () => executeAppiumTest(devices, versionId),
            () => checkApplicationVersion(devices, versionId)
        );
    }).catch(function(err) {
        throw new Error(err);    
    })
}

function processKobitonTest(devices, filename, appId) {
    generateUploadURL(filename, appId)
        .then(function(res) {
            return res.json();
        }).then(function(body) {
            const { appPath, url } = body;
            uploadFile(filename, url);
            createApplication(filename, appPath)
                .then(function(res) {
                    return res.json();
                }).then(function(body) {
                    const { versionId } = body;
                    checkApplicationVersion(devices, versionId);
                }).catch(function(err) {
                    throw new Error(err);    
                });
        }).catch(function(err) {
            throw new Error(err);
        });
}

function kobiton() {
    const platform = process.argv[2];

    if (platform == 'ios') {
        try {
            processKobitonTest(iPhones, iOS.filename, iOS.appId);
        } catch(err) {
            throw new Error(err);
        };
    } else if (platform == 'android') {
        try {
            processKobitonTest(Androids, Android.filename, Android.appId);
        } catch(err) {
            throw new Error(err);
        };
    } else {
        throw new Error('Require input platform')
    }
}

kobiton();
