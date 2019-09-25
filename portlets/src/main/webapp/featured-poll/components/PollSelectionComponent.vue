<template>
  <div v-if="polls.length > 0" class="listpoll">
    <div class="form-horizontal">
      <div class="control-group">
        <label for="poll_id" class="control-label">{{ $t('featuredPoll.selectOtherPoll') }}: </label>
        <div class="uiSelectbox">
          <select id="poll_id" v-model="selectedPoll" class="selectbox" @change="selectPoll()">
            <option v-for="poll in polls" :key="poll.id" :value="poll.id" :selected="selectedPoll === poll.id">{{ poll.name }}</option>
          </select>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  props: {
    polls: {
      type: Array,
      default: () => []
    },
    selected: {
      type: String,
      default: null
    }
  },
  data() {
    return {
      selectedPoll: ''
    };
  },
  created() {
    if (this.polls.length > 0) {
      this.selectedPoll = this.polls.find(poll => poll.id === this.selected);
      if (!this.selectedPoll) {
        this.selectedPoll = this.polls[0].id;
      } else {
        this.selectedPoll = this.selectedPoll.id;
      }
    }
  },
  methods: {
    selectPoll() {
      this.$emit('pollSelected', this.selectedPoll);
    }
  }
};
</script>
