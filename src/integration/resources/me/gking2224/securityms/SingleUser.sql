
insert into user (user_id, username, password, firstName, surname) values (100, 'test', '$2a$10$SLiHSqap.nWFyNW32En9FOwxz/JN5AyK/2kETN5c.BQxQdJWgWNYa', 'Test', 'User');

insert into role (role_id, name) values (100, 'Test Role');

insert into userrole(user_id, role_id) values (100, 100);

insert into permission(permission_id, name) values (100, 'Parent Permission');

insert into permission(permission_id, name, parent_id) values (101, 'Child Permission', 100);

insert into rolepermission(role_id, permission_id) values (100, 100);
