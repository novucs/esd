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
                sh './gradlew build'
            }
        }
    }
}
