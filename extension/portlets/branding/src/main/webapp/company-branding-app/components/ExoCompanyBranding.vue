<template>
  <div class="uiBrandingPortlet">
    <div id="mustpng" class="alert">
      <i class="uiIconWarning"></i>{{ $t('mustpng.label') }}
    </div>
    <div id="toobigfile" class="alert">
      <i class="uiIconWarning"></i>{{ $t('toobigfile.label') }}
    </div>
    <div id="savenotok" class="alert">
      <i class="uiIconWarning"></i>{{ $t('info.savenotok.label') }}
    </div>
    <div class="logoForm boxContent">
      <h4>{{ $t('companyName.label') }}</h4>
      <div>
        <input id="companyNameInput" v-model="branding.companyName" :placeholder="$t('companyName.placeholder')" type="text" name="formOp" value="">
      </div> 
      <h4>
        {{ $t('selectlogo.label') }}
      </h4>
      <div class="clearfix">
        <div class="pull-left">
          <div class="info">
            {{ $t('noteselectlogo.label') }}
          </div>
          <div class="fileDrop">
            <div ref="dropFileBox" class="dropZone">
              <label class="dropMsg" for="attachLogo">
                <i class="uiIcon attachFileIcon"></i> {{ $t('attachment') }}
              </label>
              <input id="attachLogo" type="file" class="attachFile" name="file" @change="onFileChange">
            </div>
          </div>                      
        </div>
        <div class="pull-left">
          <div id="previewLogo" class="previewLogo">
            <a v-if="removeLogoButtonDisplayed" :title="$t('delete.label')" class="removeButton" @click="removeLogo"><i class="uiIconRemove"></i></a>
            <img id="previewLogoImg" :src="logoPreview" alt="">
          </div>
          <div v-if="uploadInProgress" :class="[uploadProgress === 100 ? 'upload-completed': '']" class="progress progress-striped pull-left">
            <div :style="'width:' + uploadProgress + '%'" class="bar">{{ uploadProgress }}%</div>
          </div>
        </div>
      </div>
      <div class="navigationStyle boxContent">
        <h4>
          {{ $t('selectstyle.label') }}
        </h4>

        <div id="navigationStyle" class="btn-group uiDropdownWithIcon">
          <div class="control-group">
            <label class="uiRadio">
              <input v-model="branding.topBarTheme" class="radio" type="radio" value="Dark" @change="changePreviewStyle"> <span>{{ $t('style.dark.label') }}</span>
            </label>
            <label class="uiRadio">
              <input v-model="branding.topBarTheme" class="radio" type="radio" value="Light" @change="changePreviewStyle"> <span>{{ $t('style.light.label') }}</span>
            </label>
          </div>
        </div>
      </div>    
      <div class="preview boxContent">
        <div id="StylePreview">
        </div>
      </div>
      <div class="uiAction boxContent">
        <button id="save" :disabled="!branding.companyName || !branding.companyName.trim()" class="btn btn-primate" type="button" @click="save">
          {{ $t('save.label') }}
        </button>
        <button id="cancel" class="btn" type="button" @click="cancel">
          {{ $t('cancel.label') }}
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { brandingConstants }  from '../companyBrandingConstants';
import * as  brandingServices  from '../companyBrandingServices';
import axios from 'axios';

