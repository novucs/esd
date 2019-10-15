pipeline {
    agent {
        label 'master'
    }
    options {
        disableConcurrentBuilds()
    }
    stages {
        stage('check') {
            steps {
                sh './gradlew check --no-daemon'
            }
            post {
                always {
                    script {
                        junit 'build/test-results/test/*.xml'
                        step( [ $class: 'JacocoPublisher' ] )
                        publishHTML target: [
                                allowMissing         : false,
                                alwaysLinkToLastBuild: false,
                                keepAll              : true,
                                reportDir            : 'build/reports/checkstyle/',
                                reportFiles          : 'main.html,test.html',
                                reportName           : 'checkstyle',
                        ]
                        publishHTML target: [
                                allowMissing         : false,
                                alwaysLinkToLastBuild: false,
                                keepAll              : true,
                                reportDir            : 'build/reports/pmd/',
                                reportFiles          : 'main.html,test.html',
                                reportName           : 'pmd',
                        ]
                        publishHTML target: [
                                allowMissing         : false,
                                alwaysLinkToLastBuild: false,
                                keepAll              : true,
                                reportDir            : 'build/reports/spotbugs/',
                                reportFiles          : 'main.html,test.html',
                                reportName           : 'spotbugs',
                        ]
                    }
                }
            }
        }
        stage('build') {
            steps {
                sh './gradlew autodeploy --no-daemon'
            }
        }
        stage('deploy') {
            when {
                expression {
                    BRANCH_NAME == 'master' || BRANCH_NAME.startsWith('release/')
                }
            }
            steps {
                sh "ssh billy@esd.novucs.net ./esd/cicd/redeploy.sh ${pwd()} ${BRANCH_NAME == 'master' ? 'latest' : 'stable'}"
            }
        }
    }
}
