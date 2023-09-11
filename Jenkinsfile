#!groovy

library identifier: '3scale-toolbox-jenkins@master',
        retriever: modernSCM([$class: 'GitSCMSource',
                              remote: 'https://github.com/rh-integration/3scale-toolbox-jenkins.git',
                              traits: [[$class: 'jenkins.plugins.git.traits.BranchDiscoveryTrait']]])

def service = null

pipeline{
  agent {
    node {
      label 'master'
    }
  }
  parameters{
      string (defaultValue: 'http://hello-production-apicast.apps.cluster-bt5c8.bt5c8.sandbox1023.opentlc.com:80', name:'PRODUCTION_PUBLIC_BASE_URL', description:'3Scale production public domain')
      string (defaultValue: 'http://hello-staging-apicast.apps.cluster-bt5c8.bt5c8.sandbox1023.opentlc.com:80', name:'STAGING_PUBLIC_BASE_URL', description:'3Scale staging public domain')
      string (defaultValue: 'threescale-toolbox', name:'TOOLBOX_PROJECT', description:'3Scale Toolbox OCP Project Name')
      string (defaultValue: '3scale-tenant', name:'TARGET_INSTANCE', description:'Target instance for toolbox')
      string (defaultValue: 'registry.redhat.io/3scale-amp2/toolbox-rhel8:1.9.0-46', name:'TOOLBOX_IMAGE_REGISTRY', description:'Toolbox image registry')
      string (defaultValue: 'yes', name:'DISABLE_TLS_VALIDATION', description:'Disable TLS verification')
      string (defaultValue: '3scale-toolbox', name:'SECRET_NAME', description:'3Scale Toolbox OCP Secret')
      string (defaultValue: 'john', name:'DEVELOPER_ACCOUNT_ID', description:'Developer Account Id')
      string (defaultValue: 'https://raw.githubusercontent.com/chengkuangan/3scale-cicd/main/plan.yaml', name:'PLAN_YAML_FILE_PATH', description:'Application Plan YAML file')
      string (defaultValue: 'openapi-spec.json', name:'OPENAPI_FILE', description:'Open API file')
      string (defaultValue: 'IfNotPresent', name:'IMAGE_PULL_POLICY', description:'3Scale Toolbox Image Pull Strategy')
      string (defaultValue: 'http://hello-service.backend-api.svc.cluster.local:8080', name:'BACKEND_API_PRIVATE_URL', description:'Backend API private URL')
      
  }
  
  stages{
    stage('Checkout Source'){
      steps {
        checkout scm
      }
    }

    stage('Publish Hello API to 3Scale'){

      steps{
        script {
          def app_name= 'hello-service'
          
          echo "Prepare 3Scale Configuration"

          echo "OpenAPI File: " + params.OPENAPI_FILE
          echo "Toolbox Image Registry: " + params.TOOLBOX_IMAGE_REGISTRY

          service = toolbox.prepareThreescaleService(
                  openapi: [filename: params.OPENAPI_FILE],
                  environment: [privateBaseUrl                : params.BACKEND_API_PRIVATE_URL,
                                targetSystemName              : "hello_service",
                                stagingPublicBaseURL          : params.STAGING_PUBLIC_BASE_URL,
                                productionPublicBaseURL       : params.PRODUCTION_PUBLIC_BASE_URL
                              ],
                  toolbox: [openshiftProject: params.TOOLBOX_PROJECT, 
                            destination     : params.TARGET_INSTANCE,
                            image           : params.TOOLBOX_IMAGE_REGISTRY,
                            insecure        : params.DISABLE_TLS_VALIDATION == "yes",
                            secretName      : params.SECRET_NAME,
                            imagePullPolicy : params.IMAGE_PULL_POLICY,
                            verbose         : false],
                  service: [:],
                  applicationPlans: [
                          //[systemName: "plan1", name: "Hello Application Plan", defaultPlan: true, costPerMonth: 100, setupFee: 10, trialPeriodDays: 5],
                          [artefactFile: params.PLAN_YAML_FILE_PATH],
                  ],
                  applications: [
                          [name: app_name, description: "This is used for test environment ", plan: "plan1", account: params.DEVELOPER_ACCOUNT_ID]

                  ]

          ) // END Prepare 3Scale Configuration

          echo "toolbox version = " + service.toolbox.getToolboxVersion()

          echo "Import OpenAPI"
          service.importOpenAPI()
          echo "Service with system_name ${service.environment.targetSystemName} created !"

          echo "Import Policies"
          def myCommandLine = ["3scale", "policies", "import", "-u", "https://raw.githubusercontent.com/chengkuangan/3scale-cicd/main/policies.yaml"] + service.toolbox.getGlobalToolboxOptions() + service.toolbox.destination + ["hello_service"]
          
          service.toolbox.runToolbox(commandLine: myCommandLine,
                jobName: "import-policies")

          echo "Create an Application Plan"
          service.applyApplicationPlans()

          echo "Create an Application"
          service.applyApplication()

          echo "Promote to production"
          service.promoteToProduction()
        }
      }
    } // END Public Hello API to 3Scale



  }

}