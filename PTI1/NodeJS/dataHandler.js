const mysql = require('mysql');
const jwt = require('jsonwebtoken');
const bcrypt = require('bcryptjs');

var pool  = mysql.createPool({
  connectionLimit : 10,
  host            : 'localhost',
  user            : 'root',
  password        : 'password',
  database        : 'PositionbyFingerprint'
});

module.exports = {
    getAps: function(callback) {
      pool.query('SELECT * FROM ap', function (error, results) {
        if (error){
					let err={code: 500, message:error};
					return callback(err,null);
				}
        else{
          return callback(null,results);
        }
      });
		},
		getApsById: function(req, callback) {
      let query = "SELECT * FROM ap WHERE id_ap = ?";
			let table = [req.params.id_ap];
			query = mysql.format(query,table);
			pool.query(query, function (error, results) {
				if (error) {
					err={code: 500, message:error};
					return callback(err,null);
				}
				else{
					if(results.length>0)
						return callback(null,results);
					else{
						let err ={code: 404, message:"AP doesn't exist"};
						return callback(err,null);
					}
				}
			});
		},
		insertAps: function(req, callback) {
			if (!req.body.ssid || !req.body.mac) {
				let err ={code: 400, message:"Please provide AP"};
				return callback(err,null);
			}else{
				//verificar se mac já existe
				let query = "select id_ap from ap where mac =?";
				let table = [req.body.mac];
				query = mysql.format(query,table);
				pool.query(query, function (errorMac, resultsMac) {
					if (errorMac) {
						err={code: 500, message:errorMac};
						return callback(err,null);
					}
					else{
						if(resultsMac.length>0){
							//mac já existe, devolver id e atualizar ssid
							let query = "UPDATE ap SET ssid = ? WHERE id_ap = ?";
							let table = [req.body.ssid, resultsMac[0].id_ap];
							query = mysql.format(query,table);
							pool.query(query, function (errorUpdate, resultsUpdate) {
								if (errorUpdate) {
									err={code: 500, message:errorUpdate};
									return callback(err,null);
								}
								else{
									if(resultsUpdate.affectedRows>0){
										resultsUpdate.insertId=resultsMac[0].id_ap;
										return callback(null,resultsUpdate);
									}else{
										err={code: 404, message:"AP doesn't exist"};
										return callback(err,null);
									}
								}
							});
						}else{
							//mac não existe, pode fazer insert
							let query = "INSERT INTO ap (ssid,mac) VALUES (?,?)";
							let table = [req.body.ssid, req.body.mac];
							query = mysql.format(query,table);
							pool.query(query, function (errorInsert, resultsInsert) {
								if (errorInsert) {
									err={code: 500, message:errorInsert};
									return callback(err,null);
								}
								else{
									return callback(null,resultsInsert);
								}
							});
						}
					}
				});
			}
		},
		updateAps: function(req, callback) {
			if (!req.body.ssid || !req.body.mac) {
				let err ={code: 400, message:"Please provide AP"};
				return callback(err,null);
			}else{
				let query = "UPDATE ap SET ssid = ?,mac = ? WHERE id_ap = ?";
				let table = [req.body.ssid, req.body.mac,req.params.id_ap];
				query = mysql.format(query,table);
				pool.query(query, function (error, results) {
					if (error) {
						err={code: 500, message:error};
						return callback(err,null);
					}
					else{
						if(results.affectedRows>0)
							return callback(null,results);
						else{
							err={code: 404, message:"AP doesn't exist"};
							return callback(err,null);
						}
					}
				});
			}
		},
		deleteAps: function(req, callback) {
			let query = "DELETE from ap WHERE id_ap = ?";
			let table = [req.params.id_ap];
			query = mysql.format(query,table);
			pool.query(query, function (error, results) {
				if (error) {
					err={code: 500, message:error};
					return callback(err,null);
				}
				else{
					if(results.affectedRows>0)
						return callback(null,results);
					else{
						err={code: 404, message:"AP doesn't exist"};
						return callback(err,null);
					}
				}
			});
		},
		getContributionsHistory: function(callback) {
      pool.query('SELECT * FROM contribution_history', function (error, results) {
        if (error){
					let err={code: 500, message:error};
					return callback(err,null);
				}
        else{
          return callback(null,results);
        }
      });
		},
		getContributionsHistoryById: function(req, callback) {
      let query = "SELECT * FROM contribution_history WHERE id_contribution_history = ?";
			let table = [req.params.id_contributions_history];
			query = mysql.format(query,table);
			pool.query(query, function (error, results) {
				if (error) {
					err={code: 500, message:error};
					return callback(err,null);
				}
				else{
					if(results.length>0)
						return callback(null,results);
					else{
						let err ={code: 404, message:"Contributions History doesn't exist"};
						return callback(err,null);
					}
				}
			});
		},
		insertContributionsHistory: function(req, callback) {
			if (!req.body.date || !req.body.coordx_offset || !req.body.coordy_offset || !req.body.id_reference_point || !req.body.id_user) {
				let err ={code: 400, message:"Please provide Contributions History"};
				return callback(err,null);
			}else{
				// construct query
				let query = "INSERT INTO contribution_history (date,coordx_offset,coordy_offset,id_reference_point,id_user) VALUES (?,?,?,?,?)";
				let table = [req.body.date,req.body.coordx_offset,req.body.coordy_offset,req.body.id_reference_point,req.body.id_user];
				query = mysql.format(query,table);
				pool.query(query, function (error, results) {
					if (error) {
						err={code: 500, message:error};
						return callback(err,null);
					}
					else{
						return callback(null,results);
					}
				});
			}
		},
		updateContributionsHistory: function(req, callback) {
			if (!req.body.date || !req.body.coordx_offset || !req.body.coordy_offset || !req.body.id_reference_point || !req.body.id_user) {
				let err ={code: 400, message:"Please provide contribution_history"};
				return callback(err,null);
			}else{
				let query = "UPDATE contribution_history SET date = ?, coordx_offset = ?, coordy_offset = ?, id_reference_point = ?, id_user = ? WHERE id_contribution_history = ?";
				let table = [req.body.date,req.body.coordx_offset,req.body.coordy_offset,req.body.id_reference_point,req.body.id_user,req.params.id_contributions_history];
				query = mysql.format(query,table);
				pool.query(query, function (error, results) {
					if (error) {
						err={code: 500, message:error};
						return callback(err,null);
					}
					else{
						if(results.affectedRows>0)
							return callback(null,results);
						else{
							err={code: 404, message:"Contributions History doesn't exist"};
							return callback(err,null);
						}
					}
				});
			}
		},
		deleteContributionsHistory: function(req, callback) {
			let query = "DELETE from contribution_history WHERE id_contribution_history = ?";
			let table = [req.params.id_contributions_history];
			query = mysql.format(query,table);
			pool.query(query, function (error, results) {
				if (error) {
					err={code: 500, message:error};
					return callback(err,null);
				}
				else{
					if(results.affectedRows>0)
						return callback(null,results);
					else{
						err={code: 404, message:"Contribution_history doesn't exist"};
						return callback(err,null);
					}
				}
			});
		},
		getFingerprintAps: function(callback) {
      pool.query('SELECT * FROM fingerprint_ap', function (error, results) {
        if (error){
					let err={code: 500, message:error};
					return callback(err,null);
				}
        else{
          return callback(null,results);
        }
      });
		},
		getFingerprintApsById: function(req, callback) {
      let query = "SELECT * FROM fingerprint_ap WHERE id_fingerprint_ap = ?";
			let table = [req.params.id_fingerprint_aps];
			query = mysql.format(query,table);
			pool.query(query, function (error, results) {
				if (error) {
					err={code: 500, message:error};
					return callback(err,null);
				}
				else{
					if(results.length>0)
						return callback(null,results);
					else{
						let err ={code: 404, message:"Fingerprint_aps doesn't exist"};
						return callback(err,null);
					}
				}
			});
		},
		insertFingerprintAps: function(req, callback) {
			if(!req.body.id_fingerprint || !req.body.id_ap){
				let err ={code: 400, message:"Please provide Fingerprint_aps"};
				return callback(err,null);
			}else{
				// construct query
				let query = "INSERT INTO fingerprint_ap (id_fingerprint,id_ap) VALUES (?,?)";
				let table = [req.body.id_fingerprint,req.body.id_ap];
				query = mysql.format(query,table);
				pool.query(query, function (error, results) {
					if (error) {
						err={code: 500, message:error};
						return callback(err,null);
					}
					else{
						return callback(null,results);
					}
				});
			}
		},
		updateFingerprintAps: function(req, callback) {
			if(!req.body.id_fingerprint || !req.body.id_ap){
				let err ={code: 400, message:"Please provide fingerprint_ap"};
				return callback(err,null);
			}else{
				let query = "UPDATE fingerprint_ap SET id_fingerprint = ?, id_ap = ? WHERE id_fingerprint_ap = ?";
				let table = [req.body.id_fingerprint, req.body.id_ap,req.params.id_fingerprint_ap];
				query = mysql.format(query,table);
				pool.query(query, function (error, results) {
					if (error) {
						err={code: 500, message:error};
						return callback(err,null);
					}
					else{
						if(results.affectedRows>0)
							return callback(null,results);
						else{
							err={code: 404, message:"Fingerprint_ap doesn't exist"};
							return callback(err,null);
						}
					}
				});
			}
		},
		deleteFingerprintAps: function(req, callback) {
			let query = "DELETE FROM fingerprint_ap WHERE id_fingerprint_ap = ?";
			let table = [req.params.id_fingerprint_ap];
			query = mysql.format(query,table);
			pool.query(query, function (error, results) {
				if (error) {
					err={code: 500, message:error};
					return callback(err,null);
				}
				else{
					if(results.affectedRows>0)
						return callback(null,results);
					else{
						err={code: 404, message:"Fingerprint_ap doesn't exist"};
						return callback(err,null);
					}
				}
			});
		},
		getFingerprints: function(callback) {
      pool.query('SELECT * FROM fingerprint', function (error, results) {
        if (error){
					let err={code: 500, message:error};
					return callback(err,null);
				}
        else{
          return callback(null,results);
        }
      });
		},
		getFingerprintsById: function(req, callback) {
      let query = "SELECT * FROM fingerprint WHERE id_fingerprint = ?";
			let table = [req.params.id_fingerprint];
			query = mysql.format(query,table);
			pool.query(query, function (error, results) {
				if (error) {
					err={code: 500, message:error};
					return callback(err,null);
				}
				else{
					if(results.length>0)
						return callback(null,results);
					else{
						let err ={code: 404, message:"Fingerprint doesn't exist"};
						return callback(err,null);
					}
				}
			});
		},
		insertFingerprints: function(req, callback) {
			if(!req.body.date || !req.body.id_reference_point || !req.body.id_user){
				let err ={code: 400, message:"Please provide Fingerprints"};
				return callback(err,null);
			}else{
				// construct query
				let query = "INSERT INTO fingerprint (date,id_reference_point,id_user) VALUES (?,?,?)";
				let table = [req.body.date,req.body.id_reference_point,req.body.id_user];
				query = mysql.format(query,table);
				pool.query(query, function (error, results) {
					if (error) {
						err={code: 500, message:error};
						return callback(err,null);
					}
					else{
						return callback(null,results);
					}
				});
			}
		},
		updateFingerprints: function(req, callback) {
			if(!req.body.date || !req.body.id_reference_point || !req.body.id_user){
				let err ={code: 400, message:"Please provide fingerprint"};
				return callback(err,null);
			}else{
				let query = "UPDATE fingerprint SET date = ?, id_reference_point = ?, id_user = ? WHERE id_fingerprint = ?";
				let table = [req.body.date,req.body.id_reference_point,req.body.id_user,req.params.id_fingerprint];
				query = mysql.format(query,table);
				pool.query(query, function (error, results) {
					if (error) {
						err={code: 500, message:error};
						return callback(err,null);
					}
					else{
						if(results.affectedRows>0)
							return callback(null,results);
						else{
							err={code: 404, message:"Fingerprint doesn't exist"};
							return callback(err,null);
						}
					}
				});
			}
		},
		deleteFingerprints: function(req, callback) {
			let query = "DELETE FROM fingerprint WHERE id_fingerprint = ?";
			let table = [req.params.id_fingerprint];
			query = mysql.format(query,table);
			pool.query(query, function (error, results) {
				if (error) {
					err={code: 500, message:error};
					return callback(err,null);
				}
				else{
					if(results.affectedRows>0)
						return callback(null,results);
					else{
						err={code: 404, message:"Fingerprint doesn't exist"};
						return callback(err,null);
					}
				}
			});
		},
		getInterestPoints: function(callback) {
      pool.query('SELECT * FROM interest_point', function (error, results) {
        if (error){
					let err={code: 500, message:error};
					return callback(err,null);
				}
        else{
          return callback(null,results);
        }
      });
		},
		getInterestPointsById: function(req, callback) {
      let query = "SELECT * FROM interest_point WHERE id_interest_point = ?";
			let table = [req.params.id_interest_points];
			query = mysql.format(query,table);
			pool.query(query, function (error, results) {
				if (error) {
					err={code: 500, message:error};
					return callback(err,null);
				}
				else{
					if(results.length>0)
						return callback(null,results);
					else{
						let err ={code: 404, message:"Interest_point doesn't exist"};
						return callback(err,null);
					}
				}
			});
		},
		insertInterestPoints: function(req, callback) {
			if(!req.body.name || !req.body.coordx || !req.body.coordy || !req.body.id_space){
				let err ={code: 400, message:"Please provide interest_point"};
				return callback(err,null);
			}else{
				// construct query
				let query = "INSERT INTO interest_point (name,coordx,coordy,id_space) VALUES (?,?,?,?)";
				let table = [req.body.name,req.body.coordx,req.body.coordy,req.body.id_space];
				query = mysql.format(query,table);
				pool.query(query, function (error, results) {
					if (error) {
						err={code: 500, message:error};
						return callback(err,null);
					}
					else{
						return callback(null,results);
					}
				});
			}
		},
		updateInterestPoints: function(req, callback) {
			if(!req.body.name ||!req.body.coordx || !req.body.coordy || !req.body.id_space){
				let err ={code: 400, message:"Please provide interest_point"};
				return callback(err,null);
			}else{
				let query = "UPDATE interest_point SET name = ?, coordx = ?, coordy = ?, id_space = ? WHERE id_interest_point = ?";
				let table = [req.body.name, req.body.coordx, req.body.coordy, req.body.id_space, req.params.id_interest_points];
				query = mysql.format(query,table);
				pool.query(query, function (error, results) {
					if (error) {
						err={code: 500, message:error};
						return callback(err,null);
					}
					else{
						if(results.affectedRows>0)
							return callback(null,results);
						else{
							err={code: 404, message:"Interest_point doesn't exist"};
							return callback(err,null);
						}
					}
				});
			}
		},
		deleteInterestPoints: function(req, callback) {
			let query = "DELETE FROM interest_point WHERE id_interest_point = ?";
			let table = [req.params.id_interest_points];
			query = mysql.format(query,table);
			pool.query(query, function (error, results) {
				if (error) {
					err={code: 500, message:error};
					return callback(err,null);
				}
				else{
					if(results.affectedRows>0)
						return callback(null,results);
					else{
						err={code: 404, message:"Interest_point doesn't exist"};
						return callback(err,null);
					}
				}
			});
		},
		getReferencePoints: function(callback) {
      pool.query('SELECT * FROM reference_point', function (error, results) {
        if (error){
					let err={code: 500, message:error};
					return callback(err,null);
				}
        else{
          return callback(null,results);
        }
      });
		},
		getReferencePointsById: function(req, callback) {
      let query = "SELECT * FROM reference_point WHERE id_reference_point = ?";
			let table = [req.params.id_reference_point];
			query = mysql.format(query,table);
			pool.query(query, function (error, results) {
				if (error) {
					err={code: 500, message:error};
					return callback(err,null);
				}
				else{
					if(results.length>0)
						return callback(null,results);
					else{
						let err ={code: 404, message:"Reference Point doesn't exist"};
						return callback(err,null);
					}
				}
			});
		},
		insertReferencePoints: function(req, callback) {
			if(!req.body.name || !req.body.coordx || !req.body.coordy || !req.body.isOffline || !req.body.id_space){
				let err ={code: 400, message:"Please provide reference point"};
				return callback(err,null);
			}else{
				// construct query
				let query = "INSERT INTO reference_point (name,coordx,coordy,isOffline,id_space) VALUES (?,?,?,?,?)";
				let table = [req.body.name,req.body.coordx,req.body.coordy,req.body.isOffline,req.body.id_space];
				query = mysql.format(query,table);
				pool.query(query, function (error, results) {
					if (error) {
						err={code: 500, message:error};
						return callback(err,null);
					}
					else{
						return callback(null,results);
					}
				});
			}
		},
		updateReferencePoints: function(req, callback) {
			if(!req.body.name ||!req.body.coordx || !req.body.coordy || !req.body.isOffline || !req.body.id_space){
				let err ={code: 400, message:"Please provide reference point"};
				return callback(err,null);
			}else{
				let query = "UPDATE reference_point SET name = ?, coordx = ?, coordy = ?, isOffline = ?, id_space = ? WHERE id_reference_point = ?";
				let table = [req.body.name, req.body.coordx, req.body.coordy, req.body.isOffline, req.body.id_space, req.params.id_reference_point];
				query = mysql.format(query,table);
				pool.query(query, function (error, results) {
					if (error) {
						err={code: 500, message:error};
						return callback(err,null);
					}
					else{
						if(results.affectedRows>0)
							return callback(null,results);
						else{
							err={code: 404, message:"Reference point doesn't exist"};
							return callback(err,null);
						}
					}
				});
			}
		},
		deleteReferencePoints: function(req, callback) {
			let query = "DELETE FROM reference_point WHERE id_reference_point = ?";
			let table = [req.params.id_reference_point];
			query = mysql.format(query,table);
			pool.query(query, function (error, results) {
				if (error) {
					err={code: 500, message:error};
					return callback(err,null);
				}
				else{
					if(results.affectedRows>0)
						return callback(null,results);
					else{
						err={code: 404, message:"Reference Point doesn't exist"};
						return callback(err,null);
					}
				}
			});
		},
		getRssis: function(callback) {
      pool.query('SELECT * FROM rssi', function (error, results) {
        if (error){
					let err={code: 500, message:error};
					return callback(err,null);
				}
        else{
          return callback(null,results);
        }
      });
		},
		getRssisById: function(req, callback) {
      let query = "SELECT * FROM rssi WHERE id_rssi = ?"
			let table = [req.params.id_rssi];
			query = mysql.format(query,table);
			pool.query(query, function (error, results) {
				if (error) {
					err={code: 500, message:error};
					return callback(err,null);
				}
				else{
					if(results.length>0)
						return callback(null,results);
					else{
						let err ={code: 404, message:"Rssi doesn't exist"};
						return callback(err,null);
					}
				}
			});
		},
		insertRssis: function(req, callback) {
			if(!req.body.value || !req.body.id_ap){
				let err ={code: 400, message:"Please provide Rssi"};
				return callback(err,null);
			}else{
				// construct query
				let query = "INSERT INTO rssi (value,id_ap,id_fingerprint) VALUES (?,?,?)";
				let table = [req.body.value,req.body.id_ap,req.body.id_fingerprint];
				query = mysql.format(query,table);
				pool.query(query, function (error, results) {
					if (error) {
						err={code: 500, message:error};
						return callback(err,null);
					}
					else{
						return callback(null,results);
					}
				});
			}
		},
		updateRssis: function(req, callback) {
			if(!req.body.value || !req.body.id_ap){
				let err ={code: 400, message:"Please provide rssi"};
				return callback(err,null);
			}else{
				let query = "UPDATE rssi SET value = ?, id_ap = ?, id_fingerprint = ? WHERE id_rssi = ?";
				let table = [req.body.value,req.body.id_ap,req.body.id_fingerprint,req.params.id_rssi];
				query = mysql.format(query,table);
				pool.query(query, function (error, results) {
					if (error) {
						err={code: 500, message:error};
						return callback(err,null);
					}
					else{
						if(results.affectedRows>0)
							return callback(null,results);
						else{
							err={code: 404, message:"Rssis doesn't exist"};
							return callback(err,null);
						}
					}
				});
			}
		},
		deleteRssis: function(req, callback) {
			let query = "DELETE FROM rssi WHERE id_rssi = ?";
			let table = [req.params.id_rssi];
			query = mysql.format(query,table);
			pool.query(query, function (error, results) {
				if (error) {
					err={code: 500, message:error};
					return callback(err,null);
				}
				else{
					if(results.affectedRows>0)
						return callback(null,results);
					else{
						err={code: 404, message:"Rssi doesn't exist"};
						return callback(err,null);
					}
				}
			});
		},
		getSpaces: function(callback) {
      pool.query('SELECT * FROM space', function (error, results) {
        if (error){
					let err={code: 500, message:error};
					return callback(err,null);
				}
        else{
          return callback(null,results);
        }
      });
		},
		getSpacesById: function(req, callback) {
      let query = "SELECT * FROM space WHERE id_space = ?";
			let table = [req.params.id_space];
			query = mysql.format(query,table);
			pool.query(query, function (error, results) {
				if (error) {
					err={code: 500, message:error};
					return callback(err,null);
				}
				else{
					if(results.length>0)
						return callback(null,results);
					else{
						let err ={code: 404, message:"Space doesn't exist"};
						return callback(err,null);
					}
				}
			});
		},
		insertSpaces: function(req, callback) {
			if (!req.body.name || !req.body.description || !req.body.map_path || !req.body.map_width || !req.body.map_length || !req.body.id_user) {
				let err ={code: 400, message:"Please provide Space"};
				return callback(err,null);
			}else{
				// construct query

				let query = "INSERT INTO space (name,description,map_path,map_width,map_length,id_user) VALUES (?,?,?,?,?,?)";
				let table = [req.body.name, req.body.description, req.body.map_path, req.body.map_width, req.body.map_length, req.body.id_user];
				query = mysql.format(query,table);
				pool.query(query, function (error, results) {
					if (error) {
						err={code: 500, message:error};
						return callback(err,null);
					}
					else{
						return callback(null,results);
					}
				});
			}
		},
		updateSpaces: function(req, callback) {
			if (!req.body.name || !req.body.description || !req.body.map_path || !req.body.map_width || !req.body.map_length || !req.body.id_user) {
				let err ={code: 400, message:"Please provide Space"};
				return callback(err,null);
			}else{
				let query = "UPDATE space SET name = ?,description = ?, map_path = ?, map_width = ?, map_length = ?, id_user = ? WHERE id_space = ?";
				let table = [req.body.name, req.body.description, req.body.map_path, req.body.map_width,req.body.map_length, req.body.id_user, req.params.id_space];
				query = mysql.format(query,table);
				pool.query(query, function (error, results) {
					if (error) {
						err={code: 500, message:error};
						return callback(err,null);
					}
					else{
						if(results.affectedRows>0)
							return callback(null,results);
						else{
							err={code: 404, message:"Space doesn't exist"};
							return callback(err,null);
						}
					}
				});
			}
		},
		deleteSpaces: function(req, callback) {
			let query = "DELETE from space WHERE id_space = ?";
			let table = [req.params.id_space];
			query = mysql.format(query,table);
			pool.query(query, function (error, results) {
				if (error) {
					err={code: 500, message:error};
					return callback(err,null);
				}
				else{
					if(results.affectedRows>0)
						return callback(null,results);
					else{
						err={code: 404, message:"Space doesn't exist"};
						return callback(err,null);
					}
				}
			});
		},
		getUsers: function(callback) {
      pool.query('SELECT * FROM user', function (error, results) {
        if (error){
					let err={code: 500, message:error};
					return callback(err,null);
				}
        else{
          return callback(null,results);
        }
      });
		},
		getUsersById: function(req, callback) {
      let query = "SELECT * FROM user WHERE id_user = ?";
			let table = [req.params.id_user];
			query = mysql.format(query,table);
			pool.query(query, function (error, results) {
				if (error) {
					err={code: 500, message:error};
					return callback(err,null);
				}
				else{
					if(results.length>0)
						return callback(null,results);
					else{
						let err ={code: 404, message:"User doesn't exist"};
						return callback(err,null);
					}
				}
			});
		},
		register: function(req, callback) {
			if(!req.body.name || !req.body.username || !req.body.password || !req.body.email || !req.body.address || !req.body.birthday || !req.body.date_registration || !req.body.id_user_type){
				let err ={code: 400, message:"Please provide User"};
				return callback(err,null);
			}else{
				//verificar se email já existe
				let query = "select * from user where email = ?";
				let table = [req.body.email];
				query = mysql.format(query,table);
				pool.query(query, function (errorMail, resultsMail) {
					if (errorMail) {
						err={code: 500, message:errorMail};
						return callback(err,null);
					}
					else{
						if(resultsMail.length>0){
							//mail já existe, não é possivel registar
							err={code: 400, message:"Email already exists"};
							return callback(err,null);
						}else{
							let hashedPassword = bcrypt.hashSync(req.body.password, 8);
							//mail não existe, pode fazer insert
							let query = "INSERT INTO user (name, username, password, email, address, birthday, date_registration, id_user_type) VALUES (?,?,?,?,?,?,?,?)"
							let table = [req.body.name, req.body.username, hashedPassword, req.body.email, req.body.address, req.body.birthday, req.body.date_registration, req.body.id_user_type];
							query = mysql.format(query,table);
							pool.query(query, function (error, results) {
								if (error) {
									err={code: 500, message:error};
									return callback(err,null);
								}
								else{
									//create token
									var token = jwt.sign({ id: results.insertId }, "config.secret", {
										expiresIn: 86400 // expires in 24 hours
									});
									results.token = token;
									results.auth = true;
									console.log(JSON.stringify(results));
									return callback(null,results);
								}
							});
						}
					}
				});
			}
		},
		login: function(req, callback) {
			if(!req.body.password || !req.body.email){
				let err ={code: 400, message:"Please provide User"};
				return callback(err,null);
			}else{
				let query = "select * from user where email = ?";
				let table = [req.body.email];
				query = mysql.format(query,table);
				pool.query(query, function (error, results) {
					if (error) {
						err={code: 500, message:error};
						return callback(err,null);
					}
					else{
						if(results.length>0){
							//check password
							console.log(JSON.stringify(req.body)+" \n"+JSON.stringify(results));
							var passwordIsValid = bcrypt.compareSync(req.body.password, results[0].password);
							if (!passwordIsValid){
								err={code: 401, message: "Wrong password"};
								return callback(err,null);
							}
							var token = jwt.sign({ id: results.id_user }, "config.secret", {
								expiresIn: 86400 // expires in 24 hours
							});
							results[0].token = token;
							results[0].auth = true;
							console.log(JSON.stringify(results));
							return callback(null,results[0]);
						}else{
							let err ={code: 404, message:"User doesn't exist"};
							return callback(err,null);
						}
					}
				});
			}
		},
		updateUsers: function(req, callback) {
			if(!req.body.name || !req.body.username || !req.body.password || !req.body.email || !req.body.address || !req.body.birthday || !req.body.date_registration || !req.body.id_user_type){
				let err ={code: 400, message:"Please provide User"};
				return callback(err,null);
			}else{
				let query = "UPDATE user SET name = ?, username = ?, password = ?, email = ?, address = ?, birthday = ?, date_registration = ?, id_user_type = ? WHERE id_user = ?";
				let table = [req.body.name, req.body.username, req.body.password, req.body.email, req.body.address, req.body.birthday, req.body.date_registration, req.body.id_user_type,req.params.id_user];
				query = mysql.format(query,table);
				pool.query(query, function (error, results) {
					if (error) {
						err={code: 500, message:error};
						return callback(err,null);
					}
					else{
						if(results.affectedRows>0)
							return callback(null,results);
						else{
							err={code: 404, message:"User doesn't exist"};
							return callback(err,null);
						}
					}
				});
			}
		},
		deleteUsers: function(req, callback) {
			let query = "DELETE FROM user WHERE id_user = ?";
			let table = [req.params.id_user];
			query = mysql.format(query,table);
			pool.query(query, function (error, results) {
				if (error) {
					err={code: 500, message:error};
					return callback(err,null);
				}
				else{
					if(results.affectedRows>0)
						return callback(null,results);
					else{
						err={code: 404, message:"User doesn't exist"};
						return callback(err,null);
					}
				}
			});
		},
		getUserTypes: function(callback) {
      pool.query('SELECT * FROM user_type', function (error, results) {
        if (error){
					let err={code: 500, message:error};
					return callback(err,null);
				}
        else{
          return callback(null,results);
        }
      });
		},
		getUserTypesById: function(req, callback) {
      let query = "SELECT * FROM user_type WHERE id_user_type = ?";
			let table = [req.params.id_user_type];
			query = mysql.format(query,table);
			pool.query(query, function (error, results) {
				if (error) {
					err={code: 500, message:error};
					return callback(err,null);
				}
				else{
					if(results.length>0)
						return callback(null,results);
					else{
						let err ={code: 404, message:"User_type doesn't exist"};
						return callback(err,null);
					}
				}
			});
		},
		insertUserTypes: function(req, callback) {
			if(!req.body.description){
				let err ={code: 400, message:"Please provide User_type"};
				return callback(err,null);
			}else{
				// construct query
				let query = "INSERT INTO user_type (description) VALUES (?)";
				let table =[req.body.description];
				query = mysql.format(query,table);
				pool.query(query, function (error, results) {
					if (error) {
						err={code: 500, message:error};
						return callback(err,null);
					}
					else{
						return callback(null,results);
					}
				});
			}
		},
		updateUserTypes: function(req, callback) {
			if (!req.body.description) {
				let err ={code: 400, message:"Please provide User_type"};
				return callback(err,null);
			}else{
				let query = "UPDATE user_type SET description = ? WHERE id_user_type = ?";
				let table = [req.body.description,req.params.id_user_type];
				query = mysql.format(query,table);
				pool.query(query, function (error, results) {
					if (error) {
						err={code: 500, message:error};
						return callback(err,null);
					}
					else{
						if(results.affectedRows>0)
							return callback(null,results);
						else{
							err={code: 404, message:"User_type doesn't exist"};
							return callback(err,null);
						}
					}
				});
			}
		},
		deleteUserTypes: function(req, callback) {
			let query = "DELETE from user_type WHERE id_user_type = ?";
			let table = [req.params.id_user_type];
			query = mysql.format(query,table);
			pool.query(query, function (error, results) {
				if (error) {
					err={code: 500, message:error};
					return callback(err,null);
				}
				else{
					if(results.affectedRows>0)
						return callback(null,results);
					else{
						err={code: 404, message:"User_type doesn't exist"};
						return callback(err,null);
					}
				}
			});
		},
		getSpacesByIdFingerprint: function(req, callback) {
			/*
			select space.id_space,space.name from space
			inner join reference_point on reference_point.id_space = space.id_space and reference_point.isOffline = TRUE
			inner join fingerprint on fingerprint.id_reference_point = reference_point.id_reference_point
			inner join fingerprint_ap on fingerprint_ap.id_fingerprint = fingerprint.id_fingerprint
			where fingerprint_ap.id_ap in (select id_ap from fingerprint_ap where id_fingerprint = ?)
			*/
      let query = "select distinct space.id_space,space.name from space inner join reference_point on reference_point.id_space = space.id_space and reference_point.isOffline = TRUE inner join fingerprint on fingerprint.id_reference_point = reference_point.id_reference_point inner join fingerprint_ap on fingerprint_ap.id_fingerprint = fingerprint.id_fingerprint where fingerprint_ap.id_ap in (select id_ap from fingerprint_ap where id_fingerprint = ?)";
			let table = [req.params.id_fingerprint];
			query = mysql.format(query,table);
			pool.query(query, function (error, results) {
				if (error) {
					let err={code: 500, message:error};
					return callback(err,null);
				}
				else{
					if(results.length>0)
						return callback(null,results);
					else{
						let err ={code: 404, message:"There is no Spaces"};
						return callback(err,null);
					}
				}
			});
		},
		getRssisByIdFingerprint: function(req, callback) {
      let query = "select * from rssi where id_fingerprint = ?";
			let table = [req.params.id_fingerprint];
			query = mysql.format(query,table);
			pool.query(query, function (error, results) {
				if (error) {
					let err={code: 500, message:error};
					return callback(err,null);
				}
				else{
					if(results.length>0)
						return callback(null,results);
					else{
						let err ={code: 404, message:"There is no Rssis"};
						return callback(err,null);
					}
				}
			});
		},
		getSpacesByIdUser: function(req, callback) {
      let query = "select * from space where id_user = ?";
			let table = [req.params.id_user];
			query = mysql.format(query,table);
			pool.query(query, function (error, results) {
				if (error) {
					let err={code: 500, message:error};
					return callback(err,null);
				}
				else{
					if(results.length>0)
						return callback(null,results);
					else{
						let err ={code: 404, message:"There is no Spaces for User "+req.params.id_user};
						return callback(err,null);
					}
				}
			});
		},
		getInterestPointsByIdSpace: function(req,callback) {
			let query = "select * from interest_point where id_space = ?";
			let table = [req.params.id_space];
			query = mysql.format(query,table);
			pool.query(query, function (error, results) {
				if (error) {
					let err={code: 500, message:error};
					return callback(err,null);
				}
				else{
					if(results.length>0)
						return callback(null,results);
					else{
						let err ={code: 404, message:"There is no interest_points for Space "+req.params.id_space};
						return callback(err,null);
					}
				}
			});
		},
		getFingeprint: function(req){
			return new Promise(
				function (resolve, reject) {
					let query = "select * from rssi where id_fingerprint = ? ORDER BY id_rssi ASC";
					let table = [req.params.id_fingerprint];
					let fprint=[];
					query = mysql.format(query,table);
					pool.query(query, function (error, results) {
						if (error) {
							let err={code: 500, message:error};
							reject(err);
						}
						else{
							if(results.length>0){
								results.forEach(element => {
									fprint.push({ id : element.id_ap, rssi : element.value});
									console.log("Fingerprint:"+JSON.stringify(fprint));
								});
								resolve(fprint);
							}
							else{
								let err ={code: 404, message:"There is no Rssis in fingerprint "+req.params.id_fingerprint};
								reject(err);
							}
						}
					});
				}
			);
		},
		getRefPointsXY: function(req){
			return new Promise(
				function (resolve, reject) {
					let query = "select reference_point.id_reference_point, reference_point.coordx, reference_point.coordy from reference_point where id_space = (select reference_point.id_space from reference_point inner join fingerprint on reference_point.id_reference_point = fingerprint.id_reference_point where fingerprint.id_fingerprint = ?) and reference_point.isOffline = true ORDER BY reference_point.id_reference_point ASC";
					let table = [req.params.id_fingerprint];
					let ref_points = [];
					query = mysql.format(query,table);
					pool.query(query, function (error, results) {
						if (error) {
							let err={code: 500, message:error};
							reject(err);
						}
						else{
							if(results.length>0){
								//organizar resultados em json no ref_points
								let lastid=0;
								results.forEach(er => {
									ref_points.push({ id: er.id_reference_point, x: er.coordx, y: er.coordy, values: []});
								});
								resolve(ref_points);
							}
							else{
								let err ={code: 404, message:"There is no Reference_Points in fingerprint "+req.params.id_fingerprint};
								reject(err);
							}
						}
					});
				}
			);
		},
		insertValuesRefPoints: function(ref_points){
			return new Promise(
				function (resolve, reject) {
					//percorrer ref_points
					for(let i=0; i<ref_points.length; i++){
						let query = "select rssi.id_ap, rssi.value from rssi inner join fingerprint on fingerprint.id_fingerprint = rssi.id_fingerprint where fingerprint.id_reference_point = ? ORDER BY rssi.id_ap ASC";
						let table = ref_points[i].id;
						query = mysql.format(query,table);
						pool.query(query, function (error, results) {
							if (error) {
								let err={code: 500, message:error};
								reject(err);
							}
							else{
								if(results.length>0){
									//percorrer rssis e inserir nos values do ref_point
									results.forEach(element => {
										ref_points[i].values.push({id: element.id_ap, rssi: element.value});
									});
									if(i==(ref_points.length-1)){
										resolve(ref_points);
									}
								}
								else{
									let err ={code: 404, message:"There is no Rssis in reference_point "+ref_points[i].id};
									reject(err);
								}
							}
						});
					}
				}
			);
		},
		getXYForFingerprint: function(req, callback) {
			module.exports.getFingeprint(req).then(function(fprint){
				module.exports.getRefPointsXY(req).then(function(ref_pointsXY){
					module.exports.insertValuesRefPoints(ref_pointsXY).then(function(ref_points){
						//ordernar fingerprint por id
						fprint.sort(function(a, b){return a.id-b.id});
						console.log("FingerprintFinal: "+JSON.stringify(fprint));				
						console.log("RefPointsFinal: "+JSON.stringify(ref_points));
						//algoritmo
						console.log("numero de refs: "+ref_points.length);

						if(ref_points.length>0){
							//tem dados suficientes
							//definir k consoante numero de refs points
							var k;
							if(ref_points.length>3){
								k=3;
							}else{
								k=ref_points.length;
							}
							var aps=[];
							//print ref points and insert aps
							ref_points.forEach(element => {
								console.log(JSON.stringify("element:"+JSON.stringify(element)));
								element.values.forEach(ev => {
									console.log("ev:"+JSON.stringify(ev));
									console.log("id:"+ev.id+" rssi:"+ev.rssi);
									aps.push(ev.id);
								});
								console.log("x:"+element.x);
								console.log("y:"+element.y);
							});

							//filtrar ids unicos de aps
							var unique = aps.filter((v, i, a) => a.indexOf(v) === i);

							//inserir aps com rssi 0
							for(let i=0; i<ref_points.length; i++){
								unique.forEach(uap => {
									var found = ref_points[i].values.find(function(e) {
										return e.id==uap;
									});
									if(found)
										console.log("found id:"+found.id+" rssi:"+found.rssi);
									else{
										ref_points[i].values.push({id : uap, rssi : 0});
									}
								});
							}

							//print ref points preenchidos
							ref_points.forEach(element => {
								element.values.forEach(ev => {
									console.log("id:"+ev.id+" rssi:"+ev.rssi);
								});
								console.log("x:"+element.x);
								console.log("y:"+element.y);
							});

							//implementação algoritmo
							var distance = [];
							var sum;
							ref_points.forEach(refp => {
								sum=0;
								refp.values.forEach(raps => {
									//get fap
									var fap = fprint.find(function(e) {
										return e.id==raps.id;
									});
									if(fap){
										console.log("sumBefore="+sum);
										sum = (sum + (Math.pow((raps.rssi-fap.rssi),2)));
										console.log("sum="+sum);
										console.log("raps.value="+raps.rssi);
										console.log("fap:"+JSON.stringify(fap)+" raps:"+JSON.stringify(raps));
										console.log("fap.rssi="+fap.rssi);
									}
									else{
										sum = (sum + (Math.pow(raps.rssi-0,2)));
										console.log("sum="+sum);
										console.log("raps.value="+raps.rssi);
									}
								});
								distance.push(Math.sqrt(sum));
								console.log("sum:"+sum)
								console.log("FIM:"+Math.sqrt(sum));
							});

							console.log(distance);

							let nearest=[];

							for(let i=0; i<ref_points.length; i++){
								nearest.push({
									index : i,
									distance : distance[i]
								});
							}

							nearest = nearest.sort(function (a, b) {
								return a.distance - b.distance;
							})
							.slice(0,k);

							console.log("Verificar distancias nulas. k="+k);
							let distanciazero=0;
							for(let i=0; i<k; i++){
								if(nearest[i].distance==0){
									console.log("Distancia zero");
									distanciazero=i;
									distanciazero++;
								}
							}
							if(distanciazero==0){
								console.log("Distancia != zero");
								let weights=[];
								nearest.forEach(nelem => {
									weights.push({
										index : nelem.index,
										weight : (1/nelem.distance)
									});
								});

								let sumW=0;

								for(let i=0; i<k; i++){
									sumW+=weights[i].weight;
								}
								let x=0,y=0;
								for(let i=0;i<k;i++){
									console.log("weights-index"+weights[i].index);
									console.log("weights-weight"+weights[i].weight);
									console.log("sumW"+sumW);
									console.log("refpoints.x"+ref_points[weights[i].index].x);
									console.log("refpoints.y"+ref_points[weights[i].index].y);
									weights[i].weight = weights[i].weight / sumW;
									x+=ref_points[weights[i].index].x*weights[i].weight;
									y+=ref_points[weights[i].index].y*weights[i].weight;
								}

								console.log("X:"+x);
								console.log("Y:"+y);

								let location = {x:x,y:y};
								console.log("x:"+location.x);
								console.log("y:"+location.y);
								//enviar location
								return callback(null,location);
							}else{
								console.log("Distancia == zero");
								let location = {x:ref_points[nearest[distanciazero-1].index].x,y:ref_points[nearest[distanciazero-1].index].y};
								console.log("location: "+JSON.stringify(location))
								//enviar location
								return callback(null,location);
							}

						}else{
							//não existem dados para enviar location
							let err ={code: 400, message:"There isn't enough data to process location "+req.params.id_fingerprint};
							return callback(err,null);
						}
					}).catch(function(error){
						return callback(error,null);
					})
				}).catch(function(error){
					return callback(error,null);
				})
			}).catch(function(error){
				return callback(error,null);
			})
		}
}
