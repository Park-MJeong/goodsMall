CREATE DATABASE IF NOT EXISTS user_service;
CREATE DATABASE IF NOT EXISTS product_service;
CREATE DATABASE IF NOT EXISTS order_service;
CREATE DATABASE IF NOT EXISTS cart_service;

GRANT ALL PRIVILEGES ON product_service.* TO 'user'@'%';

GRANT ALL PRIVILEGES ON cart_service.* TO 'user'@'%';

GRANT ALL PRIVILEGES ON order_service.* TO 'user'@'%';

GRANT ALL PRIVILEGES ON user_service.* TO 'user'@'%';


GRANT ALL PRIVILEGES ON user_service.* TO 'user'@'%';

FLUSH PRIVILEGES;