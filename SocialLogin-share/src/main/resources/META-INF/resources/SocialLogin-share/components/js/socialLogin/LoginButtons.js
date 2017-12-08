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
                                // window.open(url, "_self");//todo it is a full screen mode

                                var oauthpopup = function (options) {
                                    options.windowName = options.windowName || 'ConnectWithOAuth'; // should not include space for IE
                                    options.windowOptions = options.windowOptions || 'location=0,status=0,width=400,height=400';
                                    options.callback = options.callback || function () {
                                        window.location.reload();
                                    };
                                    var that = this;
                                    console.log(options.path);
                                    that._oauthWindow = window.open(options.path, options.windowName, options.windowOptions);
                                    that._oauthInterval = window.setInterval(function () {

                                        if (that._oauthWindow.closed) {
                                            window.clearInterval(that._oauthInterval);
                                            options.callback();
                                        }
                                    }, 1000);

                                    YAHOO.util.Event.addListener(window, 'beforeunload', function () {
                                        if (!that._oauthWindow.closed) that._oauthInterval.close();
                                    });

                                    YAHOO.util.Event.addListener(that._oauthInterval, 'beforeunload', function () {
                                        window.clearInterval(that._oauthInterval);
                                    });
                                };

                                //create new oAuth popup window and monitor it
                                oauthpopup({
                                    path: url
                                    // callback: function()
                                    // {
                                    //     console.log('callback');
                                    //     //do callback stuff
                                    // }
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