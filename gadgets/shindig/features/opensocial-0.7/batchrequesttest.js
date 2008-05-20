/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

var gadgets = gadgets || {};
var opensocial = opensocial || {};

function BatchRequestTest(name) {
  TestCase.call(this, name);
};
BatchRequestTest.inherits(TestCase);

BatchRequestTest.prototype.setUp = function() {
  // Prepare for mocks
  gadgets.io = gadgets.io || {};
  this.oldEncodeValues = gadgets.io.encodeValues;
  this.oldMakeNonProxiedRequest = gadgets.io.makeNonProxiedRequest;
};

BatchRequestTest.prototype.tearDown = function() {
  // Remove mocks
  gadgets.io.encodeValues = this.oldEncodeValues;
  gadgets.io.makeNonProxiedRequest = this.oldMakeNonProxiedRequest;
};

BatchRequestTest.prototype.testSend = function() {
  var expectedCallback = function() {};
  var request = new BatchRequest('path', 'jsonText', expectedCallback,
      {'extraParam1' : 'extraValue1'});

  // Mock out makeRequest methods
  gadgets.io.encodeValues = function(params) {
    return params;
  }

  var testcase = this;
  gadgets.io.makeNonProxiedRequest = function(actualPath, actualCallback,
      actualParams) {
    testcase.assertEquals('path', actualPath);
    testcase.assertEquals(expectedCallback, actualCallback);

    testcase.assertEquals('JSON', actualParams['CONTENT_TYPE']);
    testcase.assertEquals('POST', actualParams['METHOD']);
    testcase.assertEquals('SIGNED', actualParams['AUTHORIZATION']);

    var postData = actualParams['POST_DATA'];
    testcase.assertEquals('extraValue1', postData['extraParam1']);
    testcase.assertEquals('jsonText', postData['request']);
  }

  request.send();
};