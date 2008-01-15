
function Topic(){
	this.topics = {};
}

/**
 * Private
 */
Topic.prototype._normalizeTopicName = function(/*String*/ topic){
	if(topic.charAt(topic.length - 1) != '/') {
		topic = topic + "/";
	}
	return topic;
}

/**
 * publish is used to publish an event to the other subscribers to the given channels
 * parameter:senderId is a string that identify the sender
 * parameter:topic is the topic that the message will be published
 * parameter:message is the message that's going to be delivered to the subscribers to the topic
 */
Topic.prototype.publish = function(/*String*/ senderId, /*String*/ topicName, /*Object*/ message ) {
	topicName = this._normalizeTopicName(topicName);
	
	var event = {senderId:senderId, message:message, topic: topicName};
	
	for (var topic in this.topics) {
		if(topic && topicName.indexOf(topic) === 0)  {
			var callbacks = this.topics[topicName];
			for (var j=0;j<callbacks.length;j++) {
				callback = callbacks[j];
		
				//to call the function in the right context;
				callback["obj"][callback["funcName"]](event);
			}
		}
	}
}

/**
 * isSubscribed is used to check if a function receive the events from a topic
 * parameter:topic is the topic name
 * parameter:obj is the context object
 * parameter:funcName is the name of the function of obj to call when a message is received on the topic
 *
 * TODO: accept as funcName a real Function
 */
Topic.prototype.isSubscribed = function(/*String*/ topic, /*Object*/ obj, /*String*/ funcName) {
	topic = this._normalizeTopicName(topic);
	callbacks = this.topics[topic];
	if(!callbacks) 
		return false;
		
	for (var i=0;i<callbacks.length;i++) {
		callback = callbacks[i];

		if (callback["obj"] == obj && callback["funcName"] == funcName ) {
			return true;
		}
	}
	return false;
}

/**
 * subscribe is used to subscribe a callback to a topic
 * parameter:topic is the topic that will be listened
 * parameter:obj is the context object
 * parameter:funcName is the name of the function of obj to call when a message is received on the topic
 * 
 * funcName have to be a function that take a Object in parameter. the event received have this format:
 * {senderId:senderId, message:message, topic: topic}
 *
 * TODO: accept as funcName a real Function
 */
Topic.prototype.subscribe = function(/*String*/ topic, /*Object*/ obj, /*String*/ funcName) {
	topic = this._normalizeTopicName(topic);
	if (this.isSubscribed(topic, obj, funcName))
		return false;
	if(!this.topics[topic]) {
		this.topics[topic] = new Array();
	}
	this.topics[topic][this.topics[topic].length] = {obj: obj, funcName:funcName};
	return true;
}

/**
 * unsubscribe is used to unsubscribe a callback to a topic
 * parameter:topic is the topic that will be unsubscribe
 * parameter:obj is the context object
 * parameter:funcName is the name of the function of obj to call when a message is received on the topic
 *
 * TODO: accept as funcName a real Function
 */
Topic.prototype.unsubscribe = function(/*String*/ topic, /*Object*/ obj, /*String*/ funcName) {
	topic = this._normalizeTopicName(topic);
	callbacks = this.topics[topic];
	if(!callbacks) 
		return false;
	var removed = false
	for (var i=0;i<callbacks.length;i++) {
		callback = callbacks[i];
		
		// if we removed an item, we move them to not let a blank in the middle
		if(removed) {
			callbacks[i - 1] = callbacks[i];
		}
		if (callback["obj"] == obj && callback["funcName"] == funcName ) {
			delete callbacks[i];
			alert("callback removed");
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