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

                var templateUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + "api/socialLogin/{api}/authorizationUrl", {
                    api: "linkedIn"//todo other implementations
                });

                Alfresco.util.Ajax.request(
                    {
                        url: templateUrl,
                        method: Alfresco.util.Ajax.GET,
                        successCallback: {
                            fn: function (response, p_obj) {
                                var url = response.json.authorizationUrl;
                                window.open(url, "_self");//todo popup
                            },
                            scope: this
                        },
                        failureCallback: {
                            fn: function (response) {
                                response = response.serverResponse ? YAHOO.lang.JSON.parse(response.serverResponse.responseText) : response;
                                console.error(response);
                                this.showSpinner(response, 10);
                            },
                            scope: this
                        }
                    });
            }
        });
})();