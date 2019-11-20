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
  stage('run unit test'){
           
            steps{
			container('kubectl'){
				sh 'export MASTER_NAME=$(kubectl get pods -l app.kubernetes.io/component=master -o jsonpath="{.items[*].metadata.name}")'			
				sh 'echo $MASTER_NAME'	
				sh 'export SERVER_IPS=$(kubectl get pods -l app.kubernetes.io/component=server -o jsonpath="{.items[*].status.podIP}" | tr " " ",")'
				sh 'echo $SERVER_IPS'
				sh 'kubectl cp sample.jmx $MASTER_NAME:/jmeter'
				sh 'kubectl exec -it $MASTER_NAME -- jmeter -n -t /jmeter/sample.jmx -R $SERVER_IPS -l log.jtl'
				sh 'kubectl cp $MASTER_NAME:log.jtl ./log.jtl'
				sh 'cat log.jtl'
			    }
                
            
            }
        }
}
}
