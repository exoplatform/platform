<template>
  <div id="OnlinePortlet" class="onlinePortlet">
    <div id="onlineContent" class="uiBox">
      <h6 class="title center">{{ $t("platform.portlet.whoIsOnline.title") }}</h6>
      <ul id="onlineList" class="gallery uiContentBox">
        <li v-for="user in users" :key="user" :id="user.id">
          <a :href="user.profileUrl" class="avatarXSmall"><img :src="user.avatar" alt="image" /></a>
        </li>
      </ul>
    </div>
  </div>
</template>

<script>
  import * as whoIsOnlineServices from '../whoIsOnlineServices';

  export default {
    data() {
      return {
        users: []
      };
    },
    created() {
      this.getUsers();
    },
    methods: {
      getUsers() {
        whoIsOnlineServices.getOnlineUsers(eXo.env.portal.userName, eXo.env.portal.spaceName).then(response => {
          if (response) {
            this.users = response;
          }
        });
      }
    }
  };
</script>