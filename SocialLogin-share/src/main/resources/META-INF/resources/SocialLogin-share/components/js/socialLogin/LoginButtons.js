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

                Alfresco.util.Ajax.request(
                    {
                        url: Alfresco.constants.PROXY_URI_RELATIVE.replace("/alfresco/", "/alfresco-noauth/") + "oauth2/enabled-list",
                        method: Alfresco.util.Ajax.GET,
                        successCallback: {
                            fn: function (response, p_obj) {
                                var list = response.json;

                                console.log(list);

                                for (var i = 0; i < list.length; i++) {
                                    var socialButton = list[i];
                                    this.widgets[socialButton.id] = new YAHOO.widget.Button({
                                        type: "push",
                                        label: Alfresco.util.message(socialButton.labelKey),
                                        id: socialButton.id,
                                        container: this.id,
                                        onclick: {
                                            fn: this.showDialog,
                                            obj: socialButton,
                                            scope: this
                                        }
                                    });
                                }
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
            },

            showDialog: function (p_event, p_obj) {

                var templateUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + "api/socialLogin/{api}/authorizationUrl", {
                    api: p_obj.id
                });

                Alfresco.util.Ajax.request(
                    {
                        url: templateUrl,
                        method: Alfresco.util.Ajax.GET,
                        successCallback: {
                            fn: function (response, p_obj) {
                                var url = response.json.authorizationUrl;

                                var oauthpopup = function (options) {
                                    options.windowName = options.windowName || 'ConnectWithOAuth'; // should not include space for IE
                                    options.windowOptions = options.windowOptions || 'location=0,status=0,width=400,height=400';
                                    options.callback = options.callback || function () {
                                        window.location.reload();
                                    };
                                    var that = this;
                                    console.log(options.path);
                                    that._oauthWindow = window.open(options.path, options.windowName, options.windowOptions);
                                    if (that._oauthWindow) {
                                        that._oauthInterval = window.setInterval(function () {

                                            if (that._oauthWindow.closed) {
                                                window.clearInterval(that._oauthInterval);
                                                options.callback();
                                            }
                                        }, 1000);

                                        YAHOO.util.Event.addListener(window, 'beforeunload', function () {
                                            if (!that._oauthWindow.closed) that._oauthWindow.close();
                                        });

                                        YAHOO.util.Event.addListener(that._oauthWindow, 'beforeunload', function () {
                                            window.clearInterval(that._oauthInterval);
                                        });
                                    } else {
                                        Alfresco.util.PopupManager.displayPrompt({
                                            title: "Warning",
                                            text: "Please allow popup windows in your browser for Oauth2 Signing in"
                                        });
                                    }
                                };

                                //create new oAuth popup window and monitor it
                                oauthpopup({
                                    path: url
                                });
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