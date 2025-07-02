sap.ui.define([
    "sap/ui/model/json/JSONModel",
    "sap/ui/Device"
],
    function (JSONModel, Device) {
        "use strict";

        return {
            /**
             * Provides runtime information for the device the UI5 app is running on as a JSONModel.
             * @returns {sap.ui.model.json.JSONModel} The device model.
             */
            createDeviceModel: function () {
                var oModel = new JSONModel(Device);
                oModel.setDefaultBindingMode("OneWay");
                return oModel;
            },

            /**
             * Creates a model by calling the getUserInfo OData v4 function.
             * @returns {Promise<sap.ui.model.json.JSONModel>} Promise that resolves to the user info model.
             */
            createUserInfoModel: function (oComponent) {
                const oModel = oComponent.getModel();
                const oJSONModel = new JSONModel({});
                oModel.setDefaultBindingMode("OneWay");
                const oBinding = oModel.bindContext("/getUserInfo");
                oBinding.requestObject().then((oUserInfo) => {
                    if (oUserInfo) {
                        oJSONModel.setData(oUserInfo);
                    }
                });
                return oJSONModel;
            }
        };
    });