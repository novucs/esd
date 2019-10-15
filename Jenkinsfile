pipeline {
    agent {
        label 'master'
    }
    options {
        disableConcurrentBuilds()
    }
    stages {
        stage('lint') {
            steps {
                sh './gradlew --status'
                sh './gradlew --stop'
                sh './gradlew --status'
                sh './gradlew check --no-daemon'
            }
            post {
                always {
                    script {
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
        stage('test') {
            steps {
                sh './gradlew test --no-daemon'
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
