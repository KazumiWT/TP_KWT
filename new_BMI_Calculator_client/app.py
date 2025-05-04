from flask import Flask, render_template, request, redirect, session, url_for, flash
from functools import wraps

import requests


app = Flask(__name__)
app.secret_key = 'client_secret_key'
app.config['SESSION_PERMANENT'] = False
SERVICE_URL = "http://localhost:5000"

def login_required(route_function):
    @wraps(route_function)
    def wrapper(*args, **kwargs):
        if 'token' not in session:
            flash("Session expirée. Veuillez vous reconnecter.")
            return redirect(url_for('login'))
        return route_function(*args, **kwargs)
    return wrapper

@app.route('/')
def home():
    return redirect(url_for('login'))

@app.route('/register', methods=['GET', 'POST'])
def register():
    if request.method == 'POST':
        data = {
            "last_name": request.form['last_name'],
            "first_name": request.form['first_name'],
            "sex": request.form['sex'],
            "age": int(request.form['age']),
            "email": request.form['email'],
            "password": request.form['password']
        }
        resp = requests.post(f"{SERVICE_URL}/v1/service/register", json=data)
        if resp.status_code == 201:
            flash("Inscription réussie. Vous pouvez maintenant vous connecter.")
            return redirect(url_for('login'))
        else:
            flash("Erreur : " + resp.json().get("error", "Impossible de créer le compte."))

    return render_template('register.html')


@app.route('/login', methods=['GET', 'POST'])
def login():
    if request.method == 'POST':
        email = request.form['email']
        password = request.form['password']
        resp = requests.post(f"{SERVICE_URL}/v1/service/login", json={"email": email, "password": password})
        if resp.status_code == 200:
            data = resp.json()
            session['token'] = data['token']
            session['user_id'] = data['user_id']
            session['first_name'] = data['first_name']
            session['last_name'] = data['last_name']
            session['sex'] = data['sex']
            session['email'] = email
            return redirect(url_for('dashboard'))
        else:
            flash('Identifiants invalides')
    return render_template('login.html')


@app.route('/dashboard', methods=['GET', 'POST'])
@login_required
def dashboard():
    if 'token' not in session:
        return redirect(url_for('login'))

    if request.method == 'POST':
        data = {
            "user_id": session['user_id'],
            "weight": float(request.form['weight']),
            "height": float(request.form['height']),
            "waist": float(request.form['waist']),
            "sex": request.form['sex']
        }
        headers = {"Authorization": session['token']}
        requests.post(f"{SERVICE_URL}/v1/service/bmi", json=data, headers=headers)

        flash("Enregistrement effectué.")

    return render_template(
        'dashboard.html',
        first=session.get('first_name'),
        last=session.get('last_name'),
        sex=session.get('sex')
    )


@app.route('/history')
@login_required
def history():
    if 'token' not in session:
        return redirect(url_for('login'))

    headers = {"Authorization": session['token']}
    resp = requests.get(f"{SERVICE_URL}/v1/service/history/{session['user_id']}", headers=headers)
    data = resp.json() if resp.status_code == 200 else []
    return render_template(
        'history.html',
        history=data,
        first=session.get('first_name'),
        last=session.get('last_name')
    )

@app.route('/delete/<int:entry_id>')
@login_required
def delete(entry_id):
    headers = {"Authorization": session['token']}
    requests.delete(f"{SERVICE_URL}/v1/service/history/{entry_id}", headers=headers)
    return redirect(url_for('history'))

@app.route('/logout')
def logout():
    session.clear()
    return redirect(url_for('login'))

if __name__ == '__main__':
    app.run(port=3000, debug=True)