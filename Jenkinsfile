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
                stage('spot bugs') {
                    steps {
                        sh './gradlew check'
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
