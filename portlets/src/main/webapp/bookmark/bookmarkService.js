import {exoConstants} from '../js/eXoConstants';

function loadBookmarks() {
  return fetch('/rest/bookmarks/get').then(resp => resp.json()).then(bookmarks => {
    return bookmarks.map(bookmark => {
      bookmark.link = bookmark.link.replace(/__SLASH__/g, '/')
        .replace('/$PORTAL', exoConstants.PORTAL)
        .replace('$SITENAME', exoConstants.PORTAL_NAME);

      bookmark.name = bookmark.name.replace(/__SLASH__/g, '/');

      return bookmark;
    });
  });
}

function saveBookmarks(bookmarks) {
  const dataString = encodeURI(JSON.stringify(bookmarks).replace(/\//g, '__SLASH__'));
  return fetch(`/rest/bookmarks/set/${dataString}`).then(resp => resp.json());
}

export default {
  loadBookmarks: loadBookmarks,
  saveBookmarks: saveBookmarks
};




