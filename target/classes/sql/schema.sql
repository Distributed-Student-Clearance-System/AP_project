CREATE TABLE users (

    id INT PRIMARY KEY AUTO_INCREMENT,

    username VARCHAR(50) UNIQUE NOT NULL,

    password VARCHAR(255) NOT NULL,

    full_name VARCHAR(100) NOT NULL,

    role VARCHAR(20) NOT NULL
);

CREATE TABLE students (

    id INT PRIMARY KEY AUTO_INCREMENT,

    user_id INT UNIQUE,

    student_id VARCHAR(30) UNIQUE NOT NULL,

    department_name VARCHAR(100),

    batch_year INT,

    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE department_officers (

    id INT PRIMARY KEY AUTO_INCREMENT,

    user_id INT UNIQUE,

    department VARCHAR(50),

    office_name VARCHAR(100),

    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE admins (

    id INT PRIMARY KEY AUTO_INCREMENT,

    user_id INT UNIQUE,

    admin_level VARCHAR(50),

    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE clearance_requests (

    request_id INT PRIMARY KEY AUTO_INCREMENT,

    student_id INT,

    submitted_at DATETIME,

    status VARCHAR(30),

    FOREIGN KEY (student_id) REFERENCES students(id)
);

CREATE TABLE department_approvals (

    approval_id INT PRIMARY KEY AUTO_INCREMENT,

    request_id INT,

    department VARCHAR(50),

    officer_id INT,

    status VARCHAR(30),

    comment TEXT,

    reviewed_at DATETIME,

    FOREIGN KEY (request_id)
        REFERENCES clearance_requests(request_id),

    FOREIGN KEY (officer_id)
        REFERENCES department_officers(id)
);

CREATE TABLE notifications (

    notification_id INT PRIMARY KEY AUTO_INCREMENT,

    recipient_user_id INT,

    message TEXT,

    type VARCHAR(50),

    created_at DATETIME,

    is_read BOOLEAN,

    FOREIGN KEY (recipient_user_id)
        REFERENCES users(id)
);