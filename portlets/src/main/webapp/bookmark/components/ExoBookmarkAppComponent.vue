<template>
  <div id="content" class="bookmark uiBox">
    <h6 class="title center">
      {{ $t('bookmark.title') }}
      <a id="customize" class="actionIcon pull-right" href="javascript:void(0);" @click="toggleAdd">
        <i class="uiIconPlus uiIconLightGray"></i>
      </a>
    </h6>

    <div v-if="errorMsg" class="alert-error">
      <i class="uiIconError"></i>&nbsp;{{ errorMsg }}
    </div>

    <div v-if="loaded" class="uiContentBox">
      <div v-if="bookmarks.length == 0">
        {{ $t('bookmark.emptyBookmarkPre') }}
      </div>
      <ul v-else>
        <exo-bookmark v-for="(bookmark, index) in bookmarks"
                      :key="bookmark.link"
                      :bookmark="bookmark"
                      :editable="editable"
                      @toggleEdit="toggleEdit"
                      @remove="remove(index)"
                      @update="update($event, index)"
        ></exo-bookmark>
      </ul>
      <exo-bookmark-form v-if="adding"
                         is-new="true"
                         @cancel="toggleAdd"
                         @save="update($event, -1)">
      </exo-bookmark-form>
    </div>
  </div>
</template>
<script>
import bookmarkService from '../bookmarkService';

export default {
  data() {
    return {
      loaded: false,
      adding: false,
      editable: true,
      bookmarks: [],
      errorMsg: ''
    };
  },
  created() {
    this.loadBookmarks();
  },
  methods: {
    loadBookmarks() {
      bookmarkService.loadBookmarks().then(bookmarks => {
        this.bookmarks = bookmarks;
        this.loaded = true;
      });
    },
    toggleEdit($event) {
      this.errorMsg = '';
      this.editable = !$event;
    },
    toggleAdd() {
      this.errorMsg = '';
      if (!this.editable && !this.adding) {
        return;
      }
      this.adding = !this.adding;
      this.editable = !this.adding;
    },
    save(bookmarks) {
      this.errorMsg = '';
      bookmarkService.saveBookmarks(bookmarks).then((bookmarks) => {
        this.bookmarks = bookmarks;
        this.loadBookmarks();
        this.errorMsg = '';
      }).catch(() => {
        this.errorMsg = this.$t('bookmark.saveErrorMsg');
      });
    },
    remove(idx) {
      const bookmarks = this.bookmarks.filter((bookmark, index) => index !== idx);
      this.save(bookmarks);
    },
    update(data, idx) {
      let bookmarks;
      if (idx < 0) {
        bookmarks = [...this.bookmarks, data];
      } else {
        bookmarks = this.bookmarks.map((bookmark, index) => {
          const bm = Object.assign({}, bookmark);
          if (idx === index) {
            bm.name = data.name;
            bm.link = data.link;
          }

          return bm;
        });
      }

      this.editable = true;
      this.adding = false;
      this.save(bookmarks);
    }
  }
};
</script>
