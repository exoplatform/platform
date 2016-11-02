/**
 * Created by TClement on 12/15/15.
 */
/**
 * Module handling the stats section (number of connectors/operations/errors).
 * @exports statController
 */
define('statController', ['SHARED/jquery', 'indexingManagementApi', 'appBroadcaster'],
    function($, indexingManagementApi, appBroadcaster) {

        //Service
        var myIndexingManagementApi = new indexingManagementApi();
        //Event broadcaster
        var myAppBroadcaster;

        var statController = function statController() {
            var self = this;

            /**
             * Initialize the Stat Controller
             *
             * @param {broadcaster} appBroadcaster the appBroadcaster responsible to spread the event to other controller
             * @return void
             */
            self.init = function(appBroadcaster) {

                myAppBroadcaster = appBroadcaster;

                //Init stats value
                self.refreshStatNbConnector();
                self.refreshStatNbOperation();
                self.refreshStatNbError();

                //Set refresh interval for operations stats to 5 seconds
                setInterval(function(){
                    self.refreshStatNbOperation();
                }, 5000);

            }

            /**
             * Refresh the Indexing connector number
             *
             * @return void
             */
            self.refreshStatNbConnector = function() {
                updateStatNbConnectorValue()
            }

            /**
             * Refresh the Indexing operation number
             *
             * @return void
             */
            self.refreshStatNbOperation = function() {
                updateStatNbOperationValue()
            }

            /**
             * Refresh the Indexing error number
             *
             * @return void
             */
            self.refreshStatNbError = function() {
                updateStatNbErrorValue()
            }

        }

        /**
         * Update the connector number vlue with latest value
         *
         * @return void
         */
        function updateStatNbConnectorValue() {
            myIndexingManagementApi.getConnectors(null, true, updateStatNbConnectorUi);
        }

        /**
         * Update the operation number value with latest value
         *
         * @return void
         */
        function updateStatNbOperationValue() {
            myIndexingManagementApi.getOperations(null, 0, 0, true, updateStatNbOperationUi);
        }

        /**
         * Update the error number value with latest value
         *
         * @return void
         */
        function updateStatNbErrorValue() {
            //TODO when Error management will be implemented
            updateStatNbErrorUi(null);
        }


        /**
         * Manipulate the DOM to display the connector number value
         *
         * @param {json} json a JSON containing the size of connectors
         * @return void
         */
        function updateStatNbConnectorUi(json) {
            $('#statNbConnector').text(json.size);
        }

        /**
         * Manipulate the DOM to display the operation number value
         *
         * @param {json} json a JSON containing the size of operations
         * @return void
         */
        function updateStatNbOperationUi(json) {
            $('#statNbOperation').text(json.size);
        }

        /**
         * Manipulate the DOM to display the connector number value
         *
         * @param {json} json a JSON containing the size of errors
         * @return void
         */
        function updateStatNbErrorUi(json) {
            $('#statNbError').text('NA');
        }

        return statController;
    }
);
