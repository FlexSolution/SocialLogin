<@standalone>
    <@markup id="css" >
        <#include "/org/alfresco/components/form/form.css.ftl"/>
        <@link rel="stylesheet" type="text/css" href="SocialLogin-share/components/css/templates/oauth2-config-form.css"/>
    </@>

    <@markup id="js">
        <#include "/org/alfresco/components/form/form.js.ftl"/>
        <@script type="text/javascript" src="SocialLogin-share/components/js/templates/oauth2-config-form.js"/>
    </@>

    <@markup id="widgets">
        <@createWidgets/>
    </@>

    <@markup id="html">
        <@uniqueIdDiv>
            <#import "/org/alfresco/components/form/form.lib.ftl" as formLib />
         <div class="oauth2-config-form-container">
         <#if error?exists>
             <div class="error">${error}</div>
         <#elseif form?exists>
             <#assign formId=args.htmlid?js_string?html + "-form">
             <#assign formUI><#if args.formUI??>${args.formUI}<#else>true</#if></#assign>

             <#if form.viewTemplate?? && form.mode == "view">
                 <#include "${form.viewTemplate}" />
             <#elseif form.editTemplate?? && form.mode == "edit">
                 <#include "${form.editTemplate}" />
             <#elseif form.createTemplate?? && form.mode == "create">
                 <#include "${form.createTemplate}" />
             <#else>
                 <#if formUI == "true">
                     <@formLib.renderFormsRuntime formId=formId />
                 </#if>

                 <@formLib.renderFormContainer formId=formId>
                     <#list form.structure as item>
                         <#if item.kind == "set">
                             <#if item.children?size &gt; 0>
                                 <@formLib.renderSet set=item />
                             </#if>
                         <#else>
                             <@formLib.renderField field=form.fields[item.id] />
                         </#if>
                     </#list>
                 </@>
             </#if>
         <#else>
            <div class="form-container">${msg("form.not.present")}</div>
         </#if>
         </div>
        </@>
    </@>
</@>
