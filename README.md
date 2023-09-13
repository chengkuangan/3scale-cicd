## CI/CD Demo Using Jenkins

> Note: This demo is using the [HelloService](/HelloService/) as the backend API

**Deploy Jenkins**

1. Create a project called `3scale-cicd`
    ```
    oc new-project threescale-jenkins
    ```
2. Use the Helm Chart comes with OpenShift to deploy Jenkins. Make sure the project `3scale-cicd` is selected. Install Jenkins with default settings.

**Install 3Scale Toolbox**

Refers product documentation for [3Scale Toolbox Installation](https://access.redhat.com/documentation/en-us/red_hat_3scale_api_management/2.13/html/operating_3scale/the-threescale-toolbox#installing-the-toolbox) for officailly Red Hat supported installation method.

You can also use `gem` to install the toolbox. Follow this [instruction from Github](https://github.com/3scale/3scale_toolbox)

**Configure 3Scale Toolbox**

1. Create project for 3Scale toolbox

    ```
    # This is the OCP project where 3Scale Toolbox POD is scheduled for job.
    oc new-project threescale-toolbox
    ```

3. Create a Access Token on the 3Scale Admin Console. Configure `Read/Write` access for all the options.  Note down the new access token, e.g. `1f5a8eb4f71f2d34555ea77fa6f3711fc767463c10f60eae7c52642af6559e39`
    

2. Generate toolbox configuration file and create Kubernetes secret


    ```
    # 3scale remote add 3scale-tenant https://$TOKEN@$TENANT.3scale.net/

    3scale remote add 3scale-tenant  https://c63b69c2c6d541fc17bfe223c9c526fa4832e2747043dce44c69b9f6ea33eecc@user1-admin.apps.cluster-hk42t.hk42t.sandbox2512.opentlc.com
    
    # The file is created at the home directory
    oc create secret generic 3scale-toolbox --from-file=$HOME/.3scalerc.yaml -n threescale-toolbox

    ```
    >Note: This is run locally in your laptop. The .3scalerc.yaml file is created locally at the home dircetory.

    Example `.3scalerc.yaml` content:

    ```
    ---
    ---
    :remotes:
        3scale-tenant:
            :authentication: 1f5a8eb4f71f2d34555ea77fa6f3711fc767463c10f60eae7c52642af6559e39
            :endpoint: https://user1-admin.apps.cluster-bt5c8.bt5c8.sandbox1023.opentlc.com
    ```

**Configure Jenkins**

1. Configure cross project access for Jenkins:

    ```
    oc policy add-role-to-user edit system:serviceaccount:threescale-jenkins:jenkins -n backend-api
    oc policy add-role-to-user edit system:serviceaccount:threescale-jenkins:jenkins -n threescale-toolbox

    ```

2. Create a new Pipeline Project in Jenkins and point it to the [Jenkinsfile](https://github.com/chengkuangan/3scale-cicd/blob/main/Jenkinsfile).

> Note: While testing, you should delete the failed service using `3scale service delete 3scale-tenant <service_id>` command

**Deploy Self Managed API Cast**

1. Create a project:

    ```
    oc new-project threescale-apicast
    ```

2. Create the secret:
    ```
    oc create secret generic 3scaleportal --from-literal=AdminPortalURL=https://c63b69c2c6d541fc17bfe223c9c526fa4832e2747043dce44c69b9f6ea33eecc@user1-admin.apps.cluster-hk42t.hk42t.sandbox2512.opentlc.com -n threescale-apicast
    ```

3.  Deploy the apicast. This creates a staging and production APICast without TLS.

    ```
    oc apply -f self-managed-apicast.yaml -n threescale-apicast
    ```

## References:

- [Installing 3Scale Toolbox Command Line locally](https://github.com/3scale/3scale_toolbox#installation)
- [3Scale Toolbox Jenkins](https://github.com/rh-integration/3scale-toolbox-jenkins)
- [IntegrationApp Automation](https://github.com/rh-integration/IntegrationApp-Automation)
- [Using the 3scale toolbox Jenkins Shared Library](https://developers.redhat.com/blog/2019/07/31/using-the-3scale-toolbox-jenkins-shared-library#what_needs_to_be_improved)
- [Deploy your API from a Jenkins Pipeline](https://developers.redhat.com/blog/2019/07/30/deploy-your-api-from-a-jenkins-pipeline?p=612387#)