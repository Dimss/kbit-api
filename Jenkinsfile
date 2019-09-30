pipeline {
    agent {
        node {
            label 'maven'
        }
    }
    stages {


        stage("Step One - test ") {
            steps {
                script {
                    sh "echo 'Hello world'"
                }
            }
        }


    }


    post {
        failure {
            script {
                openshift.withCluster() {
                    openshift.withProject() {
                        sh "echo 'this is failure catch'"

                    }
                }
            }
        }
    }
}