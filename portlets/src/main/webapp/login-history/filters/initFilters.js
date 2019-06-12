import prettyTimeDiff from './prettyDate';

Vue.filter('prettyTimeDiff', function(time) {
  let prefix = '';
  let suffix = '';
  if (time > new Date().getTime()) {
    prefix = 'next ';
  } else {
    suffix = ' ago';
  }
  const diff = prettyTimeDiff.bind(this)(time, true);

  return `${prefix}${diff}${suffix}`;
});
