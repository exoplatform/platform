(function($) {
    $(document).ready(function() {
        $('body').on('click', '.feedContainer a.titlelink', function(evt) {
            evt.preventDefault();
            evt.stopPropagation();
            var $more = $(this).closest('.item').find('.more');

            if ($more.is(':visible')) {
                $more.hide();
            } else {
                $more.show();
            }

            return false;
        });
    });
})(jq);
