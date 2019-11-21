
  def MASTER_NAME
  def SERVER_IPS

pipeline {

  environment {
    PROJECT = "core-feat-241406"
    FE_SVC_NAME = "${APP_NAME}-frontend"
    CLUSTER = "hipster2"
    CLUSTER_ZONE = "us-central1-a"
    JENKINS_CRED = "${PROJECT}"
  }

  agent {
    kubernetes {
      label 'default'
      defaultContainer 'jnlp'
      yaml """
apiVersion: v1
kind: Pod
metadata:
labels:
  component: ci
spec:
  # Use service account that can deploy to all namespaces
  serviceAccountName: esteemed-bear-jenkins
  containers:
  - name: golang
    image: golang:1.10
    command:
    - cat
    tty: true
  - name: gcloud
    image: gcr.io/cloud-builders/gcloud
    command:
    - cat
    tty: true
  - name: dotnet
    image: mcr.microsoft.com/dotnet/core/sdk:2.1
    command:
    - cat
    tty: true    
  - name: kubectl
    image: gcr.io/cloud-builders/kubectl
    command:
    - cat
    tty: true
  - name: mvn
    image: gcr.io/cloud-builders/mvn
    command:
    - cat
    tty: true
"""
}
}
stages {

stage('Unit Test'){
   		steps{
    			container('dotnet') {
                  		script {
                            		sh 'dotnet --info'
                            		sh 'dotnet test tests/cartservice/cartservice.tests.csproj'
                            		sh 'echo Tests successful !!'
                          	}
                        }
  		}
        }

/*stage('functional test'){

            steps{


script {

                    // Variables for input
                    def inputConfig
                    def inputTest

                    // Get the input
                    def userInput = input(
                            id: 'userInput', message: 'Enter path of test reports:?',
                            parameters: [

                                    string(defaultValue: 'None',
                                            description: 'Path of config file',
                                            name: 'Config'),
                                    string(defaultValue: 'None',
                                            description: 'Test Info file',
                                            name: 'Test'),
                            ])

                    // Save to variables. Default to empty string if not found.
                    inputConfig = userInput.Config?:''
                    inputTest = userInput.Test?:''

                    // Echo to console
                    echo("IQA Sheet Path: ${inputConfig}")
                    echo("Test Info file path: ${inputTest}")
}
                        container('mvn'){
                                sh 'uname -a'
                                sh 'mvn --version'
                         //       sh 'mvn  -f selenium-tests/pom.xml -DsuiteXmlFile=selenium-tests/src/test/resources/testng.xml test'
                                sh 'ls -lrt selenium-tests/target/test-classes'
                            }


            }
        }
  stage('performance test'){
           
            steps{
			container('kubectl'){
				sh 'kubectl cp sample.jmx \$(kubectl get pods -l app.kubernetes.io/component=master -o jsonpath="{.items[*].metadata.name}"):/jmeter'
				sh 'kubectl exec -it \$(kubectl get pods -l app.kubernetes.io/component=master -o jsonpath="{.items[*].metadata.name}") -- rm -f *.jtl'
				sh 'kubectl exec -it \$(kubectl get pods -l app.kubernetes.io/component=master -o jsonpath="{.items[*].metadata.name}") -- jmeter -n -t /jmeter/sample.jmx -R \$(kubectl get pods -l app.kubernetes.io/component=server -o jsonpath="{.items[*].status.podIP}" | tr " " ",") -l log.jtl'
				sh 'kubectl cp \$(kubectl get pods -l app.kubernetes.io/component=master -o jsonpath="{.items[*].metadata.name}"):log.jtl ./log.jtl'
				sh 'cat log.jtl'
                
            
            }
        }
}
*/

}
}
