from flask import Flask
from mod_dao_user import dao_user
from mod_dao_history import dao_history
from db_config import creer_tables

app = Flask(__name__)
creer_tables()

app.register_blueprint(dao_user)
app.register_blueprint(dao_history)

if __name__ == '__main__':
    app.run(port=5600, debug=True)
