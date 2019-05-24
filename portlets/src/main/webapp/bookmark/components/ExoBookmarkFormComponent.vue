<template>
  <div class="editZone clearfix">
    <div class="uiGrayLightBox pull-left">
      <div class="alert-error"></div>
      <input v-model="name" :placeholder="$t('bookmark.placeholder.name')" type="text" class="editName">
      <input v-model="link" :placeholder="$t('bookmark.placeholder.link')" type="text" class="editLink">
    </div>
    <button type="button" class="btn btn-primary pull-left save" @click="save">{{ $t(isNew ? 'bookmark.add' : 'bookmark.ok') }}</button>
    <button type="button" class="btn pull-left cancel" @click="cancel()">{{ $t('bookmark.cancel') }}</button>
  </div>
</template>
<script>
export default {
  props: {
    bookmark: {
      type: Object,
      default: null
    },
    isNew: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      name: '',
      link: ''
    };
  },
  created() {
    if (this.bookmark) {
      this.name = this.bookmark.name;
      this.link = this.bookmark.link;
    }
  },
  methods: {
    cancel() {
      this.$emit('cancel');
    },
    save() {
      this.$emit('save', {name: this.name, link: this.link});
    }
  }
};
</script>
