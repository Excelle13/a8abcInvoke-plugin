var exec = require('cordova/exec');

exports.abcInvoke = function (arg0, success, error) {
    exec(success, error, 'A8abcResInvoke', 'abcInvoke', [arg0]);
};
