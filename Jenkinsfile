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
        stage('deploy') {
            steps {
                sh 'cp build/app.war /var/jenkins_home/deployments/app.war'
            }
        }
    }
}
