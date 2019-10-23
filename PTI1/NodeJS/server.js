const express = require('express');
const app = express();
const bodyParser = require('body-parser');
const status = require('http-status');
var dataHandler = require("./dataHandler");
const jwt = require('jsonwebtoken');
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
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({
	extended: true
}));
app.use('/', express.static(path.join(__dirname, '/')));


app.listen(8080);
console.log('Server listening on port 8080');

// default route
app.get('/', function (req, res) {
	return res.sendFile(path.join(__dirname, '../Web/pages/login.html'));
});
// route: get all aps
app.get('/aps', verifyToken, function (req, res) {
	jwt.verify(req.token, "config.secret", (err, authData) => {
    if(err) {
			res.status(403).send("Wrong token");
    } else {
			dataHandler.getAps(function(error,results){
				if(error){
					res.status(error.code).send(error.message);
				}
				else{
					res.status(status.OK).send({ error: false, data: results, message: 'APs.' });
				}
			});
    }
  });
});
// route: get one ap
app.get('/aps/:id_ap', verifyToken, function (req, res) {
	jwt.verify(req.token, "config.secret", (err, authData) => {
    if(err) {
			res.status(403).send("Wrong token");
    } else {
			dataHandler.getApsById(req,function(error,results){
				if (error) {
					res.status(error.code).send(error.message);
				}
				else{
						return res.status(status.OK).send({ error: false, data: results, message: 'AP.' });
				}
			});
    }
  });
});
// route: post ap
app.post('/aps', verifyToken, function (req, res) {
	jwt.verify(req.token, "config.secret", (err, authData) => {
    if(err) {
			res.status(403).send("Wrong token");
    } else {
			dataHandler.insertAps(req,function(error,results){
				if (error) {
					res.status(error.code).send(error.message);
				}
				else{
					return res.status(status.CREATED).send({ error: false, data: results, message: 'New ap has been created successfully.' });
				}
			});
    }
  });
});
// route: update ap
app.put('/aps/:id_ap', verifyToken, function(req,res){
	jwt.verify(req.token, "config.secret", (err, authData) => {
    if(err) {
			res.status(403).send("Wrong token");
    } else {
			dataHandler.updateAps(req,function(error,results){
				if (error) {
					res.status(error.code).send(error.message);
				}
				else{
					return res.status(status.CREATED).send({ error: false, data: results, message: 'New ap has been modified successfully.' });
				}
			});
    }
  });
});
// route: delete ap
app.delete('/aps/:id_ap', verifyToken, function (req,res){
	jwt.verify(req.token, "config.secret", (err, authData) => {
    if(err) {
			res.status(403).send("Wrong token");
    } else {
			dataHandler.deleteAps(req,function(error,results){
				if (error) {
					res.status(error.code).send(error.message);
				}
				else{
					return res.status(status.CREATED).send({ error: false, data: results, message: 'AP deleted successfully'});
				}
			});
    }
  });
});
//route: get all contribution_history
app.get('/contributions_history', verifyToken, function (req, res) {
	jwt.verify(req.token, "config.secret", (err, authData) => {
    if(err) {
			res.status(403).send("Wrong token");
    } else {
			dataHandler.getContributionsHistory(function(error,results){
				if(error){
					res.status(error.code).send(error.message);
				}
				else{
					res.status(status.OK).send({ error: false, data: results, message: 'Contributions History.' });
				}
			});
    }
  });
});
// route: get one contribution_history
app.get('/contributions_history/:id_contributions_history', verifyToken, function (req, res) {
	jwt.verify(req.token, "config.secret", (err, authData) => {
    if(err) {
			res.status(403).send("Wrong token");
    } else {
			dataHandler.getContributionsHistoryById(req,function(error,results){
				if (error) {
					res.status(error.code).send(error.message);
				}
				else{
						return res.status(status.OK).send({ error: false, data: results, message: 'AP.' });
				}
			});
    }
  });
});
// route: post contribution_history
app.post('/contributions_history', verifyToken, function (req, res) {
	jwt.verify(req.token, "config.secret", (err, authData) => {
    if(err) {
			res.status(403).send("Wrong token");
    } else {
			dataHandler.insertContributionsHistory(req,function(error,results){
				if (error) {
					res.status(error.code).send(error.message);
				}
				else{
					return res.status(status.CREATED).send({ error: false, data: results, message: 'New contribution_history has been created successfully.' });
				}
			});
    }
  });
});
// route: update contribution_history
app.put('/contributions_history/:id_contributions_history', verifyToken, function(req,res){
	jwt.verify(req.token, "config.secret", (err, authData) => {
    if(err) {
			res.status(403).send("Wrong token");
    } else {
			dataHandler.updateContributionsHistory(req,function(error,results){
				if (error) {
					res.status(error.code).send(error.message);
				}
				else{
					return res.status(status.CREATED).send({ error: false, data: results, message: 'Contribution_history has been modified successfully.' });
				}
			});
    }
  });
});
// route: delete contribution_history
app.delete('/contributions_history/:id_contributions_history', verifyToken, function (req,res){
	jwt.verify(req.token, "config.secret", (err, authData) => {
    if(err) {
			res.status(403).send("Wrong token");
    } else {
			dataHandler.deleteContributionsHistory(req,function(error,results){
				if (error) {
					res.status(error.code).send(error.message);
				}
				else{
					return res.status(status.CREATED).send({ error: false, data: results, message: 'Contribution_history deleted successfully'});
				}
			});
    }
  });
});
//route:get all fingerprint_ap
app.get('/fingerprint_aps', verifyToken, function(req,res){
	jwt.verify(req.token, "config.secret", (err, authData) => {
    if(err) {
			res.status(403).send("Wrong token");
    } else {
			dataHandler.getFingerprintAps(function(error,results){
				if(error){
					res.status(error.code).send(error.message);
				}
				else{
					res.status(status.OK).send({ error: false, data: results, message: 'Fingerprint_aps.' });
				}
			});
    }
  });
});
//route:get one fingerprint_ap
app.get('/fingerprint_aps/:id_fingerprint_aps', verifyToken, function (req, res) {
	jwt.verify(req.token, "config.secret", (err, authData) => {
    if(err) {
			res.status(403).send("Wrong token");
    } else {
			dataHandler.getFingerprintApsById(req,function(error,results){
				if (error) {
					res.status(error.code).send(error.message);
				}
				else{
						return res.status(status.OK).send({ error: false, data: results, message: 'Fingerprint_aps.' });
				}
			});
    }
  });
});
//route:post fingerprint_ap
app.post('/fingerprint_aps', verifyToken, function(req,res){
	jwt.verify(req.token, "config.secret", (err, authData) => {
    if(err) {
			res.status(403).send("Wrong token");
    } else {
			dataHandler.insertFingerprintAps(req,function(error,results){
				if (error) {
					res.status(error.code).send(error.message);
				}
				else{
					return res.status(status.CREATED).send({ error: false, data: results, message: 'fingerprint_ap has been created successfully' });
				}
			});
    }
  });
});
//route:update fingerprint_ap
app.put('/fingerprint_aps/:id_fingerprint_ap', verifyToken, function(req,res){
	jwt.verify(req.token, "config.secret", (err, authData) => {
    if(err) {
			res.status(403).send("Wrong token");
    } else {
			dataHandler.updateFingerprintAps(req,function(error,results){
				if (error) {
					res.status(error.code).send(error.message);
				}
				else{
					return res.status(status.CREATED).send({ error: false, data: results, message: 'Fingerprint_ap has been modified successfully.' });
				}
			});
    }
  });
});
//route:delete fingerprint_ap
app.delete('/fingerprint_aps/:id_fingerprint_ap', verifyToken, function(req,res){
	jwt.verify(req.token, "config.secret", (err, authData) => {
    if(err) {
			res.status(403).send("Wrong token");
    } else {
			dataHandler.deleteFingerprintAps(req,function(error,results){
				if (error) {
					res.status(error.code).send(error.message);
				}
				else{
					return res.status(status.CREATED).send({ error: false, data: results, message: 'Fingerprint_ap deleted successfully'});
				}
			});
    }
  });
});
//route: get all fingerprint
app.get('/fingerprints', verifyToken, function(req,res){
	jwt.verify(req.token, "config.secret", (err, authData) => {
    if(err) {
			res.status(403).send("Wrong token");
    } else {
			dataHandler.getFingerprints(function(error,results){
				if(error){
					res.status(error.code).send(error.message);
				}
				else{
					res.status(status.OK).send({ error: false, data: results, message: 'Fingerprints.' });
				}
			});
    }
  });
});
//route: get one fingerprint
app.get('/fingerprints/:id_fingerprint', verifyToken, function(req,res){
	jwt.verify(req.token, "config.secret", (err, authData) => {
    if(err) {
			res.status(403).send("Wrong token");
    } else {
			dataHandler.getFingerprintsById(req,function(error,results){
				if (error) {
					res.status(error.code).send(error.message);
				}
				else{
						return res.status(status.OK).send({ error: false, data: results, message: 'Fingerprint.' });
				}
			});
    }
  });
});
//route: post fingerprint
app.post('/fingerprints', verifyToken, function(req,res){
	jwt.verify(req.token, "config.secret", (err, authData) => {
    if(err) {
			res.status(403).send("Wrong token");
    } else {
			dataHandler.insertFingerprints(req,function(error,results){
				if (error) {
					res.status(error.code).send(error.message);
				}
				else{
					return res.status(status.CREATED).send({ error: false, data: results, message: 'New fingerprint has been created successfully' });
				}
			});
    }
  });
});
//route: put fingerprint
app.put('/fingerprints/:id_fingerprint', verifyToken, function(req,res){
	jwt.verify(req.token, "config.secret", (err, authData) => {
    if(err) {
			res.status(403).send("Wrong token");
    } else {
			dataHandler.updateFingerprints(req,function(error,results){
				if (error) {
					res.status(error.code).send(error.message);
				}
				else{
					return res.status(status.CREATED).send({ error: false, data: results, message: 'Fingerprint has been modified successfully.' });
				}
			});
    }
  });
});
//route: delete fingerprint
app.delete('/fingerprints/:id_fingerprint', verifyToken, function(req,res){
	jwt.verify(req.token, "config.secret", (err, authData) => {
    if(err) {
			res.status(403).send("Wrong token");
    } else {
			dataHandler.deleteFingerprints(req,function(error,results){
				if (error) {
					res.status(error.code).send(error.message);
				}
				else{
					return res.status(status.CREATED).send({ error: false, data: results, message: 'Fingerprint deleted successfully'});
				}
			});
    }
  });
});
//route: get all interest_points
app.get('/interest_points', verifyToken, function (req, res){
	jwt.verify(req.token, "config.secret", (err, authData) => {
    if(err) {
			res.status(403).send("Wrong token");
    } else {
			dataHandler.getInterestPoints(function(error,results){
				if(error){
					res.status(error.code).send(error.message);
				}
				else{
					res.status(status.OK).send({ error: false, data: results, message: 'Interest_points.' });
				}
			});
    }
  });
});
//route: get one interest_point
app.get('/interest_points/:id_interest_points', verifyToken, function(req,res){
	jwt.verify(req.token, "config.secret", (err, authData) => {
    if(err) {
			res.status(403).send("Wrong token");
    } else {
			dataHandler.getInterestPointsById(req,function(error,results){
				if (error) {
					res.status(error.code).send(error.message);
				}
				else{
						return res.status(status.OK).send({ error: false, data: results, message: 'Interest_point.' });
				}
			});
    }
  });
});
//route: post interest_point
app.post('/interest_points', verifyToken, function(req,res){
	jwt.verify(req.token, "config.secret", (err, authData) => {
    if(err) {
			res.status(403).send("Wrong token");
    } else {
			dataHandler.insertInterestPoints(req,function(error,results){
				if (error) {
					res.status(error.code).send(error.message);
				}
				else{
					return res.status(status.CREATED).send({ error: false, data: results, message: 'New interest_point has been created successfully' });
				}
			});
    }
  });
});
//route: update interest_point
app.put('/interest_points/:id_interest_points', verifyToken, function(req,res){
	jwt.verify(req.token, "config.secret", (err, authData) => {
    if(err) {
			res.status(403).send("Wrong token");
    } else {
			dataHandler.updateInterestPoints(req,function(error,results){
				if (error) {
					res.status(error.code).send(error.message);
				}
				else{
					return res.status(status.CREATED).send({ error: false, data: results, message: 'Interest_point has been modified successfully.' });
				}
			});
    }
  });
});
//route: delete interest_point
app.delete('/interest_points/:id_interest_points', verifyToken, function(req,res){
	jwt.verify(req.token, "config.secret", (err, authData) => {
    if(err) {
			res.status(403).send("Wrong token");
    } else {
			dataHandler.deleteInterestPoints(req,function(error,results){
				if (error) {
					res.status(error.code).send(error.message);
				}
				else{
					return res.status(status.CREATED).send({ error: false, data: results, message: 'Interest_point deleted successfully'});
				}
			});
    }
  });
});
//route: get all reference_points
app.get('/reference_points', verifyToken, function (req, res){
	jwt.verify(req.token, "config.secret", (err, authData) => {
    if(err) {
			res.status(403).send("Wrong token");
    } else {
			dataHandler.getReferencePoints(function(error,results){
				if(error){
					res.status(error.code).send(error.message);
				}
				else{
					res.status(status.OK).send({ error: false, data: results, message: 'References Points' });
				}
			});
    }
  });
});
//route: get one reference point
app.get('/reference_points/:id_reference_point', verifyToken, function(req,res){
	jwt.verify(req.token, "config.secret", (err, authData) => {
    if(err) {
			res.status(403).send("Wrong token");
    } else {
			dataHandler.getReferencePointsById(req,function(error,results){
				if (error) {
					res.status(error.code).send(error.message);
				}
				else{
						return res.status(status.OK).send({ error: false, data: results, message: 'References Point' });
				}
			});
    }
  });
});
//route: post reference_point
app.post('/reference_points', verifyToken, function(req,res){
	jwt.verify(req.token, "config.secret", (err, authData) => {
    if(err) {
			res.status(403).send("Wrong token");
    } else {
			dataHandler.insertReferencePoints(req,function(error,results){
				if (error) {
					res.status(error.code).send(error.message);
				}
				else{
					return res.status(status.CREATED).send({ error: false, data: results, message: 'New reference point has been created successfully' });
				}
			});
    }
  });
});
//route: update reference point
app.put('/reference_points/:id_reference_point', verifyToken, function(req,res){
	jwt.verify(req.token, "config.secret", (err, authData) => {
    if(err) {
			res.status(403).send("Wrong token");
    } else {
			dataHandler.updateReferencePoints(req,function(error,results){
				if (error) {
					res.status(error.code).send(error.message);
				}
				else{
					return res.status(status.CREATED).send({ error: false, data: results, message: 'Reference Point has been modified successfully.' });
				}
			});
    }
  });
});
//route: delete reference_point
app.delete('/reference_points/:id_reference_point', verifyToken, function(req,res){
	jwt.verify(req.token, "config.secret", (err, authData) => {
    if(err) {
			res.status(403).send("Wrong token");
    } else {
			dataHandler.deleteReferencePoints(req,function(error,results){
				if (error) {
					res.status(error.code).send(error.message);
				}
				else{
					return res.status(status.CREATED).send({ error: false, data: results, message: 'Reference point deleted successfully'});
				}
			});
    }
  });
});
//route:get all rssi
app.get('/rssis', verifyToken, function (req, res){
	jwt.verify(req.token, "config.secret", (err, authData) => {
    if(err) {
			res.status(403).send("Wrong token");
    } else {
			dataHandler.getRssis(function(error,results){
				if(error){
					res.status(error.code).send(error.message);
				}
				else{
					res.status(status.OK).send({ error: false, data: results, message: 'Rssis' });
				}
			});
    }
  });
});
//route: get one rssi
app.get('/rssis/:id_rssi', verifyToken, function(req,res){
	jwt.verify(req.token, "config.secret", (err, authData) => {
    if(err) {
			res.status(403).send("Wrong token");
    } else {
			dataHandler.getRssisById(req,function(error,results){
				if (error) {
					res.status(error.code).send(error.message);
				}
				else{
						return res.status(status.OK).send({ error: false, data: results, message: 'Rssi' });
				}
			});
    }
  });
});
//route: post rssi
app.post("/rssis", verifyToken, function(req,res){
	jwt.verify(req.token, "config.secret", (err, authData) => {
    if(err) {
			res.status(403).send("Wrong token");
    } else {
			dataHandler.insertRssis(req,function(error,results){
				if (error) {
					res.status(error.code).send(error.message);
				}
				else{
					return res.status(status.CREATED).send({ error: false, data: results, message: 'New rssis has been created successfully' });
				}
			});
    }
  });
});
//route: update rssi
app.put('/rssis/:id_rssi', verifyToken, function(req,res){
	jwt.verify(req.token, "config.secret", (err, authData) => {
    if(err) {
			res.status(403).send("Wrong token");
    } else {
			dataHandler.updateRssis(req,function(error,results){
				if (error) {
					res.status(error.code).send(error.message);
				}
				else{
					return res.status(status.CREATED).send({ error: false, data: results, message: 'Rssi has been modified successfully.' });
				}
			});
    }
  });
});
//route: delete rssi
app.delete('/rssis/:id_rssi', verifyToken, function(req,res){
	jwt.verify(req.token, "config.secret", (err, authData) => {
    if(err) {
			res.status(403).send("Wrong token");
    } else {
			dataHandler.deleteRssis(req,function(error,results){
				if (error) {
					res.status(error.code).send(error.message);
				}
				else{
					return res.status(status.CREATED).send({ error: false, data: results, message: 'Rssi deleted successfully'});
				}
			});
    }
  });
});
// route: get rssis of a fingerprint
app.get('/fingerprints/:id_fingerprint/rssis', verifyToken, function (req, res) {
	jwt.verify(req.token, "config.secret", (err, authData) => {
    if(err) {
			res.status(403).send("Wrong token");
    } else {
			dataHandler.getRssisByIdFingerprint(req,function(error,results){
				if (error) {
					res.status(error.code).send(error.message);
				}
				else{
					return res.status(status.OK).send({ error: false, data: results, message: 'Rssis of a Fingerprint.' });
				}
			});
    }
  });
});
// route: get all spaces
app.get('/spaces', verifyToken, function (req, res) {
	jwt.verify(req.token, "config.secret", (err, authData) => {
    if(err) {
			res.status(403).send("Wrong token");
    } else {
			dataHandler.getSpaces(function(error,results){
				if(error){
					res.status(error.code).send(error.message);
				}
				else{
					res.status(status.OK).send({ error: false, data: results, message: 'Spaces' });
				}
			});
    }
  });
});
// route: get one space
app.get('/spaces/:id_space', verifyToken, function (req, res) {
	jwt.verify(req.token, "config.secret", (err, authData) => {
    if(err) {
			res.status(403).send("Wrong token");
    } else {
			dataHandler.getSpacesById(req,function(error,results){
				if (error) {
					res.status(error.code).send(error.message);
				}
				else{
						return res.status(status.OK).send({ error: false, data: results, message: 'Space' });
				}
			});
    }
  });
});
// route: post space
app.post('/spaces', verifyToken, function (req, res) {
	jwt.verify(req.token, "config.secret", (err, authData) => {
    if(err) {
			res.status(403).send("Wrong token");
    } else {
			dataHandler.insertSpaces(req,function(error,results){
				if (error) {
					res.status(error.code).send(error.message);
				}
				else{
					return res.status(status.CREATED).send({ error: false, data: results, message: 'New Space has been created successfully' });
				}
			});
    }
  });
});
// route: update space
app.put('/spaces/:id_space', verifyToken, function(req,res){
	jwt.verify(req.token, "config.secret", (err, authData) => {
    if(err) {
			res.status(403).send("Wrong token");
    } else {
			dataHandler.updateSpaces(req,function(error,results){
				if (error) {
					res.status(error.code).send(error.message);
				}
				else{
					return res.status(status.CREATED).send({ error: false, data: results, message: 'Space has been modified successfully.' });
				}
			});
    }
  });
});
// route: delete space
app.delete('/spaces/:id_space', verifyToken, function (req,res){
	jwt.verify(req.token, "config.secret", (err, authData) => {
    if(err) {
			res.status(403).send("Wrong token");
    } else {
			dataHandler.deleteSpaces(req,function(error,results){
				if (error) {
					res.status(error.code).send(error.message);
				}
				else{
					return res.status(status.CREATED).send({ error: false, data: results, message: 'Space has been deleted successfully'});
				}
			});
    }
  });
});
//route: get all Users
app.get('/users', verifyToken, function(req,res){
	jwt.verify(req.token, "config.secret", (err, authData) => {
    if(err) {
			res.status(403).send("Wrong token");
    } else {
			dataHandler.getUsers(function(error,results){
				if(error){
					res.status(error.code).send(error.message);
				}
				else{
					res.status(status.OK).send({ error: false, data: results, message: 'Users' });
				}
			});
    }
  });
});
//route: get one user
app.get('/users/:id_user', verifyToken, function(req,res){
	jwt.verify(req.token, "config.secret", (err, authData) => {
    if(err) {
			res.status(403).send("Wrong token");
    } else {
			dataHandler.getUsersById(req,function(error,results){
				if (error) {
					res.status(error.code).send(error.message);
				}
				else{
						return res.status(status.OK).send({ error: false, data: results, message: 'User' });
				}
			});
    }
  });
});
//route: register User
app.post('/register',function(req,res){
	dataHandler.register(req,function(error,results){
		if (error) {
			res.status(error.code).send(error.message);
		}
		else{
			return res.status(status.CREATED).send({ error: false, data: results, message: 'New User has been registered successfully' });
		}
	});
});
//route: login user
app.post('/login',function(req,res){
	dataHandler.login(req,function(error,results){
		if (error) {
			res.status(error.code).send(error.message);
		}
		else{
			return res.status(status.OK).send({ error: false, data: results, message: 'User logged in successfully' });
		}
	});
});
//route: update User
app.put('/users/:id_user', verifyToken, function(req,res){
	jwt.verify(req.token, "config.secret", (err, authData) => {
    if(err) {
			res.status(403).send("Wrong token");
    } else {
			dataHandler.updateUsers(req,function(error,results){
				if (error) {
					res.status(error.code).send(error.message);
				}
				else{
					return res.status(status.CREATED).send({ error: false, data: results, message: 'User has been modified successfully.' });
				}
			});
    }
  });
});
//route: delete Users
app.delete('/users/:id_user', verifyToken, function(req,res){
	jwt.verify(req.token, "config.secret", (err, authData) => {
    if(err) {
			res.status(403).send("Wrong token");
    } else {
			dataHandler.deleteUsers(req,function(error,results){
				if (error) {
					res.status(error.code).send(error.message);
				}
				else{
					return res.status(status.CREATED).send({ error: false, data: results, message: 'User has been deleted successfully'});
				}
			});
    }
  });
});
//route: get all user_type
app.get('/user_types', verifyToken, function (req, res){
	jwt.verify(req.token, "config.secret", (err, authData) => {
    if(err) {
			res.status(403).send("Wrong token");
    } else {
			dataHandler.getUserTypes(function(error,results){
				if(error){
					res.status(error.code).send(error.message);
				}
				else{
					res.status(status.OK).send({ error: false, data: results, message: 'User_types' });
				}
			});
    }
  });
});
// route: get one user_type
app.get('/user_types/:id_user_type', verifyToken, function (req, res) {
	jwt.verify(req.token, "config.secret", (err, authData) => {
    if(err) {
			res.status(403).send("Wrong token");
    } else {
			dataHandler.getUserTypesById(req,function(error,results){
				if (error) {
					res.status(error.code).send(error.message);
				}
				else{
						return res.status(status.OK).send({ error: false, data: results, message: 'User_type' });
				}
			});
    }
  });
});
//route: post user_Type
app.post('/user_types', verifyToken, function(req,res){
	jwt.verify(req.token, "config.secret", (err, authData) => {
    if(err) {
			res.status(403).send("Wrong token");
    } else {
			dataHandler.insertUserTypes(req,function(error,results){
				if (error) {
					res.status(error.code).send(error.message);
				}
				else{
					return res.status(status.CREATED).send({ error: false, data: results, message: 'New User_type has been created successfully' });
				}
			});
    }
  });
});
// route: update user_type
app.put('/user_types/:id_user_type', verifyToken, function(req,res){
	jwt.verify(req.token, "config.secret", (err, authData) => {
    if(err) {
			res.status(403).send("Wrong token");
    } else {
			dataHandler.updateUserTypes(req,function(error,results){
				if (error) {
					res.status(error.code).send(error.message);
				}
				else{
					return res.status(status.CREATED).send({ error: false, data: results, message: 'User_type has been modified successfully.' });
				}
			});
    }
  });
});
//route: delete user_Type
app.delete('/user_types/:id_user_type', verifyToken, function (req,res){
	jwt.verify(req.token, "config.secret", (err, authData) => {
    if(err) {
			res.status(403).send("Wrong token");
    } else {
			dataHandler.deleteUserTypes(req,function(error,results){
				if (error) {
					res.status(error.code).send(error.message);
				}
				else{
					return res.status(status.CREATED).send({ error: false, data: results, message: 'User_type has been deleted successfully'});
				}
			});
    }
  });
});
// route: get spaces for a fingerprint
app.get('/spaces/fingerprints/:id_fingerprint', verifyToken, function (req, res) {
	jwt.verify(req.token, "config.secret", (err, authData) => {
    if(err) {
			res.status(403).send("Wrong token");
    } else {
			dataHandler.getSpacesByIdFingerprint(req,function(error,results){
				if (error) {
					res.status(error.code).send(error.message);
				}
				else{
					return res.status(status.OK).send({ error: false, data: results, message: 'Spaces for a Fingerprint.' });
				}
			});
    }
  });
});
// route: get location for a fingerprint
app.get('/spaces/fingerprints/:id_fingerprint/location', verifyToken, (req, res) => {
  jwt.verify(req.token, "config.secret", (err, authData) => {
    if(err) {
			res.status(403).send("Wrong token");
    } else {
			dataHandler.getXYForFingerprint(req,function(error,results){
				if (error) {
					res.status(error.code).send(error.message);
				}
				else{
					return res.status(status.OK).send({ error: false, data: results, message: 'Location for a Fingerprint.', authData});
				}
			});
    }
  });
});
// route: get spaces for a user
app.get('/users/:id_user/spaces', verifyToken, function (req, res) {
	jwt.verify(req.token, "config.secret", (err, authData) => {
    if(err) {
			res.status(403).send("Wrong token");
    } else {
			dataHandler.getSpacesByIdUser(req,function(error,results){
				if (error) {
					res.status(error.code).send(error.message);
				}
				else{
					return res.status(status.OK).send({ error: false, data: results, message: 'Spaces for a User.' });
				}
			});
    }
  });
});
// route: get interest_points for a space
app.get('/spaces/:id_space/interest_points', verifyToken, function (req, res) {
	jwt.verify(req.token, "config.secret", (err, authData) => {
    if(err) {
			res.status(403).send("Wrong token");
    } else {
			dataHandler.getInterestPointsByIdSpace(req,function(error,results){
				if (error) {
					res.status(error.code).send(error.message);
				}
				else{
					return res.status(status.OK).send({ error: false, data: results, message: 'Interest_points for a Space.' });
				}
			});
    }
  });
});

// FORMAT OF TOKEN
// Authorization: Bearer <access_token>

// Verify Token
function verifyToken(req, res, next) {
  // Get auth header value
  const bearerHeader = req.headers['authorization'];
  // Check if bearer is undefined
  if(typeof bearerHeader !== 'undefined') {
    // Split at the space
    const bearer = bearerHeader.split(' ');
    // Get token from array
    const bearerToken = bearer[1];
    // Set the token
    req.token = bearerToken;
    // Next middleware
    next();
  } else {
    // Forbidden
    res.status(403).send("No authorization");
  }

}
