pipeline {
    agent {
        label 'master'
    }
    options {
        disableConcurrentBuilds()
    }
    stages {
        stage('setup') {
            steps {
                sh './gradlew'
            }
        }
        stage('tests') {
            parallel {
                stage('linting | checkstyle main') {
                    steps {
                        sh './gradlew checkstyleMain'
                    }
                }
                stage('linting | checkstyle test') {
                    steps {
                        sh './gradlew checkstyleTest'
                    }
                }
                stage('linting | pmd main') {
                    steps {
                        sh './gradlew pmdMain'
                    }
                }
                stage('linting | pmd test') {
                    steps {
                        sh './gradlew pmdTest'
                    }
                }
                stage('linting | spotbugs main') {
                    steps {
                        sh './gradlew spotbugsMain'
                    }
                }
                stage('linting | spotbugs test') {
                    steps {
                        sh './gradlew spotbugsTest'
                    }
                }
            }
            post {
                always {
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
        }
        stage('build') {
            steps {
                sh './gradlew autodeploy'
            }
        }
        stage('deploy') {
            when {
                expression {
                    BRANCH_NAME == 'master'
                }
            }
            steps {
                sh "ssh billy@esd.novucs.net ./esd/cicd/redeploy.sh ${pwd()} latest"
            }
        }
    }
}
