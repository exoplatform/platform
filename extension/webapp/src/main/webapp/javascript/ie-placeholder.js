//IE placeholder;
$(function (){
    if (/MSIE 9|MSIE 8|MSIE 7|MSIE 6/g.test(navigator.userAgent)) {
        function resetPlaceholder() {
            if ($(this).val() === '') {
                $(this).val($(this).attr('placeholder'))
                        .attr('data-placeholder', true)
                        .addClass('ie-placeholder');
                if ($(this).is(':password')) {
                    var field = $('<input />');
                    $.each(this.attributes, function (i, attr) {
                        if (attr.name !== 'type') {
                            field.attr(attr.name, attr.value);
                        }
                    });
                    field.attr({
                        'type': 'text',
                        'data-input-password': true,
                        'value': $(this).val()
                    });
                    $(this).replaceWith(field);
                }
            }
        }

        $('[placeholder]').each(function () {
            //ie user refresh don't reset input values workaround
            if ($(this).attr('placeholder') !== '' && $(this).attr('placeholder') === $(this).val()){
                $(this).val('');
            }
            resetPlaceholder.call(this);
        });
        $(document).on('focus', '[placeholder]', function () {
            // add test  if ($(this).val !== 'root') to not reset value for "root"
            if ($(this).val() !== 'root') {
                if ($(this).attr('data-placeholder')) {
                    $(this).val('').removeAttr('data-placeholder').removeClass('ie-placeholder');
                }
            }
        }).on('blur', '[placeholder]', function () { resetPlaceholder.call(this); });
        $(document).on('focus', '[data-input-password]', function () {
            var field = $('<input />');
            $.each(this.attributes, function (i, attr) {
                if (['type','data-placeholder','data-input-password','value'].indexOf(attr.name) === -1) {
                    field.attr(attr.name, attr.value);
                }
            });
            field.attr('type', 'password').on('focus', function () { this.select(); });
            $(this).replaceWith(field);
            field.trigger('focus');
        });
    }
});