/**
 * Module responsible to initialise the application.
 */
require(['SHARED/jquery', 'statController', 'connectorController', 'operationController', 'appBroadcaster'],
    function($, statController, connectorController, operationController, appBroadcaster){

        //Controller
        var myStatController = new statController();
        var myConnectorController = new connectorController();
        var myOperationController = new operationController();
        //Event broadcaster
        var myAppBroadcaster = new appBroadcaster();

        /**
         * Initiliaze all the controller when the doc is ready
         *
         */
        $(document).ready(
            function($) {

                //Init Stats
                myStatController.init(myAppBroadcaster);

                //Init Connector list
                myConnectorController.init(myAppBroadcaster);

                //Init Operation list
                myOperationController.init(myAppBroadcaster);

            }
        );

    }
);