var namespace = function (identifier) {
    var klasses = arguments[1] || false;
    var ns = window;

    if (identifier !== '') {
        var parts = identifier.split(".");
        for (var i = 0; i < parts.length; i++) {
            if (!ns[parts[i]]) {
                ns[parts[i]] = {};
            }
            ns = ns[parts[i]];
        }
    }

    if (klasses) {
        for (var klass in klasses) {
            if (klasses.hasOwnProperty(klass)) {
                ns[klass] = klasses[klass];
            }
        }
    }

    return ns;
};


(function () {

    namespace("FlexSolution.component");

    var Dom = YAHOO.util.Dom;

    FlexSolution.component.SocialLoginButtons = function Login_constructor(htmlId) {
        FlexSolution.component.SocialLoginButtons.superclass.constructor.call(this, "FlexSolution.component.SocialLoginButtons", htmlId);

        return this;
    };

    YAHOO.extend(FlexSolution.component.SocialLoginButtons, Alfresco.component.Base,
        {
            options: {
                spinner: null
            },

            onReady: function Login_onReady() {
                Alfresco.util.createYUIButton(this, "loginButtons", this.showDialog);
            },

            showDialog: function (p_event, p_obj) {

                // var url = YAHOO.lang.substitute("https://www.linkedin.com/oauth/v2/authorization?" +
                var url = YAHOO.lang.substitute("https://www.linkedin.com/uas/oauth2/authorization?" +
                    "response_type={response_type}&" +
                    "redirect_uri={redirect_uri}&" +
                    "state={state}&" +
                    "client_id={client_id}", {
                        response_type: "code",
                        client_id: "78njxd1uv7zrvq",//todo from registered App
                        redirect_uri: encodeURIComponent(location.origin + Alfresco.constants.URL_CONTEXT +  "service/api/social-login"),
                        state:"ololo1_DCEeFWf45A53sdfKef424"//todo CSRF here
                    });
                window.open(url,"_self");
            }
        });
})();