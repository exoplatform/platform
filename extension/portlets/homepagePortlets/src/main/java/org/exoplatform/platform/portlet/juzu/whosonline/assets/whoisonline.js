$(function() {




    $.getJSON("/rest/homepage/intranet/online/contacts", function(contacts){

        if (contacts.length){
            $("#OnlinePortlet").append('<div id="onlineContent"><div class="onlineHeader">Who is online?</div><ul id="onlineList" class="gallery">'+ '</ul></div>');


            $.each(contacts, function(i, contact){

                var onlineContact = '<li><a target="_parent" href="'+contact.profileUrl+'"><img src="'+contact.avatarUrl+'" alt="image" /></a></li>';
                $onlineContact = $(onlineContact);

                var pos;
                if(!((i+1)%4))
                    pos = ['-50', '30'];
                else
                    pos = ['-20', '30'];

                $onlineContact.tipTip({
                    content: '<table id="tipName"><tbody><tr><td style="width: 50px;"><img src="'+contact.avatarUrl+'" alt="image" /></td><td>'+contact.name+'<div style="font-weight: normal;">'+contact.title+'</div></td></tr></tbody></table><blockquote>'+contact.activity+'</blockquote>',defaultPosition: "left"
                });


                $("#onlineList").append($onlineContact);


            });

        }else{
            $("#OnlinePortlet").hide();
        }

    });




});