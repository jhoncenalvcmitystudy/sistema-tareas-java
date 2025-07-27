DROP DATABASE sistema_login;
CREATE DATABASE sistema_login;
USE sistema_login;

CREATE TABLE usuarios (
                          id INT PRIMARY KEY AUTO_INCREMENT,
                          nombres VARCHAR(100),
                          apellido_paterno VARCHAR(100),
                          apellido_materno VARCHAR(100),
                          correo VARCHAR(100) UNIQUE,
                          contraseña VARCHAR(100)
);

CREATE TABLE proyectos (
                           id INT PRIMARY KEY AUTO_INCREMENT,
                           id_usuario INT,
                           nombre VARCHAR(255),
                           FOREIGN KEY (id_usuario) REFERENCES usuarios(id) ON DELETE CASCADE
);

CREATE TABLE tareas (
                        id INT PRIMARY KEY AUTO_INCREMENT,
                        id_proyecto INT,
                        nombre VARCHAR(255),
                        fecha DATE,
                        completada BOOLEAN DEFAULT FALSE,
                        FOREIGN KEY (id_proyecto) REFERENCES proyectos(id) ON DELETE CASCADE
);
