const db = require('../config/config');
const bcript = require('bcryptjs');
const { oneOrNone } = require('../config/config');

const User = {};

User.getAll = async () => {
    const sql = `SELECT * FROM users`;

    return await db.manyOrNone(sql);
}

User.findByEmail = (email) => {
    const sql = `SELECT id, email, name, lastname, phone, image, password, session_token
    FROM users
    WHERE email= $1`;

    return db.oneOrNone(sql, email);

}

User.findById = (id, callback) =>{
    const sql = `SELECT id, email, name, lastname, phone, image, password, session_token
    FROM users
    WHERE id= $1`;

    return db.oneOrNone(sql, id).then(user =>{ callback(null, user)});
}

User.create = async (user) => {

    // encriptar la contraseña para que no se vea en la bd
    const hash = await bcript.hash(user.password, 10);

    const sql = `INSERT INTO users(email, name, lastname, phone, image, password, created_at, updated_at)
                VALUES ($1, $2, $3, $4, $5, $6, $7, $8) RETURNING id`;
                
    return db.oneOrNone(sql, [
        user.email, 
        user.name,
        user.lastname,
        user.phone,
        user.image,
        hash,
        new Date(),
        new Date()
    ]);
}

User.update = (user) => {
    const sql = `UPDATE users SET name = $2,
                                lastname = $3,
                                phone = $4,
                                image = $5,
                                updated_at = $6
                WHERE id = $1`;

    return db.none(sql, [
        user.id,
        user.name,
        user.lastname,
        user.phone,
        user.image,
        new Date()
    ]);

}

module.exports = User;