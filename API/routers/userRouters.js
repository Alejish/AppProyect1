const UserController = require('../controllers/usersController');
const User = require('../models/user');

module.exports = (app, upload) =>{

    // obtener datos
    app.get('/api/users/getAll', UserController.getAll);

    // inserción de datos
    app.post('/api/users/create', UserController.register);

    app.post('/api/users/login', UserController.login);

    // actualizar datos
    app.put('/api/users/update', upload.array('image', 1), UserController.update);

    // actualizar datos sin imagen
    app.put('/api/users/updateWithoutImage', UserController.updateWithoutImage);

};