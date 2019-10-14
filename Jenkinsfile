pipeline {
    agent {
        label 'master'
    }
    options {
        disableConcurrentBuilds()
    }
    environment {
        if (BRANCH_NAME == 'master') {
            DEPLOYMENT_TAG = 'latest'
        } else {
            DEPLOYMENT_TAG = GIT_COMMIT
        }
    }
    stages {
//        stage('build') {
//            steps {
//                sh './gradlew autodeploy'
//            }
//        }
//        stage('run all tests') {
//            parallel {
//                stage('source checks') {
//                    steps {
//                        sh './gradlew check'
//                    }
//                }
//            }
//            post {
//                always {
//                    script {
//                        publishHTML target: [
//                                allowMissing         : false,
//                                alwaysLinkToLastBuild: false,
//                                keepAll              : true,
//                                reportDir            : 'build/reports/checkstyle/',
//                                reportFiles          : 'main.html',
//                                reportName           : 'checkstyle',
//                        ]
//                        publishHTML target: [
//                                allowMissing         : false,
//                                alwaysLinkToLastBuild: false,
//                                keepAll              : true,
//                                reportDir            : 'build/reports/pmd/',
//                                reportFiles          : 'main.html',
//                                reportName           : 'pmd',
//                        ]
//                        publishHTML target: [
//                                allowMissing         : false,
//                                alwaysLinkToLastBuild: false,
//                                keepAll              : true,
//                                reportDir            : 'build/reports/spotbugs/',
//                                reportFiles          : 'main.html',
//                                reportName           : 'spotbugs',
//                        ]
//                    }
//                }
//            }
//        }
        stage('deploy') {
//            when {
//                expression {
//                    BRANCH_NAME == 'master'
//                }
//            }
            steps {
                sh 'printenv'
                echo BRANCH_NAME
//                sh "ssh billy@esd.novucs.net ./esd/cicd/redeploy.sh ${pwd()} latest"
            }
        }
    }
}
