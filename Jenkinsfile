node {
  try {
    stage('React-Checkout/Prepare/Tests') {
      notifyBuild('STARTED')

      checkout scm
      
      def result = sh (script: "git log -1 | grep '\\[full build\\]'", returnStatus: true)

      parallel(
        react_native: {
          if (result != 0) {
            sh "${env.CI_STEPS}/cache_build.sh"
          } else {
            sh "${env.CI_STEPS}/full_build.sh"
          }

          if (env.BRANCH_NAME == 'dev') {
            sh "${env.CI_STEPS}/sonar.sh"
          }
        },
        ios: {
          if (env.BRANCH_NAME == 'release') {
            if (result != 0) {
              sh "${env.CI_STEPS}/ios_cache_build.sh"
            } else {
              sh "${env.CI_STEPS}/ios_full_build.sh"
            }
          }
        }
      )
    }

    if (env.BRANCH_NAME == 'release') {
      stage('Native-Clean/Archive/Export') {
        parallel(
          ios: {
            sh "${env.CI_STEPS}/ios_export.sh ${env.ROOT} ${env.WORKSPACE}"
          },
          android: {
            sh "${env.CI_STEPS}/android_export.sh ${env.ROOT}"
          }
        )
      }

      stage('Kobiton-Cloud-Tests') {
        withEnv(['PATH+NODE=/usr/local/bin']) {
          def result = sh returnStatus: true, script: "node ${env.ROOT}/kobiton/index.js ios"
          if (result != 0) {
            notifyBuild('FAILED')
          }
          sh "exit ${result}"
        }
      }

      stage('Deployment') {
        parallel(
          iTunesConnect: {
            sh "${env.CI_STEPS}/testflight.sh ${env.ROOT}"
          },
          GoogleBeta: {
            // TODO: need google paid account to use Pusblishing API
          },
          CodePushOTA: {
            // TODO: need config for native iOS - Android to get update notification
          }
        )
      }
    }

    stage('Notify-Success-Release') {
      notifyBuild('SUCCESSFUL')
    }
  } catch (e) {
    notifyBuild('FAILED')
    throw e
  }
}

// Notify build
def detailsByStatus(String buildStatus = 'STARTED') {
  def detail = ''

  switch(buildStatus) {
    case 'STARTED': case 'SUCCESSFUL': case 'FAILED':
      detail = """<p>${buildStatus}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':</p>
    <p>Check console output at "<a href="${env.BUILD_URL}">${env.JOB_NAME} [${env.BUILD_NUMBER}]</a>"</p>"""
    break

    case 'AUTOMATION-FAILED':
      detail = '''${FILE,path="${env.ROOT}/wingman/screenshots/DetailReport.html"}'''
    break
  }

  return detail;
}

def notifyBuild(String buildStatus = 'STARTED') {
  buildStatus = buildStatus ?: 'SUCCESSFUL'

  def colorName = 'RED'
  def subject = "${buildStatus}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'"
  def summary = "${subject} (${env.BUILD_URL})"
  def details = detailsByStatus(buildStatus)

  if (buildStatus == 'STARTED') {
    color = 'YELLOW'
  } else if ('SUCCESSFUL') {
    color = 'GREEN'
  }

  hipchatSend (color: color, notify: true, message: summary)
  emailext (
    subject: subject,
    body: details,
    to: "${env.DEFAULT_RECIPIENTS}",
    attachlog: true,
    compresslog: true,
    mimeType: 'text/html',
    charset: 'UTF-8'
  )
}
