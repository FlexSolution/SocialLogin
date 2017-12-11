var main = function(){

    YAHOO.Bubbling.on("afterFormRuntimeInit", function(event, p_obj) {

        var redirectToSiteDashboard = function() {

            Alfresco.util.PopupManager.displayPrompt({
                title: "Success",
                text: "Oauth2 configuration has been saved",
                buttons: [
                    {
                        text: "Ok",
                        isDefault: true,
                        handler: function () {
                            this.hide();
                            window.location.reload(true);
                        }
                    }
                ]
            });
        };

        var form = p_obj[1].runtime;

        form.ajaxSubmitHandlers.successCallback = {
            fn: function(resp, p_obj)
            {
                redirectToSiteDashboard();
                return true;
            },
            obj: form,
            scope: this
        };

    }, this);
}();