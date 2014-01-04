(function($, parent, window, document, undefined) {

	var

		// Namespace.
		self = parent.Twitter = parent.Twitter || {},

        console = parent['console'],

        tweetPanel = $('div.panel.twitter'),
        tweetBody = tweetPanel.find('div.panel-body'),

        linkify = (function() {
            var links = function(tweet) {
                var exp = /(\b(https?|ftp|file):\/\/[-A-Z0-9+&@#\/%?=~_|!:,.;]*[-A-Z0-9+&@#\/%=~_|])/ig;
                return tweet.replace(exp,"<a href='$1' target='_blank'>$1</a>");
            },
            users = function(tweet) {
                var exp = /\B\@([\w\-]+)/gim;
                return tweet.replace(exp, "<a href='https://twitter.com/$1' target='_blank'>@$1</a>");
            };
            return function(tweet) {
                return users(links(tweet));
            };
        }()),

        // Logic borrowed from http://williamsportwebdeveloper.com/cgi/wp/?p=503
        // Refactored for my own needs.
        toISO8601String = (function() {
            var padzero = function(n) {
                    return n < 10 ? '0' + n : n;
                },
                pad2zeros = function(n) {
                    if (n < 100) {
                        n = '0' + n;
                    }
                    if (n < 10) {
                        n = '0' + n;
                    }
                    return n;
                };
            return function(d) {
                return d.getUTCFullYear() + '-'
                    + padzero(d.getUTCMonth() + 1)
                    + '-' + padzero(d.getUTCDate())
                    + 'T'
                    + padzero(d.getUTCHours()) + ':'
                    + padzero(d.getUTCMinutes()) + ':'
                    + padzero(d.getUTCSeconds()) + '.'
                    + pad2zeros(d.getUTCMilliseconds())
                    + 'Z';
            };
        }()),

		init = function() {
		    $.getJSON("tweets.json", function(data) {
                var tweets = data.tweets;
                if(tweets && tweets.length > 0) {
                    var ul = $('<ul>').hide();
                    for(var i in tweets) {
                        var tweet = tweets[i],
                            li = $('<li>').addClass('tweet small'),
                            text = $('<p>').append(linkify(tweet.text)),
                            timestamp = $('<p>').append($.localtime.toLocalTime(tweet.created_at,'h:mm:ss a')).addClass('smaller');
                        li.append(text).append(timestamp);
                        ul.append(li);
                    }
                    ul.find('li.tweet').first().addClass('first');
                    tweetBody.append(ul);
                    ul.slideDown(500);
                }
		    });
        };

    (tweetPanel.length > 0) && init();

})(jQuery, Kolich.Blog || {}, this, this.document);
