<?
/*
 * I really detest such config files to be honest, why put configuration in a web document! 
 * But since PHP lacks a propper way to set application configurations, and any other method 
 * would be horribly slow (db, xml, ini files etc), so ... here's our config.php
 */
$shindigConfig = array(
	// Show debug backtrace? Set this to false on anything that resembles a production env
	'debug' => false,

	// The base prefix under which the our url's live, if its the root set this to ''
	// don't forget to update your .htaccess to reflect this, as well as your container 
	// javascript like: gadget.setServerBase('/someBaseUrl/');
	'web_prefix' => '',

	// Max age of a security token, defaults to one hour
	'st_max_age' => 60 * 60,

	// Security token keys
	'token_cipher_key' => 'INSECURE_DEFAULT_KEY',
	'token_hmac_key' => 'INSECURE_DEFAULT_KEY',

	// The html / javascript samples use a plain text demo token,
	// set this to false on anything resembling a real site
	'allow_plaintext_token' => true,
	
	// P3P (Platform for Privacy Preferences) header for allowing cross domain cookies.
	// Setting this to an empty string: '' means no P3P header will be send
	'P3P' => 'CP="CAO PSA OUR"',

	// location of the features directory on disk. The default setting assumes you did a 
	// full checkout of the shindig project, and not just the php part.
	// Otherwise also checkout the features, config and javascript directories and set
	// these to their locations
	'features_path' => realpath(dirname(__FILE__)) . '/../features/',
	'container_path' => realpath(dirname(__FILE__)) . '/../config/',
	'javascript_path' => realpath(dirname(__FILE__)) . '/../javascript/', 
	'container_config' => realpath(dirname(__FILE__)) . '/../config/container.js',

	// The data handlers for the social data, this is a list of class names
	// seperated by a , For example:
	//'handlers' => 'PartuzaHandler',
	// if the value is empty, the defaults used in the example above will be used.
	'handlers' => '',

	'focedJsLibs' => '',
	
	// The PHP gadget server can compress feature javascript libraries on the fly,
	// set this command to the javascript compressor you want to to use, we advice
	// using yuicompressor (http://developer.yahoo.com/yui/compressor/) but many others
	// will work too. When building your command, use %1 as the input file, and %2 
	// as the output file. Leave empty if you don't want this functionality.
	//
	// Config example for using the yuicompressor:
	//'compress_command' => "java -jar " . realpath(dirname(__FILE__)) . "/yuicompressor-2.3.5.jar -o %2\$s %1\$s",
	'compress_command' => '',
	
	// Configurable classes to use, this way we provide extensibility for what 
	// backends the gadget server uses for its logic functionality. 
	'blacklist_class' => 'BasicGadgetBlacklist',
	'remote_content' => 'BasicRemoteContent',
	'gadget_signer' => 'BasicGadgetTokenDecoder',
	'gadget_token' => 'BasicGadgetToken',
	'data_cache' => 'CacheFile', 
	
	// gadget server specific settings
	'userpref_param_prefix' => 'up_',
	'libs_param_name' => 'libs',
	// location  of the javascript handler (include the full path), default this is /gadgets/js
	'default_js_prefix' => '/gadgets/js/',
	// location of the gadget iframe renderer, default this is /gadgets/ifr?
	'default_iframe_prefix' => '/gadgets/ifr?', 
	
	// if your using memcached, these values are used for locating the server
	// if your not using memcached, ignore these values
	'cache_host' => 'localhost',
	'cache_port' => 11211, 
	
	// global cache age policy and location
	'cache_time' => 24 * 60 * 60,
	'cache_root' => '/tmp/shindig', 
	
	// In some cases we need to know the site root (for features forinstance)
	'base_path' => realpath(dirname(__FILE__))
);

class ConfigException extends Exception {}

/**
 * Abstracts how to retrieve configuration values so we can replace the
 * not so pretty $config array some day.
 *
 */
class Config {
	static function get($key)
	{
		global $shindigConfig;
		if (isset($shindigConfig[$key])) {
			return $shindigConfig[$key];
		} else {
			throw new ConfigException("Invalid Config Key");
		}
	}
}
