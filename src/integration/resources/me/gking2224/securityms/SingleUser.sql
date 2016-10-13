
insert into user (user_id, username, password) values (1, 'Test User', 's1l$s!g26be&2n*gw$l3£l3%krbj');

insert into role (role_id, name) values (1, 'Test Role');

insert into userrole(user_id, role_id) values (1, 1);

insert into permission(permission_id, name) values (1, 'Parent Permission');

insert into permission(permission_id, name, parent_id) values (2, 'Child Permission', 1);

insert into rolepermission(role_id, permission_id) values (1, 1);
