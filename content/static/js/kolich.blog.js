(function($, parent, window, document, undefined) {

	var

		// Namespace.
		self = parent.Blog = parent.Blog || {},

		console = parent['console'],
        baseAppUrl = parent['baseAppUrl'],

		blogJsonApi = baseAppUrl + "blog.json",

		init = (function() {
		    var
                prettyprint = function() {
                    $('pre:not(.prettyprint)').addClass('prettyprint');
                    window.prettyPrint && prettyPrint();
                },
                reveal = function() {
                    $('div.entry').unbind().click(function(e) {
                        var node = e.target.nodeName;
                        if(node != 'A') {
                            $(this).find('div.fader').remove();
                            $(this).css('max-height','none').css('cursor','auto');
                            e.preventDefault();
                        }
                    });
                },
                more = function() {
                    var rejiggerScroll = function(moreButton, entryDiv) {
                        var winHeight = $(window).height(),
                            newTop = 0;
                        if(moreButton.is(':visible')) {
                            var btnHeight = moreButton.height() * 4,
                                btnOffset = moreButton.offset().top;
                            newTop = (btnOffset+btnHeight) - winHeight;
                        } else {
                            var newDivHeight = entryDiv.height(),
                                newDivOffset = entryDiv.offset().top;
                            newTop = (newDivOffset+newDivHeight) - winHeight;
                        }
                        $('html, body').animate({scrollTop:newTop}, 'fast');
                    };
                    $('button.more').click(function(e) {
                        var moreDiv = $(this).parent();
                        var lastCommit = $('p.hash:last').html();
                        $.getJSON(blogJsonApi, {before: lastCommit}, function(json) {
                            var entries = json.content, count = entries.length;
                            // Hide the "load more" button if no entries are left.
                            (json.remaining <= 0) && moreDiv.slideUp('fast');
                            for(i in entries) {
                                var entry = entries[i];
                                var entryDiv = $('<div>').addClass('entry').hide(),
                                    h2 = $('<h2>').addClass('title'),
                                    link = $('<a>').attr('href',baseAppUrl+entry.name).html(entry.title),
                                    hash = $('<p>').addClass('hash').html(entry.commit),
                                    date = $('<p>').addClass('date').html(entry.date),
                                    content = $('<p>').html(entry.html),
                                    fader = $('<div>').addClass('fader');
                                entryDiv.append(h2.append(link)).append(hash).append(date).append(content);
                                entryDiv.append(fader);
                                moreDiv.before(entryDiv);
                                entryDiv.slideDown('fast', function(e) {
                                    // Only adjust the scroll position if we're in the callback for
                                    // the "last" fetched entry in the list.  This is so we don't
                                    // execute the same callback multiple times, just once at the end.
                                    var moreButton = $('button.more');
                                    (--count <= 0) && rejiggerScroll(moreButton, entryDiv);
                                });
                            } /* for */
                            prettyprint();
                            reveal();
                        });
                    });
                };
            return function() {
                prettyprint();
                reveal();
                ($('button.more').length > 0) && more();
            };
        }());

    self['path'] = parent['path'];
    self['baseAppUrl'] = parent['baseAppUrl'];

    self['debug'] = parent['debug'];
    self['console'] = parent['console'];

    init();

})(jQuery, Kolich || {}, this, this.document);
