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
import { exoConstants } from '../../js/eXoConstants.js';

export default {
  data() {
    return {
      users: []
    };
  },
  created() {
    this.initOnlineUsers();
    // And we should use setInterval with 60 seconds
    const delay = 60000;
    setInterval(function () {
      this.initOnlineUsers();
    }.bind(this), delay);
  },
  updated() {
    this.initPopup();
  },
  methods: {
    initOnlineUsers() {
      whoIsOnlineServices.getOnlineUsers(eXo.env.portal.spaceId).then(response => {
        let got;
        if (response) {
          got = response.users;
          if (got && got.length > 0) {
            this.users = [];
            for (const el of got) {
              el.href = `${exoConstants.PORTAL}/${exoConstants.PORTAL_NAME}/profile/${el.username}`;
              if (!el.avatar) {
                el.avatar = `${exoConstants.SOCIAL_USER_API}/${el.username}/avatar`;
              }
              this.users.push(el);
            }
            $('#OnlinePortlet').show();
          } else {
            $('#OnlinePortlet').hide();
          }
        }
      });
    },
    initPopup() {
      const restUrl = `//${exoConstants.HOST_NAME}${exoConstants.PORTAL}/${exoConstants.PORTAL_REST}/social/people/getPeopleInfo/{0}.json`;
      const labels = {
        youHaveSentAnInvitation: this.$t('message.label'),
        StatusTitle: this.$t('Loading.label'),
        Connect: this.$t('Connect.label'),
        Confirm: this.$t('Confirm.label'),
        CancelRequest: this.$t('CancelRequest.label'),
        RemoveConnection: this.$t('RemoveConnection.label'),
        Ignore: this.$t('Ignore.label')
      };
      $('#onlineList').find('a').each(function (idx, el) {
        $(el).userPopup({
          restURL: restUrl,
          labels: labels,
          content: false,
          defaultPosition: 'left',
          keepAlive: true,
          maxWidth: '240px'
        });
      });
    }
  }
};
</script>