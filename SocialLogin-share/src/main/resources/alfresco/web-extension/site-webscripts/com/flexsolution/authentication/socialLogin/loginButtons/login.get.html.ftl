<@markup id="reset-pass-css" target="js" action="after">
<#-- CSS Dependencies -->
    <@link href="SocialLogin-share/components/css/socialLogin/LoginButtons.css" group="login"/>
    <@link href="SocialLogin-share/components/libs/font-awesome-4.7.0/css/font-awesome.min.css" group="login"/>

<script type="text/javascript">
    new FlexSolution.component.SocialLoginButtons("${args.htmlid}");
</script>
</@markup>

<@markup id="reset-pass-js">
<#-- JavaScript Dependencies -->
    <@script src="SocialLogin-share/components/js/socialLogin/LoginButtons.js" group="login"/>
</@markup>

<@markup id="reset-password" target="buttons" action="after" scope="page">
<div class="form-field social-login-button">
    <input type="button" id="${args.htmlid}-loginButtons" value="${msg("button.socialLogin")}"/>
</div>
</@>