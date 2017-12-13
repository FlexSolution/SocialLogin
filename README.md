# Setup
* Before startup an Alfresco server check if you are not overwriting `authentication.chain` in the `alfresco-global.properties`. If not - skip this step. If you are already have configured more then one authentication systems, you have to append to your chain one more authentication separated by comma in the end og the chain `flex_oauth2:flex_oauth2`. Example: `authentication.chain=alfrescoNtlm1:alfrescoNtlm,ldap1:ldap,flex_oauth2:flex_oauth2`. **Note if you disable all authentications systems and leave only `flex_oauth2:flex_oauth2` you will not have ability to access to `Oauth2 Configuration` page as Oauth2 is disabled by default from admin UI, leave at least one system by witch one you can sign in as Admin user and setup Oauth2 application credentials**
* Check that properties have correct values of your real IP or DNS name in the `alfresco-global.properties` **(We strongly recommend using HTTPS whenever possible on your server)**:
```properties
alfresco.context=alfresco
alfresco.host=your.company.nds
alfresco.port=443
alfresco.protocol=https

share.context=share
share.host=your.company.nds
share.port=443
share.protocol=https
```
* Login to your admin LinkedIn account on [https://www.linkedin.com/developer/apps](https://www.linkedin.com/developer/apps). Click on the button `Create Application`.
* Fill out all required fields and save it.
* Open `Authentication` tab where you can see your `Client Id` and `Secret Key`. **Do not show this keys to anybody**. This codes will need you for further setup.
* Check the checkboxes from `Default Application Permissions` set. The `r_basicprofile`	and `r_emailaddress` have to be checked (true), and `rw_company_admin` and `w_share` - unchecked (false).
* Add `OAuth 2.0 Authorized Redirect URLs`: enter a URL of your installed Alfresco with format `https://<IP>:<Port>/share/service/api/social-login` where `IP` - is the IP address of your server or DNS name, `Port` - http port (keep empty if you are using default http port). 
Examples: `https://192.168.0.119:8443/share/service/api/social-login`, `https://your.company.nds/share/service/api/social-login`
**Do not forget click `Add` button and `Save/Update` on the page bottom. Do not close the page until you can see success saving message**.
* login as the admin user to Alfresco Share. Open:`Admin Tools`/`Tools`/`Oauth2 Configuration` -  Sample URL for locally installed Alfresco: [http://localhost:8080/share/page/console/admin-console/oauth2-config-form](http://localhost:8080/share/page/console/admin-console/oauth2-config-form) 
* Fill `Client ID` and `Secret KEY` from your LinkedIn application that you have already created. Set checkbox `Is LinkedIn Oauth2 Sign In Enabled ?` for enabling `Sign In with LinkedIn` button on the login page. Click `Submit`.
* Done. You can open login page an sing in to Alfresco Share as a LinkedIn user.

