'use strict';
/*global angular*/
var courtly = angular.module('courtly', [
    'ngRoute',
    'ngCookies',
    'angularMoment',
    'ui.keypress',
    'ui.bootstrap',
    'courtly.services',
    'courtly.controllers'
]);
courtly.config(['$routeProvider', function ($routeProvider) {
    $routeProvider
        .when('/', {
            templateUrl: 'views/home.html',
            controller: 'headerCtrl'
        })
        .otherwise({
            redirectTo: '/challenge'
        });
}]);

