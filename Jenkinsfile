pipeline {

  environment {
    PROJECT = "core-feat-241406"
    FE_SVC_NAME = "${APP_NAME}-frontend"
    CLUSTER = "hipster"
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
  - name: kubectl
    image: gcr.io/cloud-builders/kubectl
    command:
    - cat
    tty: true
"""
}
}
stages {
	stage('Build and push image with Container Builder') {
      		steps {
        		container('gcloud') {
				sh("sed -i.bak 's#%name%#frontend#' ./release/kubernetes-manifests.yaml")
    				sh("sed -i.bak 's#%version%#$BUILD_NUMBER#' ./release/kubernetes-manifests.yaml")
    				sh("cat ./release/kubernetes-manifests.yaml")	
          			sh "PYTHONUNBUFFERED=1 gcloud builds submit --config=cloudbuild.yaml --substitutions=_ZONE=$CLUSTER_ZONE,_CLUSTER=$CLUSTER,_BUILD_NUMBER=$BUILD_NUMBER ."
        		}
      		}
    	}

}
}
