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

function JsonActivityTest(name) {
  TestCase.call(this, name);
};
JsonActivityTest.inherits(TestCase);

JsonActivityTest.prototype.setUp = function() {
  // Prepare for mocks
  gadgets.util = gadgets.util || {};
  this.oldEscape = gadgets.util.escape;
  gadgets.util.escape = function(param) {
    return param;
  };
};

JsonActivityTest.prototype.tearDown = function() {
  // Remove mocks
  gadgets.util.escape = this.oldEscape;
};

JsonActivityTest.prototype.testConstructArrayObject = function() {
  var map = {'fakeClass' : [{'field1' : 'value1'}, {'field2' : 'value2'}]};
  FakeClass = function(opt_params) {
    this.fields = opt_params;
  }

  JsonActivity.constructArrayObject(map, 'fakeClass', FakeClass);

  var result = map['fakeClass'];
  this.assertTrue(result instanceof Array);
  this.assertTrue(result[0] instanceof FakeClass);
  this.assertTrue(result[1] instanceof FakeClass);
  this.assertEquals('value1', result[0].fields['field1']);
  this.assertEquals('value2', result[1].fields['field2']);
};

JsonActivityTest.prototype.testJsonActivityConstructor = function() {
  var activity = new JsonActivity({'title' : 'green',
    'mediaItems' : [{'mimeType' : 'black', 'url' : 'white',
      'type' : 'orange'}]});

  var fields = opensocial.Activity.Field;
  this.assertEquals('green', activity.getField(fields.TITLE));

  var mediaItems = activity.getField(fields.MEDIA_ITEMS);
  this.assertTrue(mediaItems instanceof Array);
  this.assertTrue(mediaItems[0] instanceof JsonMediaItem);

  var mediaItemFields = opensocial.Activity.MediaItem.Field;
  this.assertEquals('black', mediaItems[0].getField(mediaItemFields.MIME_TYPE));
  this.assertEquals('white', mediaItems[0].getField(mediaItemFields.URL));
  this.assertEquals('orange', mediaItems[0].getField(mediaItemFields.TYPE));
};

JsonActivityTest.prototype.testJsonActivityMediaItemConstructor = function() {
  var mediaItem = new JsonMediaItem({'mimeType' : 'black', 'url' : 'white',
      'type' : 'orange'});

  var fields = opensocial.Activity.MediaItem.Field;
  this.assertEquals('black', mediaItem.getField(fields.MIME_TYPE));
  this.assertEquals('white', mediaItem.getField(fields.URL));
  this.assertEquals('orange', mediaItem.getField(fields.TYPE));
};