export default {
  data(){
    return {
      branding: {
        id: null,
        companyName: null,
        topBarTheme: null,
        logo: {
          uploadId: null,
          data: [],
          size: 0,
        }
      },
      defaultLogo: null,
      uploadInProgress: false,
      uploadProgress: 0,
      maxFileSize: 2097152,
      informationLoaded: false
    };
  },
  computed: {
    logoPreview: function() {
      if(this.informationLoaded) {
        if(this.branding.logo.data == null || !this.branding.logo.data.length) {
          if(this.defaultLogo != null) {
            return this.convertImageDataAsSrc(this.defaultLogo);
          } else {
            return `${brandingConstants.HOMEICON}`;
          }
        } else if(Array.isArray(this.branding.logo.data)) {
          return this.convertImageDataAsSrc(this.branding.logo.data);
        } else {
          return this.branding.logo.data;
        }
      }
    },
    removeLogoButtonDisplayed: function() {
      return this.branding.logo.uploadId || this.branding.logo.data != null && this.branding.logo.data.length > 0;
    }
  },
  created() {
    this.initBrandingInformation();
  },
  mounted() {
    // init top bar preview
    $('#PlatformAdminToolbarContainer').clone().attr('id', 'PlatformAdminToolbarContainer-preview').appendTo($('#StylePreview'));
    const toolbarPreview = $('#StylePreview #PlatformAdminToolbarContainer-preview');
    ['hover', 'click', 'blur'].forEach(evt => {
      toolbarPreview.bind(evt, (e) => {
        e.stopPropagation();
        e.preventDefault();
      });
    });

    // init file drop zone
    ['drag', 'dragstart', 'dragend', 'dragover', 'dragenter', 'dragleave', 'drop'].forEach( function( evt ) {
      this.$refs.dropFileBox.addEventListener(evt, function(e) {
        e.preventDefault();
        e.stopPropagation();
      }.bind(this), false);
    }.bind(this));

    this.$refs.dropFileBox.addEventListener('drop', function(e) {
      this.onFileChange(e);
    }.bind(this));
  },
  methods: {
    convertImageDataAsSrc(imageData) {
      let binary = '';
      const bytes = new Uint8Array(imageData);
      bytes.forEach(byte => binary += String.fromCharCode(byte));
      return `data:image/png;base64,${btoa(binary)}`;
    },
    onFileChange(e) {
      const files = e.target.files || e.dataTransfer.files;
      if (!files.length) {
        return;
      }

      if(!this.isLogoFileExtensionValid(files[0])) {
        this.$el.querySelector('#mustpng').style.display = 'block';
        return;
      }

      if(!this.isLogoFileSizeValid(files[0])) {
        this.$el.querySelector('#toobigfile').style.display = 'block';
        return;
      }

      const reader = new FileReader();
      reader.onload = (e) => {
        this.branding.logo.data = e.target.result;
      };
      reader.readAsDataURL(files[0]);

      this.branding.logo.name = files[0].name;
      this.branding.logo.size = files[0].size;

      this.uploadFile(files[0]);
    },
    isLogoFileExtensionValid(logoFile) {
      const logoName = logoFile.name;
      const logoNameExtension = logoName.substring(logoName.lastIndexOf('.')+1, logoName.length) || logoName;
      return logoNameExtension.toLowerCase() === 'png';
    },
    isLogoFileSizeValid(logoFile) {
      const logoSize = logoFile.size;
      return logoSize <= this.maxFileSize;
    },
    changePreviewStyle() {
      const topBarPreviewContainer = document.querySelector('#StylePreview #UIToolbarContainer');
      if(topBarPreviewContainer) {
        topBarPreviewContainer.setAttribute('class', `UIContainer UIToolbarContainer UIToolbarContainer${this.branding.topBarTheme}`);
      }
    },
    save() {
      this.cleanMessage();
      if(this.branding.logo.uploadId && !this.isLogoFileExtensionValid(this.branding.logo)) {
        this.$el.querySelector('#mustpng').style.display = 'block';
        this.branding.logo.data = [];
        this.branding.logo.uploadId = null;
        return;
      }
      this.changePreviewStyle();
      brandingServices.updateBrandingInformation(this.branding).then(() => document.location.reload(true));
    },
    cancel() {
      document.location.href = brandingConstants.PORTAL;
    },
    initBrandingInformation() {
      this.informationLoaded = false;
      const brandingInformationPromise = brandingServices.getBrandingInformation().then(data => {
        this.branding.companyName = data.companyName;
        this.branding.topBarTheme = data.topBarTheme;
        if(data.logo) {
          this.branding.logo = data.logo;
        }
      });

      const defaultLogoPromise = brandingServices.getBrandingDefaultLogo().then(data => {
        this.defaultLogo = data;
      });

      Promise.all([brandingInformationPromise, defaultLogoPromise]).then(() => {
        this.informationLoaded = true;
      });
    },
    cleanMessage() {
      this.$el.querySelector('#savenotok').style.display = 'none';
      this.$el.querySelector('#mustpng').style.display = 'none';
      this.$el.querySelector('#toobigfile').style.display = 'none';
    },
    uploadFile(data) {
      const formData = new FormData();               
      formData.append('file', data);
      const MAX_RANDOM_NUMBER = 100000;
      const uploadId = Math.round(Math.random() * MAX_RANDOM_NUMBER); 
      this.branding.logo.uploadId = uploadId;
      this.branding.logo.data = data;

      const maxProgress = 100;

      this.uploadInProgress = true;

      const self = this;
      // Had to use axios here since progress observation is still not supported by fetch
      axios.request({
        method: 'POST',
        url: `${brandingConstants.PORTAL}/upload?uploadId=${uploadId}&action=upload`,
        credentials: 'include',
        data: formData,
        onUploadProgress: (progress) => {
          this.uploadProgress = Math.round(progress.loaded * maxProgress / progress.total);
        }
      }).then(() => {
        // Check if the file has correctly been uploaded (progress=100) before refreshing the upload list
        const progressUrl = `${brandingConstants.PORTAL}/upload?action=progress&uploadId=${uploadId}`;
        fetch(progressUrl)
          .then(response => response.text())
          .then(responseText => {
            // TODO fix malformed json from upload service
            let responseObject;
            try {
              // trick to parse malformed json
              eval(`responseObject = ${responseText}`); // eslint-disable-line no-eval
            } catch (err) {
              return;
            }

            if(!responseObject.upload[uploadId] || !responseObject.upload[uploadId].percent ||
              responseObject.upload[uploadId].percent !== maxProgress.toString()) {
              self.$el.querySelector('#savenotok').style.display = 'block';
              self.uploadInProgress = false;
            } else {
              self.uploadProgress = maxProgress;
              self.$el.querySelector('.upload-completed').addEventListener('transitionend', function(e) {
                if(e.propertyName === 'visibility') {
                  self.uploadInProgress = false;
                  self.uploadProgress = 0;
                }
              }, true);
            }
          });
      });
    },
    removeLogo() {
      this.branding.logo.uploadId = null;
      this.branding.logo.data = [];
      this.branding.logo.size = 0;
    }
  }
};
</script>