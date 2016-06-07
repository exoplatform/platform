/**
 * Created by TClement on 12/15/15.
 */
/**
 * Module handling the operation list section.
 * @exports operationController
 */
define('operationController', ['SHARED/jquery', 'indexingManagementApi', 'appBroadcaster'],
    function($, indexingManagementApi, appBroadcaster) {

        //Service
        var myIndexingManagementApi = new indexingManagementApi();
        //Event broadcaster
        var myAppBroadcaster;

        var operationController = function operationController() {
            var self = this;

            /**
             * Initialize the Operation Controller
             *
             * @param {broadcaster} appBroadcaster the appBroadcaster responsible to spread the event to other controller
             * @return void
             */
            self.init = function(appBroadcaster) {

                myAppBroadcaster = appBroadcaster;

                //Init the Operation list
                self.refreshOperationList();
                initUiListener();

                //Set refresh interval for operations list to 5 seconds
                setInterval(function(){
                    self.refreshOperationList();
                }, 5000);

            }

            /**
             * Refresh the list of Indexing operations
             *
             * @return void
             */
            self.refreshOperationList = function() {
                updateOperationTable();
            }

        }


        /**
         * Initialize the listener on UI events (such as onClick, onHover, ...)
         *
         * @return void
         */
        function initUiListener() {
            addDeleteOperationUiListener();
        }

        /**
         * Add the event listener handling the click on delete operation button
         *
         * @return void
         */
        function addDeleteOperationUiListener() {
            //Deleting queue operation Event
            $(document).on('click.btn-operation-delete', '.btn-operation-delete', function () {

                //Get operation Id
                var jOperation = $(this);
                var jOperationId = jOperation.attr('data-operationId');

                //Trigger the deleting operation
                deleteOperation(jOperationId);
            });
        }

        /**
         * Update the table of list of operations with latest value
         *
         * @return void
         */
        function updateOperationTable() {
            myIndexingManagementApi.getOperations(null, 0, 10, false, fillOperationTable);
        }

        /**
         * Delete an operation using the Indexing Management Api
         *
         * @param {String} indexingOperationId the id of the operation to delete
         * @return void
         */
        function deleteOperation(indexingOperationId) {
            myIndexingManagementApi.deleteOperation(indexingOperationId, myAppBroadcaster.onDeleteOperation);
        }

        /**
         * Manipulate the DOM to fill the Indexing Operation table
         *
         * @param {IndexingOperationArray} json a JSON array of Indexing Operation
         * @return void
         */
        function fillOperationTable(json) {

            //Loop on operations to add one line per Operation in the table
            var html = "";
            for(var i = 0; i < json.resources.length; i++) {

                html += "<tr>" +
                "    <th scope='row'>" + json.resources[i].id + "</th>" +
                "    <td>" + json.resources[i].entityType + "</td>" +
                "    <td>" + json.resources[i].entityId + "</td>" +
                "    <td>" + json.resources[i].operation + "</td>" +
                "    <td>" + json.resources[i].timestamp + "</td>" +
                "    <td>" +
                "        <button type='button'" +
                "                data-operationId=''" +
                "                class='btn-operation-delete btn btn-primary btn-mini'>" +
                "            Delete" +
                "        </button>" +
                "    </td>" +
                "</tr>";
            }

            //Update the table
            $('#indexingOperationTable tbody').html(html);

        }

        return operationController;
    }
);
