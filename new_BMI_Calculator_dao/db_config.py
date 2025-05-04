import mysql.connector


def creer_connexion():
    return mysql.connector.connect(
        host='localhost',
        user='root',
        password='pass',
        database='bmi_app'
    )


def creer_tables():
    conn = creer_connexion()
    cur = conn.cursor()

    cur.execute('''
        CREATE TABLE IF NOT EXISTS user (
            id INT AUTO_INCREMENT PRIMARY KEY,
            last_name VARCHAR(50),
            first_name VARCHAR(50),
            sex VARCHAR(10),
            age INT,
            email VARCHAR(100) UNIQUE,
            password VARCHAR(100)
        )
    ''')

    cur.execute('''
        CREATE TABLE IF NOT EXISTS history (
            history_id INT AUTO_INCREMENT PRIMARY KEY,
            user_id INT,
            weight FLOAT,
            height FLOAT,
            waist FLOAT,
            bmi DECIMAL(5,2),
            bmi_classification VARCHAR(100),
            new_measure VARCHAR(100),
            date DATETIME,
            FOREIGN KEY (user_id) REFERENCES user(id)
        )
    ''')

    conn.commit()
    conn.close()
