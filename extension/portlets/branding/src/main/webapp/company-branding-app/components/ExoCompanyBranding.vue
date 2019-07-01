<template>
  <div class="uiBrandingPortlet">
    <div id="saveinfo" class="alert alert-success">
      <i class="uiIconSuccess"></i>{{ $t('info.saveok.label') }}
    </div>
    <div id="mustpng" class="alert">
      <i class="uiIconWarning"></i>{{ $t('mustpng.label') }}
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
            <a v-if="removeLogoButtonDisplayed" class="removeButton" @click="removeLogo"><i class="uiIconRemove"></i></a>
            <img id="previewLogoImg" :src="logoPreview" alt="">
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

      const reader = new FileReader();
      reader.onload = (e) => {
        this.branding.logo.data = e.target.result;
      };
      reader.readAsDataURL(files[0]);

      this.branding.logo.name = files[0].name;
      this.branding.logo.size = files[0].size;

      this.uploadFile(files[0]);
    },
    changePreviewStyle() {
      const topBarPreviewContainer = document.querySelector('#StylePreview #UIToolbarContainer');
      if(topBarPreviewContainer) {
        topBarPreviewContainer.setAttribute('class', `UIContainer UIToolbarContainer UIToolbarContainer${this.branding.topBarTheme}`);
      }
    },
    save() {
      this.cleanMessage();
      if(this.branding.logo.uploadId) {
        const logoName = this.branding.logo.name;
        const logoNameExtension = logoName.substring(logoName.lastIndexOf('.')+1, logoName.length) || logoName;
        if(logoNameExtension !== 'png') {
          this.$el.querySelector('#mustpng').style.display = 'block';
          this.branding.logo.data = [];
          this.branding.logo.uploadId = null;
          return;
        }
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
      this.$el.querySelector('#saveinfo').style.display = 'none';
      this.$el.querySelector('#mustpng').style.display = 'none';
    },
    uploadFile(data) {
      const formData = new FormData();               
      formData.append('file', data);
      const MAX_RANDOM_NUMBER = 100000;
      const uploadId = Math.round(Math.random() * MAX_RANDOM_NUMBER); 
      this.branding.logo.uploadId = uploadId;
      this.branding.logo.data = data;
      return fetch(`${brandingConstants.PORTAL}/upload?uploadId=${uploadId}&action=upload`, {
        method: 'POST',
        credentials: 'include',
        body: formData
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