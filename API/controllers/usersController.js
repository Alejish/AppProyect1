const { json } = require('express');
const User = require('../models/user');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const keys = require('../config/keys');
const { update } = require('../models/user');
const storage = require('../utils/cloud_storage');

//const passport = require('../config/passport');

module.exports = {

    async getAll(req, res, next){
        try {
            const data = await User.getAll();
            console.log(`Usuarios : ${data}`);
            return res.status(201).json(data);

        } catch (error) {
            console.log(`error: ${error}`);
            return res.status(501).json({
                success: false,
                message: 'Error al obtener los usuarios'
            });
        }
    },

    async register(req, res, next){
        try {
            const user = req.body;
            const data = await User.create(user);

            return res.status(201).json({
                success: true,
                message: 'El registro se realizó correctamente',
                data: {
                    "id": data.id
                }
            });

        } catch (error) {
            console.log(`error: ${error}`);
            return res.status(501).json({
                success: false,
                message: 'Hubo un error con el registro del usuario',
                error : error
            });
        }
    },

    async login(req, res, next){
        try {
            
            const email = req.body.email;
            const password = req.body.password;

            const myUser = await User.findByEmail(email);

            if(!myUser){
                return res.status(401).json({
                    success: false,
                    message: 'EL email no fue encontrado'
                });
            }

            const inPasswordValid = await bcrypt.compare(password, myUser.password);

            if (inPasswordValid) {
                const token = jwt.sign({ id: myUser.id, email: myUser.email}, keys.secretOrKey, {
                    //expiresIn: 
                })

                const data = {
                    id: myUser.id,
                    name: myUser.name,
                    lastname: myUser.lastname,
                    email: myUser.email,
                    phone: myUser.phone,
                    image: myUser.image,
                    session_token: `JWT ${token}`
                };

                return res.status(201).json({
                    success: true,
                    message: 'EL usuario ha sido autenticado',
                    data: data
                });

            }
            else{
                return res.status(401).json({
                    success: false,
                    message: 'La contraseña es incorrecta'
                    
                });
            }

        } catch (error) {
            console.log(`error: ${error}`);
            return res.status(501).json({
                success: false,
                message: 'Hubo un error con el login del usuario',
                error : error
            });
        }
    },

    async update(req, res, next){
        try {
            
            console.log('Usuario', req.body.user );
            const user = JSON.parse(req.body.user); //Cliente debe enviarnos un objeto user

            const files = req.files;

            if(files.length > 0){ // significa que el cliente aenvia un archivo 

                const pathImage = `image_${Date.now()}`; // nombre del archivo
                const url = await storage([0], pathImage);

                if(url != undefined && url != null){
                    user.image = url;
                }
            }

            await User.update(user); // guardando la URL de la bdd

            return res.status(201).json({
                success: true,
                message: 'Los datos del usuario se han actualizado correctamente',
                data: user
            });

        } catch (error) {
            console.log(`error: ${error}`);
            return res.status(501).json({
                success: false,
                message: 'Hubo un error al actualizar del usuario',
                error : error
            });
        }
    },

    async updateWithoutImage(req, res, next){
        try {
            
            console.log('Usuario', req.body);
            const user = req.body; //Cliente debe enviarnos un objeto user

            const files = req.files;

            await User.update(user); // guardando la URL de la bdd

            return res.status(201).json({
                success: true,
                message: 'Los datos del usuario se han actualizado correctamente',
                data: user
            });

        } catch (error) {
            console.log(`error: ${error}`);
            return res.status(501).json({
                success: false,
                message: 'Hubo un error al actualizar del usuario',
                error : error
            });
        }
    }
};