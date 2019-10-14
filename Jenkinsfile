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
                sh './gradlew autodeploy'
            }
        }
        stage('run all tests') {
            parallel {
                stage('source checks') {
                    steps {
                        sh './gradlew check'
                    }
                }
            }
        }
        stage('publish reports') {
            steps {
                script {
                    publishHTML target: [
                            allowMissing         : false,
                            alwaysLinkToLastBuild: false,
                            keepAll              : true,
                            reportDir            : 'build/reports/checkstyle/',
                            reportFiles          : 'main.html',
                            reportName           : 'checkstyle',
                    ]
                    publishHTML target: [
                            allowMissing         : false,
                            alwaysLinkToLastBuild: false,
                            keepAll              : true,
                            reportDir            : 'build/reports/pmd/',
                            reportFiles          : 'main.html',
                            reportName           : 'pmd',
                    ]
                    publishHTML target: [
                            allowMissing         : false,
                            alwaysLinkToLastBuild: false,
                            keepAll              : true,
                            reportDir            : 'build/reports/spotbugs/',
                            reportFiles          : 'main.html',
                            reportName           : 'spotbugs',
                    ]
                }
            }
        }
        stage('deploy') {
            steps {
                sh "ssh billy@esd.novucs.net ./esd/cicd/redeploy.sh ${pwd()} app"
            }
        }
    }
}
