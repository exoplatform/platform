<template>
  <div id="OnlinePortlet" class="onlinePortlet">
    <div id="onlineContent" class="uiBox">
      <h6 class="title center">{{ $t("header.label") }}</h6>
      <ul id="onlineList" class="gallery uiContentBox">
        <li v-for="user in users" :key="user" :id="user.id">
          <a :href="user.href" class="avatarXSmall"><img :src="user.avatar" alt="image" /></a>
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
      this.initOnlineUsers();
      // And we should use setInterval with 60 seconds
      setInterval(function () {
        this.initOnlineUsers();
      }.bind(this), 60000);
    },
    methods: {
      initOnlineUsers() {
        whoIsOnlineServices.getOnlineUsers(eXo.env.portal.spaceName).then(response => {
          if (response) {
            this.users = response.users;
          }
        });
      }
    }
  };
</script>