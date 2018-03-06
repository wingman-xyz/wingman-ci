const rootFolder        = '/Users/Shared/Jenkins/Documents/release';
const appiumPath        = `${rootFolder}/wingman`;
const appiumResultPath  = `${appiumPath}/data/resultTestCases.xls`;
const iosFilePath       = `${rootFolder}/build/ios`;
const androidFilePath   =  `${rootFolder}/build/android`;
const execKobitonPath   = `${rootFolder}/kobiton`;
const execTestPath      = `${execKobitonPath}/scripts/test.sh`;
const execUploadPath    = `${execKobitonPath}/scripts/upload.sh`;

module.exports = {
    rootFolder,
    appiumPath,
    appiumResultPath,
    iosFilePath,
    androidFilePath,
    execKobitonPath,
    execTestPath,
    execUploadPath,
}