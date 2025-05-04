import jwt
import datetime
import bcrypt
from flask import request, jsonify

# Clé secrète pour signer les tokens JWT
SECRET_KEY = "bmi_secret_key"

# Calcul BMI
def calculer_bmi(weight, height):
    height_m = height / 100
    return round(weight / (height_m ** 2), 2)

# Classification BMI
#modifier le logique *classifivation BMI et nouvel mésure sont different
def classifier_bmi(bmi, waist, sex):
    sex = sex.lower()
    # Classification BMI
    if bmi < 18.5:
        classification = "Poids insuffisant"
    elif bmi < 25:
        classification = "Poids normal"
    elif bmi < 30:
        classification = "Excès de poids"
    elif bmi < 40:
        classification = "Obésité"
    else:
        classification = "Obésité morbide"

    # Nouvelle mesure
    if bmi < 18.5:
        new_measure = "Pas obèse"
    elif bmi >= 40:
        new_measure = "Obésité clinique"
    else:
        if (sex == "homme" and waist > 102) or (sex == "femme" and waist > 88):
            new_measure = "Obésité préclinique"
        else:
            new_measure = "Pas obèse"

    return classification, new_measure

# Hachage mot de passe
def hash_password(password):
    return bcrypt.hashpw(password.encode('utf-8'), bcrypt.gensalt()).decode('utf-8')

# Vérification du mot de passe haché
def verify_password(password, hashed):
    return bcrypt.checkpw(password.encode('utf-8'), hashed.encode('utf-8'))

# Création du token JWT
def generate_token(user_id):
    payload = {
        'user_id': user_id,
        'exp': datetime.datetime.utcnow() + datetime.timedelta(hours=2)
    }
    return jwt.encode(payload, SECRET_KEY, algorithm='HS256')

# Vérification du token dans une route (manuellement)
def check_token():
    token = request.headers.get('Authorization')
    if not token:
        return None, jsonify({'message': 'Token manquant'}), 403
    try:
        payload = jwt.decode(token, SECRET_KEY, algorithms=['HS256'])
        return payload['user_id'], None, None
    except jwt.ExpiredSignatureError:
        return None, jsonify({'message': 'Token expire'}), 401
    except jwt.InvalidTokenError:
        return None, jsonify({'message': 'Token invalide'}), 403