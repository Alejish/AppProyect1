const promise = require('bluebird');
const options = {
    promiseLib: promise,
    query: (e) => {}
}

const pgp = require('pg-promise')(options);
const types = pgp.pg.types;
types.setTypeParser(1114, function(stringValue){
    return stringValue;
});

const databaseConfing = {
    'host': 'localhost',
    'port': 5432,
    'database': 'app_db',
    'user': 'postgres',
    'password': '123456'
}

const db = pgp(databaseConfing);

module.exports = db;