def MASTER_NAME
def SERVER_IPS
def FRONTEND_ADDRESS
def SELENIUM_GRID_ADDRESS
def JMETER_MASTER
def JMETER_SERVERS

pipeline {

  environment {

    PROJECT = "jovial-coral-242011"
    FE_SVC_NAME = "${APP_NAME}-frontend"
    CLUSTER = "application-infra"
    CLUSTER_ZONE = "us-east1-b"
    JENKINS_CRED = "${PROJECT}"
  }
  
  agent {

    kubernetes {
      label 'masked-terrier-jenkins-slave'
      defaultContainer 'jnlp'
      yaml """
      apiVersion: v1
      kind: Pod
      metadata:
      labels:
      component: ci  
      spec:
        # Use service account that can deploy to all namespaces
        serviceAccountName: masked-terrier-jenkins-slave
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

    stage('Static Code analysis') {
        
        when{
          branch 'staging'
        }

        environment {
          scannerHome = tool 'HipsterSonarScanner'
        }

        steps {

            withSonarQubeEnv('Sonar-Scanner') {
                sh "${scannerHome}/bin/sonar-scanner -Dsonar.java.binaries=. -Dsonar.login='ac26c3839989e62c59c24282b3c0498fc1ef2ebc' -Dsonar.projectKey=hipster -Dsonar.sources='./src/productcatalogservice'"
            }

        }
    } 

    stage("Quality Gate") {

        when{
          branch 'staging'
        }

        steps {
          timeout(time: 2, unit: 'MINUTES') {
            waitForQualityGate abortPipeline: true
          }
        }
    }

    stage('Unit Test'){

        when{
          branch 'master'
        }
        
        steps{
          container('dotnet') {
              sh 'dotnet --info'
              sh 'dotnet test tests/cartservice/cartservice.tests.csproj'
              sh 'echo Unit Tests Successful !!'
                                
          }
        }
    }

    stage('Staging Buildi& Deploy') {
        
        when {
          branch 'staging'
        }
                
        steps {
          container('gcloud') {
              sh "PYTHONUNBUFFERED=1 gcloud builds submit --config=cloudbuild.yaml --substitutions=_PROJECT_ID=$PROJECT,_ZONE=$CLUSTER_ZONE,_CLUSTER=$CLUSTER,_BUILD_NUMBER=$BUILD_NUMBER,_NAMESPACE=staging ."
          }
        }
    }

    stage('Production Build & Deploy') {
      
        when {
          branch 'master'
        }
        
        steps {
          container('gcloud') {
              sh "PYTHONUNBUFFERED=1 gcloud builds submit --config=cloudbuild.yaml --substitutions=_PROJECT_ID=$PROJECT,_ZONE=$CLUSTER_ZONE,_CLUSTER=$CLUSTER,_BUILD_NUMBER=$BUILD_NUMBER,_NAMESPACE='production' ."
          }
        }
    }

    stage('Functional Testing'){

      steps{
        
        container('kubectl'){
          
          withKubeConfig([credentialsId: 'kube-token-application-infra',
                          serverUrl: 'https://34.73.114.12',
                          clusterName: 'application-infra',
                          namespace: 'default'
                        ]){
                            sh 'kubectl config view'
                            
                            sh 'kubectl get svc frontend-external -n production   -o jsonpath="{.status.loadBalancer.ingress[*].ip}"  > front_end_address'

                            script{
                              FRONTEND_ADDRESS = readFile('front_end_address')
                            }
                            echo "${FRONTEND_ADDRESS}"
                          }
        }
        container('kubectl'){

          sh 'kubectl get svc selenium-grid-selenium-hub   -o jsonpath="{.status.loadBalancer.ingress[*].ip}"  > selenium_grid_address'
          
          script{
            SELENIUM_GRID_ADDRESS = readFile('selenium_grid_address')
          }

          echo "${SELENIUM_GRID_ADDRESS}"
        }

        container('mvn'){
            
          sh 'mvn --version'
          sh "mvn  -f selenium-tests/pom.xml -DsuiteXmlFile=selenium-tests/src/test/resources/testng.xml -DTARGET_URL=${FRONTEND_ADDRESS} -DSELENIUM_GRID_URL=${SELENIUM_GRID_ADDRESS} test"
        }
      }
    }

    stage('Performance Testing'){

      steps{

        sh "sed -i 's/@@target_address@@/${FRONTEND_ADDRESS}/' performance_test.jmx"

        container('kubectl'){
          sh 'kubectl get pods -l app.kubernetes.io/component=master -o jsonpath="{.items[*].metadata.name}" > jmeter_master'
          sh 'kubectl get pods -l app.kubernetes.io/component=server -o jsonpath="{.items[*].status.podIP}" | tr " " "," > jmeter_servers'  
          
          script{
             JMETER_MASTER = readFile('jmeter_master')  
             JMETER_SERVERS = readFile('jmeter_servers')  
          }
          
          sh "kubectl cp performance_test.jmx ${JMETER_MASTER}:/jmeter"
          sh "kubectl exec -it ${JMETER_MASTER} -- rm -f *.jtl"
          sh "kubectl exec -it ${JMETER_MASTER} -- jmeter -n -t /jmeter/performance_test.jmx -R ${JMETER_SERVERS} -l log.jtl"
          sh "kubectl cp ${JMETER_MASTER}:log.jtl ./log.jtl"
          sh "cat log.jtl"
        }
      }
    }
  }
}
