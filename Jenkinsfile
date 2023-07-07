#!groovy

// ------------------------------------------------------------------------------ 
// 1. Pre-deploy HelloService
// 2. Create Product
// 3. 
// ------------------------------------------------------------------------------ //

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
      string (defaultValue: 'user1-apicast-production.apps.cluster-hk42t.hk42t.sandbox2512.opentlc.com', name:'PUBLIC_PRODUCTION_WILDCARD_DOMAIN', description:'3Scale production public domain')
      string (defaultValue: 'user1-apicast-staging.apps.cluster-hk42t.hk42t.sandbox2512.opentlc.com', name:'PUBLIC_STAGING_WILDCARD_DOMAIN', description:'3Scale staging public domain')
      string (defaultValue: 'backend-api', name:'DEV_PROJECT', description:'API Base System Name')
      string (defaultValue: 'hello_service', name:'API_BASE_SYSTEM_NAME', description:'API Base System Name')
      string (defaultValue: 'image-registry.openshift-image-registry.svc:5000', name:'IMAGE_REGISTRY', description:'open shift token')
      string (defaultValue: 'https://github.com/rh-integration/IntegrationApp-Automation.git', name:'GIT_REPO', description:'Git source')
      string (defaultValue: 'main', name:'GIT_BRANCH', description:'Git branch in the source git')
      string (defaultValue: 'instance_a', name:'TARGET_INSTANCE', description:'Target instance for toolbox')
      string (defaultValue: 'image-registry.openshift-image-registry.svc:5000/rh-dev/toolbox:v0.12.4', name:'TOOLBOX_IMAGE_REGISTRY', description:'Toolbox image registry')
      string (defaultValue: 'yes', name:'DISABLE_TLS_VALIDATION', description:'Disable TLS verification')
      string (defaultValue: '3scale-toolbox', name:'SECRET_NAME', description:'Disable TLS verification')
      string (defaultValue: 'Developer', name:'DEVELOPER_ACCOUNT_ID', description:'Developer Account Id')
      string (defaultValue: 'https://raw.githubusercontent.com/chengkuangan/3scale-cicd/main/plan.yaml', name:'PLAN_YAML_FILE_PATH', description:'Developer Account Id')
      
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
          def envName = params.DEV_PROJECT
          def app_name= 'hello-service'
          def backend_service = app_name + '.backend-api.svc.cluster.local'
          def targetPort = '8080'
          backend_service=  "http://"+backend_service
          
          echo "Prepare 3Scale Configuration"
          service = toolbox.prepareThreescaleService(
                  openapi: [filename: "openapi-spec.json"],
                  environment: [baseSystemName                : params.API_BASE_SYSTEM_NAME,
                                privateBaseUrl                : backend_service,
                                privateBasePath               : "/cicd",
                                environmentName               :  envName,
                                publicStagingWildcardDomain: params.PUBLIC_STAGING_WILDCARD_DOMAIN != "" ? params.PUBLIC_STAGING_WILDCARD_DOMAIN : null,
                                publicProductionWildcardDomain: params.PUBLIC_PRODUCTION_WILDCARD_DOMAIN != "" ? params.PUBLIC_PRODUCTION_WILDCARD_DOMAIN : null
                              ],
                  toolbox: [openshiftProject: params.DEV_PROJECT, destination: params.TARGET_INSTANCE,
                            image           : params.TOOLBOX_IMAGE_REGISTRY,
                            insecure        : params.DISABLE_TLS_VALIDATION == "yes",
                            secretName      : params.SECRET_NAME],
                  service: [:],
                  applicationPlans: [
                          [systemName: "hello_simple_plan", name: "Hello Application Plan", defaultPlan: true, costPerMonth: 100, setupFee: 10, trialPeriodDays: 5],
                          [systemName: "hello_silver_plan", name: "Hello Application Silver Plan", costPerMonth: 100, setupFee: 10, trialPeriodDays: 5],
                          [artefactFile: params.PLAN_YAML_FILE_PATH],
                  ],
                  applications: [
                          [name: envName, description: "This is used for test environment ", plan: "hello_simple_plan", account: params.DEVELOPER_ACCOUNT_ID]

                  ]

          ) // END Prepare 3Scale Configuration

          echo "toolbox version = " + service.toolbox.getToolboxVersion()

          echo "Import OpenAPI"
          service.importOpenAPI()
          echo "Service with system_name ${service.environment.targetSystemName} created !"

          echo "Create an Application Plan"
          service.applyApplicationPlans()

          echo "Create an Application"
          service.applyApplication()
        }
      }
    } // END Public Hello API to 3Scale



  }

}