function getLoginHistories(userId, fromTime, toTime) {
  const restURI = `/rest/loginhistory/loginhistory/${userId}/${fromTime}/${toTime}`;
  return fetch(restURI).then(resp => resp.json()).then(resp => resp.data);
}
async function getDailyStats(userId, week) {
  const currentWeek = new Date(week);
  const restURI = `/rest/loginhistory/weekstats/${userId}/${currentWeek.toString('yyyy-MM-dd')}`;
  const res = await fetch(restURI);
  return await (await res.json()).data;
}

async function getLastLogins(userId, noLogin) {
  const restURI = encodeURI(`/rest/loginhistory/lastlogins/${noLogin}/${userId}`);
  const res = await fetch(restURI);
  return await (await res.json()).data;
}

export default {
  getLoginHistories,
  getDailyStats,
  getLastLogins
};
