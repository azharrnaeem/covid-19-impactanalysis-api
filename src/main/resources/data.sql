--username: mnaeem02, password: secret
insert into APP_USER(ID, PASSWORD, USERNAME) values(1, '$2b$10$OZqqT2751zwfC3KFhCogqufqN8pWPo4hLxGzSKROpy6DH66qBGeRi', 'mnaeem02');
insert into USER_ROLE(APP_USER_ID, ROLE) values(1, 'ADMIN');
insert into USER_ROLE(APP_USER_ID, ROLE) values(1, 'PREMIUM_MEMBER');