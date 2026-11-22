INSERT INTO roles (name) VALUES ('ADMIN');
INSERT INTO roles (name) VALUES ('USER');

INSERT INTO permissions (name) VALUES ('MANAGE_ROLES');
INSERT INTO permissions (name) VALUES ('MANAGE_PERMISSIONS');
INSERT INTO permissions (name) VALUES ('ASSIGN_PERMISSIONS');
INSERT INTO permissions (name) VALUES ('ASSIGN_ROLES');
INSERT INTO permissions (name) VALUES ('READ_SECURE_DATA');

INSERT INTO users (username, password, active)
VALUES (
        'admin',
        '$2a$12$T76psstOKbVtjRWeSU/hL.8Y3dsyzZKo5NB0x3VJTUBrUVHhfh8jy',
        TRUE
);

INSERT INTO user_roles (user_id, role_id)
VALUES (
        (SELECT id FROM users WHERE username = 'admin'),
        (SELECT id FROM roles WHERE name = 'ADMIN')
);

INSERT INTO role_permissions (role_id, permission_id)
VALUES (
        (SELECT id FROM roles WHERE name = 'ADMIN'),
        (SELECT id FROM permissions WHERE name = 'MANAGE_ROLES')
);

INSERT INTO role_permissions (role_id, permission_id)
VALUES (
        (SELECT id FROM roles WHERE name = 'ADMIN'),
        (SELECT id FROM permissions WHERE name = 'MANAGE_PERMISSIONS')
);

INSERT INTO role_permissions (role_id, permission_id)
VALUES (
        (SELECT id FROM roles WHERE name = 'ADMIN'),
        (SELECT id FROM permissions WHERE name = 'ASSIGN_PERMISSIONS')
);

INSERT INTO role_permissions (role_id, permission_id)
VALUES (
        (SELECT id FROM roles WHERE name = 'ADMIN'),
        (SELECT id FROM permissions WHERE name = 'ASSIGN_ROLES')
);

INSERT INTO role_permissions (role_id, permission_id)
VALUES (
        (SELECT id FROM roles WHERE name = 'USER'),
        (SELECT id FROM permissions WHERE name = 'READ_SECURE_DATA')
);
