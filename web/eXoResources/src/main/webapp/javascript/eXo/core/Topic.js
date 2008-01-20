
/** 
 * @constructor 
 */
function Topic(){
	this.topics = {};
	this.nextListenerInstanceId_ = 0;
}

/**
 * @private
 */
Topic.prototype._normalizeTopicName = function(/*String*/ topic){
	if(topic.charAt(topic.length - 1) != '/') {
		topic = topic + "/";
	}
	return topic;
}

/**
 * @private
 */
Topic.prototype._getNextListenerInstanceId = function() {
  return this.nextListenerInstanceId_++;
};

/**
 * publish is used to publish an event to the other subscribers to the given channels
 * @param {Object} senderId is a string that identify the sender
 * @param {String} topic is the topic that the message will be published
 * @param {Object} message is the message that's going to be delivered to the subscribers to the topic
 */
Topic.prototype.publish = function(/*Object*/ senderId, /*String*/ topicName, /*Object*/ message ) {
	topicName = this._normalizeTopicName(topicName);
	
	var event = {senderId:senderId, message:message, topic: topicName};
	
	for (var topic in this.topics) {
		if(topic && topicName.indexOf(topic) === 0)  {
			var callbacks = this.topics[topic];
			for (var j=0;j<callbacks.length;j++) {
				callback = callbacks[j];
				callback["func"](event);
			}
		}
	}
}

/**
 * isSubscribed is used to check if a function receive the events from a topic
 * @param {String} topic The topic.
 * @param {Function} func is the name of the function of obj to call when a message is received on the topic
 */
Topic.prototype.isSubscribed = function(/*String*/ topic, /*Function*/ func) {
	topic = this._normalizeTopicName(topic);
	callbacks = this.topics[topic];
	if(!callbacks) 
		return false;
		
	for (var i=0;i<callbacks.length;i++) {
		callback = callbacks[i];

		if (callback["func"] == func) {
			return true;
		}
	}
	return false;
}

/**
 * subscribe is used to subscribe a callback to a topic
 * @param {String} topic is the topic that will be listened
 * @param {Function} func is the name of the function of obj to call when a message is received on the topic
 * 
 * func is a function that take a Object in parameter. the event received have this format:
 * {senderId:senderId, message:message, topic: topic}
 *
 */
Topic.prototype.subscribe = function(/*String*/ topic, /*Function*/ func) {
	topic = this._normalizeTopicName(topic);
	if (this.isSubscribed(topic, func))
		return -1;
	if(!this.topics[topic]) {
		this.topics[topic] = new Array();
	}
	var id = this._getNextListenerInstanceId();
	this.topics[topic][this.topics[topic].length] = {id: id, func:func};
	return id;
}

/**
 * unsubscribe is used to unsubscribe a callback to a topic
 * @param {String} topic is the topic
 * @param {Object} id is the id of the listener we want to unsubscribe
 */
Topic.prototype.unsubscribe = function(/*String*/ topic, /*Object*/ id) {
	topic = this._normalizeTopicName(topic);
	callbacks = this.topics[topic];
	if(!callbacks) 
		return false;
	var removed = false;
	for (var i=0;i<callbacks.length;i++) {
		callback = callbacks[i];
		
		// if we removed an item, we move them to not let a blank in the middle
		if(removed) {
			callbacks[i - 1] = callbacks[i];
		}
		if (callback["id"] == id) {
			delete callbacks[i];
			removed = true;
		}
	}
	//  if a callback has been removed, we delete the last since we moved all the callback
	if(removed) {
		callbacks.length = callbacks.length - 1;
	}
	return removed;
}

eXo.core.Topic = new Topic();