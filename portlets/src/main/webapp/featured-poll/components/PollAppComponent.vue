<template>
  <div class="featured-poll uiFeaturedPoll uiBox">
    <h6 class="gadgetTitle title left clearfix">
      {{ $t('featuredPoll.title') }}
      <a v-if="isAdmin" class="actionIcon pull-right settingBt" data-original-title="Setting" data-placement="left" data-toggle="tooltip" @click="togglePollSelection()">
        <i class="uiIconSetting uiIconLightGray"></i>
      </a>
    </h6>
    <!-- Display poll  -->
    <div v-if="polls.length > 0" class="uiContentBox gadContent">
      <exo-poll-selection v-if="isAdmin && showPollSelection" :polls="polls" :selected="poll" @pollSelected="onPollSelected($event)"></exo-poll-selection>
      <exo-poll-detail :poll="selectedPoll"></exo-poll-detail>
    </div>
    <div v-else class="light_message uiContentBox">
      <i class="uiIconPoll"></i> {{ $t('featuredPoll.noPoll') }}
    </div>
  </div>
</template>
<script>
import pollService from '../pollService';

export default {
  props: {
    poll: {
      type: String,
      default: null
    }
  },
  data() {
    return {
      showPollSelection: false,
      polls: [],
      selectedPoll: null,
      isAdmin: false
    };
  },
  created() {
    this.fetchPolls();
  },
  methods: {
    togglePollSelection() {
      this.showPollSelection = !this.showPollSelection && this.isAdmin;
    },
    async fetchPolls() {
      const data = await pollService.getPolls();

      this.isAdmin = data.isAdmin;

      const polls = [];
      const pollSize = data.pollId.length;
      for (let i = 0; i < pollSize; i++) {
        const poll = {
          id: data.pollId[i],
          name: data.pollName[i]
        };
        polls.push(poll);
      }
      this.polls = polls;

      let idx = -1;
      if (this.poll) {
        idx = data.pollId.indexOf(this.poll);
      }

      if (idx === -1) {
        this.poll = data.pollId[0];
      }
      this.onPollSelected(this.poll);
    },
    async onPollSelected(id) {
      this.selectedPoll = await pollService.getPoll(id);
      if (id !== this.poll) {
        this.poll = id;
        await pollService.saveChosenPoll(id);
      }
    }
  }
};
</script>
