/**
 * Created by TClement on 12/14/15.
 */

define('apiClient', ['SHARED/jquery'],
    function($) {

        var apiClient = function apiClient() {
            var self = this;

            /**
             * Invoke API by sending HTTP request with the given options.
             *
             * @param path The sub-path of the HTTP URL
             * @param method The request method, one of "GET", "POST", "PUT", and "DELETE"
             * @param queryParams The query parameters
             * @param body The request body object - if it is not binary, otherwise null
             * @param binaryBody The request body object - if it is binary, otherwise null
             * @param headerParams The header parameters
             * @param formParams The form parameters
             * @param accept The request's Accept header
             * @param contentType The request's Content-Type header
             * @param authNames The authentications to apply
             * @param {function} callback the callback function
             * @return The response body in type of string
             */
            self.invokeAPI = function(path, method, queryParams, body, binaryBody, headerParams, formParams, accept, contentType, authNames, callback) {

                //TODO manage other parameters (header, form) and accept + contentType + basic authentication ?

                var request = {
                    url: path,
                    type: method,
                    cache: false,
                    dataType: 'json',
                    contentType: 'application/json; charset=UTF-8',
                    data: body,
                    error: function(jqXHR) {
                        //TODO manage error
                        console.log("Ajax error " + jqXHR.status);
                    },
                    success: function(response) {
                        callback(response);
                    }
                };
                $.ajax(request);

            }

            /**
             * Escape the given string to be used as URL query value.
             */
            self.escapeString = function(str) {
                //TODO escape string
                return str;
            }


        }

        return apiClient;
    }
);