
/**
 * Module representing an Indexing Operation Enum.
 * @exports operationEnum
 */
define('operationEnum', ['SHARED/jquery'],
    function($) {

        var OperationEnum = function OperationEnum() {
            var self = this;


            /**
             * @const
             */
            self.INIT = "init",

            /**
             * @const
             */
                self.INDEX = "index",

            /**
             * @const
             */
                self.REINDEX = "reindex",

            /**
             * @const
             */
                self.UNINDEX = "unindex",

            /**
             * @const
             */
                self.REINDEXALL = "reindexAll",

            /**
             * @const
             */
                self.UNINDEXALL = "unindexAll";

        }

        return OperationEnum;
    }
);


/**
 * Module representing an Indexing Operation Resource.
 * @exports indexingOperationResource
 */
define('indexingOperationResource', ['SHARED/jquery'],
    function($) {


        var AnIndexingOperationResource = function AnIndexingOperationResource() {
            var self = this;

            /**
             * The Entity Type
             * datatype: String
             **/
            self.entityType = null;

            /**
             * The Entity Id
             * datatype: String
             **/
            self.entityId = null;

            /**
             * The Indexing operation
             * datatype: OperationEnum
             **/
            self.operation = null;


            self.constructFromObject = function(data) {

                self.entityType = data.entityType;

                self.entityId = data.entityId;

                self.operation = data.operation;

            }


            /**
             * get The Entity Type
             * @return {String}
             **/
            self.getEntityType = function() {
                return self.entityType;
            }

            /**
             * set The Entity Type
             * @param {String} entityType
             **/
            self.setEntityType = function (entityType) {
                self.entityType = entityType;
            }

            /**
             * get The Entity Id
             * @return {String}
             **/
            self.getEntityId = function() {
                return self.entityId;
            }

            /**
             * set The Entity Id
             * @param {String} entityId
             **/
            self.setEntityId = function (entityId) {
                self.entityId = entityId;
            }

            /**
             * get The Indexing operation
             * @return {OperationEnum}
             **/
            self.getOperation = function() {
                return self.operation;
            }

            /**
             * set The Indexing operation
             * @param {OperationEnum} operation
             **/
            self.setOperation = function (operation) {
                self.operation = operation;
            }


            self.toJson = function () {
                return JSON.stringify(self);
            }
        }

        return AnIndexingOperationResource;
    }
);
