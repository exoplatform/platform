<template>
  <li :class="{'editing': editing, 'editable': editable}" class="ListItem">
    <div class="clearfix">
      <div class="editHoverZone pull-right">
        <a class="linkeditHoverZone pull-right" href="javascript:void(0)" @click="remove()">
          <i class="uiIconDelete uiIconLightGray"></i>
        </a>
        <a class="linkeditHoverZone pull-right" href="javascript:void(0)" @click="toggleEdit()">
          <i class="uiIconEdit uiIconLightGray"></i>
        </a>
      </div>
      <a :href="bookmark.link" class="bookmarkTit">{{ bookmark.name }}</a>
    </div>
    <exo-bookmark-form v-if="editing"
                       :bookmark="bookmark"
                       @cancel="toggleEdit()"
                       @save="save"
    ></exo-bookmark-form>
  </li>
</template>
<script>
export default {
  props: {
    bookmark: {
      type: Object,
      default: null
    },
    editable: {
      type: Boolean,
      default: true
    }
  },
  data() {
    return {
      editing: false
    };
  },
  methods: {
    toggleEdit() {
      if (!this.editable && !this.editing) {
        return;
      }
      this.editing = !this.editing;
      this.$emit('toggleEdit', this.editing);
    },
    save(data) {
      this.$emit('update', data);
      this.editing = !this.editing;
    },
    remove() {
      this.$emit('remove');
    }
  }
};
</script>
