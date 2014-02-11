//casper.options.verbose = true;
//casper.options.logLevel='debug';
//casper.on('resource.received', function(resource) {
//    casper.echo("received : "+resource.url);
//});
//casper.on('resource.requested', function(resource) {
//    casper.echo("requested : "+resource.url);
//});
//casper.on('')
casper.test.begin('access home', 2, function suite(test) {
    casper.start('http://localhost:9001', function afterLoad() {
        test.assertTitle("courtly", "homepage title is the one expected");
    });
    casper.run(function() {
        test.done();
    });
});