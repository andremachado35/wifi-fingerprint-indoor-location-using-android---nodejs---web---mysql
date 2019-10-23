const express = require('express');
const app = express();
var cors = require('cors');
var path = require("path");

//cors middleware
var allowCrossDomain = function(req, res, next) {
	res.header('Access-Control-Allow-Origin', '*');
	res.header('Access-Control-Allow-Methods', 'GET,PUT,POST,DELETE');
	res.header('Access-Control-Allow-Headers', 'Content-Type');
	next();
}

app.use(allowCrossDomain);
app.use(cors({credentials: true, origin: true}));
app.use('/', express.static(path.join(__dirname, '/')));

app.listen(8081);

app.get('/', function (req, res) {
	res.sendFile('redir.html', { root: __dirname });
});