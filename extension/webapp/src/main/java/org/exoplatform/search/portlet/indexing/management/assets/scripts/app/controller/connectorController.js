/**
 * Created by TClement on 12/15/15.
 */
/**
 * Module handling the connector list section.
 * @exports connectorController
 */
define('connectorController', ['SHARED/jquery', 'indexingManagementApi', 'appBroadcaster', 'indexingOperationResource', 'operationEnum'],
    function($, indexingManagementApi, appBroadcaster, indexingOperationResource, operationEnum) {

        //Service
        var myIndexingManagementApi = new indexingManagementApi();
        //Event broadcaster
        var myAppBroadcaster;


        var connectorController = function connectorController() {
            var self = this;

            /**
             * Initialize the Connector Controller
             *
             * @param {broadcaster} appBroadcaster the appBroadcaster responsible to spread the event to other controller
             * @return void
             */
            self.init = function(appBroadcaster) {

                myAppBroadcaster = appBroadcaster;

                //Init the connector list
                self.refreshConnectorList();
                initUiListener();

            }

            /**
             * Refresh the list of Indexing connectors
             *
             * @return void
             */
            self.refreshConnectorList = function() {
                updateConnectorTable();
            }

        }

        /**
         * Initialize the listener on UI events (such as onClick, onHover, ...)
         *
         * @return void
         */
        function initUiListener() {
            addReindexConnectorUiListener();
        }

        /**
         * Declare the event listener handling the click on reindex connector button
         *
         * @return void
         */
        function addReindexConnectorUiListener() {

            //Reindex connector Event
            $(document).on('click.btn-connector-reindex', '.btn-connector-reindex', function () {

                //Get operation type
                var jConnector = $(this);
                var jConnectorType = jConnector.attr('data-connectorType');

                //Trigger the new Indexing Operation
                reindexConnector(jConnectorType);
            });

        }

        /**
         * Update the table of list of connectors with latest value
         *
         * @return void
         */
        function updateConnectorTable() {
            myIndexingManagementApi.getConnectors(null, false, fillConnectorTable);
        }

        /**
         * Reindex a connector using the Indexing Management Api
         *
         * @param {String} connectorType the type of the connector to reindex
         * @return void
         */
        function reindexConnector(connectorType) {

            //Construct the indexingOperation
            var indexingOperation = new indexingOperationResource();
            indexingOperation.setEntityType(connectorType);
            indexingOperation.setOperation(new operationEnum().REINDEXALL);

            myIndexingManagementApi.addOperation(indexingOperation, myAppBroadcaster.onReindexConnector);
        }

        /**
         * Manipulate the DOM to fill the Indexing Connector table
         *
         * @param {IndexingConnectorArray} json a JSON array of Indexing Connector
         * @return void
         */
        function fillConnectorTable(json) {

            //Loop on connectors to add one line per connector in the table
            var html = "";
            for(var i = 0; i < json.resources.length; i++) {

                var checked = '';
                if (json.resources[i].enable) checked = 'checked';

                html += "<tr>" +
                "    <th scope='row'>" + json.resources[i].type + "</th>" +
                "    <td>" + json.resources[i].description + "</td>" +
                "    <td>" + json.resources[i].index + "</td>" +
                "    <td>" +
                "        <div class='connector-switch'>" +
                "            <input type='checkbox' name='connector-switch' data-connectorType='" + json.resources[i].type + "' " + checked + ">" +
                "        </div>" +
                "    </td>" +
                "    <td>" +
                "        <button data-connectorType='" + json.resources[i].type + "' type='button' class='btn-connector-reindex btn btn-primary btn-mini'>" +
                "           Reindex" +
                "        </button>" +
                "    </td>" +
                "</tr>";
            }

            //Update the table
            $('#indexingConnectorTable tbody').html(html);

        }

        return connectorController;
    }
);
