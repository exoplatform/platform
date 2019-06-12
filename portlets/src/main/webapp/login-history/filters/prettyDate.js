export default function prettyTimeDiff(aTime, shortFormat, smallest, bTime) {
  aTime = +new Date(aTime);
  bTime = !bTime ? +new Date() : +new Date(bTime);

  const THOUSAND = 1000;
  let timeGap = Math.abs(bTime - aTime) / THOUSAND, amount, measure, smaller;
  const ints = {
    sec: 1,
    min: 60,
    hr: 3600,
    day: 86400,
    week: 604800,
    mon: 2592000,
    year: 31536000,
    decade: 315360000
  };

  if (shortFormat) {
    if (smallest === 'second') {
      smallest = 'sec';
    }
    if (smallest === 'minute') {
      smallest = 'min';
    }
    if (smallest === 'hour') {
      smallest = 'hr';
    }
    if (smallest === 'month') {
      smallest = 'mon';
    }
  }

  for (const i in ints) {
    if (timeGap > ints[i] && ints[i] > (ints[measure] || 0)) {
      smaller = measure;
      measure = i;
    }
  }

  amount = Math.floor(timeGap / ints[measure]);

  if (timeGap > ints.year) {
    /* Handle leap years */
    const FOUR = 4;
    timeGap -= Math.floor(ints[measure] * amount / ints.year / FOUR) * ints.day;
  }

  amount += ` ${measure}${amount > 1 ? 's' : ''}`;

  const remainder = timeGap % ints[measure];

  if (!smallest) {
    smallest = smaller;
    if (smallest === 'sec' || smallest === 'second') {
      return amount;
    }
  }

  if (remainder >= ints[smallest]) {
    amount += `, ${prettyTimeDiff(+new Date() - remainder * THOUSAND, shortFormat, smallest)}`;
  }

  return amount;
}
