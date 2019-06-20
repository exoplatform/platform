<template>
  <div class="uiBrandingPortlet">
    <div id="cancelinfo" class="alert">
      <i class="uiIconWarning"></i>{{ $t('info.cancelok.label') }}
    </div>
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
        <input v-model="companyName" :placeholder="$t('companyName.placeholder=')" type="text" name="formOp" value="">
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
            <div class="dropZone">
              <label class="dropMsg" for="attachLogo">
                <i class="uiIcon attachFileIcon"></i> {{ $t('attachment') }}
              </label>
              <input id="attachLogo" type="file" class="attachFile" name="file" @change="onFileChange">
            </div>
          </div>                      
        </div>
        <div class="pull-left">
          <div id="PreviewImgDiv" class="previewLogo">
            <img id="ajaxUploading1" alt="" :src="loader" style="display:none">
            <img id="PreviewImg" :src="image" alt="">
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
              <input v-model="styleNavigation" class="radio" type="radio" value="Dark" @change="changePreviewStyle"> <span>{{ $t('style.dark.label') }}</span>
            </label>
            <label class="uiRadio">
              <input v-model="styleNavigation" class="radio" type="radio" value="Light" @change="changePreviewStyle"> <span>{{ $t('style.light.label') }}</span>
            </label>
          </div>
        </div>
      </div>    
      <div class="preview boxContent">
        <div id="StylePreview">
          <img id="ajaxUploading2" alt="" :src="loader" style="display:none">
        </div>
      </div>
      <div class="uiAction boxContent">
        <button id="save" class="btn btn-primate" type="button" @click="save">
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
import * as  brandingConstants  from '../companyBrandingConstants';
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
          src: null,
          size: 0,
        }
      },
      image: '',
      companyName: '',
      loader: brandingConstants.LOADER,
      styleNavigation: ''
    };
  },
  created() {
    this.getBrandingInformation();
  },
  mounted() {
    $('#PlatformAdminToolbarContainer').clone().attr('id', 'PlatformAdminToolbarContainer-preview').appendTo($('#StylePreview'));
    $('#StylePreview #PlatformAdminToolbarContainer-preview').bind({
      hover : function(e) {
        e.stopPropagation();
        e.preventDefault();
      },
      click : function(e) {
        e.stopPropagation();
        e.preventDefault();
      },
      blur : function(e) {
        e.stopPropagation();
        e.preventDefault();
      }
    });
  },
  methods: {
    onFileChange(e) {
      const files = e.target.files || e.dataTransfer.files;
      if (!files.length)
      {return;}
      this.createImage(files[0]);
      this.uploadFile(files[0]);
      this.branding.logo.size = files[0].size;
    },
    createImage(file) {
      const reader = new FileReader();
      reader.onload = (e) => {
        this.image = e.target.result;
      };
      reader.readAsDataURL(file);
    },
    changePreviewStyle() {
      document.querySelector('#StylePreview #UIToolbarContainer').setAttribute('class', `UIContainer UIToolbarContainer  UIToolbarContainer${  this.styleNavigation}`);
    },
    save(){
      this.cleanMessage();
      if(this.branding.logo.uploadId === null ) {
        this.saveAll();
      } else {
        const verificationPng = this.branding.logo.data.name.split('.');
        if(verificationPng[1] !== 'png') {
          document.getElementById('mustpng').style.display = 'block';
          this.branding.logo.data = [];
          this.branding.logo.uploadId = null;
        } else {
          this.saveAll();
        }
      }
    },
    saveAll(){
      this.branding.companyName = this.companyName;
      this.branding.topBarTheme = this.styleNavigation;
      this.branding.file = this.image;
      this.cleanMessage();
      this.changePreviewStyle();
      this.updateTopBarNavigation();
      brandingServices.updateBrandingInformation(this.branding).then(() => document.location.reload(true));
    },
    cancel(){
      this.getBrandingInformation();
      this.cleanMessage();
      document.getElementById('cancelinfo').style.display = 'block';
    },
    getBrandingInformation() {
      brandingServices.getBrandingInformation().then(data =>{
        this.companyName = data.companyName;
        this.styleNavigation = data.topBarTheme;
      });
    },
    cleanMessage() {
      document.getElementById('savenotok').style.display = 'none';
      document.getElementById('saveinfo').style.display = 'none';
      document.getElementById('cancelinfo').style.display = 'none';
      document.getElementById('mustpng').style.display = 'none';
    },
    updateTopBarNavigation() {
      $('#PlatformAdminToolbarContainer #UIToolbarContainer:first')
        .removeAttr('class');
      $('#PlatformAdminToolbarContainer #UIToolbarContainer:first').addClass(
        `UIContainer UIToolbarContainer  UIToolbarContainer${  this.styleNavigation}`);
    },
    uploadFile(data){
      const formData = new FormData();               
      formData.append('file', data);
      const MAX_RANDOM_NUMBER = 100000;
      const uploadId = Math.round(Math.random() * MAX_RANDOM_NUMBER); 
      this.branding.logo.uploadId = uploadId;
      this.branding.logo.data = data;
      return axios.post(`/portal/upload?uploadId=${uploadId}&action=upload`, formData,
        {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        });
    }
  }
};
</script>




