/**
 * Created by TClement on 12/17/15.
 */
/**
 * Module responsible to broadcast events to the controllers.
 * @exports appBroadcaster
 */
define('appBroadcaster', ['SHARED/jquery', 'operationController', 'statController', 'connectorController'],
    function($, operationController, statController, connectorController) {


        //Controller
        var myStatController = new statController();
        var myOperationController = new operationController();
        var myConnectorController = new connectorController();

        var appBroadcaster = function appBroadcaster() {
            var self = this;

            self.onReindexConnector = function() {
                myConnectorController.refreshConnectorList();
                myStatController.refreshStatNbOperation();
                myOperationController.refreshOperationList();
            }

            self.onDeleteOperation = function() {
                myOperationController.refreshOperationList();
                myStatController.refreshStatNbOperation();
            }

        }

        return appBroadcaster;
    }
);