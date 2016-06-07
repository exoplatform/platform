/*
 * @javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavascriptClientCodegen", date = "2015-12-09T15:57:02.471+07:00")
 */

define('indexingManagementApi', ['SHARED/jquery', 'apiClient'],
    function($, apiClient) {

        var VindexingManagementApi = function VindexingManagementApi() {
            var self = this;

            var myApiClient = new apiClient();

            /**
             * Return all Indexing Connectors
             *
             * @param {String}  jsonp The name of a JavaScript function to be used as the JSONP callback
             * @param {Boolean}  returnSize Tell the service if it must return the size of the collection in the store
             * @param {function} callback the callback function
             * @return void
             */
            self.getConnectors = function(jsonp, returnSize, callback) {

                var postBody = null;
                var postBinaryBody = null;

                // create path and map variables
                var basePath = '/rest/private/';
                // if basePath ends with a /, remove it as path starts with a leading /
                if (basePath.substring(basePath.length-1, basePath.length)=='/') {
                    basePath = basePath.substring(0, basePath.length-1);
                }

                var path = basePath + replaceAll(replaceAll("/v1/indexingManagement/connectors", "\\{format\\}","json"));

                var queryParams = {};
                var headerParams =  {};
                var formParams =  {};


                if (jsonp != null) queryParams.jsonp = jsonp;

                queryParams.returnSize = returnSize;




                path += createQueryString(queryParams);

                //if (console) {
                //console.log('path: ' + path);
                //console.log('queryParams: ' + queryParams);
                //}





                myApiClient.invokeAPI(path, "GET", queryParams, postBody, postBinaryBody, headerParams, formParams, null, null, null, callback);




            }

            /**
             * Return the Indexing Connectors with the specified Connector Type
             *
             * @param {String}  connectorType Type of the Indexing Connector to retrieve
             * @param {String}  jsonp The name of a JavaScript function to be used as the JSONP callback
             * @param {function} callback the callback function
             * @return void
             */
            self.getConnector = function(connectorType, jsonp, callback) {

                var postBody = null;
                var postBinaryBody = null;

                // verify the required parameter 'connectorType' is set
                if (connectorType == null) {
                    //throw new ApiException(400, "Missing the required parameter 'connectorType' when calling getConnector");
                    var errorRequiredMsg = "Missing the required parameter 'connectorType' when calling getConnector";
                    throw errorRequiredMsg;
                }

                // create path and map variables
                var basePath = '/rest/private/';
                // if basePath ends with a /, remove it as path starts with a leading /
                if (basePath.substring(basePath.length-1, basePath.length)=='/') {
                    basePath = basePath.substring(0, basePath.length-1);
                }

                var path = basePath + replaceAll(replaceAll("/v1/indexingManagement/connectors/{connectorType}", "\\{format\\}","json")
                        , "\\{" + "connectorType" + "\\}", myApiClient.escapeString(connectorType.toString()));

                var queryParams = {};
                var headerParams =  {};
                var formParams =  {};


                if (jsonp != null) queryParams.jsonp = jsonp;




                path += createQueryString(queryParams);

                //if (console) {
                //console.log('path: ' + path);
                //console.log('queryParams: ' + queryParams);
                //}





                myApiClient.invokeAPI(path, "GET", queryParams, postBody, postBinaryBody, headerParams, formParams, null, null, null, callback);




            }

            /**
             * Update an Indexing Connector to enable / disable it
             *
             * @param {String}  connectorType Type of the Indexing Connector to update
             * @param {AnIndexingConnectorResources}  body An Indexing Connector Resource
             * @param {function} callback the callback function
             * @return void
             */
            self.updateConnector = function(connectorType, body, callback) {

                var postBody = JSON.stringify(body);
                var postBinaryBody = null;

                // verify the required parameter 'connectorType' is set
                if (connectorType == null) {
                    //throw new ApiException(400, "Missing the required parameter 'connectorType' when calling updateConnector");
                    var errorRequiredMsg = "Missing the required parameter 'connectorType' when calling updateConnector";
                    throw errorRequiredMsg;
                }

                // verify the required parameter 'body' is set
                if (body == null) {
                    //throw new ApiException(400, "Missing the required parameter 'body' when calling updateConnector");
                    var errorRequiredMsg = "Missing the required parameter 'body' when calling updateConnector";
                    throw errorRequiredMsg;
                }

                // create path and map variables
                var basePath = '/rest/private/';
                // if basePath ends with a /, remove it as path starts with a leading /
                if (basePath.substring(basePath.length-1, basePath.length)=='/') {
                    basePath = basePath.substring(0, basePath.length-1);
                }

                var path = basePath + replaceAll(replaceAll("/v1/indexingManagement/connectors/{connectorType}", "\\{format\\}","json")
                        , "\\{" + "connectorType" + "\\}", myApiClient.escapeString(connectorType.toString()));

                var queryParams = {};
                var headerParams =  {};
                var formParams =  {};





                path += createQueryString(queryParams);

                //if (console) {
                //console.log('path: ' + path);
                //console.log('queryParams: ' + queryParams);
                //}





                myApiClient.invokeAPI(path, "PUT", queryParams, postBody, postBinaryBody, headerParams, formParams, null, null, null, callback);




            }

            /**
             * Return all Indexing Operations
             *
             * @param {String}  jsonp The name of a JavaScript function to be used as the JSONP callback
             * @param {Integer}  offset The starting point when paging through a list of entities
             * @param {Integer}  limit The maximum number of results when paging through a list of entities. If not specified or exceed the *query_limit* configuration of Indexing Management rest service, it will use the *query_limit*
             * @param {Boolean}  returnSize Tell the service if it must return the size of the collection in the store
             * @param {function} callback the callback function
             * @return void
             */
            self.getOperations = function(jsonp, offset, limit, returnSize, callback) {

                var postBody = null;
                var postBinaryBody = null;

                // create path and map variables
                var basePath = '/rest/private/';
                // if basePath ends with a /, remove it as path starts with a leading /
                if (basePath.substring(basePath.length-1, basePath.length)=='/') {
                    basePath = basePath.substring(0, basePath.length-1);
                }

                var path = basePath + replaceAll(replaceAll("/v1/indexingManagement/operations", "\\{format\\}","json"));

                var queryParams = {};
                var headerParams =  {};
                var formParams =  {};


                if (jsonp != null) queryParams.jsonp = jsonp;

                queryParams.offset = offset;

                queryParams.limit = limit;

                queryParams.returnSize = returnSize;




                path += createQueryString(queryParams);

                //if (console) {
                //console.log('path: ' + path);
                //console.log('queryParams: ' + queryParams);
                //}





                myApiClient.invokeAPI(path, "GET", queryParams, postBody, postBinaryBody, headerParams, formParams, null, null, null, callback);




            }

            /**
             * Add an Indexing Operation to the queue
             *
             * @param {AnIndexingOperationResources}  body An Indexing Operation Resource
             * @param {function} callback the callback function
             * @return void
             */
            self.addOperation = function(body, callback) {

                var postBody = JSON.stringify(body);
                var postBinaryBody = null;

                // verify the required parameter 'body' is set
                if (body == null) {
                    //throw new ApiException(400, "Missing the required parameter 'body' when calling addOperation");
                    var errorRequiredMsg = "Missing the required parameter 'body' when calling addOperation";
                    throw errorRequiredMsg;
                }

                // create path and map variables
                var basePath = '/rest/private/';
                // if basePath ends with a /, remove it as path starts with a leading /
                if (basePath.substring(basePath.length-1, basePath.length)=='/') {
                    basePath = basePath.substring(0, basePath.length-1);
                }

                var path = basePath + replaceAll(replaceAll("/v1/indexingManagement/operations", "\\{format\\}","json"));

                var queryParams = {};
                var headerParams =  {};
                var formParams =  {};





                path += createQueryString(queryParams);

                //if (console) {
                //console.log('path: ' + path);
                //console.log('queryParams: ' + queryParams);
                //}





                myApiClient.invokeAPI(path, "POST", queryParams, postBody, postBinaryBody, headerParams, formParams, null, null, null, callback);




            }

            /**
             * Delete all Indexing Operation
             *
             * @param {function} callback the callback function
             * @return void
             */
            self.deleteOperations = function(callback) {

                var postBody = null;
                var postBinaryBody = null;

                // create path and map variables
                var basePath = '/rest/private/';
                // if basePath ends with a /, remove it as path starts with a leading /
                if (basePath.substring(basePath.length-1, basePath.length)=='/') {
                    basePath = basePath.substring(0, basePath.length-1);
                }

                var path = basePath + replaceAll(replaceAll("/v1/indexingManagement/operations", "\\{format\\}","json"));

                var queryParams = {};
                var headerParams =  {};
                var formParams =  {};





                path += createQueryString(queryParams);

                //if (console) {
                //console.log('path: ' + path);
                //console.log('queryParams: ' + queryParams);
                //}





                myApiClient.invokeAPI(path, "DELETE", queryParams, postBody, postBinaryBody, headerParams, formParams, null, null, null, callback);




            }

            /**
             * Return the Indexing Operation with the specified Opertion Id
             *
             * @param {String}  operationId Id of the Indexing Operation to retrieve
             * @param {String}  jsonp The name of a JavaScript function to be used as the JSONP callback
             * @param {function} callback the callback function
             * @return void
             */
            self.getOperation = function(operationId, jsonp, callback) {

                var postBody = null;
                var postBinaryBody = null;

                // verify the required parameter 'operationId' is set
                if (operationId == null) {
                    //throw new ApiException(400, "Missing the required parameter 'operationId' when calling getOperation");
                    var errorRequiredMsg = "Missing the required parameter 'operationId' when calling getOperation";
                    throw errorRequiredMsg;
                }

                // create path and map variables
                var basePath = '/rest/private/';
                // if basePath ends with a /, remove it as path starts with a leading /
                if (basePath.substring(basePath.length-1, basePath.length)=='/') {
                    basePath = basePath.substring(0, basePath.length-1);
                }

                var path = basePath + replaceAll(replaceAll("/v1/indexingManagement/operations/{operationId}", "\\{format\\}","json")
                        , "\\{" + "operationId" + "\\}", myApiClient.escapeString(operationId.toString()));

                var queryParams = {};
                var headerParams =  {};
                var formParams =  {};


                if (jsonp != null) queryParams.jsonp = jsonp;




                path += createQueryString(queryParams);

                //if (console) {
                //console.log('path: ' + path);
                //console.log('queryParams: ' + queryParams);
                //}





                myApiClient.invokeAPI(path, "GET", queryParams, postBody, postBinaryBody, headerParams, formParams, null, null, null, callback);




            }

            /**
             * Delete a specified Indexing Operation
             *
             * @param {String}  operationId Id of the Indexing Operation to delete
             * @param {function} callback the callback function
             * @return void
             */
            self.deleteOperation = function(operationId, callback) {

                var postBody = null;
                var postBinaryBody = null;

                // verify the required parameter 'operationId' is set
                if (operationId == null) {
                    //throw new ApiException(400, "Missing the required parameter 'operationId' when calling deleteOperation");
                    var errorRequiredMsg = "Missing the required parameter 'operationId' when calling deleteOperation";
                    throw errorRequiredMsg;
                }

                // create path and map variables
                var basePath = '/rest/private/';
                // if basePath ends with a /, remove it as path starts with a leading /
                if (basePath.substring(basePath.length-1, basePath.length)=='/') {
                    basePath = basePath.substring(0, basePath.length-1);
                }

                var path = basePath + replaceAll(replaceAll("/v1/indexingManagement/operations/{operationId}", "\\{format\\}","json")
                        , "\\{" + "operationId" + "\\}", myApiClient.escapeString(operationId.toString()));

                var queryParams = {};
                var headerParams =  {};
                var formParams =  {};





                path += createQueryString(queryParams);

                //if (console) {
                //console.log('path: ' + path);
                //console.log('queryParams: ' + queryParams);
                //}





                myApiClient.invokeAPI(path, "DELETE", queryParams, postBody, postBinaryBody, headerParams, formParams, null, null, null, callback);




            }



            function replaceAll (haystack, needle, replace) {
                var result= haystack;
                if (needle !=null && replace!=null) {
                    result= haystack.replace(new RegExp(needle, 'g'), replace);
                }
                return result;
            }

            function createQueryString (queryParams) {
                var queryString ='';
                var i = 0;
                for (var queryParamName in queryParams) {
                    if (i==0) {
                        queryString += '?' ;
                    } else {
                        queryString += '&' ;
                    }

                    queryString +=  queryParamName + '=' + encodeURIComponent(queryParams[queryParamName]);
                    i++;
                }

                return queryString;
            }
        }

        return VindexingManagementApi;
    }
);
