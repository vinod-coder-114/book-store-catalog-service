// Initialize a least-privilege user for catalog service access.
use('catalog_db');
db.createUser({
  user: 'catalog_user',
  pwd: 'catalog_password',
  roles: [
    { role: 'readWrite', db: 'catalog_db' }
  ]
});
