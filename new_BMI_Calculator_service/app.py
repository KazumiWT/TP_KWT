from flask import Flask
from service_user import user_bp
from service_history import history_bp

app = Flask(__name__)
app.register_blueprint(user_bp)
app.register_blueprint(history_bp)

if __name__ == '__main__':
    app.run(port=5000, debug=False)