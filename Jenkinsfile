env.JENKINS_NODE_COOKIE = 'dontKillMe'

pipeline {
    agent {
        label 'master'
    }
    options {
        disableConcurrentBuilds()
    }
    stages {
        stage('Testing') {
            steps {
                sh './gradlew autodeploy'
                sh 'cp build/app.war /deployments/latest.war'
                script {
                    if (BRANCH_NAME == 'master') {
                        sh "echo hi"
                    } else {
                        sh "echo not hi"
                    }
                }
            }
        }
//        stage('Build') {
//            steps {
//                sh './gradlew autodeploy'
//            }
//        }
//        stage('Lint (src/main)') {
//            steps {
//                sh './gradlew checkstyleMain'
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
//                                reportName           : 'Lint Report (src/main)',
//                        ]
//                    }
//                }
//            }
//        }
//        stage('Lint (src/test)') {
//            steps {
//                sh './gradlew checkstyleTest'
//            }
//            post {
//                always {
//                    script {
//                        publishHTML target: [
//                                allowMissing         : false,
//                                alwaysLinkToLastBuild: false,
//                                keepAll              : true,
//                                reportDir            : 'build/reports/checkstyle/',
//                                reportFiles          : 'test.html',
//                                reportName           : 'Lint Report (src/test)',
//                        ]
//                    }
//                }
//            }
//        }
//        stage('PMD (src/main)') {
//            steps {
//                sh './gradlew pmdMain'
//            }
//            post {
//                always {
//                    script {
//                        publishHTML target: [
//                                allowMissing         : false,
//                                alwaysLinkToLastBuild: false,
//                                keepAll              : true,
//                                reportDir            : 'build/reports/pmd/',
//                                reportFiles          : 'main.html',
//                                reportName           : 'PMD Report (src/main)',
//                        ]
//                    }
//                }
//            }
//        }
//        stage('PMD (src/test)') {
//            steps {
//                sh './gradlew pmdTest'
//            }
//            post {
//                always {
//                    script {
//                        publishHTML target: [
//                                allowMissing         : false,
//                                alwaysLinkToLastBuild: false,
//                                keepAll              : true,
//                                reportDir            : 'build/reports/pmd/',
//                                reportFiles          : 'test.html',
//                                reportName           : 'PMD Report (src/test)',
//                        ]
//                    }
//                }
//            }
//        }
//        stage('Test') {
//            steps {
//                sh './gradlew test'
//                sh 'cd build/test-results/test && touch *.xml'
//            }
//            post {
//                always {
//                    script {
//                        junit 'build/test-results/test/*.xml'
//                    }
//                }
//            }
//        }
//        stage('Coverage') {
//            steps {
//                sh './gradlew coverage'
//            }
//            post {
//                always {
//                    script {
//                        publishHTML target: [
//                                allowMissing         : false,
//                                alwaysLinkToLastBuild: false,
//                                keepAll              : true,
//                                reportDir            : 'build/reports/jacoco/test/html/',
//                                reportFiles          : 'index.html',
//                                reportName           : 'Coverage Report',
//                        ]
//                    }
//                }
//            }
//        }
//        stage('Deploy') {
//            when {
//                expression {
//                    BRANCH_NAME == 'master' || BRANCH_NAME.startsWith('release/')
//                }
//            }
//            steps {
//                sh "ssh billy@esd.novucs.net ./esd/cicd/redeploy.sh ${pwd()} ${BRANCH_NAME == 'master' ? 'latest' : 'stable'}"
//            }
//        }
    }
}
