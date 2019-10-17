env.JENKINS_NODE_COOKIE = 'dontKillMe'

pipeline {
    agent {
        label 'master'
    }
    options {
        disableConcurrentBuilds()
    }
    stages {
        stage('build') {
            steps {
                sh './gradlew checkBuildReport'
                sh 'cd build/test-results/test && touch *.xml'
            }
            post {
                always {
                    script {
                        junit 'build/test-results/test/*.xml'
                        publishHTML target: [
                                allowMissing         : false,
                                alwaysLinkToLastBuild: false,
                                keepAll              : true,
                                reportDir            : 'build/reports/checkstyle/',
                                reportFiles          : 'main.html,test.html',
                                reportName           : 'CheckStyle Linting Report',
                        ]
                        publishHTML target: [
                                allowMissing         : false,
                                alwaysLinkToLastBuild: false,
                                keepAll              : true,
                                reportDir            : 'build/reports/pmd/',
                                reportFiles          : 'main.html,test.html',
                                reportName           : 'PMD Static Code Analysis Report',
                        ]
                        publishHTML target: [
                                allowMissing         : false,
                                alwaysLinkToLastBuild: false,
                                keepAll              : true,
                                reportDir            : 'build/reports/jacoco/test/html/',
                                reportFiles          : 'index.html',
                                reportName           : 'Coverage Report',
                        ]
                    }
                }
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
