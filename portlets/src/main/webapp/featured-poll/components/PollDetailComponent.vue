<template>
  <div v-if="poll" class="poll">
    <h6 v-if="hasTopic" class="clearfix sdf1">
      <a :href="topicURL" class="question">
        <i class="uiIconPoll uiIconLightGray"></i> <span class="text">{{ poll.question }}</span>
      </a>
      <a :href="topicURL" class="discuss btn" type="button">
        {{ $t('featuredPoll.discussInForum') }}
      </a>
    </h6>
    <h6 v-else class="question">
      {{ poll.question }}
    </h6>
    <form v-if="showVote">
      <label v-for="(option, index) in poll.option" :key="index" :class="labelInputClass">
        <input :type="inputType" :class="inputType" v-model="userVote" :value="index">
        <span v-html="option"></span>
      </label>
      <div class="uiAction btnform">
        <button class="btn" type="button" name="btnVote" @click="submitVote()">{{ $t('featuredPoll.vote') }}</button>
      </div>
    </form>
    <table v-if="showResult" class="voteResult">
      <tbody>
        <tr v-for="(option, index) in poll.option" :key="index">
          <td>
            <div class="label-vote" data-placement="right" style="width: 100px;" v-html="option"></div>
          </td>
          <td>
            <div class="horizontalBG">
              <div :style="getOptionWidthStyle(index)" class="horizontalBar">&nbsp;</div>
            </div>
          </td>
          <td class="percent">
            {{ getOptionResult(index) }}%
          </td>
        </tr>
      </tbody>
    </table>
    <div v-if="showResult && canVoteAgain" class="clearfix btnform">
      <span class="uiAction"><button class="btn" type="button" @click="toggleVoteAgain()">{{ $t('featuredPoll.voteAgain') }}</button></span>
    </div>
  </div>
</template>
<script>
import pollService from '../pollService';

export default {
  props: {
    poll: {
      type: Object,
      default: null
    }
  },
  data() {
    return {
      voteAgain: false,
      userVote: []
    };
  },
  computed: {
    showVote: function() {
      return this.poll && (!this.poll.showVote || this.voteAgain);
    },
    showResult: function() {
      return !this.showVote;
    },
    topicURL: function() {
      return this.poll ? this.poll.link : '';
    },
    hasTopic: function() {
      const parentPath = this.poll ? this.poll.parentPath : '';
      return parentPath && parentPath.indexOf('ForumData/CategoryHome') !== -1;
    },
    canVoteAgain: function() {
      return this.poll && this.poll.isAgainVote;
    },
    inputType: function() {
      return this.poll && this.poll.isMultiCheck ? 'checkbox' : 'radio';
    },
    labelInputClass: function() {
      return this.poll && this.poll.isMultiCheck ? 'uiCheckbox' : 'uiRadio';
    }
  },
  watch: {
    poll: function(newVal) {
      if (newVal && newVal.isMultiCheck) {
        this.userVote = [];
      } else {
        this.userVote = null;
      }
    }
  },
  methods: {
    getOptionResult(index) {
      return Math.round(this.poll.vote[index]);
    },
    getOptionWidthStyle(index) {
      const result = this.getOptionResult(index);
      return `width: ${result}%;`;
    },
    toggleVoteAgain() {
      this.voteAgain = !this.voteAgain;
    },
    async submitVote() {
      let votes = [];
      if (this.poll && this.poll.isMultiCheck) {
        votes = this.userVote;
      } else if(this.userVote != null) {
        votes = [this.userVote];
      }

      if (votes.length < 1) {
        return;
      }

      const poll = await pollService.submitVote(this.poll.id, votes);
      this.poll = poll;
      this.voteAgain = false;
    }
  }
};
</script>
