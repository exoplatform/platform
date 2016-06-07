/*
 * @javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavascriptClientCodegen", date = "2015-12-09T15:57:02.471+07:00")
 */

/**
 * Module representing an Indexing Connector Resource.
 * @exports indexingConnectorResource
 */
define('indexingConnectorResource', ['SHARED/jquery'],
    function($) {

        var AnIndexingConnectorResources = function AnIndexingConnectorResources() {
            var self = this;

            /**
             * The Connector Type
             * datatype: String
             **/
            self.type = null;

            /**
             * Does the Connector is enable or not
             * datatype: Boolean
             **/
            self.enable = null;


            self.constructFromObject = function(data) {

                self.type = data.type;

                self.enable = data.enable;

            }


            /**
             * get The Connector Type
             * @return {String}
             **/
            self.getType = function() {
                return self.type;
            }

            /**
             * set The Connector Type
             * @param {String} type
             **/
            self.setType = function (type) {
                self.type = type;
            }

            /**
             * get Does the Connector is enable or not
             * @return {Boolean}
             **/
            self.getEnable = function() {
                return self.enable;
            }

            /**
             * set Does the Connector is enable or not
             * @param {Boolean} enable
             **/
            self.setEnable = function (enable) {
                self.enable = enable;
            }


            self.toJson = function () {
                return JSON.stringify(self);
            }
        }

        return AnIndexingConnectorResources;
    }
);
