import {exoConstants} from '../js/eXoConstants';
const baseURL = `${exoConstants.PORTAL}/${exoConstants.PORTAL_REST}/ks/poll`;

let saveChosenPollURL = '';

async function getPolls() {
  const url = `${baseURL}/viewpoll/pollid`;
  return await fetch(url).then(resp => resp.json());
}

async function getPoll(id) {
  const url = `${baseURL}/viewpoll/${id}`;
  return await fetch(url).then(resp => resp.json());
}

async function submitVote(id, votes) {
  if (votes.length < 1) {
    return;
  }
  const url = `${baseURL}/votepoll/${id}/${votes.join(':')}`;
  return await fetch(url).then(resp => resp.json());
}

function setChosenPollURL(url) {
  saveChosenPollURL = url;
}
async function saveChosenPoll(pollId) {
  return await fetch(saveChosenPollURL, {
    method: 'POST',
    body: `pollid=${pollId}`,
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  });
}

export default {
  getPolls,
  getPoll,
  submitVote,
  setChosenPollURL,
  saveChosenPoll
};
