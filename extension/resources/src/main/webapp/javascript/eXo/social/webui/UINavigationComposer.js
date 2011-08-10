/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */

/**
 * UINavigationComposer.js
 * 
 * Requires: eXo.social.Util
 *
 */


(function () {
  var Util = eXo.social.Util;
  var window_ = this;

  function UINavigationComposer(params) {
    this.configure(params);
    this.init();
  }

  UINavigationComposer.prototype.configure = function(params) {
    this.composerId = params.composerId || 'composerInput';
    this.defaultInput = params.defaultInput || "";
    this.minCharactersRequired = params.minCharactersRequired || 0;
    this.maxCharactersAllowed = params.maxCharactersAllowed || 0;
    this.focusColor = params.focusColor || '#000000';
    this.blurColor = params.blurColor || '#777777';
    this.minHeight = params.minHeight || '20px';
    this.focusHeight = params.focusHeight || '35px';
    this.maxHeight = params.maxHeight || '50px';
    this.focusCallback = params.focusCallback;
    this.blurCallback = params.blurCallback;
    this.keypressCallback = params.keypressCallback;
    this.postMessageCallback = params.postMessageCallback;
    this.userTyped = false;
  }


  UINavigationComposer.prototype.init = function() {
    this.composer = Util.getElementById(this.composerId);
    //this.shareButton = Util.getElementById('ShareButton');
    //if (!(this.composer && this.shareButton)) {
    //  alert('error: can not find composer or shareButton!');
    //}

    this.composer.value = this.defaultInput;
    this.composer.style.height = this.minHeight;
    this.composer.style.color = this.blurColor;
    //this.shareButton.style.background = 'white'
    //this.shareButton.disabled = true;
    this.currentValue = this.composer.value;
    var uiComposer = this;
    var isReadyEl = document.getElementById("isReadyId");
    var composerContainerEl = document.getElementById("ComposerContainer");
   	var isReadyVal;
    Util.addEventListener(this.composer, 'focus', function() {
      if (uiComposer.composer.value === uiComposer.defaultInput) {
        uiComposer.composer.value = '';
      }
      if (uiComposer.focusCallback) {
        uiComposer.focusCallback();
      }
      uiComposer.composer.style.height = uiComposer.maxHeight;
      uiComposer.composer.style.color = uiComposer.focusColor;     
    }, false);

    Util.addEventListener(this.composer, 'blur', function() {
      if (uiComposer.composer.value === '') {
        uiComposer.composer.value = uiComposer.defaultInput;
        uiComposer.composer.style.height = uiComposer.minHeight;
        uiComposer.composer.style.color = uiComposer.blurColor;

        //if current composer is default composer then disable share button
        /*if(document.getElementById("ComposerContainer") == null){
          uiComposer.shareButton.disabled = true;
        }*/

      } else {
        	uiComposer.currentValue = uiComposer.composer.value;
      }
      
      if (uiComposer.blurCallback) {
        uiComposer.blurCallback();
      }
    }, false);

    Util.addEventListener(this.composer, 'keypress', function() {
      /*if (uiComposer.minCharactersRequired !== 0) {
        //TODO hoatle handle backspace problem
        if (uiComposer.composer.value.length >= uiComposer.minCharactersRequired) {
          uiComposer.shareButton.style.background = 'white';
          if(document.getElementById("ComposerContainer") == null){
            uiComposer.shareButton.disabled = false;
          }
        } else {
          uiComposer.shareButton.style.background = '';
        }
      } else {
        uiComposer.shareButton.style.background = 'white';
        if(document.getElementById("ComposerContainer") == null){
          uiComposer.shareButton.disabled = false;
        }
      }*/
      
      if (uiComposer.maxCharactersAllowed !== 0) {
        if (uiComposer.composer.value.length >= uiComposer.maxCharactersAllowed) {
          //substitue it
          //TODO hoatle have a countdown displayed on the form
          uiComposer.composer.value = uiComposer.composer.value.substring(0, uiComposer.maxCharactersAllowed);
        }
      }
      if (uiComposer.keypressCallback) {
        uiComposer.keypressCallback();
      }
    }, false);
  }

  UINavigationComposer.prototype.getValue = function() {
  	if (!this.currentValue) {
  		return this.defaultInput;
  	}
    return this.currentValue;
  }

  UINavigationComposer.prototype.setCurrentValue = function() {
  	var uiInputText = Util.getElementById(this.composerId);
	  this.currentValue = uiInputText.value;
  }
  
  //expose
  window_.eXo = window_.eXo || {};
  window_.eXo.social = window_.eXo.social || {};
  window_.eXo.social.webui = window_.eXo.social.webui || {};
  window_.eXo.social.webui.UINavigationComposer = UINavigationComposer;
})();
