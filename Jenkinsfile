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
                    def reports = [
                            'build/reports/checkstyle/main.html',
                            'build/reports/pmd/main.html',
                            'build/reports/spotbugs/main.html',
                    ]
                    for (report in reports) {
                        publishHTML target: [
                                allowMissing         : true,
                                alwaysLinkToLastBuild: false,
                                keepAll              : true,
                                reportDir            : report,
                                reportFiles          : report,
                                reportName           : report,
                        ]
                    }
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
