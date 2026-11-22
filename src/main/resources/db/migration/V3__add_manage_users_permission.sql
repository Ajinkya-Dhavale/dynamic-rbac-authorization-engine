INSERT INTO permissions (name)
SELECT 'MANAGE_USERS'
WHERE NOT EXISTS (
    SELECT 1 FROM permissions WHERE name = 'MANAGE_USERS'
);

INSERT INTO role_permissions (role_id, permission_id)
SELECT admin_role.id, manage_users_permission.id
FROM roles admin_role
CROSS JOIN permissions manage_users_permission
WHERE admin_role.name = 'ADMIN'
  AND manage_users_permission.name = 'MANAGE_USERS'
  AND NOT EXISTS (
      SELECT 1
      FROM role_permissions existing_mapping
      WHERE existing_mapping.role_id = admin_role.id
        AND existing_mapping.permission_id = manage_users_permission.id
  );
